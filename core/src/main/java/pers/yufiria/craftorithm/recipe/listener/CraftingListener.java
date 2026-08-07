package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessorManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessors;
import pers.yufiria.craftorithm.util.EventUtils;

import java.util.Optional;

@EventListener
public enum CraftingListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void processResult(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            // Bukkit 匹配到了配方，Craftorithm 配方需要 refresh
            NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
            if (recipeKey != null && recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
                processResultItem(event, recipe.getResult(), recipeKey);
            }
        }
    }

    private void processResultItem(PrepareItemCraftEvent event, ItemStack item, NamespacedKey recipeKey) {
        // clone 避免直接修改 Bukkit Recipe 内部缓存的共享对象
        ItemStack result = item.clone();
        // 重新从物品源获取物品, 刷新结果的组件
        ItemManager.INSTANCE.matchItemId(result, true)
            .flatMap(id -> EventUtils.getViewer(event)
                .flatMap(player -> ItemManager.INSTANCE.matchItem(id, player))
            )
            .ifPresent(refreshItem -> {
                if (!result.isSimilar(refreshItem)) {
                    result.setItemMeta(refreshItem.getItemMeta());
                }
            });
        // 处理结果处理器（工作台配方没有sourceItem）
        Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
        processors.ifPresent(p -> p.processItem(null, result));
        event.getInventory().setResult(result);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkCannotCraft(PrepareItemCraftEvent event) {
        ItemStack[] items = event.getInventory().getMatrix();
        boolean cannotCraft = ItemManager.INSTANCE.containsCannotCraftItem(items);
        if (cannotCraft) {
            event.getInventory().setResult(null);
        }
    }

}
