package me.yufiria.craftorithm.arcenciel.node;

import me.yufiria.craftorithm.api.arcenciel.keyword.AbstractArcencielKeyword;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class KeywordIf extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordIf INSTANCE = new KeywordIf();

    protected KeywordIf() {
        super("if");
    }

    @Override
    public Boolean exec(Player player, List<String> args) {
        if (args.size() < 1)
            return true;
        StringJoiner arcencielBlock = new StringJoiner(" ");
        for (String arg : args) {
            arcencielBlock.add(arg);
        }
        Object obj = ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, arcencielBlock.toString());
        if (obj instanceof Boolean)
            return (Boolean) obj;
        return obj != null;
    }
}
