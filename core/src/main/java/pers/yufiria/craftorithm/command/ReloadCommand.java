package pers.yufiria.craftorithm.command;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;

public final class ReloadCommand extends CommandNode {

    public static final ReloadCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super(CommandInfo.builder("reload").permission(new PermInfo("craftorithm.command.reload")).build());
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        try {
            Craftorithm.instance().reloadPlugin();
            LangUtils.sendLang(invoker, Languages.COMMAND_RELOAD_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LangUtils.sendLang(invoker, Languages.COMMAND_RELOAD_EXCEPTION);
        }
    }

}
