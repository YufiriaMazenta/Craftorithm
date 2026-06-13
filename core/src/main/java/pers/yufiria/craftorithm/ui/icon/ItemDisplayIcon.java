package pers.yufiria.craftorithm.ui.icon;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemDisplayIcon extends Icon {

    private ItemStack displayItem;

    public ItemDisplayIcon() {
        super(new IconDisplay(Material.AIR));
    }

    @Override
    public ItemStack display() {
        if (displayItem == null) {
            return new ItemStack(Material.AIR);
        }
        return displayItem.clone();
    }

    public ItemDisplayIcon setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        return this;
    }

    @Override
    @Deprecated
    public Icon setDisplay(@NotNull ItemStack display) {
        return super.setDisplay(display);
    }

}
