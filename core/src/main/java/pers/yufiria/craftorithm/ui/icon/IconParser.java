package pers.yufiria.craftorithm.ui.icon;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.util.IOHelper;
import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.ScriptEngine;
import pers.yufiria.craftorithm.script.compile.CompiledScript;

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
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new ActionIcon(iconDisplay, actions);
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
        NamespacedKey itemModel;
        if (config.isString("item_model")) {
            itemModel = NamespacedKey.fromString(Objects.requireNonNull(config.getString("item_model")));
        } else {
            itemModel = null;
        }
        return new IconDisplay(Objects.requireNonNull(material))
            .setName(name)
            .setLore(lore)
            .setCustomModelData(customModelData)
            .setItemModel(itemModel);
    }

    default @NotNull Map<ClickType, CompiledScript> parseActions(ConfigurationSection actionsConfig) {
        if (actionsConfig == null) {
            return new HashMap<>();
        }
        Map<ClickType, CompiledScript> actions = new HashMap<>();
        for (String key : actionsConfig.getKeys(false)) {
            ClickType clickType = ClickType.valueOf(key.toUpperCase());
            List<String> actSources = actionsConfig.getStringList(key);
            if (actSources.isEmpty()) {
                continue;
            }
            String actSource = String.join("\n", actSources);
            CompiledScript compiledScript = ScriptEngine.INSTANCE.compile("icon_act_" + actSource.hashCode(), actSource);
            actions.put(clickType, compiledScript);
        }
        return actions;
    }

}
