package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.PluginHookUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.List;

public class KeywordTakeMoney extends AbstractArcencielKeyword<Double> {

    public static final KeywordTakeMoney INSTANCE = new KeywordTakeMoney();

    protected KeywordTakeMoney() {
        super("take-money");
    }

    @Override
    public ReturnObj<Double> exec(Player player, List<String> args) {
        Economy economy = PluginHookUtil.getEconomy();
        if (args.size() < 1)
            return new ReturnObj<>(economy.getBalance(player));
        double value = Double.parseDouble(args.get(0));
        if (value >= 0)
            economy.withdrawPlayer(player, value);
        else
            economy.depositPlayer(player, - value);
        return new ReturnObj<>(economy.getBalance(player));
    }
}
