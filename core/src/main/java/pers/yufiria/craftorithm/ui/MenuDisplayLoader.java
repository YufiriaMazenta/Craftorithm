package pers.yufiria.craftorithm.ui;

import crypticlib.ui.display.IconCreator;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配方展示UI的加载器接口
 */
public interface MenuDisplayLoader {

    IconParser iconParser();

    default MenuDisplay loadMenuDisplay(String title, List<String> layout, ConfigurationSection iconsConfig) {
        Map<Character, IconCreator> iconMap = new HashMap<>();
        for (String key : iconsConfig.getKeys(false)) {
            char c = key.charAt(0);
            IconCreator iconCreator = iconParser().parse(iconsConfig.getConfigurationSection(key));
            iconMap.put(c, iconCreator);
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
