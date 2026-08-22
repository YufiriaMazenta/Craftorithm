package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static pers.yufiria.craftorithm.resultprocessor.impl.ProcessorUtils.*;

/**
 * 简单组件的处理器工厂。
 * 逻辑复杂的组件（enchantments/attributes/item_flag/lore）见各自独立的工厂类。
 */
public enum SimpleComponentProcessorFactory implements ComponentProcessorFactory {

    ALL("all", null, Map.of(
        "copy_from_source", data -> processorRequireSource("all", (sourceItem, resultItem, player) -> {
            resultItem.setItemMeta(sourceItem.getItemMeta());
        })
    )),

    DISPLAY_NAME("display_name", null, Map.of(
        "copy_from_source", data -> processorRequireSource("display_name", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasDisplayName()) {
                resultMeta.setDisplayName(sourceMeta.getDisplayName());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            String value = data.getString("value");
            return processor("display_name", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                String parsed = player != null ? BukkitTextProcessor.placeholder(player, value) : value;
                resultMeta.setDisplayName(BukkitTextProcessor.color(parsed));
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("display_name", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setDisplayName(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    TRIM("trim", null, Map.of(
        "copy_from_source", data -> processorRequireSource("trim", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta instanceof ArmorMeta sourceArmorMeta
                && resultMeta instanceof ArmorMeta resultArmorMeta
                && sourceArmorMeta.hasTrim()) {
                resultArmorMeta.setTrim(sourceArmorMeta.getTrim());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> processor("trim", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (resultMeta instanceof ArmorMeta armorMeta) {
                armorMeta.setTrim(null);
            }
            resultItem.setItemMeta(resultMeta);
        })
    )),

    UNBREAKABLE("unbreakable", null, Map.of(
        "copy_from_source", data -> processorRequireSource("unbreakable", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setUnbreakable(sourceMeta.isUnbreakable());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value");
            return processor("unbreakable", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setUnbreakable(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("unbreakable", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setUnbreakable(false);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    CUSTOM_MODEL_DATA("custom_model_data", null, Map.of(
        "copy_from_source", data -> processorRequireSource("custom_model_data", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasCustomModelData()) {
                resultMeta.setCustomModelData(sourceMeta.getCustomModelData());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            int value = data.getInt("value");
            return processor("custom_model_data", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setCustomModelData(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("custom_model_data", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setCustomModelData(0);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    FOOD("food", MinecraftVersion.V1_20_5, Map.of(
        "copy_from_source", data -> processorRequireSource("food", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasFood()) {
                resultMeta.setFood(sourceMeta.getFood());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> processor("food", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setFood(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    HIDE_TOOLTIP("hide_tooltip", MinecraftVersion.V1_20_5, Map.of(
        "copy_from_source", data -> processorRequireSource("hide_tooltip", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setHideTooltip(sourceMeta.isHideTooltip());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value");
            return processor("hide_tooltip", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setHideTooltip(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("hide_tooltip", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setHideTooltip(false);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    ITEM_NAME("item_name", MinecraftVersion.V1_20_5, Map.of(
        "copy_from_source", data -> processorRequireSource("item_name", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasItemName()) {
                resultMeta.setItemName(sourceMeta.getItemName());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            String value = data.getString("value");
            return processor("item_name", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                String parsed = player != null ? BukkitTextProcessor.placeholder(player, value) : value;
                resultMeta.setItemName(BukkitTextProcessor.color(parsed));
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("item_name", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setItemName(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    MAX_STACK_SIZE("max_stack_size", MinecraftVersion.V1_20_5, Map.of(
        "copy_from_source", data -> processorRequireSource("max_stack_size", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasMaxStackSize()) {
                resultMeta.setMaxStackSize(sourceMeta.getMaxStackSize());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            int value = data.getInt("value");
            return processor("max_stack_size", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setMaxStackSize(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("max_stack_size", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setMaxStackSize(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    RARITY("rarity", MinecraftVersion.V1_20_5, Map.of(
        "copy_from_source", data -> processorRequireSource("rarity", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasRarity()) {
                resultMeta.setRarity(sourceMeta.getRarity());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> processor("rarity", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setRarity(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    TOOL("tool", MinecraftVersion.V1_21, Map.of(
        "copy_from_source", data -> processorRequireSource("tool", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasTool()) {
                resultMeta.setTool(sourceMeta.getTool());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> processor("tool", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setTool(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    FIRE_RESISTANCE("fire_resistance", MinecraftVersion.V1_20_5, Map.of(
        "copy_from_source", data -> processorRequireSource("fire_resistance", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setFireResistant(sourceMeta.isFireResistant());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value");
            return processor("fire_resistance", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setFireResistant(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("fire_resistance", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setFireResistant(false);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    CUSTOM_MODEL_DATA_COMPONENT("custom_model_data_component", MinecraftVersion.V1_21_4, Map.of(
        "copy_from_source", data -> processorRequireSource("custom_model_data_component", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setCustomModelDataComponent(sourceMeta.getCustomModelDataComponent());
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> processor("custom_model_data_component", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setCustomModelDataComponent(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    ITEM_MODEL("item_model", MinecraftVersion.V1_21_4, Map.of(
        "copy_from_source", data -> processorRequireSource("item_model", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasItemModel()) {
                resultMeta.setItemModel(sourceMeta.getItemModel());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            String value = data.getString("value");
            return processor("item_model", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setItemModel(NamespacedKey.fromString(value));
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> processor("item_model", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setItemModel(null);
            resultItem.setItemMeta(resultMeta);
        })
    ));

    private final String componentName;
    private final MinecraftVersion minVersion;
    private final Map<String, Function<ConfigurationSection, ResultProcessor>> types;

    SimpleComponentProcessorFactory(String componentName, @Nullable MinecraftVersion minVersion, Map<String, Function<ConfigurationSection, ResultProcessor>> types) {
        this.componentName = componentName;
        this.minVersion = minVersion;
        this.types = new HashMap<>(types);
    }

    public boolean supportedByCurrentVersion() {
        return minVersion == null || MinecraftVersion.CURRENT.afterOrEquals(minVersion);
    }

    @Override
    public String componentName() {
        return componentName;
    }

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = types.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(componentName + " does not support type: " + type);
        }
        return handler.apply(data);
    }

}
