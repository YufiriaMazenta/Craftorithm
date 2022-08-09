package top.oasismc.oasisrecipe.script.condition.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;
import top.oasismc.oasisrecipe.script.util.ValueUtil;

public enum LevelCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        int asteriskIndex = arg.indexOf("*");
        int needValue = Integer.parseInt(arg.substring(asteriskIndex + 1));
        int level = player.getLevel();
        String type = arg.substring(0, asteriskIndex);
        return ValueUtil.compare(level, needValue, type);
    }

    @Override
    public String getStatement() {
        return "level";
    }

}
