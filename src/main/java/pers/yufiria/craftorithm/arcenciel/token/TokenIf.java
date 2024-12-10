package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import org.bukkit.entity.Player;

import java.util.List;

import static pers.yufiria.craftorithm.arcenciel.obj.ArcencielSignal.IF;

public class TokenIf extends AbstractArcencielToken<Boolean> {

    public static final TokenIf INSTANCE = new TokenIf();

    protected TokenIf() {
        super("if");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        if (args.isEmpty())
            return new ReturnObj<>(IF, true);
        if ("true".equalsIgnoreCase(args.get(0)))
            return new ReturnObj<>(IF, true);
        else if ("false".equalsIgnoreCase(args.get(0)))
            return new ReturnObj<>(IF, false);
        ReturnObj<Object> returnObj = ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, CollectionsUtils.list2ArcencielBlock(args));
        Object obj = returnObj.obj();
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
