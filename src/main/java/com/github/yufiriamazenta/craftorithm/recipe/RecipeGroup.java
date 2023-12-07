package com.github.yufiriamazenta.craftorithm.recipe;

import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class RecipeGroup {

    private String groupName;
    private List<NamespacedKey> groupRecipeKeys;

    public RecipeGroup(String groupName) {
        this.groupName = groupName;
        this.groupRecipeKeys = new ArrayList<>();
    }

    public RecipeGroup(String groupName, List<NamespacedKey> groupRecipeKeys) {
        this.groupName = groupName;
        this.groupRecipeKeys = groupRecipeKeys;
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
}
