package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.util.ScriptValueUtil;
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
            result = ScriptValueUtil.compare(player.getLevel(), Integer.parseInt(args.get(1)), args.get(0));
        return new ReturnObj<>(result);
    }
}
