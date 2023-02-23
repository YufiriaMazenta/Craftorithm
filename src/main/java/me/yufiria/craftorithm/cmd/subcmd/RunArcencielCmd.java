package me.yufiria.craftorithm.cmd.subcmd;

import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.api.cmd.AbstractSubCommand;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.MapUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class RunArcencielCmd extends AbstractSubCommand {

    public static final RunArcencielCmd INSTANCE = new RunArcencielCmd();

    protected RunArcencielCmd() {
        super("run-arcenciel");
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
        StringJoiner arcencielBlock = new StringJoiner(" ");
        for (String arg : args) {
            arcencielBlock.add(arg);
        }
        ReturnObj<Object> returnObj = ArcencielDispatcher.INSTANCE.dispatchArcencielBlock((Player) sender, arcencielBlock.toString());
        LangUtil.sendMsg(sender, "command.run_arcenciel.success", MapUtil.newHashMap("<object>", String.valueOf(returnObj.getObj())));
        return true;
    }
}
