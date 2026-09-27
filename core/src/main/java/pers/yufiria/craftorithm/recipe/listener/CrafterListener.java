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
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessors;

import java.util.Optional;

@EventListener
public enum CrafterListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void processResult(CrafterCraftEvent event) {
        if (event.isCancelled()) {
            return;
        }
        ItemStack result = event.getResult();
        if (ItemHelper.isAir(result))
            return;
        //重新从物品源获取物品, 刷新结果的组件
        ItemManager.INSTANCE.matchItemId(result, true)
            .flatMap(itemIdStack -> {
                if (itemIdStack.itemId().isVanillaItem()) {
                    return Optional.empty();
                }
                return ItemManager.INSTANCE.matchItem(itemIdStack);
            })
            .ifPresent(refreshItem -> {
                if (!result.isSimilar(refreshItem)) {
                    result.setItemMeta(refreshItem.getItemMeta());
                }
            });

        // 运行结果处理器（Crafter没有sourceItem）
        Recipe recipe = event.getRecipe();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey != null) {
            Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
            processors.ifPresent(p -> p.processItem(null, result, null));
        }
        event.setResult(result);
    }

}
