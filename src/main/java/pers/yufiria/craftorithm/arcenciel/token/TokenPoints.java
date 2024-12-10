package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiria.craftorithm.hook.impl.PlayerPointsHooker;
import pers.yufiria.craftorithm.util.ScriptValueComparator;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenPoints extends AbstractArcencielToken<Boolean> {

    public static final TokenPoints INSTANCE = new TokenPoints();

    protected TokenPoints() {
        super("points");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHooker.INSTANCE.playerPoints()).getAPI();
        boolean result;
        if (args.size() < 2)
            result = api.look(player.getUniqueId()) >= Integer.parseInt(args.get(0));
        else
            result = ScriptValueComparator.compare(api.look(player.getUniqueId()), Integer.parseInt(args.get(1)), args.get(0));
        return new ReturnObj<>(result);
    }

}
