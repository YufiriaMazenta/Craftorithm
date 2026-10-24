package pers.yufiria.craftorithm.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.hook.item.ItemPluginHook;
import pers.yufiria.craftorithm.hook.item.ItemPluginHookManager;
import pers.yufiria.craftorithm.item.CraftorithmItemProvider;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Map;
import java.util.Optional;

public enum ItemApi {

    INSTANCE;

    /**
     * 获取一个Craftorithm物品
     *
     * @param itemName 物品名称（格式: namespace:id 或 纯id）
     * @return 物品栈，不存在返回null
     */
    public @Nullable ItemStack getCraftorithmItem(@NotNull String itemName) {
        return CraftorithmItemProvider.INSTANCE.matchItem(itemName);
    }

    /**
     * 根据NamespacedItemIdStack获取物品
     *
     * @param itemIdStack 物品ID栈
     * @return 物品栈，不存在返回 Optional.empty()
     */
    public Optional<ItemStack> getItem(@NotNull NamespacedItemIdStack itemIdStack) {
        return ItemManager.INSTANCE.matchItem(itemIdStack);
    }

    /**
     * 根据NamespacedItemIdStack获取物品（带玩家变量解析）
     *
     * @param itemIdStack 物品ID栈
     * @param player      玩家
     * @return 物品栈，不存在返回 Optional.empty()
     */
    public Optional<ItemStack> getItem(@NotNull NamespacedItemIdStack itemIdStack, @Nullable Player player) {
        return ItemManager.INSTANCE.matchItem(itemIdStack, player);
    }

    /**
     * 获取物品的NamespacedItemId
     *
     * @param item         物品栈
     * @param ignoreAmount 是否忽略数量
     * @return 物品ID，无法识别返回 Optional.empty()
     */
    public Optional<NamespacedItemIdStack> matchItemId(@NotNull ItemStack item, boolean ignoreAmount) {
        return ItemManager.INSTANCE.matchItemId(item, ignoreAmount);
    }

    /**
     * 注册一个物品插件挂钩
     * 通过此方式注册的挂钩会在重载时自动重新注册，不会丢失
     *
     * @param hook 物品插件挂钩
     */
    public void registerItemPluginHook(@NotNull ItemPluginHook hook) {
        ItemPluginHookManager.INSTANCE.addItemPluginHook(hook);
    }

    /**
     * 获取所有Craftorithm物品
     *
     * @return 物品映射副本
     */
    public @NotNull Map<String, ItemStack> getCraftorithmItems() {
        return CraftorithmItemProvider.INSTANCE.idItemMap();
    }

}
