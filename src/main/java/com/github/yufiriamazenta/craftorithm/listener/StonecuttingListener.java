package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import crypticlib.util.IOHelper;
import crypticlib.util.ItemHelper;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
public class StonecuttingListener implements Listener {

    private final Map<UUID, StonecuttingRecipe> playerSelectedStonecuttingRecipeKeyMap = new ConcurrentHashMap<>();

    @EventHandler
    public void onSelectStonecutting(PlayerStonecutterRecipeSelectEvent event) {
        if (!PluginConfigs.ENABLE_STONECUTTER_ACTIONS.value()) {
            return;
        }
        playerSelectedStonecuttingRecipeKeyMap.put(event.getPlayer().getUniqueId(), event.getStonecuttingRecipe());
    }

    @EventHandler
    public void onStonecutting(InventoryClickEvent event) {
        if (!PluginConfigs.ENABLE_STONECUTTER_ACTIONS.value()) {
            return;
        }
        if (!(event.getInventory() instanceof StonecutterInventory stonecutterInventory)) {
            return;
        }
        if (event.getSlot() != 1) {
            return;
        }
        ItemStack ingredient = stonecutterInventory.getItem(0);
        if (ItemHelper.isAir(ingredient)) {
            return;
        }
        StonecuttingRecipe recipe;
        Player player = ((Player) event.getWhoClicked());
        StonecuttingRecipe selectedRecipeCache = playerSelectedStonecuttingRecipeKeyMap.get(player.getUniqueId());
        if (selectedRecipeCache == null) {
            return;
        }

        //获取此次切石的配方
        ItemStack invResult = stonecutterInventory.getItem(1);
        String invResultItemName = ItemUtils.matchItemNameOrVanilla(invResult, false);
        IOHelper.debug("Inventory result item name: " + invResultItemName);
        ItemStack recipeResult = selectedRecipeCache.getResult();
        String recipeResultItemName = ItemUtils.matchItemNameOrVanilla(recipeResult, false);
        IOHelper.debug("Recipe result item name: " + recipeResultItemName);
        if (recipeResultItemName == null || invResultItemName == null) {
            return;
        }
        if (recipeResultItemName.equalsIgnoreCase(invResultItemName)) {
            recipe = selectedRecipeCache;
        } else {
            return;
        }

        //存储切制前的材料数量,用于判断合成次数
        int oldIngredientNum = ingredient.getAmount();
        CrypticLibBukkit.scheduler().sync(() -> {
            ItemStack newIngredient = stonecutterInventory.getItem(0);
            int usedIngredientNum;
            if (ItemHelper.isAir(newIngredient)) {
                usedIngredientNum = oldIngredientNum;
            } else {
                usedIngredientNum = oldIngredientNum - newIngredient.getAmount();
            }
            IOHelper.debug("Stone cutting num: " + usedIngredientNum);
            NamespacedKey recipeKey = recipe.getKey();
            YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(recipeKey);
            if (config == null)
                return;
            List<String> actions = config.getStringList("actions");
            for (int i = 0; i < usedIngredientNum; i++) {
                CraftorithmAPI.INSTANCE.arcencielDispatcher().dispatchArcencielFunc(player, actions);
            }
        });
    }

    @EventHandler
    public void onCloseInvResetSelectedRecipe(InventoryCloseEvent event) {
        if (!PluginConfigs.ENABLE_STONECUTTER_ACTIONS.value()) {
            return;
        }
        playerSelectedStonecuttingRecipeKeyMap.remove(event.getPlayer().getUniqueId());
    }

}
