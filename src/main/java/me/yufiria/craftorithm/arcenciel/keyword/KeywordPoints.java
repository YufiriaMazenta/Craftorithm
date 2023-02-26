package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.PluginHookUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class KeywordPoints extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordPoints INSTANCE = new KeywordPoints();

    protected KeywordPoints() {
        super("points");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        return new ReturnObj<>(PluginHookUtil.getPlayerPoints().getAPI().look(player.getUniqueId()) >= Integer.parseInt(args.get(0)));
    }

}
