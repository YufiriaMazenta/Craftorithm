package pers.yufiria.craftorithm.util;

import crypticlib.CrypticLibBukkit;
import crypticlib.util.InventoryViewHelper;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipeHandler;

import java.util.Optional;

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
        return getCraftorithmRecipeKey(event) != null;
    }

    public static @Nullable NamespacedKey getCraftorithmRecipeKey(Event event) {
        Recipe recipe = null;
        switch (event) {
            case CraftItemEvent craftItemEvent -> {
                recipe = craftItemEvent.getRecipe();
            }
            case PrepareItemCraftEvent prepareItemCraftEvent -> {
                recipe = prepareItemCraftEvent.getRecipe();
            }
            case PrepareSmithingEvent prepareSmithingEvent -> recipe = prepareSmithingEvent.getInventory().getRecipe();
            case SmithItemEvent smithItemEvent -> recipe = smithItemEvent.getInventory().getRecipe();
            case FurnaceSmeltEvent furnaceSmeltEvent -> {
                if (CrypticLibBukkit.isPaper()) {
                    recipe = furnaceSmeltEvent.getRecipe();
                } else {
                    //非paper端读取不到配方信息
                    return null;
                }
            }
            case FurnaceStartSmeltEvent furnaceStartSmeltEvent -> recipe = furnaceStartSmeltEvent.getRecipe();
            case CampfireStartEvent campfireStartEvent -> recipe = campfireStartEvent.getRecipe();
            case BlockCookEvent blockCookEvent -> recipe = blockCookEvent.getRecipe();
            case InventoryClickEvent clickEvent -> {
                Inventory clickedInventory = clickEvent.getClickedInventory();
                if (clickedInventory == null) {
                    return null;
                }
                switch (clickedInventory) {
                    case CraftingInventory craftingInventory -> recipe = craftingInventory.getRecipe();
                    case SmithingInventory smithingInventory -> recipe = smithingInventory.getRecipe();
                    case AnvilInventory anvilInventory -> {
                        ItemStack base = anvilInventory.getItem(0);
                        ItemStack addition = anvilInventory.getItem(1);
                        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
                            return null;

                        AnvilRecipe anvilRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition);
                        return anvilRecipe != null ? anvilRecipe.getKey() : null;
                    }
                    default -> {
                        return null;
                    }
                }
            }
            case PrepareAnvilEvent prepareAnvilEvent -> {
                if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
                    return null;
                ItemStack base = prepareAnvilEvent.getInventory().getItem(0);
                ItemStack addition = prepareAnvilEvent.getInventory().getItem(1);
                if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
                    return null;

                AnvilRecipe anvilRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition);
                return anvilRecipe != null ? anvilRecipe.getKey() : null;
            }
            case null -> {
                return null;
            }
            default -> {
                if (hasCrafterCraftEvent) {
                    if (event instanceof CrafterCraftEvent crafterCraftEvent) {
                        recipe = crafterCraftEvent.getRecipe();
                    }
                }
            }
        }
        return getCraftorithmRecipeKey(recipe);
    }

    public static @Nullable NamespacedKey getCraftorithmRecipeKey(@Nullable Recipe recipe) {
        if (recipe == null) {
            return null;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        return isCraftorithmRecipeKey(recipeKey) ? recipeKey : null;
    }

    public static boolean isCraftorithmRecipeKey(@Nullable NamespacedKey recipeKey) {
        return recipeKey != null && recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE);
    }

    public static Optional<Player> getViewer(InventoryEvent event) {
        HumanEntity viewing = InventoryViewHelper.getViewingPlayer(InventoryViewHelper.getInventoryView(event));
        if (viewing instanceof Player player) {
            return Optional.of(player);
        }
        return Optional.empty();
    }

}
