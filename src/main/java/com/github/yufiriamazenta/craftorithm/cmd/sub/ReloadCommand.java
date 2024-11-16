package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.listener.hook.OtherPluginsListenerManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
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
            //todo 处理重载
            LangUtil.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LangUtil.sendLang(sender, Languages.COMMAND_RELOAD_EXCEPTION);
        }
    }

}
