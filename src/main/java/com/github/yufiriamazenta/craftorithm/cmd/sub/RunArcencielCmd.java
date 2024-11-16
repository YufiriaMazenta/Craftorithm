package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
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
        LangUtil.sendLang(sender, Languages.COMMAND_RUN_ARCENCIEL_SUCCESS, CollectionsUtil.newStringHashMap("<time>", String.valueOf(execTime)));
    }
}
