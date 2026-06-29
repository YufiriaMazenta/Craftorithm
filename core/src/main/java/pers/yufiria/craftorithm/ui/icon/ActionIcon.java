package pers.yufiria.craftorithm.ui.icon;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.compile.CompiledScript;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionIcon extends TranslatableIcon {

    protected final Map<ClickType, CompiledScript> actions;

    public ActionIcon(@NotNull IconDisplay iconDisplay) {
        this(iconDisplay, new HashMap<>());
    }

    public ActionIcon(@NotNull IconDisplay iconDisplay, @NotNull Map<ClickType, CompiledScript> actions) {
        super(iconDisplay);
        this.actions = new ConcurrentHashMap<>(actions);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        runActions(event, this.actions);
        return this;
    }

    public void runActions(@NotNull InventoryClickEvent event, @NotNull Map<ClickType, CompiledScript> actionsMap) {
        ClickType click = event.getClick();
        CompiledScript actionScript = actionsMap.get(click);
        if (actionScript != null) {
            actionScript.execute(new ScriptContext((Player) event.getWhoClicked()));
        }
    }

}
