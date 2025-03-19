package pers.yufiria.craftorithm.listener;

import org.bukkit.inventory.meta.ItemMeta;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtManager;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtRules;
import pers.yufiria.craftorithm.util.ItemUtils;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Objects;
import java.util.Optional;

@EventListener
public enum SmithingHandler implements Listener {

    INSTANCE;

    /**
     * 预处理配方结果
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void preprocessResult(PrepareSmithingEvent event) {
        if (event.getResult() == null)
            return;
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null)
            return;
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
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

        //处理NBT保留操作
        Optional<KeepNbtRules> recipeKeepNbtRules = KeepNbtManager.INSTANCE.getRecipeKeepNbtRules(recipeKey);
        if (recipeKeepNbtRules.isPresent()) {
            ItemMeta resultMeta = result.getItemMeta();
            ItemStack base = event.getInventory().getItem(1);
            ItemMeta baseMeta = Objects.requireNonNull(base).getItemMeta();
            resultMeta = recipeKeepNbtRules.get().processItemMeta(resultMeta, baseMeta);
            result.setItemMeta(resultMeta);
        }

        event.setResult(result);
        event.getInventory().setResult(result);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkCannotCraftLore(PrepareSmithingEvent event) {
        //TODO 修改此内容
        ItemStack[] items = event.getInventory().getContents();
        boolean containsLore = ItemUtils.hasCannotCraftLore(items);
        if (containsLore) {
            event.getInventory().setResult(null);
            event.setResult(null);
        }
    }

}
