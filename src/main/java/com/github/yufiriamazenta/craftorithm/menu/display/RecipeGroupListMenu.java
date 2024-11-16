package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecipeGroupListMenu extends Menu {

    private int page;
    private int maxPage;
    private List<Map.Entry<String, ItemStack>> recipeGroupResultList;

    public RecipeGroupListMenu(Player player) {
        super(player);
        recipeGroupResultList = new CopyOnWriteArrayList<>();
        refreshRecipes();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        resetIcons();
        if (inventoryCache == null) {
            inventoryCache = Bukkit.createInventory(
                this,
                54,
                BukkitTextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_TITLE.value(player))
            );
        }
        refreshInventory();

        return inventoryCache;
    }

    public void nextPage() {
        setPage(Math.min(page + 1, Math.max(0, maxPage - 1))).resetIcons();
        refreshInventory();
    }

    public void previousPage() {
        setPage(Math.max(page - 1, 0)).resetIcons();
        refreshInventory();
    }

    public RecipeGroupListMenu refreshRecipes() {
        Map<String, ItemStack> recipeResultMap = new HashMap<>();
        RecipeManager.INSTANCE.recipeMap().forEach((recipeType, recipeGroupMap) ->
            recipeGroupMap.forEach((groupName, recipeGroup) -> {
                if (recipeGroup == null || recipeGroup.isEmpty())
                    return;
                Recipe firstRecipe = RecipeManager.INSTANCE.getRecipe(recipeGroup.groupRecipeKeys().get(0));
                if (firstRecipe == null)
                    return;
                recipeResultMap.put(groupName, firstRecipe.getResult());
            })
        );
        recipeGroupResultList = new CopyOnWriteArrayList<>(recipeResultMap.entrySet());
        page = 0;
        int recipeGroupNum = recipeResultMap.size();
        if (recipeGroupNum % 45 == 0) {
            maxPage = recipeGroupNum / 45;
        } else {
            maxPage = recipeGroupNum / 45 + 1;
        }
        recipeGroupResultList.sort((o1, o2) -> {
            int sortId = RecipeManager.INSTANCE.getRecipeGroupSortId(o1.getKey());
            int sortId2 = RecipeManager.INSTANCE.getRecipeGroupSortId(o2.getKey());
            return Integer.compare(sortId, sortId2);
        });
        return this;
    }

    public RecipeGroupListMenu resetIcons() {
        slotMap.clear();
        int []frameSlots = {45, 47, 48, 49, 50, 51, 53};
        Icon frameIcon = new Icon(
            new IconDisplay(
                Material.BLACK_STAINED_GLASS_PANE,
                BukkitTextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_ICON_FRAME.value(player)
            )
        ));
        for (int frameSlot : frameSlots) {
            slotMap.put(frameSlot, frameIcon);
        }
        slotMap.put(46, new Icon(
            new IconDisplay(
                Material.PAPER,
                BukkitTextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_ICON_PREVIOUS.value(player))
            )
        ).setClickAction(event -> previousPage()));
        slotMap.put(52, new Icon(
            new IconDisplay(
                Material.PAPER,
                BukkitTextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_ICON_NEXT.value(player))
            )
        ).setClickAction(event -> nextPage()));
        int recipeSlot = page * 45;
        for (int invSlot = 0; invSlot < 45 && recipeSlot < recipeGroupResultList.size(); invSlot++, recipeSlot++) {
            slotMap.put(invSlot, wrapIcon(recipeSlot));
        }
        for (int i = 0; i < 45; i++) {
            if (slotMap.containsKey(i))
                continue;
            slotMap.put(i, new Icon(new ItemStack(Material.AIR)));
        }
        return this;
    }

    public RecipeGroupListMenu refreshInventory() {
        inventoryCache.clear();
        for (Integer slot : slotMap.keySet()) {
            inventoryCache.setItem(slot, slotMap.get(slot).display());
        }
        return this;
    }

    @NotNull
    private Icon wrapIcon(int recipeSlot) {
        ItemStack display = recipeGroupResultList.get(recipeSlot).getValue();
        String recipeGroupName = recipeGroupResultList.get(recipeSlot).getKey();
        Icon icon = new Icon(display).setClickAction(
            event -> {
                switch (event.getClick()) {
                    case RIGHT:
                    case SHIFT_RIGHT:
//                        if (!player.hasPermission("craftorithm.edit_recipe")) {
//                            return;
//                        }
//                        RecipeGroup recipeGroup = RecipeManager.INSTANCE.getRecipeGroup(recipeGroupName);
//                        if (recipeGroup == null) {
//                            throw new IllegalArgumentException("Can not find recipe group " + recipeGroupName);
//                        }
//                        recipeGroupEditorMap.getOrDefault(recipeGroup.recipeType(), (player, group, parent) -> {
//                            throw new RuntimeException("Unknown recipe type editor");
//                        }).apply(player, recipeGroup, this).openMenu();
                        break;
                    case LEFT:
                    case SHIFT_LEFT:
                    default:
                        RecipeGroup recipeGroup1 = RecipeManager.INSTANCE.getRecipeGroup(recipeGroupName);
                        if (recipeGroup1 == null) {
                            throw new IllegalArgumentException("Can not find recipe group " + recipeGroupName);
                        }
                        if (recipeGroup1.groupRecipeKeys().size() < 2) {
                            new RecipeDisplayMenu(
                                player,
                                RecipeManager.INSTANCE.getRecipe(recipeGroup1.groupRecipeKeys().get(0)),
                                this
                            ).openMenu();
                        } else {
                            new RecipeListMenu(
                                player,
                                recipeGroup1.groupRecipeKeys(),
                                this
                            ).openMenu();
                        }
                        break;
                }
            }
        );
        ItemHelper.setLore(icon.display(), Languages.MENU_NEW_RECIPE_LIST_ICON_ELEMENTS_LORE.value(player));
        return icon;
    }

    public int page() {
        return page;
    }

    public RecipeGroupListMenu setPage(int page) {
        this.page = page;
        return this;
    }

}
