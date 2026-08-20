package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.MinecraftVersion;
import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.register.BrewingRecipeRegister;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessorManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventListener
public enum BrewingListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void rebuildBrewResult(BrewEvent event) {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V26_1)) {
            List<ItemStack> results = event.getResults();
            //因为paper在26.1开始, PotionBrewing.mix方法必须有原材料有药水组件才能使用,所以需要自行匹配
            BrewerInventory brewerInventory = event.getContents();
            ItemStack ingredient = brewerInventory.getIngredient();
            for (int i = 0; i < 3; i++) {
                ItemStack input = brewerInventory.getItem(i);
                if (ItemHelper.isAir(input)) {
                    continue;
                }
                int finalI = i;
                BrewingRecipeRegister.INSTANCE.mix(input, ingredient).ifPresent(
                    potionMix -> {
                        results.set(finalI, potionMix.getResult());
                    }
                );
            }
        }
    }

    /**
     * 刷新酿造的结果
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void processResult(BrewEvent event) {
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            //如果服务器根本不支持酿造配方,那么也就没有必要处理酿造的结果
            return;
        }
        List<ItemStack> results = event.getResults();
        List<ItemStack> resultsClone = new ArrayList<>(results);
        BrewerInventory brewerInventory = event.getContents();
        ItemStack ingredient = brewerInventory.getIngredient();
        for (int i = 0; i < resultsClone.size(); i++) {
            ItemStack result = resultsClone.get(i);
            ItemStack input = brewerInventory.getItem(i);
            //重新从物品源获取物品, 刷新结果的组件
            ItemStack refreshItem = ItemManager.INSTANCE.matchItemId(result, true)
                .flatMap(ItemManager.INSTANCE::matchItem)
                .orElse(null);
            if (input != null) {
                Optional<NamespacedKey> recipeKeyOpt = BrewingRecipeRegister.INSTANCE.mixKey(input, ingredient);
                if (recipeKeyOpt.isPresent()) {
                    NamespacedKey recipeKey = recipeKeyOpt.get();
                    // 检查 blocked_crafting_lore_rules
                    if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{input, ingredient}, recipeKey)) {
                        continue;
                    }
                    // lore检查通过后再刷新结果
                    if (refreshItem != null && !result.isSimilar(refreshItem)) {
                        result.setItemMeta(refreshItem.getItemMeta());
                    }
                    // 运行结果处理器（酿造配方的source是输入物品）
                    Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
                    processors.ifPresent(p -> p.processItem(input, result, null));
                }
            }
            results.set(i, result);
        }
    }

}
