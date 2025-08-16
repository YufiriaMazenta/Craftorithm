package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.hook.PlayerPointsHooker;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenTakePoints extends AbstractArcencielToken<Integer> {

    public static final TokenTakePoints INSTANCE = new TokenTakePoints();

    protected TokenTakePoints() {
        super("take-points");
    }

    @Override
    public ReturnObj<Integer> exec(Player player, List<String> args) {
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHooker.INSTANCE.playerPoints()).getAPI();
        if (args.isEmpty())
            return new ReturnObj<>(api.look(player.getUniqueId()));
        int value = Integer.parseInt(args.get(0));
        if (value >= 0)
            api.take(player.getUniqueId(), value);
        else
            api.give(player.getUniqueId(), - value);
        return new ReturnObj<>(api.look(player.getUniqueId()));
    }
}
