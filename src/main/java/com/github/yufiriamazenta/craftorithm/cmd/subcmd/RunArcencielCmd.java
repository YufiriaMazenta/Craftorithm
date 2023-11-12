package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class RunArcencielCmd extends AbstractSubCommand {

    public static final RunArcencielCmd INSTANCE = new RunArcencielCmd();

    protected RunArcencielCmd() {
        super("run", "craftorithm.command.run");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            LangUtil.sendMsg(sender, "command.player_only");
            return true;
        }
        if (args.size() < 1) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        long startTime = System.currentTimeMillis();
        StringJoiner arcencielBlock = new StringJoiner(" ");
        for (String arg : args) {
            arcencielBlock.add(arg);
        }
        ArcencielDispatcher.INSTANCE.dispatchArcencielBlock((Player) sender, arcencielBlock.toString());
        long execTime = System.currentTimeMillis() - startTime;
        LangUtil.sendMsg(sender, "command.run_arcenciel.success", ContainerUtil.newHashMap("<time>", String.valueOf(execTime)));
        return true;
    }
}
