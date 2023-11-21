package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import org.bukkit.entity.Player;

import java.util.List;

import static com.github.yufiriamazenta.craftorithm.arcenciel.obj.ArcencielSignal.END;

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
                return new ReturnObj<>(END, CollectionsUtil.list2ArcencielBlock(args.subList(1, args.size())));
            default:
                return new ReturnObj<>(END, CollectionsUtil.list2ArcencielBlock(args));
            case "run":
                Object obj = ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, CollectionsUtil.list2ArcencielBlock(args.subList(1, args.size()))).obj();
                return new ReturnObj<>(END, obj);
        }
    }
}
