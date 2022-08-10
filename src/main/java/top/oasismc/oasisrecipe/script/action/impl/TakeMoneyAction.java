package top.oasismc.oasisrecipe.script.action.impl;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.IRecipeStatement;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;

public enum TakeMoneyAction implements IRecipeStatement<Double> {

    INSTANCE;

    @Override
    public Double exec(String arg, Player player) {
        double value = Double.parseDouble(arg);
        Economy economy = ActionDispatcher.INSTANCE.getEconomy();
        if (value >= 0)
            economy.withdrawPlayer(player, value);
        else
            economy.depositPlayer(player, - value);
        return economy.getBalance(player);
    }

    @Override
    public String getStatement() {
        return "take-money";
    }

}
