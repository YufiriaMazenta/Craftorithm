package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public class KeywordLevel extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordLevel INSTANCE = new KeywordLevel();

    protected KeywordLevel() {
        super("level");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        return new ReturnObj<>(player.getLevel() >= Integer.parseInt(args.get(0)));
    }
}
