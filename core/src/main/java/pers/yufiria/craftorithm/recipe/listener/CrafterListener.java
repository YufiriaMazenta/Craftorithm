package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
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
        ItemManager.INSTANCE.refreshItem(result, null);

        Recipe recipe = event.getRecipe();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey != null) {
            // 运行结果处理器
            Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
            processors.ifPresent(p -> p.processItem(null, result, null));
            // 因为合成器没有什么比较方便的方法获取材料物品，这里暂时不做拦截材料相关逻辑，所以只有Craftorithm的配方才能拦截被限制的配方
        }
        event.setResult(result);
    }

}
