package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenConsole extends AbstractArcencielToken<Boolean> {

    public static final TokenConsole INSTANCE = new TokenConsole();

    protected TokenConsole() {
        super("console");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        String command = BukkitTextProcessor.placeholder(player, CollectionsUtils.list2ArcencielBlock(args));
        return new ReturnObj<>(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

}
