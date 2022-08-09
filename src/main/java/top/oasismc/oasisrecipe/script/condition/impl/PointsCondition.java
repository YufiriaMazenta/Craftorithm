package top.oasismc.oasisrecipe.script.condition.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;
import top.oasismc.oasisrecipe.script.util.ValueUtil;

public enum PointsCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        int asteriskIndex = arg.indexOf("*");
        int needValue = Integer.parseInt(arg.substring(asteriskIndex + 1));
        int playerPoints = ActionDispatcher.INSTANCE.getPlayerPoints().getAPI().look(player.getUniqueId());
        String type = arg.substring(0, asteriskIndex);
        return ValueUtil.compare(playerPoints, needValue, type);
    }

    @Override
    public String getStatement() {
        return "points";
    }

}
