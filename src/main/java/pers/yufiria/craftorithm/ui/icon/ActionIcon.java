package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionIcon extends TranslatableIcon {

    protected final Map<ClickType, Action> actions;

    public ActionIcon(@NotNull IconDisplay iconDisplay) {
        this(iconDisplay, new HashMap<>());
    }

    public ActionIcon(@NotNull IconDisplay iconDisplay, @NotNull Map<ClickType, Action> actions) {
        super(iconDisplay);
        this.actions = new ConcurrentHashMap<>(actions);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        runActions(event, this.actions);
        return this;
    }

    public void runActions(@NotNull InventoryClickEvent event, @NotNull Map<ClickType, Action> actionsMap) {
        ClickType click = event.getClick();
        Action action = actionsMap.get(click);
        if (action != null) {
            action.run(((Player) event.getWhoClicked()), Craftorithm.instance(), null);
        }
    }

}
