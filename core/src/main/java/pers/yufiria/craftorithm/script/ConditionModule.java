package pers.yufiria.craftorithm.script;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.ScriptFunctionRegistry;
import crypticlib.script.func.ScriptModule;
import crypticlib.script.vm.ScriptVM;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.util.Optional;

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
        String moduleName = moduleName();
        registry.register(moduleName, "perm", this::perm);
        registry.register(moduleName, "papi", this::papi);
        registry.register(moduleName, "level", this::level);
        registry.register(moduleName, "world", this::world);
        registry.register(moduleName, "gamemode", this::gameMode);
        registry.register(moduleName, "item", this::item);
        registry.register(moduleName, "biome", this::biome);
        registry.register(moduleName, "in_water", this::inWater);
        registry.register(moduleName, "in_rain", this::inRain);
        registry.register(moduleName, "light_level", this::lightLevel);
    }

    private ScriptValue perm(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        String perm = args[0].asString();
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        perm = BukkitTextProcessor.placeholder(player, perm);
        return ScriptValue.of(player.hasPermission(perm));
    }

    private ScriptValue papi(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        String placeholder = args[0].asString();
        String resolved = BukkitTextProcessor.placeholder(player, placeholder);
        try {
            return ScriptValue.of(Double.parseDouble(resolved));
        } catch (NumberFormatException e) {
            return ScriptValue.of(resolved);
        }
    }

    private ScriptValue level(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue world(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        if (args.length == 0) {
            return ScriptValue.of(player.getWorld().getName());
        }
        String expected = args[0].asString();
        return ScriptValue.of(player.getWorld().getName().equals(expected));
    }

    private ScriptValue gameMode(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        if (args.length == 0) {
            return ScriptValue.of(player.getGameMode().name());
        }
        String expected = args[0].asString();
        return ScriptValue.of(player.getGameMode().name().equalsIgnoreCase(expected));
    }

    private ScriptValue biome(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        String playerBiome = player.getLocation().getBlock().getBiome().getKey().toString();
        if (args.length < 1) {
            return ScriptValue.of(playerBiome);
        }
        String expected = args[0].asString();
        return ScriptValue.of(playerBiome.equalsIgnoreCase(expected));
    }

    private ScriptValue inWater(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        Block block = player.getLocation().getBlock();
        return ScriptValue.of(block.getType() == Material.WATER);
    }

    @SuppressWarnings("all")
    private ScriptValue inRain(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        return ScriptValue.of(player.getLocation().getBlock().getBiome().name().contains("RAIN")
            || player.getWorld().hasStorm());
    }

    private ScriptValue lightLevel(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.nil();
        }
        Player player = playerOpt.get();
        int level = player.getLocation().getBlock().getLightLevel();
        return ScriptValue.of(level);
    }

    private ScriptValue item(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.of(false);
        }
        ScriptValue eventItem = ctx.getVariable("item");
        if (eventItem == null) {
            return ScriptValue.of(false);
        }
        String itemId = eventItem.asString();
        String expectedId = args[0].asString();
        return ScriptValue.of(itemId.equals(expectedId));
    }

}
