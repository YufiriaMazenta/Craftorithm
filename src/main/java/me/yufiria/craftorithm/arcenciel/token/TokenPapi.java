package me.yufiria.craftorithm.arcenciel.token;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.ContainerUtil;
import me.yufiria.craftorithm.util.LangUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenPapi extends AbstractArcencielToken<Boolean> {

    public static final TokenPapi INSTANCE = new TokenPapi();

    protected TokenPapi() {
        super("papi");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        String argStr = ContainerUtil.list2ArcencielBlock(args);
        return new ReturnObj<>(Boolean.parseBoolean(LangUtil.placeholder(player, argStr)));
    }

}
