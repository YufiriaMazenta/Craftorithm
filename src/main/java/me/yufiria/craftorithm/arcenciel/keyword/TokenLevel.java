package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenLevel extends AbstractArcencielToken<Boolean> {

    public static final TokenLevel INSTANCE = new TokenLevel();

    protected TokenLevel() {
        super("level");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        return new ReturnObj<>(player.getLevel() >= Integer.parseInt(args.get(0)));
    }
}
