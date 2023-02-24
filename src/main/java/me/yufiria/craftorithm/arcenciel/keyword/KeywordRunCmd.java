package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class KeywordRunCmd extends AbstractArcencielKeyword<Boolean> {

    public static final KeywordRunCmd INSTANCE = new KeywordRunCmd();

    protected KeywordRunCmd() {
        super("command");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        StringJoiner commandJoiner = new StringJoiner(" ");
        for (String arg : args) {
            commandJoiner.add(arg);
        }
        String command = LangUtil.placeholder(player, commandJoiner.toString());
        return new ReturnObj<>(Bukkit.dispatchCommand(player, command));
    }
}
