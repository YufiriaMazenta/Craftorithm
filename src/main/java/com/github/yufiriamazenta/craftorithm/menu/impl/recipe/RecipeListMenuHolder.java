package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.IChildBukkitMenu;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.ItemDisplayIcon;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeListMenuHolder extends BukkitMenuHandler implements IChildBukkitMenu {

    private int page;
    private final int maxPage;
    private final List<Recipe> recipeList;
    private final boolean hasEditPermission;
    private BukkitMenuHandler parentMenu;

    public RecipeListMenuHolder(Player player, List<NamespacedKey> recipeKeys) {
        this(player, recipeKeys, null);
    }

    public RecipeListMenuHolder(Player player, List<NamespacedKey> recipeKeys, BukkitMenuHandler parentMenu) {
        super();
        this.parentMenu = parentMenu;
        this.hasEditPermission = player.hasPermission("craftorithm.recipe_list.manager");
        this.recipeList = new ArrayList<>();
        for (NamespacedKey key : recipeKeys) {
            recipeList.add(Bukkit.getRecipe(key));
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

    /**
     * 为了兼容PotionMix做的特殊构造方法
     */
    protected RecipeListMenuHolder(Player player, Map<String, List<PotionMixRecipe>> potionMixGroupMap, String recipeGroupName, BukkitMenuHandler parentMenu) {
        super();
        this.parentMenu = parentMenu;
        this.hasEditPermission = player.hasPermission("craftorithm.recipe_list.manager");
        List<PotionMixRecipe> potionMixRecipes = potionMixGroupMap.get(recipeGroupName);
        this.recipeList = new ArrayList<>();
        this.recipeList.addAll(potionMixRecipes);
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

    public Inventory getNextPage() {
        setPage(Math.min(page + 1, maxPage - 1));
        return getInventory();
    }

    public Inventory getPreviousPage() {
        setPage(Math.max(page - 1, 0));
        return getInventory();
    }

    private void resetIcons() {
        getMenuIconMap().clear();
        int []frame = {45, 47, 48, 49, 50, 51, 53};
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_list.icon.frame"));
        for (int i : frame) {
            getMenuIconMap().put(i, frameIcon);
        }
        ItemStack previousDisplay = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta previousMeta = (SkullMeta) previousDisplay.getItemMeta();
        previousMeta.setOwner("MHF_ArrowLeft");
        previousMeta.setDisplayName(TextUtil.color(LangUtil.langMsg("menu.recipe_list.icon.previous")));
        previousDisplay.setItemMeta(previousMeta);
        getMenuIconMap().put(46, ItemDisplayIcon.icon(previousDisplay, (event -> {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(getPreviousPage());
        })));
        ItemStack nextDisplay = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta nextMeta = (SkullMeta) nextDisplay.getItemMeta();
        nextMeta.setOwner("MHF_ArrowRight");
        nextMeta.setDisplayName(TextUtil.color(LangUtil.langMsg("menu.recipe_list.icon.next")));
        nextDisplay.setItemMeta(nextMeta);
        getMenuIconMap().put(52, ItemDisplayIcon.icon(nextDisplay, (event -> {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(getNextPage());
        })));
        int recipeSlot = page * 45;
        for (int i = 0; i < 45 && recipeSlot < recipeList.size(); i++, recipeSlot ++) {
            Recipe recipe = recipeList.get(recipeSlot);
            if (recipe == null)
                continue;
            ItemStack display = recipe.getResult();
            getMenuIconMap().put(i, ItemDisplayIcon.icon(display, (event -> {
                event.setCancelled(true);
                event.getWhoClicked().openInventory(new RecipeDisplayMenuHolder(recipe, this).getInventory());
            })));

        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        resetIcons();
        Inventory inventory = Bukkit.createInventory(this, 54, TextUtil.color(LangUtil.langMsg("menu.recipe_list.title")));
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

    @Override
    public BukkitMenuHandler parentMenu() {
        return parentMenu;
    }

    @Override
    public void setParentMenu(BukkitMenuHandler parentMenu) {
        this.parentMenu = parentMenu;
    }
}
