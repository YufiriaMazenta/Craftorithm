package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.List;

public class ActionIcon extends Icon {

    protected final Action action;

    public ActionIcon(@NotNull IconDisplay iconDisplay) {
        this(iconDisplay, null);
    }

    public ActionIcon(@NotNull IconDisplay iconDisplay, Action action) {
        super(iconDisplay);
        this.action = action;
    }

    @Override
    public ItemStack display() {
        ItemStack clone = super.display().clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                meta.setDisplayName(
                    LangManager.INSTANCE.replaceLang(meta.getDisplayName(), parsePlayer())
                );
            }
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null) {
                    lore.replaceAll(it -> LangManager.INSTANCE.replaceLang(it, parsePlayer()));
                }
                meta.setLore(lore);
            }
            clone.setItemMeta(meta);
        }

        return clone;
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (action != null) {
            action.run(((Player) event.getWhoClicked()), Craftorithm.instance());
        }
        return this;
    }

    public Action action() {
        return action;
    }

}
