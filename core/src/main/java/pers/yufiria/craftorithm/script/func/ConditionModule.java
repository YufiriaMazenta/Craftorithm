package pers.yufiria.craftorithm.script.func;

import crypticlib.chat.BukkitTextProcessor;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.hook.PlayerPointsHook;
import pers.yufiria.craftorithm.hook.VaultHook;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.trigger.Trigger;

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
        registry.register("world", this::world);
        registry.register("game_mode", this::gameMode);
        registry.register("item", this::item);
        registry.register("biome", this::biome);
        registry.register("in_water", this::inWater);
        registry.register("in_rain", this::inRain);
        registry.register("light_level", this::lightLevel);
        registry.register("context", this::context);
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
     * papi "%placeholder%" → 解析 PAPI 变量并返回值
     * 比较通过脚本运算符实现: papi "%level%" >= 10
     */
    private ScriptValue papi(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = ctx.player();
        String placeholder = args[0].asString();
        String resolved = BukkitTextProcessor.placeholder(player, placeholder);
        try {
            return ScriptValue.of(Double.parseDouble(resolved));
        } catch (NumberFormatException e) {
            return ScriptValue.of(resolved);
        }
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

    /**
     * world "world" → 检查玩家所在世界
     */
    private ScriptValue world(ScriptContext ctx, ScriptValue... args) {
        if (args.length == 0) return ScriptValue.of(ctx.player().getWorld().getName());
        String expected = args[0].asString();
        return ScriptValue.of(ctx.player().getWorld().getName().equals(expected));
    }

    /**
     * game_mode "SURVIVAL" → 检查玩家游戏模式
     */
    private ScriptValue gameMode(ScriptContext ctx, ScriptValue... args) {
        if (args.length == 0) return ScriptValue.of(ctx.player().getGameMode().name());
        String expected = args[0].asString();
        return ScriptValue.of(ctx.player().getGameMode().name().equalsIgnoreCase(expected));
    }

    /**
     * biome "minecraft:ocean" → 检查玩家所在群系
     */
    private ScriptValue biome(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        String expected = args[0].asString();
        String actual = ctx.player().getLocation().getBlock().getBiome().getKey().toString();
        return ScriptValue.of(actual.equalsIgnoreCase(expected));
    }

    /**
     * in_water → 检查玩家是否在水中
     */
    private ScriptValue inWater(ScriptContext ctx, ScriptValue... args) {
        Block block = ctx.player().getLocation().getBlock();
        return ScriptValue.of(block.getType() == Material.WATER);
    }

    /**
     * in_rain → 检查玩家是否在雨中
     */
    private ScriptValue inRain(ScriptContext ctx, ScriptValue... args) {
        return ScriptValue.of(ctx.player().getLocation().getBlock().getBiome().name().contains("RAIN")
            || ctx.player().getWorld().hasStorm());
    }

    /**
     * light_level → 返回玩家所在位置亮度
     * light_level >= 7 → 检查亮度
     */
    private ScriptValue lightLevel(ScriptContext ctx, ScriptValue... args) {
        int level = ctx.player().getLocation().getBlock().getLightLevel();
        if (args.length == 0) return ScriptValue.of(level);
        if (args.length >= 2) {
            String operator = args[0].asString();
            int expected = (int) args[1].asNumber();
            boolean result = switch (operator) {
                case "==" -> level == expected;
                case "!=" -> level != expected;
                case ">"  -> level > expected;
                case ">=" -> level >= expected;
                case "<"  -> level < expected;
                case "<=" -> level <= expected;
                default -> false;
            };
            return ScriptValue.of(result);
        }
        return ScriptValue.of(level);
    }

    /**
     * context "key" → 返回上下文变量值
     * 比较通过脚本运算符实现: context "damage" >= 10
     */
    private ScriptValue context(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        String key = args[0].asString();
        ScriptValue var = ctx.getVariable(key);
        if (var == null) return ScriptValue.nil();
        return var;
    }

    /**
     * item "craftorithm:my_item" → 检查事件中的物品ID（不含数量部分）
     * item "craftorithm:my_item" >= 5 → 检查物品ID和数量
     */
    private ScriptValue item(ScriptContext ctx, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        ScriptValue eventItem = ctx.getVariable("item");
        if (eventItem == null) return ScriptValue.of(false);
        String itemStr = eventItem.asString();
        // itemStr 格式: "namespace:id:amount" 或 "namespace:id"
        String expectedId = args[0].asString();
        String itemId = itemStr.contains(":") ? itemStr.substring(0, itemStr.lastIndexOf(':')) : itemStr;
        if (args.length == 1) {
            return ScriptValue.of(itemId.equals(expectedId));
        }
        if (args.length >= 3) {
            String operator = args[1].asString();
            String amountStr = args[2].asString();
            try {
                boolean result = compareItem(itemStr, amountStr, operator);
                return ScriptValue.of(itemId.equals(expectedId) && result);
            } catch (NumberFormatException e) {
                return ScriptValue.of(itemId.equals(expectedId));
            }
        }
        return ScriptValue.of(itemId.equals(expectedId));
    }

    /**
     * 判断物品是否符合要求
     * @param itemStr
     * @param amountStr
     * @param operator
     * @return
     */
    private static boolean compareItem(String itemStr, String amountStr, String operator) {
        int amount = Integer.parseInt(itemStr.substring(itemStr.lastIndexOf(':') + 1));
        int expected = Integer.parseInt(amountStr);
        return switch (operator) {
            case "==" -> amount == expected;
            case "!=" -> amount != expected;
            case ">"  -> amount > expected;
            case ">=" -> amount >= expected;
            case "<"  -> amount < expected;
            case "<=" -> amount <= expected;
            default -> false;
        };
    }

}
