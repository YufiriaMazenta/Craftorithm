package top.oasismc.oasisrecipe.script.condition.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;

public enum BiomeCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        arg = arg.replaceAll("\\s", "");
        String biome = player.getLocation().getBlock().getBiome().name();
        return biome.equalsIgnoreCase(arg);
    }

    @Override
    public String getStatement() {
        return "biome";
    }
}
