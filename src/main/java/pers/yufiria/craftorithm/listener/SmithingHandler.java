package pers.yufiria.craftorithm.listener;

import pers.yufiria.craftorithm.CraftorithmAPI;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.ItemUtils;
import crypticlib.listener.EventListener;
import crypticlib.util.InventoryViewHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

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
        NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemId(result, true);
        if (resultId != null) {
            ItemStack refreshItem = ItemManager.INSTANCE.matchItem(resultId, (Player) event.getViewers().get(0));
            if (!result.isSimilar(refreshItem)) {
                result.setItemMeta(refreshItem.getItemMeta());
            }
        }

        //todo 保留附魔的选项
//        boolean copyEnchantment = RecipeManager.INSTANCE.getSmithingCopyEnchantment(recipe);
//        if (copyEnchantment) {
//            ItemStack base = event.getInventory().getItem(1);
//            if (base.hasItemMeta()) {
//                Map<Enchantment, Integer> baseEnchantments = base.getItemMeta().getEnchants();
//                ItemMeta resultMeta = result.getItemMeta();
//                Map<Enchantment, Integer> resultEnchantments = new HashMap<>(resultMeta.getEnchants());
//                CollectionsUtils.putAllIf(resultEnchantments, baseEnchantments, (type, level) -> {
//                    if (resultEnchantments.containsKey(type)) {
//                        return level > resultEnchantments.get(type);
//                    } else {
//                        return true;
//                    }
//                });
//                resultMeta.getEnchants().forEach(
//                    (enchant, level) -> {
//                        resultMeta.removeEnchant(enchant);
//                    }
//                );
//                resultEnchantments.forEach((enchant, level) -> {
//                    resultMeta.addEnchant(enchant, level, true);
//                });
//                result.setItemMeta(resultMeta);
//            }
//        }
        event.setResult(result);
        event.getInventory().setResult(result);
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
