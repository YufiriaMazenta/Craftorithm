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
        ItemStack[] items = crafter.getInventory().getContents();
        // 检查 blocked_crafting_lore_rules
        Recipe recipe = event.getRecipe();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey != null && !ItemManager.INSTANCE.canCraft(items, recipeKey)) {
            event.setCancelled(true);
        }
    }

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
            .flatMap(ItemManager.INSTANCE::matchItem)
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
