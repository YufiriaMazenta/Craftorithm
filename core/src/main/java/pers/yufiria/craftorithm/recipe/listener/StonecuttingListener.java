package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessors;
import pers.yufiria.craftorithm.worldisolation.WorldIsolationDataHandler;

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
        Player player = event.getPlayer();
        CrypticLibBukkit.scheduler().runOnEntity(player, () -> {
            ItemStack result = stonecutterInventory.getResult();
            if (ItemHelper.isAir(result)) {
                return;
            }
            StonecuttingRecipe recipe = event.getStonecuttingRecipe();
            NamespacedKey recipeKey = recipe.getKey();
            ItemStack inputItem = stonecutterInventory.getInputItem();

            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21)) {
                // 1.20.X版本这两个拦截功能由nms层进行，故只在1.21以上进行这两个判断
                if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(
                    recipeKey,
                    player.getWorld()
                )) {
                    stonecutterInventory.setResult(null);
                    return;
                }
                if (!ItemHelper.isAir(inputItem) && !ItemManager.INSTANCE.canCraft(new ItemStack[]{inputItem}, recipeKey)) {
                    stonecutterInventory.setResult(null);
                    return;
                }
            }

            // 物品检查通过后再刷新结果
            ItemManager.INSTANCE.matchItemId(result, true)
                .flatMap(ItemManager.INSTANCE::matchItem)
                .ifPresent(refreshItem -> {
                    result.setItemMeta(refreshItem.getItemMeta());
                });
            //运行结果处理器
            Optional<ResultProcessors> recipeProcessors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
            recipeProcessors.ifPresent(
                rules -> {
                    rules.processItem(inputItem, result, player);
                }
            );
            stonecutterInventory.setResult(result);
        });
    }

}
