package pers.yufiria.craftorithm.item;

import crypticlib.CrypticLib;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ItemProvider {

    //同一物品提供源的错误在此时间窗口内不重复输出, 单位毫秒
    long ERROR_LOG_INTERVAL_MILLIS = 10_000L;
    //每个物品提供源上次输出错误的时间, 用于时间窗口内去重
    Map<String, Long> LAST_LOGGED_TIMES = new ConcurrentHashMap<>();

    @NotNull
    String namespace();

    @Nullable
    NamespacedItemId matchItemId(ItemStack itemStack);

    @Nullable
    ItemStack matchItem(String itemId);

    @Nullable
    default ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        return matchItem(itemId);
    }

    /**
     * 输出该物品提供源抛出的异常, 同一物品提供源在时间窗口内不重复输出
     *
     * @param throwable 物品提供源抛出的异常
     */
    default void logError(Throwable throwable) {
        long now = System.currentTimeMillis();
        Long lastLogged = LAST_LOGGED_TIMES.get(namespace());
        if (lastLogged != null && now - lastLogged < ERROR_LOG_INTERVAL_MILLIS) {
            return;
        }
        LAST_LOGGED_TIMES.put(namespace(), now);
        CrypticLib.error("Item provider '" + namespace() + "' threw an exception, it may be incompatible with the installed plugin version", throwable);
    }

}
