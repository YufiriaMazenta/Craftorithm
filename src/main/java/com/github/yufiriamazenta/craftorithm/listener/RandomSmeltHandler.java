package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RandomCookingRecipeRegistry;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import crypticlib.listener.BukkitListener;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@BukkitListener
public enum RandomSmeltHandler implements Listener {

    INSTANCE;

    private final Map<Block, List<RandomCookingRecipeRegistry.RandomCookingResult>> randomFurnaceBlockMap;
    private final Random random;

    RandomSmeltHandler() {
        random = new Random();
        randomFurnaceBlockMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        NamespacedKey recipeKey = event.getRecipe().getKey();
        RecipeGroup recipeGroup = RecipeManager.INSTANCE.getRecipeGroup(recipeKey);
        if (recipeGroup == null)
            return;

        RecipeRegistry recipeRegistry = recipeGroup.getRecipeRegistry(recipeKey);
        if (recipeRegistry == null)
            return;

        if (!recipeRegistry.recipeType().equals(RecipeType.RANDOM_COOKING))
            return;

        RandomCookingRecipeRegistry randomCookingRecipeRegistry = (RandomCookingRecipeRegistry) recipeRegistry;
        randomFurnaceBlockMap.put(event.getBlock(), randomCookingRecipeRegistry.results());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (randomFurnaceBlockMap.containsKey(event.getBlock())) {
            List<RandomCookingRecipeRegistry.RandomCookingResult> results = randomFurnaceBlockMap.get(event.getBlock());
            int randomNum = random.nextInt(results.get(results.size() - 1).weight());
            for (RandomCookingRecipeRegistry.RandomCookingResult result : results) {
                if (randomNum < result.weight()) {
                    event.setResult(result.result());
                    break;
                }
            }
        }
    }

}
