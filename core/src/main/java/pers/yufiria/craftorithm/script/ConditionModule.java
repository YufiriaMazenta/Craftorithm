package pers.yufiria.craftorithm.script;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.ScriptFunctionRegistry;
import crypticlib.script.func.ScriptModule;
import crypticlib.script.vm.ScriptVM;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.hook.PlayerPointsHook;
import pers.yufiria.craftorithm.hook.VaultHook;

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
        registry.register(moduleName, "money", this::money);
        registry.register(moduleName, "points", this::points);
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
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.of(false);
        perm = BukkitTextProcessor.placeholder(player, perm);
        return ScriptValue.of(player.hasPermission(perm));
    }

    private ScriptValue papi(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.nil();
        String placeholder = args[0].asString();
        String resolved = BukkitTextProcessor.placeholder(player, placeholder);
        try {
            return ScriptValue.of(Double.parseDouble(resolved));
        } catch (NumberFormatException e) {
            return ScriptValue.of(resolved);
        }
    }

    private ScriptValue level(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.of(0);
        return ScriptValue.of(player.getLevel());
    }

    private ScriptValue money(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (!VaultHook.INSTANCE.isEconomyHooked()) {
            return ScriptValue.of(0);
        }
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.of(0);
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        return ScriptValue.of(economy.getBalance(player));
    }

    private ScriptValue points(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) {
            return ScriptValue.of(0);
        }
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        return ScriptValue.of(api.look(ctx.playerId()));
    }

    private ScriptValue world(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.nil();
        if (args.length == 0) return ScriptValue.of(player.getWorld().getName());
        String expected = args[0].asString();
        return ScriptValue.of(player.getWorld().getName().equals(expected));
    }

    private ScriptValue gameMode(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.nil();
        if (args.length == 0) return ScriptValue.of(player.getGameMode().name());
        String expected = args[0].asString();
        return ScriptValue.of(player.getGameMode().name().equalsIgnoreCase(expected));
    }

    private ScriptValue biome(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.of(false);
        String playerBiome = player.getLocation().getBlock().getBiome().getKey().toString();
        if (args.length < 1) return ScriptValue.of(playerBiome);
        String expected = args[0].asString();
        return ScriptValue.of(playerBiome.equalsIgnoreCase(expected));
    }

    private ScriptValue inWater(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.of(false);
        Block block = player.getLocation().getBlock();
        return ScriptValue.of(block.getType() == Material.WATER);
    }

    private ScriptValue inRain(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.of(false);
        return ScriptValue.of(player.getLocation().getBlock().getBiome().name().contains("RAIN")
            || player.getWorld().hasStorm());
    }

    private ScriptValue lightLevel(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Player player = Bukkit.getPlayer(ctx.playerId());
        if (player == null) return ScriptValue.of(0);
        int level = player.getLocation().getBlock().getLightLevel();
        return ScriptValue.of(level);
    }

    private ScriptValue context(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.nil();
        String key = args[0].asString();
        ScriptValue var = ctx.getVariable(key);
        if (var == null) return ScriptValue.nil();
        return var;
    }

    private ScriptValue item(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        ScriptValue eventItem = ctx.getVariable("item");
        if (eventItem == null) return ScriptValue.of(false);
        String itemId = eventItem.asString();
        String expectedId = args[0].asString();
        return ScriptValue.of(itemId.equals(expectedId));
    }

}
