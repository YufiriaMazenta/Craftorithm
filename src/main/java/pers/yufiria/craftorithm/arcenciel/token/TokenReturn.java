package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import pers.yufiria.craftorithm.arcenciel.obj.ArcencielSignal;
import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenReturn extends AbstractArcencielToken<Object> {

    public static final TokenReturn INSTANCE = new TokenReturn();

    protected TokenReturn() {
        super("return");
    }

    @Override
    public ReturnObj<Object> exec(Player player, List<String> args) {
        if (args.isEmpty())
            return new ReturnObj<>();
        String param = args.get(0);
        switch (param) {
            case "string":
                return new ReturnObj<>(ArcencielSignal.END, CollectionsUtils.list2ArcencielBlock(args.subList(1, args.size())));
            default:
                return new ReturnObj<>(ArcencielSignal.END, CollectionsUtils.list2ArcencielBlock(args));
            case "run":
                Object obj = ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, CollectionsUtils.list2ArcencielBlock(args.subList(1, args.size()))).obj();
                return new ReturnObj<>(ArcencielSignal.END, obj);
        }
    }
}
