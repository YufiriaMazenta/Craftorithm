package pers.yufiria.craftorithm.script.func;

import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.util.IOHelper;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.hook.PlayerPointsHook;
import pers.yufiria.craftorithm.hook.VaultHook;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptValue;

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
    }

    private ScriptValue command(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Player player = ctx.player();
        IOHelper.info(args[0].asString());
        String cmd = BukkitTextProcessor.placeholder(player, args[0].asString());
        IOHelper.info(cmd);
        return ScriptValue.of(Bukkit.dispatchCommand(player, cmd));
    }

    private ScriptValue console(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Player player = ctx.player();
        String cmd = BukkitTextProcessor.placeholder(player, args[0].asString());
        return ScriptValue.of(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
    }

    private ScriptValue tell(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        String msg = BukkitTextProcessor.placeholder(player, args[0].asString());
        BukkitMsgSender.INSTANCE.sendMsg(player, msg);
        return ScriptValue.nil();
    }

    private ScriptValue actionbar(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        String msg = BukkitTextProcessor.placeholder(player, args[0].asString());
        BukkitMsgSender.INSTANCE.sendActionBar(player, msg);
        return ScriptValue.nil();
    }

    private ScriptValue title(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        String title = BukkitTextProcessor.placeholder(player, args[0].asString());
        String subtitle = args.length > 1 ? BukkitTextProcessor.placeholder(player, args[1].asString()) : "";
        BukkitMsgSender.INSTANCE.sendTitle(player, title, subtitle, 10, 70, 20);
        return ScriptValue.nil();
    }

    private ScriptValue takeMoney(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!VaultHook.INSTANCE.isEconomyHooked()) return ScriptValue.of(false);
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        double amount = args[0].asNumber();
        if (amount > 0) {
            economy.withdrawPlayer(ctx.player(), amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue giveMoney(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!VaultHook.INSTANCE.isEconomyHooked()) return ScriptValue.of(false);
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        double amount = args[0].asNumber();
        if (amount > 0) {
            economy.depositPlayer(ctx.player(), amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue takeLevel(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        int amount = (int) args[0].asNumber();
        player.setLevel(Math.max(0, player.getLevel() - amount));
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue giveLevel(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        int amount = (int) args[0].asNumber();
        player.setLevel(player.getLevel() + amount);
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue giveExp(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        int amount = (int) args[0].asNumber();
        player.giveExp(Math.max(0, amount));
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue takePoints(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) return ScriptValue.of(false);
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        int amount = (int) args[0].asNumber();
        if (amount > 0) {
            api.take(ctx.player().getUniqueId(), amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue givePoints(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) return ScriptValue.of(false);
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        int amount = (int) args[0].asNumber();
        if (amount > 0) {
            api.give(ctx.player().getUniqueId(), amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue close(ScriptContext ctx, ScriptValue... args) {
        ctx.player().closeInventory();
        return ScriptValue.nil();
    }

}
