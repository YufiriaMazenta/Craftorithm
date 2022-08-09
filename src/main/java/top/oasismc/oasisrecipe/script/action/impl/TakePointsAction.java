package top.oasismc.oasisrecipe.script.action.impl;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.IRecipeStatement;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;

public enum TakePointsAction implements IRecipeStatement<Integer> {

    INSTANCE;

    @Override
    public Integer exec(String arg, Player player) {
        int value = Integer.parseInt(arg);
        PlayerPoints points = ActionDispatcher.INSTANCE.getPlayerPoints();
        points.getAPI().take(player.getUniqueId(), value);
        return points.getAPI().look(player.getUniqueId());
    }

    @Override
    public String getStatement() {
        return "take-points";
    }

}
