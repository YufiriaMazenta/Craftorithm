package pers.yufiria.craftorithm.command;

import crypticlib.CrypticLib;
import crypticlib.Invoker;
import crypticlib.command.CommandContext;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Command;
import crypticlib.perm.PermInfo;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptEngine;
import crypticlib.util.FunctionExecutor;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.permission.Permissions;
import pers.yufiria.craftorithm.script.RootScriptContext;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;
import java.util.Map;

@Command
public class ScriptCommand extends CommandNode {

    public static final ScriptCommand INSTANCE = new ScriptCommand();

    private ScriptCommand() {
        super("script", new PermInfo(Permissions.COMMAND_SCRIPT));
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        Invoker invoker = context.invoker();
        List<String> args = context.args();
        if (args.isEmpty()) {
            return;
        }
        String scriptLine = String.join(" ", args);
        long executeTime = FunctionExecutor.execute(() -> {
            ScriptEngine.INSTANCE.execute(scriptLine, new ScriptContext(invoker, RootScriptContext.INSTANCE));
        });
        LangUtils.sendLang(invoker, Languages.COMMAND_SCRIPT_OPERATION_TIME, Map.of("<time>", executeTime + ""));
        CrypticLib.info("Invoker \"" + invoker.name() + "\" execute script line: " + scriptLine);
    }

    @Override
    public void onNoPerm(@NotNull CommandContext context) {
        LangUtils.sendLang(context.invoker(), Languages.COMMAND_NO_PERM);
    }

}
