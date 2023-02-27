package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.PluginHookUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenPoints extends AbstractArcencielToken<Boolean> {

    public static final TokenPoints INSTANCE = new TokenPoints();

    protected TokenPoints() {
        super("points");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        return new ReturnObj<>(PluginHookUtil.getPlayerPoints().getAPI().look(player.getUniqueId()) >= Integer.parseInt(args.get(0)));
    }

}
