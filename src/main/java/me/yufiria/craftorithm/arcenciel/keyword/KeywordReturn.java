package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class KeywordReturn extends AbstractArcencielKeyword<Object> {

    public static final KeywordReturn INSTANCE = new KeywordReturn();

    protected KeywordReturn() {
        super("return");
    }

    @Override
    public ReturnObj<Object> exec(Player player, List<String> args) {
        if (args.size() < 1)
            return new ReturnObj<>();
        StringJoiner returnStr = new StringJoiner(" ");
        String param = args.get(0);
        args = args.subList(1, args.size());
        for (String arg : args) {
            returnStr.add(arg);
        }
        switch (param) {
            case "string":
            default:
                return new ReturnObj<>(returnStr.toString());
            case "run":
                return ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, returnStr.toString());
        }
    }
}
