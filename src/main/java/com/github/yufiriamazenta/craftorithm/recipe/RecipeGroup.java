package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.recipe.registry.StoneCuttingRecipeRegistry;
import crypticlib.config.ConfigWrapper;
import crypticlib.util.FileUtil;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeGroup {

    protected String groupName;
    protected Map<String, NamespacedKey> groupRecipeKeyMap = new ConcurrentHashMap<>();
    protected Map<NamespacedKey, RecipeRegistry> groupRecipeRegistryMap = new ConcurrentHashMap<>();
    protected ConfigWrapper recipeGroupConfig;
    protected int sortId;

    public RecipeGroup(@NotNull String groupName) {
        this.groupName = groupName;
        this.recipeGroupConfig = createRecipeConfig();
        this.sortId = 0;
    }

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
        if (recipeRegistry.recipeType().equals(RecipeType.STONE_CUTTING)) {
            StoneCuttingRecipeRegistry stoneCuttingRecipeRegistry = (StoneCuttingRecipeRegistry) recipeRegistry;
            groupRecipeKeyMap.putAll(stoneCuttingRecipeRegistry.subRecipeKeyMap());
            for (NamespacedKey key : stoneCuttingRecipeRegistry.subRecipeMap().keySet()) {
                groupRecipeRegistryMap.put(key, recipeRegistry);
            }
        } else {
            groupRecipeKeyMap.put(recipeName, recipeRegistry.namespacedKey());
            groupRecipeRegistryMap.put(recipeRegistry.namespacedKey(), recipeRegistry);
        }
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

    public boolean contains(String recipeName) {
        return groupRecipeKeyMap.containsKey(recipeName);
    }

    public boolean contains(NamespacedKey namespacedKey) {
        return groupRecipeRegistryMap.containsKey(namespacedKey);
    }

    public void register() {
        for (RecipeRegistry registry : groupRecipeRegistryMap.values()) {
            registry.register();
        }
    }

    public void unregister(boolean deleteFile) {
        RecipeManager.INSTANCE.removeRecipeGroup(groupName, deleteFile);
    }

    public void updateRecipeGroupData() {
        RecipeGroup newGroup = new RecipeGroupParser(groupName, recipeGroupConfig).parse();
        this.groupRecipeKeyMap = newGroup.groupRecipeKeyMap;
        this.groupRecipeRegistryMap = newGroup.groupRecipeRegistryMap;
        this.sortId = newGroup.sortId;
    }

    public void loadRecipeGroup() {
        if (RecipeManager.INSTANCE.hasRecipeGroup(groupName)) {
            RecipeManager.INSTANCE.removeRecipeGroup(groupName, false);
        }
        RecipeManager.INSTANCE.loadRecipeGroup(this);
    }

    public void updateAndLoadRecipeGroup() {
        updateRecipeGroupData();
        loadRecipeGroup();
    }

    protected ConfigWrapper createRecipeConfig() {
        File recipeFile = new File(RecipeManager.INSTANCE.RECIPE_FILE_FOLDER, groupName + ".yml");
        if (!recipeFile.exists()) {
            FileUtil.createNewFile(recipeFile);
        }
        return new ConfigWrapper(recipeFile);
    }

}
