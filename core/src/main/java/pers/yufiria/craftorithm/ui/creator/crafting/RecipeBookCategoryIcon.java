package pers.yufiria.craftorithm.ui.creator.crafting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.List;
import java.util.Objects;

/**
 * 合成配方的配方书分类图标
 * 支持 MISC, BUILDING, REDSTONE, EQUIPMENT 四种分类
 */
public class RecipeBookCategoryIcon extends TranslatableIcon {

    private CraftingBookCategory category = CraftingBookCategory.MISC;

    private final ConfigSectionConfig miscIconConfig;
    private final ConfigSectionConfig buildingIconConfig;
    private final ConfigSectionConfig redstoneIconConfig;
    private final ConfigSectionConfig equipmentIconConfig;

    private static final List<CraftingBookCategory> CATEGORIES = List.of(
        CraftingBookCategory.MISC,
        CraftingBookCategory.BUILDING,
        CraftingBookCategory.REDSTONE,
        CraftingBookCategory.EQUIPMENT
    );

    public RecipeBookCategoryIcon(ConfigSectionConfig miscIconConfig, ConfigSectionConfig buildingIconConfig,
                                  ConfigSectionConfig redstoneIconConfig, ConfigSectionConfig equipmentIconConfig) {
        super(new IconDisplay(Material.STONE));
        this.miscIconConfig = miscIconConfig;
        this.buildingIconConfig = buildingIconConfig;
        this.redstoneIconConfig = redstoneIconConfig;
        this.equipmentIconConfig = equipmentIconConfig;
    }

    @Override
    public ItemStack display() {
        ItemStack display;
        switch (category) {
            case MISC -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    miscIconConfig.value()
                ).toItemStack();
            }
            case BUILDING -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    buildingIconConfig.value()
                ).toItemStack();
            }
            case REDSTONE -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    redstoneIconConfig.value()
                ).toItemStack();
            }
            case EQUIPMENT -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    equipmentIconConfig.value()
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
