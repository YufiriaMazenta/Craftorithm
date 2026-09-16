package pers.yufiria.craftorithm.ui.icon;

import crypticlib.script.ScriptEngine;
import crypticlib.script.compile.CompiledScript;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public interface IconParser {

    default Supplier<Icon> parse(ConfigurationSection config) {
        return parseCommonIcon(config);
    }

    /**
     * 解析 icon_type 在 iconTypes 内的物品显示图标, 其余类型按普通图标解析
     * @param config 图标配置
     * @param iconTypes 需要解析为物品显示图标的 icon_type 集合
     */
    default Supplier<Icon> parseItemDisplayIcon(ConfigurationSection config, Set<String> iconTypes) {
        return parseItemDisplayIcon(config, iconTypes, null);
    }

    /**
     * 解析 icon_type 在 iconTypes 内的物品显示图标, 其余类型按普通图标解析
     * @param config 图标配置
     * @param iconTypes 需要解析为物品显示图标的 icon_type 集合
     * @param slotIconType 若不为null, 则该 icon_type 会额外读取 ingredient_slot 写入图标数据
     */
    default Supplier<Icon> parseItemDisplayIcon(ConfigurationSection config, Set<String> iconTypes, @Nullable String slotIconType) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        if (!iconTypes.contains(iconType)) {
            return parseCommonIcon(config);
        }
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        if (iconType.equals(slotIconType)) {
            int ingredientSlot = config.getInt("ingredient_slot", 0);
            return () -> {
                ItemDisplayIcon icon = new ItemDisplayIcon(actions);
                icon.putData("icon_type", iconType);
                icon.putData("ingredient_slot", ingredientSlot);
                return icon;
            };
        }
        return () -> {
            ItemDisplayIcon icon = new ItemDisplayIcon(actions);
            icon.putData("icon_type", iconType);
            return icon;
        };
    }

    private Supplier<Icon> parseCommonIcon(ConfigurationSection config) {
        Objects.requireNonNull(config);
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new ActionIcon(iconDisplay, actions);
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
