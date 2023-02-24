package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.block.StringArcencielBlock;
import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.yufiria.craftorithm.arcenciel.obj.ArcencielSignal.IF;

public class KeywordAll extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordAll INSTANCE = new KeywordAll();

    protected KeywordAll() {
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
        for (String arg : args) {
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
