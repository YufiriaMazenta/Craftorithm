package pers.yufiria.craftorithm.ui;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface RecipeDisplayLoader {

    IconParser iconParser();

    default MenuDisplay loadMenuDisplay(String title, List<String> layout, ConfigurationSection iconsConfig) {
        Map<Character, Supplier<Icon>> iconMap = new HashMap<>();
        for (String key : iconsConfig.getKeys(false)) {
            char c = key.charAt(0);
            Supplier<Icon> iconSupplier = iconParser().parse(iconsConfig.getConfigurationSection(key));
            iconMap.put(c, iconSupplier);
        }
        return new MenuDisplay(
            title,
            new MenuLayout(
                layout,
                iconMap
            )
        );
    }

}
