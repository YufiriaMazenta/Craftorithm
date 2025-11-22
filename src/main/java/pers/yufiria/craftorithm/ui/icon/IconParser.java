package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.action.ActionCompiler;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public interface IconParser {

    default Supplier<Icon> parse(ConfigurationSection config) {
        Objects.requireNonNull(config);
        String iconType = config.getString("icon_type", "common").toLowerCase();

        switch (iconType) {
            default -> {
                IconDisplay iconDisplay = parseIconDisplay(config);
                Map<ClickType, Action> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new ActionIcon(iconDisplay, actions);
            }
            case "next" -> {
                IconDisplay iconDisplay = parseIconDisplay(config);
                Map<ClickType, Action> actions = parseActions(config.getConfigurationSection("actions"));

                IconDisplay lockedDisplay;
                Map<ClickType, Action> lockedActions;
                if (config.contains("locked")) {
                    ConfigurationSection lockedConfig = Objects.requireNonNull(config.getConfigurationSection("locked"));
                    lockedDisplay = parseIconDisplay(lockedConfig);
                    lockedActions = parseActions(lockedConfig.getConfigurationSection("actions"));
                } else {
                    lockedActions = new HashMap<>();
                    lockedDisplay = null;
                }
                return () -> new NextIcon(iconDisplay, actions, lockedDisplay, lockedActions);
            }
            case "previous" -> {
                IconDisplay iconDisplay = parseIconDisplay(config);
                Map<ClickType, Action> actions = parseActions(config.getConfigurationSection("actions"));

                IconDisplay lockedDisplay;
                Map<ClickType, Action> lockedActions;
                if (config.contains("locked")) {
                    ConfigurationSection lockedConfig = Objects.requireNonNull(config.getConfigurationSection("locked"));
                    lockedDisplay = parseIconDisplay(lockedConfig);
                    lockedActions = parseActions(lockedConfig.getConfigurationSection("actions"));
                } else {
                    lockedActions = new HashMap<>();
                    lockedDisplay = null;
                }
                return () -> new PreviousIcon(iconDisplay, actions, lockedDisplay, lockedActions);
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

    default @NotNull Map<ClickType, Action> parseActions(ConfigurationSection actionsConfig) {
        if (actionsConfig == null) {
            return new HashMap<>();
        }
        Map<ClickType, Action> actions = new HashMap<>();
        for (String key : actionsConfig.getKeys(false)) {
            ClickType clickType = ClickType.valueOf(key.toUpperCase());
            Action action = ActionCompiler.INSTANCE.compile(actionsConfig.getStringList(key));
            actions.put(clickType, action);
        }
        return actions;
    }

}
