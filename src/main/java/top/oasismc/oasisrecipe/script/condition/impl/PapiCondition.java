package top.oasismc.oasisrecipe.script.condition.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;

public enum PapiCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        String papi = PlaceholderAPI.setPlaceholders(player, arg);
        return Boolean.parseBoolean(papi);
    }

    @Override
    public String getStatement() {
        return "papi";
    }

}
