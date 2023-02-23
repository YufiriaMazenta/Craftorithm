package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.api.arcenciel.keyword.AbstractArcencielKeyword;
import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
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
        StringJoiner command = new StringJoiner(" ");
        for (String arg : args) {
            command.add(arg);
        }
        return new ReturnObj<>(Bukkit.dispatchCommand(player, command.toString()));
    }
}
