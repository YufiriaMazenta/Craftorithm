package pers.yufiria.craftorithm.script;

import crypticlib.*;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.ScriptFunctionRegistry;
import crypticlib.script.func.ScriptModule;
import crypticlib.script.vm.ScriptVM;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import crypticlib.util.IOHelper;
import crypticlib.util.InventoryHelper;
import crypticlib.util.InventoryViewHelper;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.custom.CustomMenuManager;
import pers.yufiria.craftorithm.util.ItemUtils;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * 内置动作函数模块
 *
 * 使用示例:
 *   command "give %player% diamond 1"
 *   console "say hello"
 *   tell "&aHello!"
 *   take_money 100
 *   give_exp 50
 *   take_level 5
 */
public enum ActionModule implements ScriptModule {

    INSTANCE;

    @Override
    public String moduleName() {
        return "actions";
    }

    @Override
    public void register(ScriptFunctionRegistry registry) {
        String moduleName = moduleName();
        registry.register(moduleName, "command", this::command);
        registry.register(moduleName, "console", this::console);
        registry.register(moduleName, "tell", this::tell);
        registry.register(moduleName, "actionbar", this::actionbar);
        registry.register(moduleName, "title", this::title);
        registry.register(moduleName, "log", this::log);
        registry.register(moduleName, "take_level", this::takeLevel);
        registry.register(moduleName, "give_level", this::giveLevel);
        registry.register(moduleName, "give_exp", this::giveExp);
        registry.register(moduleName, "close", this::close);
        registry.register(moduleName, "back", this::back);
        registry.register(moduleName, "openmenu", this::openmenu);
        registry.register(moduleName, "discover_recipe", this::discoverRecipe);
        registry.register(moduleName, "undiscover_recipe", this::undiscoverRecipe);
        registry.register(moduleName, "sound", this::sound);
        registry.register(moduleName, "set_inv_item", this::setInvItem);
    }

    private ScriptValue setInvItem(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        Inventory topInventory = InventoryViewHelper.getTopInventory(player);
        if (args.length < 2) {
            return ScriptValue.nil();
        }
        int slot = (int) args[0].asNumber();
        String itemIdStr = args[1].asString();
        NamespacedItemIdStack itemIdStack = NamespacedItemIdStack.fromString(itemIdStr);
        if (itemIdStack == null) {
            return ScriptValue.of(false);
        }
        Optional<ItemStack> itemStackOpt = ItemManager.INSTANCE.matchItem(itemIdStack);
        if (itemStackOpt.isEmpty()) {
            return ScriptValue.of(false);
        }
        CrypticLib.scheduler().sync(() -> {
            ItemStack slotItem = topInventory.getItem(slot);
            if (ItemHelper.isAir(slotItem)) {
                topInventory.setItem(slot, itemStackOpt.get());
            } else {
                //对应位置已经有物品了,那我们就尝试放入,如果不能放入就丢在地上
                InventoryHelper.addItemOrDrop(topInventory, itemStackOpt.get());
            }
        });
        return ScriptValue.of(true);
    }

    private ScriptValue back(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        Optional<Menu> openingMenuOpt = MenuHelper.getOpeningMenu(player);
        IOHelper.debug("is opening menu: " + openingMenuOpt.isPresent());
        if (openingMenuOpt.isPresent()) {
            Menu menu = openingMenuOpt.get();
            if (menu instanceof BackableMenu backableMenu) {
                Menu parentMenu = backableMenu.parentMenu();
                if (parentMenu != null) {
                    parentMenu.openMenu();
                } else {
                    player.closeInventory();
                }
            } else {
                player.closeInventory();
            }
        }
        return null;
    }

    private ScriptValue openmenu(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.of(false);
        }
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        String menuName = args[0].asString();
        CustomMenuManager.INSTANCE.openMenu(player, menuName, result -> {});
        return null;
    }

    private ScriptValue command(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Invoker invoker = ctx.invoker();
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String cmd = sb.toString();
        if (invoker.isPlayer()) {
            String finalCmd = cmd;
            cmd = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer)
                .map(player -> BukkitTextProcessor.placeholder(player, finalCmd))
                .orElse(cmd);
        }

        CommandSender commandSender = (CommandSender) invoker.getPlatformInvoker();
        if (!CrypticLibBukkit.isFolia()) {
            return ScriptValue.of(Bukkit.dispatchCommand(commandSender, cmd));
        } else {
            //垃圾folia
            String finalCmd = cmd;
            Runnable task = () -> Bukkit.dispatchCommand(commandSender, finalCmd);
            if (commandSender instanceof Entity entity) {
                CrypticLibBukkit.scheduler().runOnEntity(entity, task, task);
            } else {
                CrypticLibBukkit.scheduler().sync(task);
            }
            return ScriptValue.of(true);
        }
    }

    private ScriptValue console(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Invoker invoker = ctx.invoker();
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String cmd = sb.toString();
        if (invoker.isPlayer()) {
            String finalCmd = cmd;
            cmd = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer)
                .map(player -> BukkitTextProcessor.placeholder(player, finalCmd))
                .orElse(cmd);
        }

        if (!CrypticLibBukkit.isFolia()) {
            return ScriptValue.of(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
        } else {
            String finalCmd1 = cmd;
            CrypticLibBukkit.scheduler().sync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd1));
            return ScriptValue.of(true);
        }
    }

    private ScriptValue tell(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Invoker invoker = ctx.invoker();
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String msg = sb.toString();
        if (invoker.isPlayer()) {
            String finalMsg = msg;
            msg = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer)
                .map(player -> BukkitTextProcessor.placeholder(player, finalMsg))
                .orElse(msg);
        }
        invoker.sendMsg(msg);
        return ScriptValue.nil();
    }

    private ScriptValue actionbar(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String msg = BukkitTextProcessor.placeholder(player, sb.toString());
        BukkitPlayer.byPlayer(player).sendActionBar(msg);
        return ScriptValue.nil();
    }

    private ScriptValue title(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        String title = BukkitTextProcessor.placeholder(player, args[0].asString());
        String subtitle = args.length > 1 ? BukkitTextProcessor.placeholder(player, args[1].asString()) : "";
        BukkitPlayer.byPlayer(player).sendTitle(title, subtitle, 10, 70, 20);
        return ScriptValue.nil();
    }

    private ScriptValue log(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String msg = BukkitTextProcessor.placeholder(playerOpt.orElse(null), sb.toString());
        IOHelper.info(msg);
        return ScriptValue.nil();
    }

    private ScriptValue takeLevel(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.nil();
        }
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        int amount = (int) args[0].asNumber();
        player.setLevel(Math.max(0, player.getLevel() - amount));
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue giveLevel(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.nil();
        }
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        int amount = (int) args[0].asNumber();
        player.setLevel(player.getLevel() + amount);
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue giveExp(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.nil();
        }
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        int amount = (int) args[0].asNumber();
        player.giveExp(Math.max(0, amount));
        return ScriptValue.of(player.getLevel());
    }



    private ScriptValue close(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        player.closeInventory();
        return ScriptValue.nil();
    }

    private ScriptValue discoverRecipe(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        NamespacedKey recipeKey = NamespacedKey.fromString(args[0].asString());
        return ScriptValue.of(player.discoverRecipe(Objects.requireNonNull(recipeKey)));
    }

    private ScriptValue undiscoverRecipe(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        NamespacedKey recipeKey = NamespacedKey.fromString(args[0].asString());
        return ScriptValue.of(player.undiscoverRecipe(Objects.requireNonNull(recipeKey)));
    }

    /**
     * sound <sound> [volume] [pitch] → 向玩家播放音频
     * 示例:
     *   sound "entity.experience_orb.pickup"
     *   sound "entity.experience_orb.pickup" 1.0 1.0
     */
    private ScriptValue sound(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        String soundName = args[0].asString();
        float volume = args.length > 1 ? (float) args[1].asNumber() : 1.0f;
        float pitch = args.length > 2 ? (float) args[2].asNumber() : 1.0f;
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21_4)) {
            Sound sound = Registry.SOUNDS.get(Objects.requireNonNull(NamespacedKey.fromString(soundName)));
            if (sound == null) {
                return ScriptValue.nil();
            }
            player.playSound(player.getLocation(), sound, volume, pitch);
        } else {
            NamespacedKey soundKey = NamespacedKey.fromString(soundName);
            if (soundKey != null) {
                player.playSound(player.getLocation(), soundKey.getKey(), volume, pitch);
            }
        }
        return ScriptValue.nil();
    }

}
