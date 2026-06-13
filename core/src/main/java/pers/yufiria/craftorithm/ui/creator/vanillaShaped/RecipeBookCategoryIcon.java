package pers.yufiria.craftorithm.ui.creator.vanillaShaped;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import pers.yufiria.craftorithm.config.menu.creator.VanillaShapedCreatorConfig;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.List;
import java.util.Objects;

public class RecipeBookCategoryIcon extends TranslatableIcon {

    private CraftingBookCategory category = CraftingBookCategory.MISC;
    private static final List<CraftingBookCategory> CATEGORIES = List.of(
        CraftingBookCategory.MISC,
        CraftingBookCategory.BUILDING,
        CraftingBookCategory.REDSTONE,
        CraftingBookCategory.EQUIPMENT
    );

    public RecipeBookCategoryIcon() {
        super(new IconDisplay(Material.STONE));
    }

    @Override
    public ItemStack display() {
        switch (category) {
            case MISC -> {
                return CreatorIconParser.INSTANCE.parseIconDisplay(VanillaShapedCreatorConfig.CATEGORY_ICON_MISC.value()).toItemStack();
            }
            case BUILDING -> {
                return CreatorIconParser.INSTANCE.parseIconDisplay(VanillaShapedCreatorConfig.CATEGORY_ICON_BUILDING.value()).toItemStack();
            }
            case REDSTONE -> {
                return CreatorIconParser.INSTANCE.parseIconDisplay(VanillaShapedCreatorConfig.CATEGORY_ICON_REDSTONE.value()).toItemStack();
            }
            case EQUIPMENT -> {
                return CreatorIconParser.INSTANCE.parseIconDisplay(VanillaShapedCreatorConfig.CATEGORY_ICON_EQUIPMENT.value()).toItemStack();
            }
            default -> {
                return new ItemStack(Material.AIR);
            }
        }
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        int index = CATEGORIES.indexOf(category) % 4;
        category = CATEGORIES.get(index);
        ((Menu) Objects.requireNonNull(event.getInventory().getHolder())).updateIcons('C');
        return this;
    }

    public CraftingBookCategory category() {
        return category;
    }

}
