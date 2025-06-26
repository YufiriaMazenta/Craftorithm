package pers.yufiria.craftorithm.util;

import crypticlib.MinecraftVersion;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipeHandler;

public class EventUtils {

    public static final Boolean hasCrafterCraftEvent;

    static {
        hasCrafterCraftEvent = getHasCrafterCraftEvent();
    }

    private static boolean getHasCrafterCraftEvent() {
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
                if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
                    return false;
                ItemStack base = prepareAnvilEvent.getInventory().getItem(0);
                ItemStack addition = prepareAnvilEvent.getInventory().getItem(1);
                if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
                    return false;

                AnvilRecipe anvilRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition);
                return anvilRecipe != null;
            }
            case null -> {
                return false;
            }
            default -> {
                if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_17_1)) {
                    if (event instanceof FurnaceStartSmeltEvent furnaceStartSmeltEvent) {
                        recipe = furnaceStartSmeltEvent.getRecipe();
                        break;
                    }
                }
                if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_19_3)) {
                    if (event instanceof CampfireStartEvent campfireStartEvent) {
                        recipe = campfireStartEvent.getRecipe();
                        break;
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
