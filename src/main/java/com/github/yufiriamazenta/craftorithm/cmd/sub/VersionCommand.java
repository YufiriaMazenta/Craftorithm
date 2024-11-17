package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class VersionCommand extends BukkitSubcommand {

    public static final VersionCommand INSTANCE = new VersionCommand();

    private VersionCommand() {
        super(CommandInfo.builder("version").permission(new PermInfo("craftorithm.command.version")).build());
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        LangUtils.sendLang(sender, Languages.COMMAND_VERSION);
    }
}
