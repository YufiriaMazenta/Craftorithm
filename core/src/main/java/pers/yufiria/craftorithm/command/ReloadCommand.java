package pers.yufiria.craftorithm.command;

import crypticlib.CrypticLib;
import crypticlib.Invoker;
import crypticlib.command.CommandContext;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.permission.Permissions;
import pers.yufiria.craftorithm.ReloadManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

public final class ReloadCommand extends AbstractSubcommand {

    public static final ReloadCommand INSTANCE = new ReloadCommand();


    private ReloadCommand() {
        super(CommandInfo.builder("reload").permission(new PermInfo(Permissions.COMMAND_RELOAD)).build());
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        Invoker invoker = context.invoker();
        try {
            ReloadManager.INSTANCE.reloadPlugin(CommandUtils.invoker2Sender(invoker));
            LangUtils.sendLang(invoker, Languages.COMMAND_RELOAD_RELOADING);
        } catch (Exception e) {
            CrypticLib.error("Failed to reload Craftorithm", e);
            LangUtils.sendLang(invoker, Languages.COMMAND_RELOAD_EXCEPTION);
        }
    }

}
