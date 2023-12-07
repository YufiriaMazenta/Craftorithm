package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import crypticlib.listener.BukkitListener;
import crypticlib.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@BukkitListener
public class AnvilHandler implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;
        ItemStack base = event.getInventory().getFirstItem();
        ItemStack addition = event.getInventory().getSecondItem();
        if (ItemUtil.isAir(base) || ItemUtil.isAir(addition))
            return;
        AnvilRecipe anvilRecipe = RecipeManager.INSTANCE.matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;
        ItemStack result = anvilRecipe.result();
        if (anvilRecipe.copyNbt()) {
            if (base.hasItemMeta())
                result.setItemMeta(base.getItemMeta());
        }
        event.getInventory().setRepairCost(anvilRecipe.costLevel());
        event.setResult(anvilRecipe.getResult());
    }

    @EventHandler
    public void onClickAnvil(InventoryClickEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;

        if (!event.getInventory().getType().equals(InventoryType.ANVIL))
            return;
        AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
        if (event.getSlot() != 2)
            return;
        ItemStack base = anvilInventory.getFirstItem();
        ItemStack addition = anvilInventory.getSecondItem();
        ItemStack result = anvilInventory.getResult();
        if (ItemUtil.isAir(base) || ItemUtil.isAir(addition) || ItemUtil.isAir(result))
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
        switch (event.getClick()) {
            case LEFT:
            case RIGHT:
            case DOUBLE_CLICK:
                if (player.getLevel() < costLevel)
                    return;
                ItemStack cursor = event.getCursor();
                if (ItemUtil.isAir(cursor)) {
                    base.setAmount(baseNum - needBaseNum);
                    addition.setAmount(additionNum - needAdditionNum);
                    event.getView().setCursor(result);
                    player.setLevel(player.getLevel() - costLevel);
                } else {
                    int resultCursor = cursor.getAmount() + result.getAmount();
                    if (resultCursor > result.getMaxStackSize())
                        break;
                    base.setAmount(baseNum - needBaseNum);
                    addition.setAmount(additionNum - needAdditionNum);
                    event.getView().getCursor().setAmount(resultCursor);
                    player.setLevel(player.getLevel() - costLevel);
                }
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
                break;
            case DROP:
                if (player.getLevel() < costLevel)
                    break;
                base.setAmount(baseNum - needBaseNum);
                addition.setAmount(additionNum - needAdditionNum);
                player.setLevel(player.getLevel() - costLevel);
                player.getWorld().dropItem(event.getWhoClicked().getLocation(), result);
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
                break;
            default:
                break;
        }

        //更新页面
        AnvilRecipe afterClickRecipe = RecipeManager.INSTANCE.matchAnvilRecipe(base, addition);
        if (afterClickRecipe == null) {
            anvilInventory.setResult(null);
            return;
        }
        anvilInventory.setResult(afterClickRecipe.getResult());
        anvilInventory.setRepairCost(anvilRecipe.costLevel());
    }

}
