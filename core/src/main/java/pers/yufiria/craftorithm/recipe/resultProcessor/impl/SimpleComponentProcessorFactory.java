package pers.yufiria.craftorithm.recipe.resultProcessor.impl;

import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processor;
import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processorRequireSource;
import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.unsupported;

/**
 * 简单组件的处理器工厂。
 * 逻辑复杂的组件（enchantments/attributes/item_flag/lore）见各自独立的工厂类。
 */
public enum SimpleComponentProcessorFactory implements ComponentProcessorFactory {

    ALL("all", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("all", (sourceItem, resultItem) -> {
                    resultItem.setItemMeta(sourceItem.getItemMeta());
                });
                default -> unsupported("all", strategy);
            };
        }
    },
    DISPLAY_NAME("display_name", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("display_name", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasDisplayName()) {
                        resultMeta.setDisplayName(sourceMeta.getDisplayName());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    String value = data.getString("value");
                    yield processor("display_name", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setDisplayName(value);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("display_name", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setDisplayName(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("display_name", strategy);
            };
        }
    },
    TRIM("trim", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("trim", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta instanceof ArmorMeta sourceArmorMeta
                        && resultMeta instanceof ArmorMeta resultArmorMeta
                        && sourceArmorMeta.hasTrim()) {
                        resultArmorMeta.setTrim(sourceArmorMeta.getTrim());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("trim", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (resultMeta instanceof ArmorMeta armorMeta) {
                        armorMeta.setTrim(null);
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("trim", strategy);
            };
        }
    },
    UNBREAKABLE("unbreakable", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("unbreakable", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setUnbreakable(sourceMeta.isUnbreakable());
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    boolean value = data.getBoolean("value");
                    yield processor("unbreakable", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setUnbreakable(value);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("unbreakable", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setUnbreakable(false);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("unbreakable", strategy);
            };
        }
    },
    CUSTOM_MODEL_DATA("custom_model_data", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("custom_model_data", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasCustomModelData()) {
                        resultMeta.setCustomModelData(sourceMeta.getCustomModelData());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    int value = data.getInt("value");
                    yield processor("custom_model_data", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setCustomModelData(value);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("custom_model_data", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setCustomModelData(0);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("custom_model_data", strategy);
            };
        }
    },
    FOOD("food", MinecraftVersion.V1_20_5) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("food", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasFood()) {
                        resultMeta.setFood(sourceMeta.getFood());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("food", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setFood(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("food", strategy);
            };
        }
    },
    HIDE_TOOLTIP("hide_tooltip", MinecraftVersion.V1_20_5) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("hide_tooltip", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setHideTooltip(sourceMeta.isHideTooltip());
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    boolean value = data.getBoolean("value");
                    yield processor("hide_tooltip", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setHideTooltip(value);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("hide_tooltip", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setHideTooltip(false);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("hide_tooltip", strategy);
            };
        }
    },
    ITEM_NAME("item_name", MinecraftVersion.V1_20_5) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("item_name", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasItemName()) {
                        resultMeta.setItemName(sourceMeta.getItemName());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    String value = data.getString("value");
                    yield processor("item_name", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setItemName(value);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("item_name", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setItemName(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("item_name", strategy);
            };
        }
    },
    MAX_STACK_SIZE("max_stack_size", MinecraftVersion.V1_20_5) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("max_stack_size", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasMaxStackSize()) {
                        resultMeta.setMaxStackSize(sourceMeta.getMaxStackSize());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    int value = data.getInt("value");
                    yield processor("max_stack_size", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setMaxStackSize(value);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("max_stack_size", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setMaxStackSize(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("max_stack_size", strategy);
            };
        }
    },
    RARITY("rarity", MinecraftVersion.V1_20_5) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("rarity", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasRarity()) {
                        resultMeta.setRarity(sourceMeta.getRarity());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("rarity", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setRarity(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("rarity", strategy);
            };
        }
    },
    TOOL("tool", MinecraftVersion.V1_21) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("tool", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasTool()) {
                        resultMeta.setTool(sourceMeta.getTool());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("tool", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setTool(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("tool", strategy);
            };
        }
    },
    FIRE_RESISTANCE("fire_resistance", MinecraftVersion.V1_20_5) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("fire_resistance", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setFireResistant(sourceMeta.isFireResistant());
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    boolean value = data.getBoolean("value");
                    yield processor("fire_resistance", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setFireResistant(value);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("fire_resistance", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setFireResistant(false);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("fire_resistance", strategy);
            };
        }
    },
    CUSTOM_MODEL_DATA_COMPONENT("custom_model_data_component", MinecraftVersion.V1_21_4) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("custom_model_data_component", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setCustomModelDataComponent(sourceMeta.getCustomModelDataComponent());
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("custom_model_data_component", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setCustomModelDataComponent(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("custom_model_data_component", strategy);
            };
        }
    },
    ITEM_MODEL("item_model", MinecraftVersion.V1_21_4) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processorRequireSource("item_model", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasItemModel()) {
                        resultMeta.setItemModel(sourceMeta.getItemModel());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    String value = data.getString("value");
                    yield processor("item_model", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setItemModel(NamespacedKey.fromString(value));
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case REMOVE -> processor("item_model", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.setItemModel(null);
                    resultItem.setItemMeta(resultMeta);
                });
                default -> unsupported("item_model", strategy);
            };
        }
    };

    private final String componentName;
    private final MinecraftVersion minVersion;

    SimpleComponentProcessorFactory(String componentName, @Nullable MinecraftVersion minVersion) {
        this.componentName = componentName;
        this.minVersion = minVersion;
    }

    public boolean supportedByCurrentVersion() {
        return minVersion == null || MinecraftVersion.CURRENT.afterOrEquals(minVersion);
    }

    @Override
    public String componentName() {
        return componentName;
    }

}
