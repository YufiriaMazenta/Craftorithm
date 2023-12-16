package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.CrypticLib;
import crypticlib.chat.TextProcessor;
import crypticlib.ui.display.Icon;
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

public class RecipeListMenuHolder extends Menu {

    private int page;
    private final int maxPage;
    private final List<Recipe> recipeList;
    private final boolean hasEditPermission;
    private final Menu parentMenu;

    public RecipeListMenuHolder(Player player, Collection<NamespacedKey> recipeKeys) {
        this(player, recipeKeys, null);
    }

    public RecipeListMenuHolder(Player player, Collection<NamespacedKey> recipeKeys, Menu parentMenu) {
        super(player);
        this.parentMenu = parentMenu;
        this.hasEditPermission = player.hasPermission("craftorithm.recipe_list.manager");
        this.recipeList = new ArrayList<>();
        for (NamespacedKey key : recipeKeys) {
            recipeList.add(RecipeManager.INSTANCE.getRecipe(key));
        }
        page = 0;
        recipeList.removeIf(recipe -> {
            if (recipe == null)
                return true;
            if (CrypticLib.minecraftVersion() >= 12000) {
                if (recipe instanceof SmithingTrimRecipe)
                    return true;
            }
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
        setPage(Math.min(page + 1, maxPage - 1)).resetIcons();
        openedInventory().clear();
        for (Integer slot : slotMap().keySet()) {
            openedInventory().setItem(slot, slotMap().get(slot).display());
        }
    }

    public void previousPage() {
        setPage(Math.max(page - 1, 0)).resetIcons();
        openedInventory().clear();
        for (Integer slot : slotMap().keySet()) {
            openedInventory().setItem(slot, slotMap().get(slot).display());
        }
    }

    private void resetIcons() {
        slotMap().clear();
        int []frame = {45, 47, 48, 49, 50, 51, 53};
        Icon frameIcon = new Icon(Material.BLACK_STAINED_GLASS_PANE, TextProcessor.color(Languages.MENU_RECIPE_LIST_ICON_FRAME.value()));
        for (int i : frame) {
            slotMap().put(i, frameIcon);
        }
        slotMap().put(46, new Icon(Material.PAPER, TextProcessor.color(Languages.MENU_RECIPE_LIST_ICON_PREVIOUS.value()), (event -> {
            event.setCancelled(true);
            previousPage();
        })));
        slotMap().put(52, new Icon(Material.PAPER, TextProcessor.color(Languages.MENU_RECIPE_LIST_ICON_NEXT.value()), (event -> {
            event.setCancelled(true);
            nextPage();
        })));
        int recipeSlot = page * 45;
        for (int i = 0; i < 45 && recipeSlot < recipeList.size(); i++, recipeSlot ++) {
            Recipe recipe = recipeList.get(recipeSlot);
            if (recipe == null)
                continue;
            ItemStack display = recipe.getResult();
            slotMap().put(i, new Icon(display, (event -> {
                event.setCancelled(true);
                new RecipeDisplayMenuHolder(player(), recipe, this).openMenu();
            })));
        }
        for (int i = 0; i < 45; i++) {
            if (slotMap().containsKey(i))
                continue;
            slotMap().put(i, new Icon(new ItemStack(Material.AIR)));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (parentMenu != null) {
            CrypticLib.platform().scheduler().runTask(
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
        Inventory inventory = Bukkit.createInventory(this, 54, TextProcessor.color(Languages.MENU_RECIPE_LIST_TITLE.value()));
        for (Integer slot : slotMap().keySet()) {
            inventory.setItem(slot, slotMap().get(slot).display());
        }
        return inventory;
    }

    public int getPage() {
        return page;
    }

    public RecipeListMenuHolder setPage(int page) {
        this.page = page;
        return this;
    }

}
