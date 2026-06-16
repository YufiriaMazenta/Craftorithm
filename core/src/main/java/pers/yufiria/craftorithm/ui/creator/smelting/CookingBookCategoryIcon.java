package pers.yufiria.craftorithm.ui.creator.smelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CookingBookCategory;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.List;
import java.util.Objects;

/**
 * 熔炼配方的配方书分类图标
 * 支持 FOOD, BLOCKS, MISC 三种分类
 */
public class CookingBookCategoryIcon extends TranslatableIcon {

    private CookingBookCategory category = CookingBookCategory.MISC;

    private final ConfigSectionConfig foodIconConfig;
    private final ConfigSectionConfig blocksIconConfig;
    private final ConfigSectionConfig miscIconConfig;

    private static final List<CookingBookCategory> CATEGORIES = List.of(
        CookingBookCategory.FOOD,
        CookingBookCategory.BLOCKS,
        CookingBookCategory.MISC
    );

    public CookingBookCategoryIcon(ConfigSectionConfig foodIconConfig, ConfigSectionConfig blocksIconConfig, ConfigSectionConfig miscIconConfig) {
        super(new IconDisplay(Material.STONE));
        this.foodIconConfig = foodIconConfig;
        this.blocksIconConfig = blocksIconConfig;
        this.miscIconConfig = miscIconConfig;
    }

    @Override
    public ItemStack display() {
        ItemStack display;
        switch (category) {
            case FOOD -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    foodIconConfig.value()
                ).toItemStack();
            }
            case BLOCKS -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    blocksIconConfig.value()
                ).toItemStack();
            }
            case MISC -> {
                display = CreatorIconParser.INSTANCE.parseIconDisplay(
                    miscIconConfig.value()
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
        // 循环切换分类: FOOD -> BLOCKS -> MISC -> FOOD -> ...
        int currentIndex = CATEGORIES.indexOf(category);
        int nextIndex = (currentIndex + 1) % CATEGORIES.size();
        category = CATEGORIES.get(nextIndex);
        // 刷新菜单中此图标
        ((Menu) Objects.requireNonNull(event.getInventory().getHolder())).updateIcons('G');
        return this;
    }

    public CookingBookCategory category() {
        return category;
    }

    public void setCategory(CookingBookCategory category) {
        this.category = category;
    }

}
