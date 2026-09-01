package pers.yufiria.craftorithm.command;

import crypticlib.CrypticLibPlugin;
import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.perm.PermInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;
import java.util.UUID;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(
    phase = LifecyclePhase.RELOAD,
    priority = Integer.MAX_VALUE,
    isAsync = true
))
public final class ReloadCommand extends CommandNode implements LifecycleTask {

    public static final ReloadCommand INSTANCE = new ReloadCommand();
    private volatile @Nullable UUID reloadSenderUuid;

    private ReloadCommand() {
        super(CommandInfo.builder("reload").permission(new PermInfo("craftorithm.command.reload")).build());
    }

    @Override
    public void execute(@NotNull Invoker invoker, List<String> args) {
        if (RecipeManager.INSTANCE.isReloadingRecipeManager()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_RELOAD_RECIPE_MANAGER_RELOADING);
            return;
        }
        try {
            reloadSenderUuid = invoker.uniqueId();
            Craftorithm.instance().reloadPlugin();
        } catch (Exception e) {
            e.printStackTrace();
            LangUtils.sendLang(invoker, Languages.COMMAND_RELOAD_EXCEPTION);
        }
    }

    @Override
    public void onNoPerm(@NotNull Invoker invoker, List<String> args) {
        LangUtils.sendLang(invoker, Languages.COMMAND_NO_PERM);
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        RecipeManager.INSTANCE.getReloadCompletion().join();
        CommandSender sender = getReloadSender();
        reloadSenderUuid = null;
        if (sender != null) {
            LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
        }
    }

    private @Nullable CommandSender getReloadSender() {
        UUID uuid = reloadSenderUuid;
        if (uuid == null || uuid.equals(Invoker.CONSOLE_UUID)) {
            return Bukkit.getConsoleSender();
        }
        return Bukkit.getPlayer(uuid);
    }

}
