package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.ContainerUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenHasPerm extends AbstractArcencielToken<Boolean> {

    public static final TokenHasPerm INSTANCE = new TokenHasPerm();

    protected TokenHasPerm() {
        super("perm");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        if (args.size() < 1) {
            LangUtil.sendMsg(player, "arcenciel.not_enough_param", ContainerUtil.newHashMap("<statement>", "if"));
            return new ReturnObj<>(false);
        }
        return new ReturnObj<>(player.hasPermission(args.get(0)));
    }
}
