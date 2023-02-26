package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.ContainerUtil;
import me.yufiria.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class KeywordConsole extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordConsole INSTANCE = new KeywordConsole();

    protected KeywordConsole() {
        super("console");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        String command = LangUtil.placeholder(player, ContainerUtil.list2ArcencielBlock(args));
        return new ReturnObj<>(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

}
