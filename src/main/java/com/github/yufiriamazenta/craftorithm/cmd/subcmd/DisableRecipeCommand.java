package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import crypticlib.command.ISubCmdExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public final class DisableRecipeCommand extends AbstractSubCommand {

    public static final DisableRecipeCommand INSTANCE = new DisableRecipeCommand();

    private DisableRecipeCommand() {
        super("disable", "craftorithm.command.disable");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        //TODO
        return super.onCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        //TODO
        return super.onTabComplete(sender, args);
    }
}
