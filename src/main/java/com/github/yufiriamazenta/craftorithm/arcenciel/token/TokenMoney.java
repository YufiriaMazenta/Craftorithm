package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.hook.impl.VaultHooker;
import com.github.yufiriamazenta.craftorithm.util.ScriptValueComparator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenMoney extends AbstractArcencielToken<Boolean> {

    public static final TokenMoney INSTANCE = new TokenMoney();

    protected TokenMoney() {
        super("money");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        Economy economy = (Economy) VaultHooker.INSTANCE.economy();
        boolean result;
        if (args.size() < 2)
            result = economy.getBalance(player) >= Double.parseDouble(args.get(0));
        else
            result = ScriptValueComparator.compare(economy.getBalance(player), Double.parseDouble(args.get(1)), args.get(0));
        return new ReturnObj<>(result);
    }
}
