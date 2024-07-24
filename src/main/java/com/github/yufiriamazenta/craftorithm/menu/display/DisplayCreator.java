package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.config.menus.display.Shaped;
import crypticlib.lifecycle.BukkitEnabler;
import crypticlib.lifecycle.BukkitReloader;
import crypticlib.lifecycle.annotation.OnEnable;
import crypticlib.lifecycle.annotation.OnReload;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@OnReload
@OnEnable
public enum DisplayCreator implements BukkitEnabler, BukkitReloader {

    INSTANCE;
    private MenuDisplay shapedMenuDisplay;

    public ShapedDisplayMenu createShapedDisplayMenu(Player player, ShapedRecipe shapedRecipe) {
        return new ShapedDisplayMenu(player, shapedMenuDisplay, shapedRecipe);
    }

    public void reloadCraftingMenuDisplay() {
        Map<Character, Supplier<Icon>> icons = new HashMap<>();
        ConfigurationSection iconsConf = Shaped.ICONS.value();
        for (String key : iconsConf.getKeys(false)) {
            if (!iconsConf.isConfigurationSection(key))
                return;
            Character c = key.charAt(0);
            Supplier<Icon> icon = parseIcon(iconsConf.getConfigurationSection(key));
            icons.put(c, icon);
        }
        shapedMenuDisplay = new MenuDisplay(
            Shaped.TITLE.value(),
            new MenuLayout(
                Shaped.LAYOUT.value(),
                icons
            )
        );
    }

    @Override
    public void enable(Plugin plugin) {
        reload(plugin);
    }

    @Override
    public void reload(Plugin plugin) {
        reloadCraftingMenuDisplay();
    }


    public Supplier<Icon> parseIcon(ConfigurationSection config) {
        //TODO
        return null;
    }

}
