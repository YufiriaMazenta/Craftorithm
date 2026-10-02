package pers.yufiria.craftorithm.command;

import crypticlib.command.CommandContext;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

public final class VersionCommand extends CommandNode {

    public static final VersionCommand INSTANCE = new VersionCommand();

    private VersionCommand() {
        super(CommandInfo.builder("version").permission(new PermInfo("craftorithm.command.version")).build());
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        LangUtils.sendLang(context.invoker(), Languages.COMMAND_VERSION);
    }

    @Override
    public void onNoPerm(@NotNull CommandContext context) {
        LangUtils.sendLang(context.invoker(), Languages.COMMAND_NO_PERM);
    }

}
