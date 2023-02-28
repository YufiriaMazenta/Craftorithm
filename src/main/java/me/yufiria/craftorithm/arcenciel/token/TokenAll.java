package me.yufiria.craftorithm.arcenciel.token;

import me.yufiria.craftorithm.arcenciel.block.StringArcencielBlock;
import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.yufiria.craftorithm.arcenciel.obj.ArcencielSignal.IF;

public class TokenAll extends AbstractArcencielToken<Boolean> {

    public static final TokenAll INSTANCE = new TokenAll();

    protected TokenAll() {
        super("all");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        if (!args.contains("&&")) {
            Boolean bool = (Boolean) StringArcencielBlock.getArcencielKeywordMap().get("if").exec(player, args).getObj();
            return new ReturnObj<>(IF, bool);
        }
        boolean base = true;
        List<String> block = new ArrayList<>();
        for (int i = 0; i < args.size() + 1; i++) {
            if (i == args.size()) {
                base = (base && (Boolean) StringArcencielBlock.getArcencielKeywordMap().get("if").exec(player, block).getObj());
                break;
            }
            String arg = args.get(i);
            if (arg.equals("&&")) {
                base = (base && (Boolean) StringArcencielBlock.getArcencielKeywordMap().get("if").exec(player, block).getObj());
                block = new ArrayList<>();
                continue;
            }
            block.add(arg);
        }
        return new ReturnObj<>(IF, base);
    }

}
