package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.hook.impl.VaultHooker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenTakeMoney extends AbstractArcencielToken<Double> {

    public static final TokenTakeMoney INSTANCE = new TokenTakeMoney();

    protected TokenTakeMoney() {
        super("take-money");
    }

    @Override
    public ReturnObj<Double> exec(Player player, List<String> args) {
        Economy economy = (Economy) VaultHooker.INSTANCE.economy();
        if (args.isEmpty())
            return new ReturnObj<>(economy.getBalance(player));
        double value = Double.parseDouble(args.get(0));
        if (value >= 0)
            economy.withdrawPlayer(player, value);
        else
            economy.depositPlayer(player, - value);
        return new ReturnObj<>(economy.getBalance(player));
    }
}
