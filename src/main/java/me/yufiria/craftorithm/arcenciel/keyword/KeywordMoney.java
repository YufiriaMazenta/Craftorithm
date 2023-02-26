package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.PluginHookUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class KeywordMoney extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordMoney INSTANCE = new KeywordMoney();

    protected KeywordMoney() {
        super("money");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        return new ReturnObj<>(PluginHookUtil.getEconomy().getBalance(player) >= Double.parseDouble(args.get(0)));
    }
}
