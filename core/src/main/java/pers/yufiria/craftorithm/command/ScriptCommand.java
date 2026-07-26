package pers.yufiria.craftorithm.command;

import crypticlib.Invoker;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Command;
import crypticlib.perm.PermInfo;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptEngine;
import crypticlib.util.FunctionExecutor;
import crypticlib.util.IOHelper;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;
import java.util.Map;

@Command
public class ScriptCommand extends CommandNode {

    public static final ScriptCommand INSTANCE = new ScriptCommand();

    private ScriptCommand() {
        super("script", new PermInfo("craftorithm.command.script"));
    }

    @Override
    public void execute(@NotNull Invoker invoker, @NotNull List<String> args) {
        if (args.isEmpty()) {
            return;
        }
        String scriptLine = String.join(" ", args);
        long executeTime = FunctionExecutor.execute(() -> {
            ScriptEngine.INSTANCE.execute(scriptLine, new ScriptContext(invoker));
        });
        LangUtils.sendLang(invoker, Languages.COMMAND_SCRIPT_OPERATION_TIME, Map.of("<time>", executeTime + ""));
        IOHelper.info("Invoker \"" + invoker.name() + "\" execute script line: " + scriptLine);
    }

    @Override
    public void onNoPerm(@NotNull Invoker invoker, @NotNull List<String> args) {
        LangUtils.sendLang(invoker, Languages.COMMAND_NO_PERM);
    }

}
