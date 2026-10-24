package pers.yufiria.craftorithm.api;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeGroup;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;

import java.util.*;

public enum RecipeApi {

    INSTANCE;


    /**
     * 根据配方Key获取配方，会从服务器里查找所有配方
     *
     * @param recipeKey 配方Key
     * @return 配方，不存在返回null
     */
    public @Nullable Recipe getRecipe(@NotNull NamespacedKey recipeKey) {
        return RecipeManager.INSTANCE.getRecipe(recipeKey);
    }

    /**
     * 检查配方是否存在
     *
     * @param recipeName 配方名称
     * @return 是否存在
     */
    public boolean containsRecipe(@NotNull String recipeName) {
        return RecipeManager.INSTANCE.containsRecipe(recipeName);
    }

    /**
     * 检查配方是否存在
     *
     * @param recipeKey 配方Key
     * @return 是否存在
     */
    public boolean containsRecipe(@NotNull NamespacedKey recipeKey) {
        return RecipeManager.INSTANCE.containsRecipe(recipeKey);
    }

    /**
     * 获取所有Craftorithm配方
     *
     * @return 配方映射副本
     */
    public @NotNull Map<NamespacedKey, Recipe> getCraftorithmRecipes() {
        Map<NamespacedKey, Recipe> result = new HashMap<>();
        RecipeManager.INSTANCE.craftorithmRecipes().forEach((key, parsed) -> result.put(key, parsed.recipe()));
        return Collections.unmodifiableMap(result);
    }

    /**
     * 获取服务器配方Key缓存
     *
     * @return 配方Key集合副本
     */
    public @NotNull Set<NamespacedKey> getServerRecipeKeys() {
        return RecipeManager.INSTANCE.serverRecipeKeys();
    }

    /**
     * 获取配方类型
     *
     * @param recipe 配方
     * @return 配方类型
     */
    public @NotNull RecipeType getRecipeType(@NotNull Recipe recipe) {
        return RecipeManager.INSTANCE.getRecipeType(recipe);
    }

    /**
     * 获取配方类型
     *
     * @param typeId 类型ID
     * @return 配方类型，不存在返回null
     */
    public @Nullable RecipeType getRecipeType(@NotNull String typeId) {
        return RecipeManager.INSTANCE.getRecipeTypeByKey(typeId);
    }

    /**
     * 注册配方类型
     *
     * @param type 配方类型
     * @return 是否成功
     */
    public boolean registerRecipeType(@NotNull RecipeType type) {
        return RecipeManager.INSTANCE.regRecipeType(type);
    }

    /**
     * 获取配方组列表
     *
     * @return 配方组ID列表
     */
    public @NotNull Collection<String> getRecipeGroups() {
        return RecipeManager.INSTANCE.getRecipeGroups();
    }

    /**
     * 获取配方组
     *
     * @param groupId 配方组ID
     * @return 配方组，不存在返回null
     */
    public @Nullable RecipeGroup getRecipeGroup(@NotNull String groupId) {
        return RecipeManager.INSTANCE.getRecipeGroup(groupId);
    }

    /**
     * 禁用配方
     *
     * @param recipeKey 配方Key
     * @param save      是否保存到配置
     * @return 是否成功
     */
    public boolean disableRecipe(@NotNull NamespacedKey recipeKey, boolean save) {
        return RecipeManager.INSTANCE.disableRecipe(recipeKey, save, true);
    }

    /**
     * 禁用配方
     *
     * @param recipeKey     配方Key
     * @param save          是否保存到配置
     * @param updateRecipes 是否给玩家更新配方列表
     * @return 是否成功
     */
    public boolean disableRecipe(@NotNull NamespacedKey recipeKey, boolean save, boolean updateRecipes) {
        return RecipeManager.INSTANCE.disableRecipe(recipeKey, save, updateRecipes);
    }

    /**
     * 删除Craftorithm配方
     *
     * @param recipeId   配方ID
     * @param deleteFile 是否删除配置文件
     * @return 是否成功
     */
    public boolean removeCraftorithmRecipe(@NotNull String recipeId, boolean deleteFile) {
        return RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeId, deleteFile, true);
    }

    /**
     * 删除Craftorithm配方
     *
     * @param recipeId   配方ID
     * @param deleteFile 是否删除配置文件
     * @return 是否成功
     */
    public boolean removeCraftorithmRecipe(@NotNull String recipeId, boolean deleteFile, boolean updateRecipes) {
        return RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeId, deleteFile, updateRecipes);
    }

    /**
     * 通过配方Key查找配方文件名
     *
     * @param recipeKey 配方Key
     * @return 配方文件名，不存在返回null
     */
    public @Nullable String getRecipeFileNameByKey(@NotNull NamespacedKey recipeKey) {
        return RecipeManager.INSTANCE.getRecipeFileNameByKey(recipeKey);
    }

    /**
     * 通过配方文件名查找配方Key
     *
     * @param recipeFileName 配方文件名
     * @return 配方Key，不存在返回null
     */
    public @Nullable NamespacedKey getRecipeKeyByFileName(@NotNull String recipeFileName) {
        return RecipeManager.INSTANCE.getRecipeKeyByFileName(recipeFileName);
    }

}
