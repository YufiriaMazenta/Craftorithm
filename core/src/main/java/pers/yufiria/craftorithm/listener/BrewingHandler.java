package pers.yufiria.craftorithm.listener;

import crypticlib.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@EventListener
public enum BrewingHandler implements Listener {

    INSTANCE;

    /**
     * 刷新酿造的结果
     */
    @EventHandler
    public void refreshBrewResult(BrewEvent event) {
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            //如果服务器根本不支持酿造配方,那么也就没有必要处理酿造的结果
            return;
        }
        List<ItemStack> results = event.getResults();
        List<ItemStack> resultsClone = new ArrayList<>(results);
        for (int i = 0; i < resultsClone.size(); i++) {
            ItemStack result = resultsClone.get(i);
            ItemStack refreshItem = ItemManager.INSTANCE.matchItemId(result, true)
                .flatMap(ItemManager.INSTANCE::matchItem)
                .orElse(null);
            if (refreshItem == null || result.isSimilar(refreshItem)) {
                return;
            }
            results.set(i, refreshItem);
        }
    }

}
