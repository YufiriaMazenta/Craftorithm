package pers.yufiria.craftorithm.ui.icon;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;

import java.util.Map;

public abstract class ItemDisplayIcon extends ActionIcon {

    private ItemStack displayItem;

    public ItemDisplayIcon() {
        super(new IconDisplay(Material.AIR));
    }

    public ItemDisplayIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(new IconDisplay(Material.AIR), actions);
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
