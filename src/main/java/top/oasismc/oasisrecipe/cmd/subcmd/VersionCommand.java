package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.util.MsgUtil;

import java.util.List;

public class VersionCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new VersionCommand();

    private VersionCommand() {
        super("version", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        MsgUtil.sendMsg(sender, "commands.version");
        return true;
    }
}
