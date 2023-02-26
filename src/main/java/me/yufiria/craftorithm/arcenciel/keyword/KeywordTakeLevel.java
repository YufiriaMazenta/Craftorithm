package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public class KeywordTakeLevel extends AbstractArcencielKeyword<Integer> {

    public static final KeywordTakeLevel INSTANCE = new KeywordTakeLevel();

    protected KeywordTakeLevel() {
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
