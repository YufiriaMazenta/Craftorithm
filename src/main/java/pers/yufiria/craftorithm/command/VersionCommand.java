package pers.yufiria.craftorithm.command;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;

public final class VersionCommand extends CommandNode {

    public static final VersionCommand INSTANCE = new VersionCommand();

    private VersionCommand() {
        super(CommandInfo.builder("version").permission(new PermInfo("craftorithm.command.version")).build());
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        LangUtils.sendLang(CommandUtils.invoker2Sender(invoker), Languages.COMMAND_VERSION);
    }

}
