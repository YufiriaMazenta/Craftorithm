package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.block.StringArcencielBlock;
import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static pers.yufiria.craftorithm.arcenciel.obj.ArcencielSignal.IF;

public class TokenAny extends AbstractArcencielToken<Boolean> {

    public static final TokenAny INSTANCE = new TokenAny();

    protected TokenAny() {
        super("any");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        if (!args.contains("||")) {
            Boolean bool = (Boolean) StringArcencielBlock.getArcencielKeywordMap().get("if").exec(player, args).obj();
            return new ReturnObj<>(IF, bool);
        }
        boolean base = false;
        List<String> block = new ArrayList<>();
        for (int i = 0; i < args.size() + 1; i++) {
            if (i == args.size()) {
                base = (base || (Boolean) StringArcencielBlock.getArcencielKeywordMap().get("if").exec(player, block).obj());
                break;
            }
            String arg = args.get(i);
            if (arg.equals("||")) {
                base = (base || (Boolean) StringArcencielBlock.getArcencielKeywordMap().get("if").exec(player, block).obj());
                block = new ArrayList<>();
                continue;
            }
            block.add(arg);
        }
        return new ReturnObj<>(IF, base);
    }
}
