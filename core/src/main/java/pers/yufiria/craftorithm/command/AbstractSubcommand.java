package pers.yufiria.craftorithm.command;

import crypticlib.command.CommandContext;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

/**
 * Craftorithm 子命令节点基类, 统一处理无权限提示。
 */
public abstract class AbstractSubcommand extends CommandNode {

    protected AbstractSubcommand(CommandInfo commandInfo) {
        super(commandInfo);
    }

    protected AbstractSubcommand(String name, PermInfo permInfo) {
        super(name, permInfo);
    }

    @Override
    public void onNoPerm(@NotNull CommandContext context) {
        LangUtils.sendLang(context.invoker(), Languages.COMMAND_NO_PERM);
    }

}
