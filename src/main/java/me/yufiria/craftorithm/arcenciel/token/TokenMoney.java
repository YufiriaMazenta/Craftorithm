package me.yufiria.craftorithm.arcenciel.token;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.PluginHookUtil;
import me.yufiria.craftorithm.util.ScriptValueUtil;
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
        Economy economy = PluginHookUtil.getEconomy();
        boolean result;
        if (args.size() < 2)
            result = economy.getBalance(player) >= Double.parseDouble(args.get(0));
        else
            result = ScriptValueUtil.compare(economy.getBalance(player), Double.parseDouble(args.get(1)), args.get(0));
        return new ReturnObj<>(result);
    }
}
