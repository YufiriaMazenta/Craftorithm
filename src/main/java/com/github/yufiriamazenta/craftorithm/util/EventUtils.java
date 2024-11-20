package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class EventUtils {

    public static final Boolean hasCrafterCraftEvent;

    static {
        hasCrafterCraftEvent = getHasCrafterCraftEvent();
    }

    private static Boolean getHasCrafterCraftEvent() {
        try {
            Class.forName("org.bukkit.event.block.CrafterCraftEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isCraftorithmRecipeEvent(Event event) {
        Recipe recipe = null;
        switch (event) {
            case CraftItemEvent craftItemEvent -> recipe = craftItemEvent.getRecipe();
            case PrepareItemCraftEvent prepareItemCraftEvent -> recipe = prepareItemCraftEvent.getRecipe();
            case PrepareSmithingEvent prepareSmithingEvent -> recipe = prepareSmithingEvent.getInventory().getRecipe();
            case SmithItemEvent smithItemEvent -> recipe = smithItemEvent.getInventory().getRecipe();
            case FurnaceSmeltEvent furnaceSmeltEvent -> recipe = furnaceSmeltEvent.getRecipe();
            case BlockCookEvent blockCookEvent -> recipe = blockCookEvent.getRecipe();
            case PrepareAnvilEvent prepareAnvilEvent -> {
                //因为只有paper及下游服务端才有这个问题,如果识别到是bukkit或者spigot,就不用处理
                if (!CrypticLibBukkit.isPaper()) {
                    return false;
                }
                if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
                    return false;
                ItemStack base = prepareAnvilEvent.getInventory().getItem(0);
                ItemStack addition = prepareAnvilEvent.getInventory().getItem(1);
                if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
                    return false;

                AnvilRecipe anvilRecipe = RecipeManager.INSTANCE.matchAnvilRecipe(base, addition);
                return anvilRecipe != null;
            }
            case null -> {
                return false;
            }
            default -> {
                if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_17_1)) {
                    if (event instanceof FurnaceStartSmeltEvent furnaceStartSmeltEvent) {
                        recipe = furnaceStartSmeltEvent.getRecipe();
                    }
                }
                if (hasCrafterCraftEvent) {
                    if (event instanceof CrafterCraftEvent crafterCraftEvent) {
                        recipe = crafterCraftEvent.getRecipe();
                    }
                }
            }
        }
        if (recipe == null) {
            return false;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        return recipeKey != null && recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE);
    }

}
