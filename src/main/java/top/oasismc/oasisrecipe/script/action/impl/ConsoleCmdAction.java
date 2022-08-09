package top.oasismc.oasisrecipe.script.action.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.IRecipeStatement;

public enum ConsoleCmdAction implements IRecipeStatement<Boolean> {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            arg = PlaceholderAPI.setPlaceholders(player, arg);
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), arg);
        return true;
    }

    @Override
    public String getStatement() {
        return "console";
    }

}
