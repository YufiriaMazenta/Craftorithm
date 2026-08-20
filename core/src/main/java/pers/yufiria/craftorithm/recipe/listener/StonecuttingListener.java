package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessorManager;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessors;

import java.util.Optional;

/**
 * 用于处理切石配方相关的事件
 * 仅在paper及衍生端有效
 */
@EventListener
public enum StonecuttingListener implements Listener {

    INSTANCE;

    /**
     * 为配方结果运行结果处理器
     */
    @EventHandler
    public void processResult(PlayerStonecutterRecipeSelectEvent event) {
        StonecutterInventory stonecutterInventory = event.getStonecutterInventory();
        CrypticLibBukkit.scheduler().runOnLocation(event.getPlayer().getLocation(), () -> {
            ItemStack result = stonecutterInventory.getResult();
            if (ItemHelper.isAir(result)) {
                return;
            }
            StonecuttingRecipe recipe = event.getStonecuttingRecipe();
            NamespacedKey recipeKey = recipe.getKey();
            // 检查 blocked_crafting_lore_rules
            ItemStack base = stonecutterInventory.getInputItem();
            if (!ItemHelper.isAir(base) && !ItemManager.INSTANCE.canCraft(new ItemStack[]{base}, recipeKey)) {
                stonecutterInventory.setResult(null);
                return;
            }
            // lore检查通过后再刷新结果
            ItemManager.INSTANCE.matchItemId(result, true)
                .flatMap(ItemManager.INSTANCE::matchItem)
                .ifPresent(refreshItem -> {
                    result.setItemMeta(refreshItem.getItemMeta());
                });
            //运行结果处理器
            Optional<ResultProcessors> recipeProcessors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
            Player processorPlayer = event.getPlayer();
            recipeProcessors.ifPresent(
                rules -> {
                    rules.processItem(base, result, processorPlayer);
                }
            );
            stonecutterInventory.setResult(result);
        });
    }

}
