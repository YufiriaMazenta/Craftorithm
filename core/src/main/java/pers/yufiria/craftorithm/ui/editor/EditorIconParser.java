package pers.yufiria.craftorithm.ui.editor;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.function.Supplier;

public enum EditorIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        //editor页面不允许解析action
        IconDisplay iconDisplay = parseIconDisplay(config);
        return () -> new TranslatableIcon(iconDisplay);
    }

}
