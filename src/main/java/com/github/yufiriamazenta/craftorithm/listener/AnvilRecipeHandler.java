package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.event.PrepareAnvilRecipeEvent;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.listener.BukkitListener;
import crypticlib.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@BukkitListener
public enum AnvilRecipeHandler implements Listener {

    INSTANCE;
    private final Map<UUID, AnvilRecipe> prepareAnvilRecipeMap;

    AnvilRecipeHandler() {
        prepareAnvilRecipeMap = new ConcurrentHashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvilFireEvent(PrepareAnvilEvent event) {
        if (!Craftorithm.getInstance().getConfig().getBoolean("enable_anvil_recipe", true))
            return;
        ItemStack base = event.getInventory().getItem(0);
        ItemStack addition = event.getInventory().getItem(1);
        AnvilRecipe anvilRecipe = RecipeManager.matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;
        PrepareAnvilRecipeEvent.instance(event, anvilRecipe).call();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnvilRecipeRename(PrepareAnvilRecipeEvent event) {
        PrepareAnvilEvent bukkitEvent = event.getBukkitAnvilEvent();
        String renameStr = bukkitEvent.getInventory().getRenameText();
        ItemStack result = event.getResult();
        Player player = (Player) bukkitEvent.getView().getPlayer();
        if (renameStr != null && !renameStr.isEmpty()) {
            ItemMeta meta = result.getItemMeta();
            if (meta != null) {
                renameStr = LangUtil.placeholder(player, renameStr);
                if (player.hasPermission("craftorithm.anvil.color"))
                    renameStr = TextUtil.color(renameStr);
                meta.setDisplayName(renameStr);
                result.setItemMeta(meta);
            }
        }
        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleAnvilRecipeEvent(PrepareAnvilRecipeEvent event) {
        PrepareAnvilEvent bukkitEvent = event.getBukkitAnvilEvent();
        bukkitEvent.setResult(event.getResult());
        bukkitEvent.getInventory().setRepairCost(event.getCostLevel());
        Location blockLocation = bukkitEvent.getInventory().getLocation();
        if (blockLocation == null)
            return;
        prepareAnvilRecipeMap.put(bukkitEvent.getView().getPlayer().getUniqueId(), event.getRecipe());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCloseAnvil(InventoryCloseEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory))
            return;
        prepareAnvilRecipeMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        prepareAnvilRecipeMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClickTakeItem(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof AnvilInventory))
            return;
        Player player = (Player) event.getWhoClicked();
        if (!prepareAnvilRecipeMap.containsKey(player.getUniqueId()))
            return;
        if (event.getSlot() != 2)
            return;
        AnvilRecipe recipe = prepareAnvilRecipeMap.get(player.getUniqueId());
        int slot1Cost = recipe.getBase().getCost();
        int slot2Cost = recipe.getAddition().getCost();
        ItemStack base = event.getInventory().getItem(0);
        ItemStack addition = event.getInventory().getItem(1);
        if (base == null || addition == null) {
            throw new RuntimeException("Raw material is null");
        }
        event.setCancelled(true);
        ItemStack cursor = event.getCursor();
        ItemStack result = recipe.getResult();
        if (cursor != null && !cursor.getType().equals(Material.AIR)) {
            if (cursor.isSimilar(recipe.getResult())) {
                int cursorAmount = cursor.getAmount();
                int resultAmount = result.getAmount();
                int sumAmount = cursorAmount + resultAmount;
                int maxStackSize = result.getMaxStackSize();
                if (sumAmount > maxStackSize) {
                    return;
                } else {
                    cursor.setAmount(sumAmount);
                    event.getView().setCursor(cursor);
                }
            } else {
                return;
            }
        } else {
            event.getView().setCursor(recipe.getResult());
        }
        base.setAmount(base.getAmount() - slot1Cost);
        addition.setAmount(addition.getAmount() - slot2Cost);
        prepareAnvilRecipeMap.remove(player.getUniqueId());
    }

}
