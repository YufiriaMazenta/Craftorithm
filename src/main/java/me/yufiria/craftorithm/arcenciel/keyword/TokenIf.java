package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import me.yufiria.craftorithm.util.ContainerUtil;
import org.bukkit.entity.Player;

import java.util.List;

import static me.yufiria.craftorithm.arcenciel.obj.ArcencielSignal.IF;

public class TokenIf extends AbstractArcencielToken<Boolean> {

    public static final TokenIf INSTANCE = new TokenIf();

    protected TokenIf() {
        super("if");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        if (args.size() < 1)
            return new ReturnObj<>(IF, true);
        if ("true".equalsIgnoreCase(args.get(0)))
            return new ReturnObj<>(IF, true);
        else if ("false".equalsIgnoreCase(args.get(0)))
            return new ReturnObj<>(IF, false);
        ReturnObj<Object> returnObj = ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, ContainerUtil.list2ArcencielBlock(args));
        Object obj = returnObj.getObj();
        if (obj instanceof Boolean)
            return new ReturnObj<>(IF, ((Boolean) obj));
        if (obj instanceof String) {
            try {
                return new ReturnObj<>(IF, Boolean.parseBoolean((String) obj));
            } catch (Exception e) {
                return new ReturnObj<>(IF, false);
            }
        }
        return new ReturnObj<>(IF, obj != null);
    }
}
