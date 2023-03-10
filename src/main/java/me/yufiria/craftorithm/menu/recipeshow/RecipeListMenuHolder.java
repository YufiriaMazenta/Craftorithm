package me.yufiria.craftorithm.menu.recipeshow;

import me.yufiria.craftorithm.menu.bukkit.BukkitMenuHandler;
import me.yufiria.craftorithm.menu.bukkit.ItemDisplayIcon;
import me.yufiria.craftorithm.recipe.RecipeManager;
import me.yufiria.craftorithm.recipe.RecipeType;
import me.yufiria.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
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
        this.recipeList = new ArrayList<>(RecipeManager.getPluginRecipes().keySet());
        int recipeNum = recipeList.size();
        page = 0;
        if (recipeNum % 45 == 0)
            maxPage = recipeNum / 45;
        else
            maxPage = recipeNum / 45 + 1;
        recipeList.sort((o1, o2) -> {
            RecipeType recipeType = RecipeManager.getPluginRecipeType(o1);
            RecipeType recipeType2 = RecipeManager.getPluginRecipeType(o2);
            return recipeType.compareTo(recipeType2);
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
        ItemStack frameDisplay = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta frameMeta = Bukkit.getItemFactory().getItemMeta(Material.BLACK_STAINED_GLASS_PANE);
        frameMeta.setDisplayName(LangUtil.color(LangUtil.lang("command.look.icon.list.frame")));
        frameDisplay.setItemMeta(frameMeta);
        for (int i : frame) {
            getMenuIconMap().put(i, ItemDisplayIcon.icon(frameDisplay.clone()));
        }
        ItemStack previousDisplay = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta previousMeta = (SkullMeta) previousDisplay.getItemMeta();
        previousMeta.setOwner("MHF_ArrowLeft");
        previousMeta.setDisplayName(LangUtil.color(LangUtil.lang("command.look.icon.list.previous")));
        previousDisplay.setItemMeta(previousMeta);
        getMenuIconMap().put(46, ItemDisplayIcon.icon(previousDisplay, (event -> {
            getPreviousPage();
        })));
        ItemStack nextDisplay = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta nextMeta = (SkullMeta) previousDisplay.getItemMeta();
        nextMeta.setOwner("MHF_ArrowRight");
        nextMeta.setDisplayName(LangUtil.color(LangUtil.lang("command.look.icon.list.next")));
        nextDisplay.setItemMeta(nextMeta);
        getMenuIconMap().put(52, ItemDisplayIcon.icon(nextDisplay, (event -> {
            getPreviousPage();
        })));
        int slot = page * 54;
        for (int i = 0; i < 45 && slot < recipeList.size(); i++, slot ++) {
            ItemStack display = recipeList.get(slot).getResult();
            int finalSlot = slot;
            getMenuIconMap().put(i, ItemDisplayIcon.icon(display, (event -> {
                event.getWhoClicked().openInventory(new RecipeShowMenuHolder(recipeList.get(finalSlot), this).getInventory());
            })));

        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        resetIcons();
        Inventory inventory = Bukkit.createInventory(this, 54, LangUtil.color(LangUtil.lang("command.look.title.list")));
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