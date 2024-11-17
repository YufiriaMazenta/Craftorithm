package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtils;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.listener.EventListener;
import crypticlib.util.InventoryViewHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventListener
public enum SmithingHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshResult(PrepareSmithingEvent event) {
        if (event.getResult() == null)
            return;
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null)
            return;
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }

        ItemStack result = event.getResult();
        String id = ItemManager.INSTANCE.matchItemName(result, false);
        if (id != null) {
            ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id, (Player) event.getViewers().get(0));
            if (!result.isSimilar(refreshItem)) {
                result.setItemMeta(refreshItem.getItemMeta());
            }
        }

        boolean copyEnchantment = RecipeManager.INSTANCE.getSmithingCopyEnchantment(recipe);
        if (copyEnchantment) {
            ItemStack base = event.getInventory().getItem(1);
            if (base.hasItemMeta()) {
                Map<Enchantment, Integer> baseEnchantments = base.getItemMeta().getEnchants();
                ItemMeta resultMeta = result.getItemMeta();
                Map<Enchantment, Integer> resultEnchantments = new HashMap<>(resultMeta.getEnchants());
                CollectionsUtils.putAllIf(resultEnchantments, baseEnchantments, (type, level) -> {
                    if (resultEnchantments.containsKey(type)) {
                        return level > resultEnchantments.get(type);
                    } else {
                        return true;
                    }
                });
                resultMeta.getEnchants().forEach(
                    (enchant, level) -> {
                        resultMeta.removeEnchant(enchant);
                    }
                );
                resultEnchantments.forEach((enchant, level) -> {
                    resultMeta.addEnchant(enchant, level, true);
                });
                result.setItemMeta(resultMeta);
            }
        }
        event.setResult(result);
        event.getInventory().setResult(result);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void runConditions(PrepareSmithingEvent event) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(event.getInventory().getRecipe());
        if (recipeKey == null)
            return;
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(recipeKey);
        if (config == null)
            return;

        Object inventoryView = InventoryViewHelper.getInventoryView(event);
        Player player = (Player) InventoryViewHelper.getPlayer(inventoryView);
        String condition = config.getString("condition", "true");
        condition = "if " + condition;
        boolean result = (boolean) ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, condition).obj();
        if (!result) {
            event.setResult(null);
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void runActions(SmithItemEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(event.getInventory().getRecipe());
        if (recipeKey == null)
            return;
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(recipeKey);
        if (config == null)
            return;
        Player player = (Player) entity;
        List<String> actions = config.getStringList("actions");
        CraftorithmAPI.INSTANCE.arcencielDispatcher().dispatchArcencielFunc(player, actions);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkCannotCraftLore(PrepareSmithingEvent event) {
        ItemStack[] items = event.getInventory().getContents();
        boolean containsLore = ItemUtils.hasCannotCraftLore(items);
        if (containsLore) {
            event.getInventory().setResult(null);
            event.setResult(null);
        }
    }

}
