package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.PluginHookUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenMoney extends AbstractArcencielToken<Boolean> {

    public static final TokenMoney INSTANCE = new TokenMoney();

    protected TokenMoney() {
        super("money");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        return new ReturnObj<>(PluginHookUtil.getEconomy().getBalance(player) >= Double.parseDouble(args.get(0)));
    }
}
