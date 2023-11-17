package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubCmdExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class VersionCommand extends AbstractSubCommand {

    public static final ISubCmdExecutor INSTANCE = new VersionCommand();

    private VersionCommand() {
        super("version", "craftorithm.command.version");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        LangUtil.sendLang(sender, "command.version");
        return true;
    }
}
