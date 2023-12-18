package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubcmdExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class VersionCommand extends AbstractSubCommand {

    public static final ISubcmdExecutor INSTANCE = new VersionCommand();

    private VersionCommand() {
        super("version", "craftorithm.command.version");
    }

    @Override
    public Boolean onCommand(CommandSender sender, List<String> args) {
        LangUtil.sendLang(sender, Languages.COMMAND_VERSION);
        return true;
    }
}
