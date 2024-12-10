package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiria.craftorithm.util.ScriptValueComparator;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenLevel extends AbstractArcencielToken<Boolean> {

    public static final TokenLevel INSTANCE = new TokenLevel();

    protected TokenLevel() {
        super("level");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        boolean result;
        if (args.size() < 2)
            result = player.getLevel() >= Integer.parseInt(args.get(0));
        else
            result = ScriptValueComparator.compare(player.getLevel(), Integer.parseInt(args.get(1)), args.get(0));
        return new ReturnObj<>(result);
    }
}
