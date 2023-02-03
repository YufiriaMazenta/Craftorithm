package top.oasismc.oasisrecipe.script.condition.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;
import top.oasismc.oasisrecipe.util.ScriptValueUtil;

public enum RandomCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        int asteriskIndex = arg.indexOf("*");
        double random = Math.random();
        double value = Double.parseDouble(arg.substring(asteriskIndex + 1));
        String type = arg.substring(0, asteriskIndex);
        return ScriptValueUtil.compare(random, value, type);
    }

    @Override
    public String getStatement() {
        return "random";
    }
}
