package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.action.ActionCompiler;
import crypticlib.action.impl.EmptyAction;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public interface IconParser {

    default Supplier<Icon> parse(ConfigurationSection config) {
        Objects.requireNonNull(config);
        String iconType = config.getString("icon_type", "common").toLowerCase();

        switch (iconType) {
            default -> {
                IconDisplay iconDisplay = parseIconDisplay(config);
                Action action = parseAction(config.getStringList("actions"));
                return () -> new ActionIcon(iconDisplay, action);
            }
            case "next" -> {
                IconDisplay iconDisplay = parseIconDisplay(config);
                Action action = parseAction(config.getStringList("actions"));

                IconDisplay lockedDisplay;
                Action lockedAction;
                if (config.contains("locked")) {
                    ConfigurationSection locked = Objects.requireNonNull(config.getConfigurationSection("locked"));
                    lockedDisplay = parseIconDisplay(locked);
                    lockedAction = parseAction(locked.getStringList("actions"));
                } else {
                    lockedAction = null;
                    lockedDisplay = null;
                }
                return () -> new NextIcon(iconDisplay, action, lockedDisplay, lockedAction);
            }
            case "previous" -> {
                IconDisplay iconDisplay = parseIconDisplay(config);
                Action action = parseAction(config.getStringList("actions"));

                IconDisplay lockedDisplay;
                Action lockedAction;
                if (config.contains("locked")) {
                    ConfigurationSection locked = Objects.requireNonNull(config.getConfigurationSection("locked"));
                    lockedDisplay = parseIconDisplay(locked);
                    lockedAction = parseAction(locked.getStringList("actions"));
                } else {
                    lockedAction = null;
                    lockedDisplay = null;
                }
                return () -> new PreviousIcon(iconDisplay, action, lockedDisplay, lockedAction);
            }
        }
    }

    default IconDisplay parseIconDisplay(ConfigurationSection config) {
        if (config == null)
            return null;
        Material material = MaterialHelper.matchMaterial(config.getString("material", "minecraft:stone"));
        String name = config.getString("name");
        List<String> lore = config.getStringList("lore");
        Integer customModelData = config.getInt("custom_model_data");
        return new IconDisplay(Objects.requireNonNull(material), name, lore, customModelData);
    }

    default @Nullable Action parseAction(List<String> actionStrList) {
        if (actionStrList == null || actionStrList.isEmpty()) {
            return new EmptyAction();
        }
        return ActionCompiler.INSTANCE.compile(actionStrList);
    }

}
