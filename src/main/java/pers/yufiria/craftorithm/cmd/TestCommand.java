package pers.yufiria.craftorithm.cmd;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.command.BukkitCommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Command;
import crypticlib.lang.LangManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command
public class TestCommand extends BukkitCommand {
    public TestCommand() {
        super(CommandInfo.builder().name("cratest").build());
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        if (args.isEmpty()) {
            return;
        }
        String join = String.join("", args);
        String text = LangManager.INSTANCE.replaceLang(join, sender);
        BukkitMsgSender.INSTANCE.sendMsg(sender, text);
    }
}
