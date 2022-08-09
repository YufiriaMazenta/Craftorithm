package top.oasismc.oasisrecipe.script.condition.impl;

import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;
import top.oasismc.oasisrecipe.script.util.ValueUtil;

public enum MoneyCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        int asteriskIndex = arg.indexOf("*");
        double needValue = Double.parseDouble(arg.substring(asteriskIndex + 1));
        double playerBal = ActionDispatcher.INSTANCE.getEconomy().getBalance(player);
        String type = arg.substring(0, asteriskIndex);
        return ValueUtil.compare(playerBal, needValue, type);
    }

    @Override
    public String getStatement() {
        return "money";
    }

}
