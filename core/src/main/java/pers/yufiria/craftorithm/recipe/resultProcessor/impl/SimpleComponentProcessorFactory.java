package pers.yufiria.craftorithm.recipe.resultProcessor.impl;

import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public enum SimpleComponentProcessorFactory implements ComponentProcessorFactory {

    ALL("all", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processor("all", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("display_name", (sourceItem, resultItem) -> {
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
    ENCHANTMENTS("enchantments", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processor("enchantments", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasEnchants()) {
                        sourceMeta.getEnchants().forEach((enchant, level) -> {
                            if (resultMeta.hasEnchant(enchant)) {
                                if (resultMeta.getEnchantLevel(enchant) > level) {
                                    return;
                                }
                                resultMeta.removeEnchant(enchant);
                            }
                            resultMeta.addEnchant(enchant, level, true);
                        });
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    List<EnchantmentEntry> enchants = parseEnchantments(data);
                    yield processor("enchantments", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        enchants.forEach(e -> resultMeta.addEnchant(e.enchantment(), e.level(), true));
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case MERGE_SOURCE -> processor("enchantments", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasEnchants()) {
                        sourceMeta.getEnchants().forEach((enchant, level) -> {
                            if (resultMeta.hasEnchant(enchant)) {
                                if (resultMeta.getEnchantLevel(enchant) >= level) {
                                    return;
                                }
                                resultMeta.removeEnchant(enchant);
                            }
                            resultMeta.addEnchant(enchant, level, true);
                        });
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("enchantments", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (data == null || data.getKeys(false).isEmpty()) {
                        for (Enchantment ench : new ArrayList<>(resultMeta.getEnchants().keySet())) {
                            resultMeta.removeEnchant(ench);
                        }
                    } else {
                        List<String> removeList = data.getStringList("value");
                        if (removeList.isEmpty()) {
                            removeList = List.copyOf(data.getKeys(false));
                        }
                        for (String enchName : removeList) {
                            Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.fromString(enchName));
                            if (ench != null && resultMeta.hasEnchant(ench)) {
                                resultMeta.removeEnchant(ench);
                            }
                        }
                    }
                    resultItem.setItemMeta(resultMeta);
                });
            };
        }
    },
    ATTRIBUTES("attributes", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processor("attributes", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasAttributeModifiers()) {
                        resultMeta.setAttributeModifiers(sourceMeta.getAttributeModifiers());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    List<ConfigurationSection> attrList = data != null ? data.getMapList("value").stream()
                        .filter(m -> m instanceof ConfigurationSection)
                        .map(m -> (ConfigurationSection) m)
                        .toList() : List.of();
                    yield processor("attributes", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        applyAttributeModifiers(resultMeta, attrList);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case MERGE_SOURCE -> processor("attributes", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasAttributeModifiers()) {
                        sourceMeta.getAttributeModifiers().entries().forEach(entry -> {
                            resultMeta.addAttributeModifier(entry.getKey(), entry.getValue());
                        });
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("attributes", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (data == null || data.getKeys(false).isEmpty()) {
                        resultMeta.setAttributeModifiers(null);
                    } else {
                        List<String> removeList = data.getStringList("value");
                        for (String attrName : removeList) {
                            Attribute attr = Registry.ATTRIBUTE.get(NamespacedKey.fromString(attrName));
                            if (attr != null) {
                                resultMeta.removeAttributeModifier(attr);
                            }
                        }
                    }
                    resultItem.setItemMeta(resultMeta);
                });
            };
        }
    },
    ITEM_FLAG("item_flag", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processor("item_flag", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.removeItemFlags(ItemFlag.values());
                    sourceMeta.getItemFlags().forEach(resultMeta::addItemFlags);
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    List<String> flagNames = data.getStringList("value");
                    yield processor("item_flag", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        for (String flagName : flagNames) {
                            resultMeta.addItemFlags(ItemFlag.valueOf(flagName.toUpperCase()));
                        }
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case MERGE_SOURCE -> processor("item_flag", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    sourceMeta.getItemFlags().forEach(resultMeta::addItemFlags);
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("item_flag", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (data == null || data.getKeys(false).isEmpty()) {
                        resultMeta.removeItemFlags(ItemFlag.values());
                    } else {
                        List<String> removeList = data.getStringList("value");
                        for (String flagName : removeList) {
                            resultMeta.removeItemFlags(ItemFlag.valueOf(flagName.toUpperCase()));
                        }
                    }
                    resultItem.setItemMeta(resultMeta);
                });
            };
        }
    },
    LORE("lore", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processor("lore", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasLore()) {
                        resultMeta.setLore(sourceMeta.getLore());
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case ADD -> {
                    List<String> lines = data.getStringList("value");
                    yield processor("lore", (sourceItem, resultItem) -> {
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setLore(lines);
                        resultItem.setItemMeta(resultMeta);
                    });
                }
                case MERGE_SOURCE -> processor("lore", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta.hasLore()) {
                        List<String> baseLore = sourceMeta.getLore();
                        if (resultMeta.hasLore()) {
                            List<String> merged = new ArrayList<>(resultMeta.getLore());
                            merged.addAll(baseLore);
                            resultMeta.setLore(merged);
                        } else {
                            resultMeta.setLore(baseLore);
                        }
                    }
                    resultItem.setItemMeta(resultMeta);
                });
                case REMOVE -> processor("lore", (sourceItem, resultItem) -> {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (data == null || data.getKeys(false).isEmpty()) {
                        resultMeta.setLore(null);
                    } else {
                        List<String> removeList = data.getStringList("value");
                        if (resultMeta.hasLore()) {
                            List<String> current = new ArrayList<>(resultMeta.getLore());
                            current.removeAll(removeList);
                            resultMeta.setLore(current.isEmpty() ? null : current);
                        }
                    }
                    resultItem.setItemMeta(resultMeta);
                });
            };
        }
    },
    TRIM("trim", null) {
        @Override
        public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
            return switch (strategy) {
                case COPY_FROM_SOURCE -> processor("trim", (sourceItem, resultItem) -> {
                    ItemMeta sourceMeta = sourceItem.getItemMeta();
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    if (sourceMeta instanceof ArmorMeta baseArmorMeta
                        && resultMeta instanceof ArmorMeta resultArmorMeta
                        && baseArmorMeta.hasTrim()) {
                        resultArmorMeta.setTrim(baseArmorMeta.getTrim());
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
                case COPY_FROM_SOURCE -> processor("unbreakable", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("custom_model_data", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("food", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("hide_tooltip", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("item_name", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("max_stack_size", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("rarity", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("tool", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("fire_resistance", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("custom_model_data_component", (sourceItem, resultItem) -> {
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
                case COPY_FROM_SOURCE -> processor("item_model", (sourceItem, resultItem) -> {
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

    private static ResultProcessor processor(String name, BiConsumer<ItemStack, ItemStack> action) {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return name;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                action.accept(sourceItem, resultItem);
            }
        };
    }

    private static ResultProcessor unsupported(String component, ProcessingStrategy strategy) {
        throw new UnsupportedOperationException(component + " does not support " + strategy);
    }

    private record EnchantmentEntry(Enchantment enchantment, int level) {}

    private static List<EnchantmentEntry> parseEnchantments(@Nullable ConfigurationSection data) {
        if (data == null) return List.of();
        List<EnchantmentEntry> result = new ArrayList<>();
        for (String key : data.getKeys(false)) {
            Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.fromString(key));
            if (ench != null) {
                int level = data.getInt(key);
                result.add(new EnchantmentEntry(ench, level));
            }
        }
        return result;
    }

    private static void applyAttributeModifiers(ItemMeta meta, List<ConfigurationSection> attrList) {
        for (ConfigurationSection attrEntry : attrList) {
            String attrName = attrEntry.getString("attribute");
            if (attrName == null) continue;
            Attribute attr = Registry.ATTRIBUTE.get(NamespacedKey.fromString(attrName));
            if (attr == null) continue;
            String slotStr = attrEntry.getString("slot");
            EquipmentSlotGroup slotGroup = slotStr != null
                ? EquipmentSlotGroup.getByName(slotStr.toLowerCase())
                : EquipmentSlotGroup.ANY;
            String opStr = attrEntry.getString("operation");
            AttributeModifier.Operation op = opStr != null
                ? AttributeModifier.Operation.valueOf(opStr.toUpperCase())
                : AttributeModifier.Operation.ADD_NUMBER;
            double amount = attrEntry.getDouble("amount");
            NamespacedKey key = new NamespacedKey("craftorithm", attrName.toLowerCase().replace(":", "_"));
            AttributeModifier modifier = new AttributeModifier(key, amount, op, slotGroup);
            meta.addAttributeModifier(attr, modifier);
        }
    }

}
