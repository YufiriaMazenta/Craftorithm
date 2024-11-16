package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RecipeListMenu extends Menu {

    private int page;
    private final int maxPage;
    private final List<Recipe> recipeList;
    private final Menu parentMenu;

    public RecipeListMenu(Player player, Collection<NamespacedKey> recipeKeys) {
        this(player, recipeKeys, null);
    }

    public RecipeListMenu(Player player, Collection<NamespacedKey> recipeKeys, Menu parentMenu) {
        super(player);
        this.parentMenu = parentMenu;
        this.recipeList = new ArrayList<>();
        for (NamespacedKey key : recipeKeys) {
            recipeList.add(RecipeManager.INSTANCE.getRecipe(key));
        }
        page = 0;
        recipeList.removeIf(recipe -> {
            if (recipe == null)
                return true;
            if (recipe instanceof SmithingTrimRecipe)
                return true;
            return recipe.getResult().getType().equals(Material.AIR);
        });
        int recipeNum = recipeList.size();
        if (recipeNum % 45 == 0)
            maxPage = recipeNum / 45;
        else
            maxPage = recipeNum / 45 + 1;
        recipeList.sort((recipe1, recipe2) -> {
            ItemStack result = recipe1.getResult();
            ItemStack result1 = recipe2.getResult();
            return Integer.compare(result.getType().ordinal(), result1.getType().ordinal());
        });
    }

    public void nextPage() {
        setPage(Math.min(page + 1, Math.max(0, maxPage - 1))).resetIcons();
        inventoryCache.clear();
        for (Integer slot : slotMap.keySet()) {
            inventoryCache.setItem(slot, slotMap.get(slot).display());
        }
    }

    public void previousPage() {
        setPage(Math.max(page - 1, 0)).resetIcons();
        inventoryCache.clear();
        for (Integer slot : slotMap.keySet()) {
            inventoryCache.setItem(slot, slotMap.get(slot).display());
        }
    }

    private void resetIcons() {
        slotMap.clear();
        int []frame = {45, 47, 48, 49, 50, 51, 53};
        Icon frameIcon = new Icon(
            new IconDisplay(
                Material.BLACK_STAINED_GLASS_PANE,
                BukkitTextProcessor.color(Languages.MENU_RECIPE_LIST_ICON_FRAME.value(player))
            )
        );
        for (int i : frame) {
            slotMap.put(i, frameIcon);
        }
        slotMap.put(46, new Icon(
            new IconDisplay(
                Material.PAPER,
                BukkitTextProcessor.color(Languages.MENU_RECIPE_LIST_ICON_PREVIOUS.value(player))
            )
        ).setClickAction(event -> previousPage()));
        slotMap.put(52, new Icon(
            new IconDisplay(
                Material.PAPER,
                BukkitTextProcessor.color(Languages.MENU_RECIPE_LIST_ICON_NEXT.value(player))
            )
        ).setClickAction(event -> nextPage()));
        int recipeSlot = page * 45;
        for (int i = 0; i < 45 && recipeSlot < recipeList.size(); i++, recipeSlot ++) {
            Recipe recipe = recipeList.get(recipeSlot);
            if (recipe == null)
                continue;
            ItemStack display = recipe.getResult();
            slotMap.put(i, new Icon(display).setClickAction(event -> new RecipeDisplayMenu(player, recipe, this).openMenu()));
        }
        for (int i = 0; i < 45; i++) {
            if (slotMap.containsKey(i))
                continue;
            slotMap.put(i, new Icon(new ItemStack(Material.AIR)));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (parentMenu != null) {
            CrypticLibBukkit.scheduler().runTask(
                Craftorithm.instance(),
                () -> {
                    InventoryType type = event.getPlayer().getOpenInventory().getType();
                    List<InventoryType> typeWhenNotOpenInv = Arrays.asList(InventoryType.CRAFTING, InventoryType.CREATIVE);
                    if (!typeWhenNotOpenInv.contains(type))
                        return;
                    parentMenu.openMenu();
                }
            );
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        resetIcons();
        Inventory inventory = Bukkit.createInventory(
            this,
            54,
            BukkitTextProcessor.color(Languages.MENU_RECIPE_LIST_TITLE.value(player))
        );
        for (Integer slot : slotMap.keySet()) {
            inventory.setItem(slot, slotMap.get(slot).display());
        }
        return inventory;
    }

    public int page() {
        return page;
    }

    public RecipeListMenu setPage(int page) {
        this.page = page;
        return this;
    }

}
