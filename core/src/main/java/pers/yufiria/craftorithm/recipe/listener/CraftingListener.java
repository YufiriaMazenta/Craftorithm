package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessors;
import pers.yufiria.craftorithm.util.EventUtils;

import java.util.Optional;

@EventListener
public enum CraftingListener implements Listener {

    INSTANCE;

    /**
     * 材料限制对所有命名空间生效。
     * 原版配方不经过本插件的 NMS 配方类，只在这里拦。
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void processResult(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            return;
        }
        CraftingInventory inventory = event.getInventory();
        // 拦截指定材料用于合成，这里主要是拦截其他插件和原版的配方
        if (!ItemManager.INSTANCE.canCraft(inventory.getMatrix(), recipeKey)) {
            inventory.setResult(null);
            return;
        }
        if (recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            processResultItem(event, recipe.getResult(), recipeKey);
        }
    }

    private void processResultItem(PrepareItemCraftEvent event, ItemStack item, NamespacedKey recipeKey) {
        ItemStack result = item.clone();
        // 重新从物品源获取物品, 刷新结果的组件
        ItemManager.INSTANCE.refreshItem(result, EventUtils.getViewer(event).orElse(null));
        // 处理结果处理器（工作台配方没有sourceItem）
        Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
        Player processorPlayer = EventUtils.getViewer(event).orElse(null);
        processors.ifPresent(p -> p.processItem(null, result, processorPlayer));
        event.getInventory().setResult(result);
    }

}
