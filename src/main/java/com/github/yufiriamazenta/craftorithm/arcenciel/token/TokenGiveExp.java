package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import crypticlib.CrypticLibBukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 给予玩家一定的经验
 */
public class TokenGiveExp extends AbstractArcencielToken<Integer> {

    public static final TokenGiveExp INSTANCE = new TokenGiveExp();

    protected TokenGiveExp() {
        super("give-exp");
    }

    @Override
    public ReturnObj<Integer> exec(Player player, List<String> args) {
        if (args.isEmpty())
            return new ReturnObj<>(player.getLevel());
        int value = Integer.parseInt(args.get(0));
        if (CrypticLibBukkit.platform().isPaper()) {
            player.giveExp(Math.max(0, value), true);
        } else {
            player.giveExp(Math.max(0, value));
        }
        return new ReturnObj<>(player.getLevel());
    }
}
