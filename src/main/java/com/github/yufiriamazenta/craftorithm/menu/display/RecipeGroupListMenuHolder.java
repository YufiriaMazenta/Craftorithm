package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.Menu;
import crypticlib.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeGroupListMenuHolder extends Menu {

    private int page;
    private final int maxPage;
    private final List<Map.Entry<String, ItemStack>> recipeGroupResultList;

    public RecipeGroupListMenuHolder(Player player) {
        super(player, () -> null);
        Map<String, ItemStack> recipeResultMap = new HashMap<>();
        RecipeManager.INSTANCE.recipeMap().forEach((recipeType, recipeGroupMap) -> {
            recipeGroupMap.forEach((groupName, recipeGroup) -> {
                if (recipeGroup == null || recipeGroup.isEmpty())
                    return;
                Recipe firstRecipe = RecipeManager.INSTANCE.getRecipe(recipeGroup.groupRecipeKeys().get(0));
                if (firstRecipe == null)
                    return;
                recipeResultMap.put(groupName, firstRecipe.getResult());
            });
        });
        recipeGroupResultList = new ArrayList<>(recipeResultMap.entrySet());
        page = 0;
        int recipeGroupNum = recipeResultMap.size();
        if (recipeGroupNum % 45 == 0) {
            maxPage = recipeGroupNum / 45;
        } else {
            maxPage = recipeGroupNum / 45 + 1;
        }
        recipeGroupResultList.sort((o1, o2) -> {
            int sortId = RecipeManager.INSTANCE.getCraftorithmRecipeSortId(o1.getKey());
            int sortId2 = RecipeManager.INSTANCE.getCraftorithmRecipeSortId(o2.getKey());
            return Integer.compare(sortId, sortId2);
        });
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        resetIcons();
        Inventory inventory = Bukkit.createInventory(this, 54, TextUtil.color(Languages.MENU_NEW_RECIPE_LIST_TITLE.value()));
        for (Integer slot : super.slotMap().keySet()) {
            inventory.setItem(slot, slotMap().get(slot).display());
        }
        return inventory;
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
        int []frameSlots = {45, 47, 48, 49, 50, 51, 53};
        Icon frameIcon = new Icon(Material.BLACK_STAINED_GLASS_PANE, TextUtil.color(Languages.MENU_NEW_RECIPE_LIST_ICON_FRAME.value()));
        for (int frameSlot : frameSlots) {
            slotMap().put(frameSlot, frameIcon);
        }
        slotMap().put(46, new Icon(Material.PAPER, TextUtil.color(Languages.MENU_NEW_RECIPE_LIST_ICON_PREVIOUS.value()), (event -> {
            event.setCancelled(true);
            previousPage();
        })));
        slotMap().put(52, new Icon(Material.PAPER, TextUtil.color(Languages.MENU_NEW_RECIPE_LIST_ICON_NEXT.value()), (event -> {
            event.setCancelled(true);
            nextPage();
        })));
        int recipeSlot = page * 45;
        for (int invSlot = 0; invSlot < 45 && recipeSlot < recipeGroupResultList.size(); invSlot++, recipeSlot++) {
            slotMap().put(invSlot, wrapIcon(recipeSlot));
        }
        for (int i = 0; i < 45; i++) {
            if (slotMap().containsKey(i))
                continue;
            slotMap().put(i, new Icon(new ItemStack(Material.AIR)));
        }
    }

    @NotNull
    private Icon wrapIcon(int recipeSlot) {
        ItemStack display = recipeGroupResultList.get(recipeSlot).getValue();
        String recipeGroupName = recipeGroupResultList.get(recipeSlot).getKey();
        return new Icon(display, event -> {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(
                new RecipeListMenuHolder(
                    (Player) event.getWhoClicked(),
                    RecipeManager.INSTANCE.getRecipeGroup(recipeGroupName).groupRecipeKeys(),
                    this
                ).getInventory()
            );
        });
    }

    public int page() {
        return page;
    }

    public RecipeGroupListMenuHolder setPage(int page) {
        this.page = page;
        return this;
    }

}