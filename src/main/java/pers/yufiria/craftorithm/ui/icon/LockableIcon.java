package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LockableIcon extends ActionIcon {

    protected boolean locked = false;
    protected IconDisplay lockedDisplay;
    protected @NotNull Map<ClickType, Action> lockedActions;

    public LockableIcon(
        @NotNull IconDisplay iconDisplay,
        @NotNull Map<ClickType, Action> actions,
        @Nullable IconDisplay lockedDisplay,
        @NotNull Map<ClickType, Action> lockedActions
    ) {
        super(iconDisplay, actions);
        this.lockedDisplay = lockedDisplay;
        this.lockedActions = lockedActions != null ? new ConcurrentHashMap<>(lockedActions) : new ConcurrentHashMap<>();
    }

    @Override
    public ItemStack display() {
        if (locked) {
            if (lockedDisplay != null) {
                return lockedDisplay.display();
            } else {
                return super.display();
            }
        } else {
            return super.display();
        }
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (locked) {
            runActions(event, lockedActions);
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
