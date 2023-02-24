package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.MapUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class KeywordHasPerm extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordHasPerm INSTANCE = new KeywordHasPerm();

    protected KeywordHasPerm() {
        super("perm");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        if (args.size() < 1) {
            LangUtil.sendMsg(player, "arcenciel.not_enough_param", MapUtil.newHashMap("<statement>", "if"));
            return new ReturnObj<>(false);
        }
        return new ReturnObj<>(player.hasPermission(args.get(0)));
    }
}
