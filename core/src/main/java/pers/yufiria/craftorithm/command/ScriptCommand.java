package pers.yufiria.craftorithm.command;

import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Command;
import crypticlib.perm.PermInfo;
import crypticlib.util.FunctionExecutor;
import crypticlib.util.IOHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptEngine;
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
    public void execute(@NotNull CommandInvoker invoker, @NotNull List<String> args) {
        if (invoker.isConsole()) {
            invoker.sendMsg(Languages.COMMAND_PLAYER_ONLY.value());
            return;
        }
        Player player = (Player) invoker.asPlayer().getPlatformPlayer();
        if (args.isEmpty()) {
            return;
        }
        String scriptLine = String.join(" ", args);
        long executeTime = FunctionExecutor.execute(() -> {
            ScriptEngine.INSTANCE.execute(scriptLine, new ScriptContext(player));
        });
        LangUtils.sendLang(player, Languages.COMMAND_SCRIPT_OPERATION_TIME, Map.of("<time>", executeTime + ""));
        IOHelper.info("Player \"" + player.getName() + "\" execute script line: " + scriptLine);
    }
}
