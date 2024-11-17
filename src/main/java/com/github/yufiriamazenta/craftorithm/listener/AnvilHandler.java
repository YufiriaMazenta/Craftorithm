package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtils;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.listener.EventListener;
import crypticlib.util.InventoryViewHelper;
import crypticlib.util.ItemHelper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventListener
public enum AnvilHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void runConditions(PrepareAnvilEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;
        ItemStack base = event.getInventory().getItem(0);
        ItemStack addition = event.getInventory().getItem(1);
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
            return;
        boolean containsLore = ItemUtils.hasCannotCraftLore(base, addition);
        if (containsLore) {
            event.setResult(null);
            return;
        }
        
        AnvilRecipe anvilRecipe = RecipeManager.INSTANCE.matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;

        //进行condition判断
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(anvilRecipe.key());
        if (config != null) {
            Object inventoryView = InventoryViewHelper.getInventoryView(event);
            Player player = (Player) InventoryViewHelper.getPlayer(inventoryView);
            String condition = config.getString("condition", "true");
            condition = "if " + condition;
            boolean conditionResult = (boolean) ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, condition).obj();
            if (!conditionResult) {
                event.getInventory().setRepairCost(0);
                event.setResult(null);
                event.getInventory().setItem(2, null);
                return;
            }
        }

        //正常合成流程
        ItemStack result = anvilRecipe.result();
        String resultId = ItemManager.INSTANCE.matchItemName(result, false);
        if (resultId != null) {
            ItemStack refreshItem = ItemManager.INSTANCE.matchItem(resultId, (Player) event.getViewers().get(0));
            result.setItemMeta(refreshItem.getItemMeta());
        }
        if (anvilRecipe.copyNbt()) {
            if (base.hasItemMeta())
                result.setItemMeta(base.getItemMeta());
        }
        if (anvilRecipe.copyEnchantments()) {
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
        event.getInventory().setRepairCost(anvilRecipe.costLevel());
        //刷新物品

        event.setResult(result);
        event.getInventory().setItem(2, result);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void matchAnvilResult(InventoryClickEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;

        if (!(event.getInventory() instanceof AnvilInventory))
            return;
        AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
        if (event.getSlot() != 2)
            return;
        ItemStack base = anvilInventory.getItem(0);
        ItemStack addition = anvilInventory.getItem(1);
        ItemStack result = anvilInventory.getItem(2);
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition) || ItemHelper.isAir(result))
            return;
        AnvilRecipe anvilRecipe = RecipeManager.INSTANCE.matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;
        Player player = (Player) event.getWhoClicked();
        if (anvilRecipe.copyNbt()) {
            if (base.hasItemMeta())
                result.setItemMeta(base.getItemMeta());
        }
        int baseNum = base.getAmount(), additionNum = addition.getAmount();
        int needBaseNum = anvilRecipe.base().getAmount(), needAdditionNum = anvilRecipe.addition().getAmount();
        int costLevel = anvilRecipe.costLevel();
        event.setCancelled(true);
        int canCraftNum = Math.min(baseNum / needBaseNum, additionNum / needAdditionNum);
        canCraftNum = Math.min(result.getMaxStackSize(), canCraftNum);
        boolean craftResult = false;
        switch (event.getClick()) {
            case LEFT:
            case RIGHT:
            case DOUBLE_CLICK:
                if (player.getLevel() < costLevel) {
                    break;
                }
                ItemStack cursor = event.getCursor();
                if (ItemHelper.isAir(cursor)) {
                    base.setAmount(baseNum - needBaseNum);
                    addition.setAmount(additionNum - needAdditionNum);
                    event.setCursor(result);
                    player.setLevel(player.getLevel() - costLevel);
                } else {
                    int resultCursor = cursor.getAmount() + result.getAmount();
                    if (resultCursor > result.getMaxStackSize())
                        break;
                    base.setAmount(baseNum - needBaseNum);
                    addition.setAmount(additionNum - needAdditionNum);
                    event.getCursor().setAmount(resultCursor);
                    player.setLevel(player.getLevel() - costLevel);
                }
                craftResult = true;
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                int costAmount1 = needBaseNum * canCraftNum;
                int costAmount2 = needAdditionNum * canCraftNum;
                int finalCostLevel = costLevel * canCraftNum;
                if (player.getLevel() < finalCostLevel)
                    break;
                base.setAmount(baseNum - costAmount1);
                addition.setAmount(additionNum - costAmount2);
                result.setAmount(canCraftNum * result.getAmount());
                HashMap<Integer, ItemStack> failedItems = event.getWhoClicked().getInventory().addItem(result);
                player.setLevel(player.getLevel() - finalCostLevel);
                if (failedItems.isEmpty())
                    break;
                for (ItemStack value : failedItems.values()) {
                    player.getWorld().dropItem(event.getWhoClicked().getLocation(), value);
                }
                craftResult = true;
                break;
            case DROP:
                if (player.getLevel() < costLevel)
                    break;
                base.setAmount(baseNum - needBaseNum);
                addition.setAmount(additionNum - needAdditionNum);
                player.setLevel(player.getLevel() - costLevel);
                player.getWorld().dropItem(event.getWhoClicked().getLocation(), result);
                craftResult = true;
                break;
            case CONTROL_DROP:
                int costAmount11 = needBaseNum * canCraftNum;
                int costAmount22 = needAdditionNum * canCraftNum;
                int finalCostLevel2 = costLevel * canCraftNum;
                if (player.getLevel() < finalCostLevel2)
                    break;
                base.setAmount(baseNum - costAmount11);
                addition.setAmount(additionNum - costAmount22);
                result.setAmount(canCraftNum * result.getAmount());
                player.setLevel(player.getLevel() - finalCostLevel2);
                player.getWorld().dropItem(event.getWhoClicked().getLocation(), result);
                craftResult = true;
                break;
            default:
                break;
        }

        //执行动作
        if (craftResult) {
            YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(anvilRecipe.key());
            if (config != null) {
                List<String> actions = config.getStringList("actions");
                CraftorithmAPI.INSTANCE.arcencielDispatcher().dispatchArcencielFunc(player, actions);
            }
        }

        //更新页面
        AnvilRecipe afterClickRecipe = RecipeManager.INSTANCE.matchAnvilRecipe(base, addition);
        if (afterClickRecipe == null) {
            anvilInventory.setItem(2, null);
            anvilInventory.setRepairCost(0);
            return;
        }
        anvilInventory.setItem(2, afterClickRecipe.getResult());
        anvilInventory.setRepairCost(anvilRecipe.costLevel());
    }

}
