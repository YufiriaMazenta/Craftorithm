package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import crypticlib.config.ConfigWrapper;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeGroup {

    protected String groupName;
    protected Map<String, NamespacedKey> groupRecipeKeyMap = new ConcurrentHashMap<>();
    protected Map<NamespacedKey, RecipeRegistry> groupRecipeRegistryMap = new ConcurrentHashMap<>();
    protected ConfigWrapper recipeGroupConfig;
    protected int sortId;

    public RecipeGroup(@NotNull String groupName, @NotNull ConfigWrapper recipeGroupConfig, int sortId) {
        this.groupName = groupName;
        this.recipeGroupConfig = recipeGroupConfig;
        this.sortId = sortId;
    }

    public String groupName() {
        return groupName;
    }

    public RecipeGroup setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public int sortId() {
        return sortId;
    }

    public RecipeGroup setSortId(int sortId) {
        this.sortId = sortId;
        return this;
    }

    public @NotNull ConfigWrapper recipeGroupConfig() {
        return recipeGroupConfig;
    }

    public RecipeGroup setRecipeGroupConfig(ConfigWrapper recipeGroupConfig) {
        this.recipeGroupConfig = recipeGroupConfig;
        return this;
    }

    public Map<String, NamespacedKey> groupRecipeKeyMap() {
        return groupRecipeKeyMap;
    }

    public Map<NamespacedKey, RecipeRegistry> groupRecipeRegistryMap() {
        return groupRecipeRegistryMap;
    }

    public RecipeGroup addRecipeRegistry(String recipeName, RecipeRegistry recipeRegistry) {
        if (!recipeRegistry.group().equals(groupName))
            throw new IllegalArgumentException(
                "Cannot add recipe " + recipeName + " to group " + groupName + " because its group is " + recipeRegistry.group()
            );
        groupRecipeKeyMap.put(recipeName, recipeRegistry.namespacedKey());
        groupRecipeRegistryMap.put(recipeRegistry.namespacedKey(), recipeRegistry);
        return this;
    }

    public RecipeGroup removeRecipeRegistry(String recipeName) {
        NamespacedKey recipeKey = groupRecipeKeyMap.get(recipeName);
        if (recipeKey == null) {
            return this;
        }
        groupRecipeRegistryMap.remove(recipeKey);
        groupRecipeKeyMap.remove(recipeName);
        return this;
    }

    public @Nullable RecipeRegistry getRecipeRegistry(@NotNull String recipeName) {
        NamespacedKey recipeKey = groupRecipeKeyMap.get(recipeName);
        if (recipeKey == null) {
            return null;
        }
        return getRecipeRegistry(recipeKey);
    }

    public @Nullable RecipeRegistry getRecipeRegistry(@NotNull NamespacedKey recipeKey) {
        return groupRecipeRegistryMap.get(recipeKey);
    }

    public void register() {
        for (RecipeRegistry registry : groupRecipeRegistryMap.values()) {
            registry.register();
        }
    }

    public void unregister(boolean deleteFile) {
        RecipeManager.INSTANCE.removeCraftorithmRecipe(groupName, deleteFile);
    }

}
