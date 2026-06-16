package pers.yufiria.craftorithm.script.func;

import crypticlib.chat.BukkitTextProcessor;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.hook.PlayerPointsHook;
import pers.yufiria.craftorithm.hook.VaultHook;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptValue;

/**
 * 内置条件函数模块
 *
 * 使用示例:
 *   perm "craftorithm.special"
 *   papi "%player_level%" >= "10"
 *   level >= 10
 *   money >= 1000
 *   points >= 50
 */
public enum ConditionModule implements ScriptModule {

    INSTANCE;

    @Override
    public String moduleName() {
        return "conditions";
    }

    @Override
    public void register(ScriptFunctionRegistry registry) {
        registry.register("perm", this::perm);
        registry.register("papi", this::papi);
        registry.register("level", this::level);
        registry.register("money", this::money);
        registry.register("points", this::points);
    }

    /**
     * perm "xxx" → 检查玩家是否有权限
     */
    private ScriptValue perm(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        String perm = args[0].asString();
        Player player = ctx.player();
        perm = BukkitTextProcessor.placeholder(player, perm);
        return ScriptValue.of(player.hasPermission(perm));
    }

    /**
     * papi "%placeholder%" → 解析 PAPI 变量
     * papi "%placeholder%" ">=" "10" → 比较
     */
    private ScriptValue papi(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        String placeholder = args[0].asString();
        String resolved = BukkitTextProcessor.placeholder(player, placeholder);

        if (args.length == 1) {
            return ScriptValue.of(resolved);
        }
        if (args.length >= 3) {
            String operator = args[1].asString();
            String value = args[2].asString();
            return compareStrings(resolved, operator, value);
        }
        return ScriptValue.of(Boolean.parseBoolean(resolved));
    }

    /**
     * level → 返回玩家等级
     */
    private ScriptValue level(ScriptContext ctx, ScriptValue... args) {
        return ScriptValue.of(ctx.player().getLevel());
    }

    /**
     * money → 返回玩家余额
     */
    private ScriptValue money(ScriptContext ctx, ScriptValue... args) {
        if (!VaultHook.INSTANCE.isEconomyHooked()) {
            return ScriptValue.of(0);
        }
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        return ScriptValue.of(economy.getBalance(ctx.player()));
    }

    /**
     * points → 返回玩家点数
     */
    private ScriptValue points(ScriptContext ctx, ScriptValue... args) {
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) {
            return ScriptValue.of(0);
        }
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        return ScriptValue.of(api.look(ctx.player().getUniqueId()));
    }

    private ScriptValue compareStrings(String left, String operator, String right) {
        try {
            double l = Double.parseDouble(left);
            double r = Double.parseDouble(right);
            boolean result = switch (operator) {
                case "==" -> l == r;
                case "!=" -> l != r;
                case ">"  -> l > r;
                case ">=" -> l >= r;
                case "<"  -> l < r;
                case "<=" -> l <= r;
                default -> false;
            };
            return ScriptValue.of(result);
        } catch (NumberFormatException e) {
            int cmp = left.compareTo(right);
            boolean result = switch (operator) {
                case "==" -> cmp == 0;
                case "!=" -> cmp != 0;
                case ">"  -> cmp > 0;
                case ">=" -> cmp >= 0;
                case "<"  -> cmp < 0;
                case "<=" -> cmp <= 0;
                default -> false;
            };
            return ScriptValue.of(result);
        }
    }
}
