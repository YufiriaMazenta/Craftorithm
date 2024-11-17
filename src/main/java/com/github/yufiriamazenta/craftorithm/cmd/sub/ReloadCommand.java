package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends BukkitSubcommand {

    public static final ReloadCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super(CommandInfo.builder("reload").permission(new PermInfo("craftorithm.command.reload")).build());
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        try {
            Craftorithm.instance().reloadPlugin();
            LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_EXCEPTION);
        }
    }

}
