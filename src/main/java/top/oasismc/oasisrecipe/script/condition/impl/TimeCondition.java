package top.oasismc.oasisrecipe.script.condition.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;
import top.oasismc.oasisrecipe.script.util.ValueUtil;

public enum TimeCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        int asteriskIndex = arg.indexOf("*");
        long needValue = Long.parseLong(arg.substring(asteriskIndex + 1));
        long time = player.getWorld().getTime();
        String type = arg.substring(0, asteriskIndex);
        return ValueUtil.compare(time, needValue, type);
    }

    @Override
    public String getStatement() {
        return "time";
    }

}
