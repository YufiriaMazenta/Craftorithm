package top.oasismc.oasisrecipe.script.action.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.IRecipeStatement;

public enum TakeLvlAction implements IRecipeStatement<Integer> {

    INSTANCE;

    @Override
    public Integer exec(String arg, Player player) {
        int value = Integer.parseInt(arg);
        player.setLevel(Math.max(0, player.getLevel() - value));
        return player.getLevel();
    }

    @Override
    public String getStatement() {
        return "take-level";
    }

}
