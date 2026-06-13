package pers.yufiria.craftorithm.ui.creator;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.function.Supplier;

public enum CreatorIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        return () -> new TranslatableIcon(iconDisplay);
    }

}
