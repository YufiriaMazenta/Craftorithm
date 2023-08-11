package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.ItemDisplayIcon;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeListMenuHolder extends BukkitMenuHandler {

    private int page;
    private final int maxPage;
    private final List<Recipe> recipeList;

    public RecipeListMenuHolder() {
        super();
        this.recipeList = new ArrayList<>(RecipeManager.getPluginRecipeTypeMap().keySet());
        int recipeNum = recipeList.size();
        page = 0;
        if (recipeNum % 45 == 0)
            maxPage = recipeNum / 45;
        else
            maxPage = recipeNum / 45 + 1;
        if (Craftorithm.getInstance().getVanillaVersion() >= 20) {
            recipeList.removeIf(recipe -> recipe instanceof SmithingTrimRecipe);
        }
        recipeList.sort((o1, o2) -> {
            int sortId = RecipeManager.getRecipeSortIdMap().get(RecipeManager.getRecipeKey(o1));
            int sortId2 = RecipeManager.getRecipeSortIdMap().get(RecipeManager.getRecipeKey(o2));
            return Integer.compare(sortId, sortId2);
        });
    }

    public Inventory getNextPage() {
        setPage(Math.min(getPage() + 1, maxPage - 1));
        return getInventory();
    }

    public Inventory getPreviousPage() {
        setPage(Math.max(getPage() - 1, 0));
        return getInventory();
    }

    private void resetIcons() {
        getMenuIconMap().clear();
        int []frame = {45, 47, 48, 49, 50, 51, 53};
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_list.icon.frame"));
        for (int i : frame) {
            getMenuIconMap().put(i, frameIcon);
        }
        ItemStack previousDisplay = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta previousMeta = (SkullMeta) previousDisplay.getItemMeta();
        previousMeta.setOwner("MHF_ArrowLeft");
        previousMeta.setDisplayName(LangUtil.color(LangUtil.lang("menu.recipe_list.icon.previous")));
        previousDisplay.setItemMeta(previousMeta);
        getMenuIconMap().put(46, ItemDisplayIcon.icon(previousDisplay, (event -> {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(getPreviousPage());
        })));
        ItemStack nextDisplay = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta nextMeta = (SkullMeta) nextDisplay.getItemMeta();
        nextMeta.setOwner("MHF_ArrowRight");
        nextMeta.setDisplayName(LangUtil.color(LangUtil.lang("menu.recipe_list.icon.next")));
        nextDisplay.setItemMeta(nextMeta);
        getMenuIconMap().put(52, ItemDisplayIcon.icon(nextDisplay, (event -> {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(getNextPage());
        })));
        int slot = page * 54;
        for (int i = 0; i < 45 && slot < recipeList.size(); i++, slot ++) {
            ItemStack display = recipeList.get(slot).getResult();
            int finalSlot = slot;
            getMenuIconMap().put(i, ItemDisplayIcon.icon(display, (event -> {
                event.setCancelled(true);
                event.getWhoClicked().openInventory(new RecipeDisplayMenuHolder(recipeList.get(finalSlot), this).getInventory());
            })));

        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        resetIcons();
        Inventory inventory = Bukkit.createInventory(this, 54, LangUtil.color(LangUtil.lang("menu.recipe_list.title")));
        for (Integer slot : getMenuIconMap().keySet()) {
            inventory.setItem(slot, getMenuIconMap().get(slot).getDisplay());
        }
        return inventory;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
