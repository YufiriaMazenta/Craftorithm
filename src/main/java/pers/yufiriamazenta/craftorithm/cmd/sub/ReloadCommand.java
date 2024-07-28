package pers.yufiriamazenta.craftorithm.cmd.sub;

import pers.yufiriamazenta.craftorithm.Craftorithm;
import pers.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import pers.yufiriamazenta.craftorithm.config.Languages;
import pers.yufiriamazenta.craftorithm.item.ItemManager;
import pers.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import pers.yufiriamazenta.craftorithm.listener.OtherPluginsListenerProxy;
import pers.yufiriamazenta.craftorithm.recipe.RecipeManager;
import pers.yufiriamazenta.craftorithm.util.ItemUtils;
import pers.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final ReloadCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", "craftorithm.command.reload");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        try {
            reloadPlugin();
            LangUtil.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LangUtil.sendLang(sender, Languages.COMMAND_RELOAD_EXCEPTION);
        }
    }

    public static void reloadPlugin() {
        reloadConfigs();
        CraftorithmItemProvider.INSTANCE.reloadItemProvider();
        ItemManager.INSTANCE.reloadCustomCookingFuel();
        RecipeManager.INSTANCE.reloadRecipeManager();
        OtherPluginsListenerProxy.INSTANCE.reloadOtherPluginsListener();
    }

    public static void reloadConfigs() {
        Craftorithm.instance().reloadConfig();
        ItemUtils.reloadCannotCraftLore();
        ArcencielDispatcher.INSTANCE.functionFile().reloadConfig();
    }

}
