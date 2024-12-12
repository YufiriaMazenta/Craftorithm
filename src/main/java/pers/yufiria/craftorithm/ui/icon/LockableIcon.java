package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;

public abstract class LockableIcon extends ActionIcon {

    protected boolean locked = false;
    protected IconDisplay lockedDisplay;
    protected Action lockedAction;

    public LockableIcon(@NotNull IconDisplay iconDisplay, @Nullable Action action, @Nullable IconDisplay lockedDisplay, @Nullable Action lockedAction) {
        super(iconDisplay, action);
        this.lockedDisplay = lockedDisplay;
    }

    @Override
    public ItemStack display() {
        if (locked) {
            if (lockedDisplay != null) {
                return lockedDisplay.display().clone();
            } else {
                return super.display().clone();
            }
        } else {
            return super.display().clone();
        }
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (locked) {
            if (lockedAction != null) {
                lockedAction.run((Player) event.getWhoClicked(), Craftorithm.instance());
            }
            return null;
        }
        return super.onClick(event);
    }

    public boolean locked() {
        return locked;
    }

    public LockableIcon setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

}
