package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenRunCmd extends AbstractArcencielToken<Boolean> {

    public static final TokenRunCmd INSTANCE = new TokenRunCmd();

    protected TokenRunCmd() {
        super("command");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        String command = BukkitTextProcessor.placeholder(player, CollectionsUtils.list2ArcencielBlock(args));
        return new ReturnObj<>(Bukkit.dispatchCommand(player, command));
    }
}
