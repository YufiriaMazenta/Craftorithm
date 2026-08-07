package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessorManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessors;

import java.util.Optional;

@EventListener
public enum CrafterListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkCanCraft(CrafterCraftEvent event) {
        BlockState blockState = event.getBlock().getState();
        if (!(blockState instanceof Crafter crafter))
            return;
        if (ItemManager.INSTANCE.containsCannotCraftItem(crafter.getInventory().getContents())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void processResult(CrafterCraftEvent event) {
        ItemStack result = event.getResult();
        if (ItemHelper.isAir(result))
            return;
        ItemManager.INSTANCE.matchItemId(result, true)
            .flatMap(ItemManager.INSTANCE::matchItem)
            .ifPresent(refreshItem -> {
                if (!result.isSimilar(refreshItem)) {
                    result.setItemMeta(refreshItem.getItemMeta());
                }
            });
        // 处理结果处理器（Crafter没有sourceItem）
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
            if (recipeKey != null) {
                Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
                processors.ifPresent(p -> p.processItem(null, result));
            }
        }
        event.setResult(result);
    }

}
