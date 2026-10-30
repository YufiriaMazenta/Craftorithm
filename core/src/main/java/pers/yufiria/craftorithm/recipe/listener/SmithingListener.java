package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingInventory;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessors;
import pers.yufiria.craftorithm.util.EventUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@EventListener
public enum SmithingListener implements Listener {

    INSTANCE;

    /**
     * 材料限制与结果处理。
     * 材料限制对所有命名空间生效（原版锻造配方不经过本插件的 NMS 配方类），结果处理仍只针对本插件配方。
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void processResult(PrepareSmithingEvent event) {
        if (event.getResult() == null)
            return;
        SmithingInventory inventory = event.getInventory();
        Recipe recipe = inventory.getRecipe();
        if (recipe == null)
            return;
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null)
            return;

        ItemStack[] ingredients = new ItemStack[]{inventory.getItem(0), inventory.getItem(1), inventory.getItem(2)};
        // 拦截指定的材料用于锻造，这里主要是拦截其他插件和原版的配方
        if (!ItemManager.INSTANCE.canCraft(ingredients, recipeKey)) {
            event.setResult(null);
            inventory.setResult(null);
            return;
        }

        if (!recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }

        AtomicReference<ItemStack> result = new AtomicReference<>(event.getResult());
        if (ItemHelper.isAir(result.get())) {
            //如果结果是air,如果接着往下设置,会报错
            return;
        }

        ItemManager.INSTANCE.refreshItem(result.get(), EventUtils.getViewer(event).orElse(null));

        // 运行结果处理器
        Optional<ResultProcessors> recipeProcessors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
        Player processorPlayer = EventUtils.getViewer(event).orElse(null);
        recipeProcessors.ifPresent(
            rules -> {
                ItemStack base = inventory.getItem(1);
                rules.processItem(base, result.get(), processorPlayer);
            }
        );

        event.setResult(result.get());
        inventory.setResult(result.get());
    }

}
