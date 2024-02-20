package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class VersionCommand extends AbstractSubCommand {

    public static final VersionCommand INSTANCE = new VersionCommand();

    private VersionCommand() {
        super("version", "craftorithm.command.version");
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        LangUtil.sendLang(sender, Languages.COMMAND_VERSION);
        return true;
    }
}
