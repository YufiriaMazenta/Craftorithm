package pers.yufiria.craftorithm.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessorManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessors;
import pers.yufiria.craftorithm.util.EventUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@EventListener
public enum SmithingHandler implements Listener {

    INSTANCE;

    /**
     * 处理配方结果
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void processResult(PrepareSmithingEvent event) {
        if (event.getResult() == null)
            return;
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null)
            return;
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }


        AtomicReference<ItemStack> result = new AtomicReference<>(event.getResult());
        if (ItemHelper.isAir(result.get())) {
            //如果结果是air,如果接着往下设置,会报错
            return;
        }
        ItemManager.INSTANCE.matchItemId(result.get(), true)
            .flatMap(id -> EventUtils.getViewer(event)
                .flatMap(player -> ItemManager.INSTANCE.matchItem(id, player))
            )
            .ifPresent(refreshItem -> {
                if (!result.get().isSimilar(refreshItem)) {
                    result.get().setItemMeta(refreshItem.getItemMeta());
                }
            });

        //处理结果处理器
        Optional<ResultProcessors> recipeProcessors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
        recipeProcessors.ifPresentOrElse(
            rules -> {
                ItemStack base = event.getInventory().getItem(1);
                rules.processItem(base, result.get());
            },
            () -> {
                result.get().setItemMeta(recipe.getResult().getItemMeta());
            }
        );

        event.setResult(result.get());
        event.getInventory().setResult(result.get());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkCannotCraft(PrepareSmithingEvent event) {
        ItemStack[] items = event.getInventory().getContents();
        boolean cannotCraft = ItemManager.INSTANCE.containsCannotCraftItem(items);
        if (cannotCraft) {
            event.getInventory().setResult(null);
            event.setResult(null);
        }
    }

}
