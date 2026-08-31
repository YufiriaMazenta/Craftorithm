package pers.yufiria.craftorithm.command;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(
    phase = LifecyclePhase.RELOAD,
    priority = Integer.MAX_VALUE,
    isAsync = true
))
public final class ReloadCommand extends CommandNode implements LifecycleTask {

    public static final ReloadCommand INSTANCE = new ReloadCommand();
    private volatile Invoker reloadInvoker;

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
            reloadInvoker = invoker;
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
        // 等待 RecipeManager 异步配方加载完成（当前已在异步线程，阻塞不会影响主线程）
        RecipeManager.INSTANCE.getReloadCompletion().join();
        // 确保消息在主线程发送
        CrypticLibBukkit.scheduler().sync(() -> {
            Invoker invoker = reloadInvoker;
            reloadInvoker = null;
            if (invoker != null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_RELOAD_SUCCESS);
            }
        });
    }

}
