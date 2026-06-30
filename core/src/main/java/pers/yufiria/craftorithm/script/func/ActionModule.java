package pers.yufiria.craftorithm.script.func;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import crypticlib.util.IOHelper;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.hook.PlayerPointsHook;
import pers.yufiria.craftorithm.hook.VaultHook;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.script.vm.ScriptVM;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.custom.CustomMenuManager;

import java.util.Objects;
import java.util.Optional;

/**
 * 内置动作函数模块
 *
 * 使用示例:
 *   command "give %player% diamond 1"
 *   console "say hello"
 *   tell "&aHello!"
 *   take-money 100
 *   give-exp 50
 *   take-level 5
 */
public enum ActionModule implements ScriptModule {

    INSTANCE;

    @Override
    public String moduleName() {
        return "actions";
    }

    @Override
    public void register(ScriptFunctionRegistry registry) {
        registry.register("command", this::command);
        registry.register("console", this::console);
        registry.register("tell", this::tell);
        registry.register("actionbar", this::actionbar);
        registry.register("title", this::title);
        registry.register("take-money", this::takeMoney);
        registry.register("give-money", this::giveMoney);
        registry.register("take-level", this::takeLevel);
        registry.register("give-level", this::giveLevel);
        registry.register("give-exp", this::giveExp);
        registry.register("take-points", this::takePoints);
        registry.register("give-points", this::givePoints);
        registry.register("close", this::close);
        registry.register("back", this::back);
        registry.register("openmenu", this::openmenu);
        registry.register("discover-recipe", this::discoverRecipe);
        registry.register("undiscover-recipe", this::undiscoverRecipe);
        registry.register("set", this::set);
        registry.register("delay", this::delay);
        registry.register("sound", this::sound);
    }

    private ScriptValue back(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
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
        Player player = ctx.player();
        if (player == null) return ScriptValue.of(false);
        String menuName = args[0].asString();
        CustomMenuManager.INSTANCE.openMenu(player, menuName, result -> {});
        return null;
    }

    private ScriptValue command(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Player player = ctx.player();
        if (player == null) return ScriptValue.of(false);
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String cmd = BukkitTextProcessor.placeholder(player, sb.toString());
        return ScriptValue.of(Bukkit.dispatchCommand(player, cmd));
    }

    private ScriptValue console(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Player player = ctx.player();
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String cmd = BukkitTextProcessor.placeholder(player, sb.toString());
        return ScriptValue.of(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
    }

    private ScriptValue tell(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String msg = BukkitTextProcessor.placeholder(player, sb.toString());
        BukkitMsgSender.INSTANCE.sendMsg(player, msg);
        return ScriptValue.nil();
    }

    private ScriptValue actionbar(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        StringBuilder sb = new StringBuilder();
        for (ScriptValue arg : args) {
            sb.append(arg.asString());
        }
        String msg = BukkitTextProcessor.placeholder(player, sb.toString());
        BukkitMsgSender.INSTANCE.sendActionBar(player, msg);
        return ScriptValue.nil();
    }

    private ScriptValue title(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        String title = BukkitTextProcessor.placeholder(player, args[0].asString());
        String subtitle = args.length > 1 ? BukkitTextProcessor.placeholder(player, args[1].asString()) : "";
        BukkitMsgSender.INSTANCE.sendTitle(player, title, subtitle, 10, 70, 20);
        return ScriptValue.nil();
    }

    private ScriptValue takeMoney(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!VaultHook.INSTANCE.isEconomyHooked()) return ScriptValue.of(false);
        Player player = ctx.player();
        if (player == null) return ScriptValue.of(false);
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        double amount = args[0].asNumber();
        if (amount > 0) {
            economy.withdrawPlayer(player, amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue giveMoney(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!VaultHook.INSTANCE.isEconomyHooked()) return ScriptValue.of(false);
        Player player = ctx.player();
        if (player == null) return ScriptValue.of(false);
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        double amount = args[0].asNumber();
        if (amount > 0) {
            economy.depositPlayer(player, amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue takeLevel(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        int amount = (int) args[0].asNumber();
        player.setLevel(Math.max(0, player.getLevel() - amount));
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue giveLevel(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        int amount = (int) args[0].asNumber();
        player.setLevel(player.getLevel() + amount);
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue giveExp(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        int amount = (int) args[0].asNumber();
        player.giveExp(Math.max(0, amount));
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue takePoints(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) return ScriptValue.of(false);
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        int amount = (int) args[0].asNumber();
        if (amount > 0) {
            api.take(ctx.playerUniqueId(), amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue givePoints(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) return ScriptValue.of(false);
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        int amount = (int) args[0].asNumber();
        if (amount > 0) {
            api.give(ctx.playerUniqueId(), amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue close(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        player.closeInventory();
        return ScriptValue.nil();
    }

    private ScriptValue discoverRecipe(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Player player = ctx.player();
        if (player == null) return ScriptValue.of(false);
        NamespacedKey recipeKey = NamespacedKey.fromString(args[0].asString());
        return ScriptValue.of(player.discoverRecipe(Objects.requireNonNull(recipeKey)));
    }

    private ScriptValue undiscoverRecipe(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Player player = ctx.player();
        if (player == null) return ScriptValue.of(false);
        NamespacedKey recipeKey = NamespacedKey.fromString(args[0].asString());
        return ScriptValue.of(player.undiscoverRecipe(Objects.requireNonNull(recipeKey)));
    }

    private ScriptValue set(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 2) return ScriptValue.nil();
        String key = args[0].asString();
        ScriptValue value = args[1];
        ctx.setVariable(key, value);
        return ScriptValue.nil();
    }

    /**
     * delay(ticks) → 暂停执行指定tick后继续
     */
    private ScriptValue delay(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        long ticks = (long) args[0].asNumber();
        if (ticks > 0) {
            vm.pauseAndScheduleResume(ticks);
        }
        return ScriptValue.nil();
    }

    /**
     * sound <sound> [volume] [pitch] → 向玩家播放音频
     * 示例:
     *   sound "entity.experience_orb.pickup"
     *   sound "entity.experience_orb.pickup" 1.0 1.0
     */
    private ScriptValue sound(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        if (player == null) return ScriptValue.nil();
        String soundName = args[0].asString();
        float volume = args.length > 1 ? (float) args[1].asNumber() : 1.0f;
        float pitch = args.length > 2 ? (float) args[2].asNumber() : 1.0f;
        Sound sound = Registry.SOUNDS.get(Objects.requireNonNull(NamespacedKey.fromString(soundName)));
        if (sound == null) {
            return ScriptValue.nil();
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
        return ScriptValue.nil();
    }

}
