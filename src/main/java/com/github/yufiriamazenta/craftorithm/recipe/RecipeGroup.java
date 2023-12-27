package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import crypticlib.config.ConfigWrapper;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecipeGroup {

    private String groupName;
    private List<NamespacedKey> groupRecipeKeys = new CopyOnWriteArrayList<>();
    private final RecipeType recipeType;
    private ConfigWrapper recipeGroupConfig;
    private int sortId;
    private boolean unlock;

    public RecipeGroup(@NotNull String groupName, @NotNull RecipeType recipeType, @NotNull ConfigWrapper recipeGroupConfig) {
        this(groupName, new ArrayList<>(), recipeType, recipeGroupConfig);
    }

    public RecipeGroup(@NotNull String groupName, @NotNull List<NamespacedKey> groupRecipeKeys, @NotNull RecipeType recipeType, @NotNull ConfigWrapper recipeGroupConfig) {
        this.groupName = groupName;
        this.groupRecipeKeys.addAll(groupRecipeKeys);
        this.recipeType = recipeType;
        this.recipeGroupConfig = recipeGroupConfig;
        this.sortId = recipeGroupConfig.config().getInt("sort_id", 0);
        this.unlock = recipeGroupConfig.config().getBoolean("unlock", PluginConfigs.DEFAULT_RECIPE_UNLOCK.value());
    }

    public String groupName() {
        return groupName;
    }

    public RecipeGroup setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public List<NamespacedKey> groupRecipeKeys() {
        return groupRecipeKeys;
    }

    public RecipeGroup setGroupRecipeKeys(List<NamespacedKey> groupRecipeKeys) {
        this.groupRecipeKeys = groupRecipeKeys;
        return this;
    }

    public boolean contains(NamespacedKey namespacedKey) {
        return groupRecipeKeys.contains(namespacedKey);
    }

    public RecipeGroup addRecipeKey(NamespacedKey namespacedKey) {
        groupRecipeKeys.add(namespacedKey);
        return this;
    }

    public boolean isEmpty() {
        return groupRecipeKeys.isEmpty();
    }

    public RecipeType recipeType() {
        return recipeType;
    }

    public int sortId() {
        return sortId;
    }

    public RecipeGroup setSortId(int sortId) {
        this.sortId = sortId;
        return this;
    }

    public boolean unlock() {
        return unlock;
    }

    public RecipeGroup setUnlock(boolean unlock) {
        this.unlock = unlock;
        return this;
    }

    public @NotNull ConfigWrapper recipeGroupConfig() {
        return recipeGroupConfig;
    }

    public RecipeGroup setRecipeGroupConfig(ConfigWrapper recipeGroupConfig) {
        this.recipeGroupConfig = recipeGroupConfig;
        return this;
    }

}
