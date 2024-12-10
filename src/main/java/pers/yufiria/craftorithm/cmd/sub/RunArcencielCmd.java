package pers.yufiria.craftorithm.cmd.sub;

import pers.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public final class RunArcencielCmd extends BukkitSubcommand {

    public static final RunArcencielCmd INSTANCE = new RunArcencielCmd();

    private RunArcencielCmd() {
        super(CommandInfo.builder("run").permission(new PermInfo("craftorithm.command.run")).build());
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!CommandUtils.checkSenderIsPlayer(sender))
            return;
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        long startTime = System.currentTimeMillis();
        StringJoiner arcencielBlock = new StringJoiner(" ");
        for (String arg : args) {
            arcencielBlock.add(arg);
        }
        ArcencielDispatcher.INSTANCE.dispatchArcencielBlock((Player) sender, arcencielBlock.toString());
        long execTime = System.currentTimeMillis() - startTime;
        LangUtils.sendLang(sender, Languages.COMMAND_RUN_ARCENCIEL_SUCCESS, CollectionsUtils.newStringHashMap("<time>", String.valueOf(execTime)));
    }
}
