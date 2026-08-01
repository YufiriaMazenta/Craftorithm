package pers.yufiria.craftorithm.recipe.resultProcessor.impl;

import crypticlib.util.IOHelper;
import crypticlib.util.ReflectionHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import java.lang.reflect.Field;
import java.util.List;

public class CustomPersistentDataProcessorFactory implements ComponentProcessorFactory {

    public static final String COMPONENT_NAME = "custom_persistent_data";

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
        return switch (strategy) {
            case COPY_FROM_SOURCE -> createCopyFromSource(data);
            case ADD -> createAdd(data);
            case MERGE_SOURCE -> createMergeSource(data);
            case REMOVE -> createRemove(data);
        };
    }

    private ResultProcessor createCopyFromSource(@Nullable ConfigurationSection data) {
        NamespacedKey key = parseKey(data);
        PersistentDataType<?, ?> type = parseType(data);
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return COMPONENT_NAME;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                if (sourceItem == null) return;
                ItemMeta sourceMeta = sourceItem.getItemMeta();
                ItemMeta resultMeta = resultItem.getItemMeta();
                PersistentDataContainer sourcePdc = sourceMeta.getPersistentDataContainer();
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                if (key != null && type != null) {
                    if (sourcePdc.has(key, type)) {
                        copyPdcValue(sourcePdc, resultPdc, key, type);
                    }
                } else {
                    sourcePdc.copyTo(resultPdc, true);
                }
                resultItem.setItemMeta(resultMeta);
            }
        };
    }

    private ResultProcessor createAdd(@Nullable ConfigurationSection data) {
        if (data == null || data.getKeys(false).isEmpty()) {
            throw new IllegalArgumentException("custom_persistent_data add requires data with entries");
        }
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return COMPONENT_NAME;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                ItemMeta resultMeta = resultItem.getItemMeta();
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                for (String keyStr : data.getKeys(false)) {
                    NamespacedKey entryKey = NamespacedKey.fromString(keyStr);
                    if (entryKey == null) continue;
                    Object value = data.get(keyStr);
                    if (value instanceof String s) {
                        resultPdc.set(entryKey, PersistentDataType.STRING, s);
                    } else if (value instanceof Integer i) {
                        resultPdc.set(entryKey, PersistentDataType.INTEGER, i);
                    } else if (value instanceof Long l) {
                        resultPdc.set(entryKey, PersistentDataType.LONG, l);
                    } else if (value instanceof Double d) {
                        resultPdc.set(entryKey, PersistentDataType.DOUBLE, d);
                    } else if (value instanceof Float f) {
                        resultPdc.set(entryKey, PersistentDataType.FLOAT, f);
                    } else if (value instanceof Byte b) {
                        resultPdc.set(entryKey, PersistentDataType.BYTE, b);
                    } else if (value instanceof Boolean b) {
                        resultPdc.set(entryKey, PersistentDataType.BOOLEAN, b);
                    } else {
                        resultPdc.set(entryKey, PersistentDataType.STRING, String.valueOf(value));
                    }
                }
                resultItem.setItemMeta(resultMeta);
            }
        };
    }

    private ResultProcessor createMergeSource(@Nullable ConfigurationSection data) {
        NamespacedKey key = parseKey(data);
        PersistentDataType<?, ?> type = parseType(data);
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return COMPONENT_NAME;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                if (sourceItem == null) return;
                ItemMeta sourceMeta = sourceItem.getItemMeta();
                ItemMeta resultMeta = resultItem.getItemMeta();
                PersistentDataContainer sourcePdc = sourceMeta.getPersistentDataContainer();
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                if (key != null && type != null) {
                    if (!resultPdc.has(key, type) && sourcePdc.has(key, type)) {
                        copyPdcValue(sourcePdc, resultPdc, key, type);
                    }
                } else {
                    sourcePdc.getKeys().forEach(k -> {
                        if (!resultPdc.has(k)) {
                            copyKnownValue(sourcePdc, resultPdc, k);
                        }
                    });
                }
                resultItem.setItemMeta(resultMeta);
            }
        };
    }

    private ResultProcessor createRemove(@Nullable ConfigurationSection data) {
        if (data == null || data.getKeys(false).isEmpty()) {
            return new ResultProcessor() {
                @Override
                public String processorName() {
                    return COMPONENT_NAME;
                }

                @Override
                public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                    ItemMeta resultMeta = resultItem.getItemMeta();
                    resultMeta.getPersistentDataContainer().getKeys().forEach(
                        key -> resultMeta.getPersistentDataContainer().remove(key)
                    );
                    resultItem.setItemMeta(resultMeta);
                }
            };
        }
        List<String> keyNames = data.getStringList("keys");
        if (keyNames.isEmpty()) {
            // 单个 key
            String singleKey = data.getString("key");
            if (singleKey != null) {
                keyNames = List.of(singleKey);
            }
        }
        List<String> finalKeyNames = keyNames;
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return COMPONENT_NAME;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                ItemMeta resultMeta = resultItem.getItemMeta();
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                for (String keyStr : finalKeyNames) {
                    NamespacedKey key = NamespacedKey.fromString(keyStr);
                    if (key != null) {
                        resultPdc.remove(key);
                    }
                }
                resultItem.setItemMeta(resultMeta);
            }
        };
    }

    @Nullable
    private static NamespacedKey parseKey(@Nullable ConfigurationSection data) {
        if (data == null) return null;
        String keyStr = data.getString("key");
        if (keyStr != null) {
            return NamespacedKey.fromString(keyStr);
        }
        return null;
    }

    @Nullable
    private static PersistentDataType<?, ?> parseType(@Nullable ConfigurationSection data) {
        if (data == null) return null;
        String typeStr = data.getString("type");
        if (typeStr != null) {
            return parseDataType(typeStr);
        }
        return null;
    }

    private static PersistentDataType<?, ?> parseDataType(String typeStr) {
        String upperTypeStr = typeStr.toUpperCase();
        Class<PersistentDataType> dataTypeClass = PersistentDataType.class;
        Field field = ReflectionHelper.getField(dataTypeClass, upperTypeStr);
        if (field == null) {
            IOHelper.info("&eUnknown PersistentDataType: " + typeStr);
            return null;
        }
        Object dataType = ReflectionHelper.getFieldObj(field, null);
        if (dataType == null) {
            IOHelper.info("&eUnknown PersistentDataType: " + typeStr);
            return null;
        }
        return (PersistentDataType<?, ?>) dataType;
    }

    @SuppressWarnings("unchecked")
    private static <T, Z> void copyPdcValue(PersistentDataContainer from, PersistentDataContainer to, NamespacedKey key, PersistentDataType<T, Z> type) {
        Z value = from.get(key, type);
        if (value != null) {
            to.set(key, type, value);
        }
    }

    private static void copyKnownValue(PersistentDataContainer from, PersistentDataContainer to, NamespacedKey key) {
        if (from.has(key, PersistentDataType.STRING)) {
            to.set(key, PersistentDataType.STRING, from.get(key, PersistentDataType.STRING));
        } else if (from.has(key, PersistentDataType.INTEGER)) {
            to.set(key, PersistentDataType.INTEGER, from.get(key, PersistentDataType.INTEGER));
        } else if (from.has(key, PersistentDataType.LONG)) {
            to.set(key, PersistentDataType.LONG, from.get(key, PersistentDataType.LONG));
        } else if (from.has(key, PersistentDataType.DOUBLE)) {
            to.set(key, PersistentDataType.DOUBLE, from.get(key, PersistentDataType.DOUBLE));
        } else if (from.has(key, PersistentDataType.FLOAT)) {
            to.set(key, PersistentDataType.FLOAT, from.get(key, PersistentDataType.FLOAT));
        } else if (from.has(key, PersistentDataType.BYTE)) {
            to.set(key, PersistentDataType.BYTE, from.get(key, PersistentDataType.BYTE));
        } else if (from.has(key, PersistentDataType.BOOLEAN)) {
            to.set(key, PersistentDataType.BOOLEAN, from.get(key, PersistentDataType.BOOLEAN));
        }
    }

}
