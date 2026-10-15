package pers.yufiria.craftorithm.ui.icon;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Supplier;

/**
 * 只解析图标外观、不解析 action 的图标解析器。
 * creator / editor 界面不允许解析 action, 统一使用本解析器。
 */
public enum TranslatableIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        //creator页面不允许解析action
        IconDisplay iconDisplay = parseIconDisplay(config);
        return () -> new TranslatableIcon(iconDisplay);
    }

}
