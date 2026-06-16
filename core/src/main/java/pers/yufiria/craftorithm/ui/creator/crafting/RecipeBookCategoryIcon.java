package pers.yufiria.craftorithm.ui.creator.crafting;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        ItemStack display;
        switch (category) {
            case MISC -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    VanillaShapedCreatorConfig.CATEGORY_ICON_MISC.value()
                ).toItemStack();
            }
            case BUILDING -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    VanillaShapedCreatorConfig.CATEGORY_ICON_BUILDING.value()
                ).toItemStack();
            }
            case REDSTONE -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    VanillaShapedCreatorConfig.CATEGORY_ICON_REDSTONE.value()
                ).toItemStack();
            }
            case EQUIPMENT -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    VanillaShapedCreatorConfig.CATEGORY_ICON_EQUIPMENT.value()
                ).toItemStack();
            }
            default -> {
                return new ItemStack(Material.AIR);
            }
        }
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                meta.setDisplayName(this.parseIconText(meta.getDisplayName()));
            }
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null) {
                    lore.replaceAll(this::parseIconText);
                }
                meta.setLore(lore);
            }
            display.setItemMeta(meta);
        }

        return display;
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        // 循环切换分类: MISC -> BUILDING -> REDSTONE -> EQUIPMENT -> MISC -> ...
        int currentIndex = CATEGORIES.indexOf(category);
        int nextIndex = (currentIndex + 1) % CATEGORIES.size();
        category = CATEGORIES.get(nextIndex);
        // 刷新菜单中此图标
        ((Menu) Objects.requireNonNull(event.getInventory().getHolder())).updateIcons('C');
        return this;
    }

    public CraftingBookCategory category() {
        return category;
    }

}
