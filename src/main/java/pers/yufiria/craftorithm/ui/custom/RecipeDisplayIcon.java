package pers.yufiria.craftorithm.ui.custom;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class RecipeDisplayIcon extends ActionIcon {

    private final NamespacedKey recipeKey;
    private final Recipe recipe;
    private final RecipeType recipeType;
    private final ClickType viewClick;
    private final ClickType editClick;
    private final List<String> extraLore;

    public RecipeDisplayIcon(NamespacedKey recipeKey, List<String> extraLore) {
        this(recipeKey, extraLore, ClickType.LEFT, ClickType.SHIFT_RIGHT);
    }

    public RecipeDisplayIcon(NamespacedKey recipeKey, List<String> extraLore, ClickType viewClick, ClickType editClick) {
        super(new IconDisplay(Material.AIR), new HashMap<>());
        this.recipeKey = recipeKey;
        this.viewClick = viewClick;
        this.editClick = editClick;
        this.extraLore = extraLore;
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(recipeKey);
        this.recipe = recipe;
        this.recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
    }

    @Override
    public ItemStack display() {
        if (recipe != null) {
            ItemStack displayItem = recipe.getResult().clone();
            ItemMeta itemMeta = displayItem.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                List<String> finalLore = lore;
                extraLore.forEach(it -> {
                    finalLore.add(parseIconText(it));
                });
                lore.addAll(finalLore);
            } else {
                lore = new ArrayList<>(extraLore);
                lore.replaceAll(this::parseIconText);
            }
            itemMeta.setLore(lore);
            displayItem.setItemMeta(itemMeta);
            return displayItem;
        }
        return new ItemStack(Material.AIR);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (recipe == null) {
            return this;
        }
        Player whoClicked = ((Player) event.getWhoClicked());
        ClickType click = event.getClick();
        if (click == viewClick) {
            Optional<BiFunction<Player, Recipe, Menu>> recipeDisplayOpt = RecipeDisplayManager.INSTANCE.getRecipeDisplay(recipeType);
            recipeDisplayOpt.ifPresentOrElse(displayFunc -> {
                Optional<Menu> openingMenuOpt = MenuHelper.getOpeningMenu(whoClicked);
                Menu openingMenu = openingMenuOpt.orElse(null);
                Menu willOpenMenu = displayFunc.apply(whoClicked, recipe);
                if (willOpenMenu instanceof BackableMenu backableMenu) {
                    backableMenu.setParentMenu(openingMenu);
                }
            }, () -> {
                LangUtils.sendLang(whoClicked, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
            });
        } else if (click == editClick) {
            if (!whoClicked.hasPermission("craftorithm.edit_recipe")) {
                return this;
            }
            //TODO 未来实现
        }
        return this;
    }

    public NamespacedKey recipeKey() {
        return recipeKey;
    }

}
