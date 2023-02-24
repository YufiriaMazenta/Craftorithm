package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

import static me.yufiria.craftorithm.arcenciel.obj.ArcencielSignal.IF;

public class KeywordIf extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordIf INSTANCE = new KeywordIf();

    protected KeywordIf() {
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
        StringJoiner arcencielBlock = new StringJoiner(" ");
        for (String arg : args) {
            arcencielBlock.add(arg);
        }
        ReturnObj<Object> returnObj = ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, arcencielBlock.toString());
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
