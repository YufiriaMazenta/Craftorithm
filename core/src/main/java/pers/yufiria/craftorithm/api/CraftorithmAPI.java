package pers.yufiria.craftorithm.api;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.item.ItemPluginHook;
import pers.yufiria.craftorithm.hook.item.ItemPluginHookManager;
import pers.yufiria.craftorithm.item.CraftorithmItemProvider;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeGroup;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public enum CraftorithmAPI {

    INSTANCE;

    // ==================== Item API ====================

    /**
     * 获取一个Craftorithm物品
     * @param itemName 物品名称（格式: namespace:id 或 纯id）
     * @return 物品栈，不存在返回null
     */
    public @Nullable ItemStack getCraftorithmItem(@NotNull String itemName) {
        return CraftorithmItemProvider.INSTANCE.matchItem(itemName);
    }

    /**
     * 根据NamespacedItemIdStack获取物品
     * @param itemIdStack 物品ID栈
     * @return 物品栈
     */
    public @NotNull ItemStack getItem(@NotNull NamespacedItemIdStack itemIdStack) {
        return ItemManager.INSTANCE.matchItem(itemIdStack);
    }

    /**
     * 根据NamespacedItemIdStack获取物品（带玩家变量解析）
     * @param itemIdStack 物品ID栈
     * @param player 玩家
     * @return 物品栈
     */
    public @NotNull ItemStack getItem(@NotNull NamespacedItemIdStack itemIdStack, @Nullable Player player) {
        return ItemManager.INSTANCE.matchItem(itemIdStack, player);
    }

    /**
     * 获取物品的NamespacedItemId
     * @param item 物品栈
     * @param ignoreAmount 是否忽略数量
     * @return 物品ID，无法识别返回null
     */
    public @Nullable NamespacedItemIdStack matchItemId(@NotNull ItemStack item, boolean ignoreAmount) {
        return ItemManager.INSTANCE.matchItemId(item, ignoreAmount);
    }

    /**
     * 注册一个物品插件挂钩
     * 通过此方式注册的挂钩会在重载时自动重新注册，不会丢失
     * @param hook 物品插件挂钩
     */
    public void registerItemPluginHook(@NotNull ItemPluginHook hook) {
        ItemPluginHookManager.INSTANCE.addItemPluginHook(hook);
    }

    /**
     * 获取所有Craftorithm物品
     * @return 物品映射副本
     */
    public @NotNull Map<String, ItemStack> getCraftorithmItems() {
        return CraftorithmItemProvider.INSTANCE.itemMap();
    }

    // ==================== Recipe API ====================

    /**
     * 根据配方Key获取配方，会从服务器里查找所有配方
     * @param recipeKey 配方Key
     * @return 配方，不存在返回null
     */
    public @Nullable Recipe getRecipe(@NotNull NamespacedKey recipeKey) {
        return RecipeManager.INSTANCE.getRecipe(recipeKey);
    }

    /**
     * 检查配方是否存在
     * @param recipeName 配方名称
     * @return 是否存在
     */
    public boolean containsRecipe(@NotNull String recipeName) {
        return RecipeManager.INSTANCE.containsRecipe(recipeName);
    }

    /**
     * 检查配方是否存在
     * @param recipeKey 配方Key
     * @return 是否存在
     */
    public boolean containsRecipe(@NotNull NamespacedKey recipeKey) {
        return RecipeManager.INSTANCE.containsRecipe(recipeKey);
    }

    /**
     * 获取所有Craftorithm配方
     * @return 配方映射副本
     */
    public @NotNull Map<NamespacedKey, Recipe> getCraftorithmRecipes() {
        return Collections.unmodifiableMap(RecipeManager.INSTANCE.craftorithmRecipes());
    }

    /**
     * 获取服务器配方缓存
     * @return 配方映射副本
     */
    public @NotNull Map<NamespacedKey, Recipe> getServerRecipes() {
        return Collections.unmodifiableMap(RecipeManager.INSTANCE.serverRecipesCache());
    }

    /**
     * 获取配方类型
     * @param recipe 配方
     * @return 配方类型
     */
    public @NotNull RecipeType getRecipeType(@NotNull Recipe recipe) {
        return RecipeManager.INSTANCE.getRecipeType(recipe);
    }

    /**
     * 获取配方类型
     * @param typeId 类型ID
     * @return 配方类型，不存在返回null
     */
    public @Nullable RecipeType getRecipeType(@NotNull String typeId) {
        return RecipeManager.INSTANCE.getRecipeType(typeId);
    }

    /**
     * 注册配方类型
     * @param type 配方类型
     * @return 是否成功
     */
    public boolean registerRecipeType(@NotNull RecipeType type) {
        return RecipeManager.INSTANCE.regRecipeType(type);
    }

    /**
     * 获取配方组列表
     * @return 配方组ID列表
     */
    public @NotNull Collection<String> getRecipeGroups() {
        return RecipeManager.INSTANCE.getRecipeGroups();
    }

    /**
     * 获取配方组
     * @param groupId 配方组ID
     * @return 配方组，不存在返回null
     */
    public @Nullable RecipeGroup getRecipeGroup(@NotNull String groupId) {
        return RecipeManager.INSTANCE.getRecipeGroup(groupId);
    }

    /**
     * 禁用配方
     * @param recipeKey 配方Key
     * @param save 是否保存到配置
     * @return 是否成功
     */
    public boolean disableRecipe(@NotNull NamespacedKey recipeKey, boolean save) {
        return RecipeManager.INSTANCE.disableRecipe(recipeKey, save);
    }

    /**
     * 删除Craftorithm配方
     * @param recipeId 配方ID
     * @param deleteFile 是否删除配置文件
     * @return 是否成功
     */
    public boolean removeCraftorithmRecipe(@NotNull String recipeId, boolean deleteFile) {
        return RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeId, deleteFile);
    }

    /**
     * 通过配方Key查找配方文件名
     * @param recipeKey 配方Key
     * @return 配方文件名，不存在返回null
     */
    public @Nullable String getRecipeFileNameByKey(@NotNull NamespacedKey recipeKey) {
        return RecipeManager.INSTANCE.getRecipeFileNameByKey(recipeKey);
    }

    /**
     * 通过配方文件名查找配方Key
     * @param recipeFileName 配方文件名
     * @return 配方Key，不存在返回null
     */
    public @Nullable NamespacedKey getRecipeKeyByFileName(@NotNull String recipeFileName) {
        return RecipeManager.INSTANCE.getRecipeKeyByFileName(recipeFileName);
    }

    // ==================== Trigger API ====================

    /**
     * 获取触发器管理器
     * @return 触发器管理器实例
     */
    public @NotNull TriggerManager getTriggerManager() {
        return TriggerManager.INSTANCE;
    }

    /**
     * 触发指定类型的触发器
     * @param typeKey 触发器类型
     * @param context 触发上下文
     * @return 是否有触发器被执行
     */
    public boolean fireTrigger(@NotNull String typeKey, @NotNull TriggerContext context) {
        return TriggerManager.INSTANCE.fire(typeKey, context);
    }

    // ==================== Utility API ====================

    /**
     * 获取插件实例
     * @return 插件实例
     */
    public @NotNull Craftorithm getPlugin() {
        return Craftorithm.instance();
    }

    /**
     * 检查配方管理器是否正在重载
     * @return 是否正在重载
     */
    public boolean isReloadingRecipeManager() {
        return RecipeManager.INSTANCE.isReloadingRecipeManager();
    }

}
