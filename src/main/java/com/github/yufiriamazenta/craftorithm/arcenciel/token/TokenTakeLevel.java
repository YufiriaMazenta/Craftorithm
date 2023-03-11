package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenTakeLevel extends AbstractArcencielToken<Integer> {

    public static final TokenTakeLevel INSTANCE = new TokenTakeLevel();

    protected TokenTakeLevel() {
        super("take-level");
    }

    @Override
    public ReturnObj<Integer> exec(Player player, List<String> args) {
        if (args.size() < 1)
            return new ReturnObj<>(player.getLevel());
        int value = Integer.parseInt(args.get(0));
        player.setLevel(Math.max(0, player.getLevel() - value));
        return new ReturnObj<>(player.getLevel());
    }
}
