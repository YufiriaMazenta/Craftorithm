package top.oasismc.oasisrecipe.script.action.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.IRecipeStatement;

public enum CloseAction implements IRecipeStatement<Void> {

    INSTANCE;

    @Override
    public Void exec(String arg, Player player) {
        player.closeInventory();
        return null;
    }

    @Override
    public String getStatement() {
        return "close";
    }

}
