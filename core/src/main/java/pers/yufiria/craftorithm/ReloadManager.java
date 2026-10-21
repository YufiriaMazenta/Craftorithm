package pers.yufiria.craftorithm;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.Invoker;
import crypticlib.scheduler.CrypticLibRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 插件重载管理: 所有重载请求的统一入口, 负责重载队列、代际状态与重载收尾汇合
 */
public enum ReloadManager {

    INSTANCE;

    private volatile @Nullable UUID reloadSenderUuid;
    private final AtomicBoolean reloading = new AtomicBoolean(false);
    private volatile CrypticLibRunnable reloadTimeoutCallback = null;
    //重载队列, 入队与消费均只发生在主线程
    private final Deque<CommandSender> pendingReloads = new ArrayDeque<>();
    //代际锁与计数: 每轮重载递增, 供超时回调与收尾任务判定自己是否仍属当前轮
    private final Object reloadLock = new Object();
    private volatile long reloadGeneration;

    /**
     * 每轮重载开始时由主类的whenReload回调触发(此时reloadConfig已执行完成):
     * 锁内递增代际并置位重载状态, 同时挂一个30秒的超时兜底任务
     * <p>
     * reloading状态的置位放在此处而非重载入口, 避免reloadConfig抛异常时状态被提前置位且无超时兜底导致永久拦截
     */
    void onReloadStart() {
        long gen;
        synchronized (reloadLock) {
            reloadGeneration++;
            gen = reloadGeneration;
            reloading.set(true);
        }
        if (reloadTimeoutCallback != null) {
            reloadTimeoutCallback.cancel();
        }
        //通过一个延迟任务，确保就算插件重载报错了也能在30秒的超时时间后正确恢复不在重载的状态
        reloadTimeoutCallback = new CrypticLibRunnable() {
            @Override
            public void run() {
                //锁内判定代际: 已有新轮启动时不触碰新轮状态, 新轮状态由其自己的收尾负责恢复
                boolean current;
                synchronized (reloadLock) {
                    current = gen == reloadGeneration;
                    if (current) {
                        reloading.set(false);
                        reloadSenderUuid = null;
                    }
                }
                if (current) {
                    drainPendingReload();
                }
            }
        };
        reloadTimeoutCallback.asyncLater(600);
    }

    /**
     * 每轮重载结束时由主类的RELOAD生命周期分支触发(异步批收尾任务):
     * 带超时等待配方重载完成, 代际判定后清理本轮状态, 发送成功消息, 最后消费重载队列
     */
    void onReloadFinish() {
        long myGen;
        synchronized (reloadLock) {
            myGen = reloadGeneration;
        }
        //带超时等待配方重载完成, 防止配方重载链路异常中断导致收尾永久阻塞
        boolean recipeReloadDone = true;
        CompletableFuture<Void> reloadCompletion = RecipeManager.INSTANCE.getReloadCompletion();
        if (reloadCompletion == null) {
            CrypticLib.warn("Recipe reload completion is null, skip reload success message");
            recipeReloadDone = false;
        } else {
            try {
                reloadCompletion.get(30, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                recipeReloadDone = false;
                CrypticLib.error("Timed out waiting for recipe reload completion, skip reload success message");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                recipeReloadDone = false;
            } catch (ExecutionException e) {
                recipeReloadDone = false;
                CrypticLib.error("Recipe reload completion ended exceptionally", e.getCause());
            }
        }
        CommandSender sender = getReloader();
        //锁内判定代际并执行清理: 已有新轮启动时本轮对状态无处置权, 不触碰新轮的状态与回调
        boolean owned;
        synchronized (reloadLock) {
            owned = myGen == reloadGeneration;
            if (owned) {
                //如果正确重载了，取消超时任务
                if (reloadTimeoutCallback != null) {
                    reloadTimeoutCallback.cancel();
                    reloadTimeoutCallback = null;
                }
                reloadSenderUuid = null;
                reloading.set(false);
            }
        }
        if (recipeReloadDone && owned && sender != null) {
            LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
        }
        drainPendingReload();
    }

    /**
     * 以指定的重载者发起一次插件重载
     * 已经处于重载中或存在排队的重载时, 本次请求进入队列, 在当前重载完成后依次执行
     *
     * @param reloader 重载发起者
     * @return true表示立即开始了重载, false表示已进入排队等待
     */
    public boolean reloadPlugin(CommandSender reloader) {
        if (reloading.get() || !pendingReloads.isEmpty()) {
            pendingReloads.addLast(reloader);
            return false;
        }
        return startReload(reloader);
    }

    private boolean startReload(CommandSender reloader) {
        reloadSenderUuid = reloader instanceof Player player
            ? player.getUniqueId()
            : Invoker.CONSOLE_UUID;
        try {
            Craftorithm.instance().reloadPlugin();
            return true;
        } catch (Exception e) {
            CrypticLib.error("Failed to reload Craftorithm", e);
            LangUtils.sendLang(reloader, Languages.COMMAND_RELOAD_EXCEPTION);
            return false;
        }
    }

    /**
     * 消费重载队列: 在主线程依次启动排队的重载, 直到无排队任务或某轮成功进入重载状态
     */
    private void drainPendingReload() {
        if (Bukkit.isPrimaryThread()) {
            drainOnMain();
        } else {
            CrypticLibBukkit.scheduler().sync(this::drainOnMain);
        }
    }

    private void drainOnMain() {
        while (!reloading.get()) {
            CommandSender next = pendingReloads.pollFirst();
            if (next == null) {
                return;
            }
            startReload(next);
        }
    }

    public @Nullable CommandSender getReloader() {
        UUID uuid = reloadSenderUuid;
        if (uuid == null || uuid.equals(Invoker.CONSOLE_UUID)) {
            return Bukkit.getConsoleSender();
        }
        return Bukkit.getPlayer(uuid);
    }

    public boolean isReloading() {
        return reloading.get();
    }

}
