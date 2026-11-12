package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import crypticlib.util.ReflectionHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum CustomPersistentDataProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "custom_persistent_data";

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void declareBehaviors(BehaviorRegistrar registrar) {
        registrar.behavior(Behaviors.COPY_FROM_SOURCE, (context, data) -> createCopyFromSource(data))
            .behavior(Behaviors.ADD, (context, data) -> createAdd(data))
            .behavior(Behaviors.SET, (context, data) -> createSet(data))
            .behavior(Behaviors.MERGE_SOURCE, (context, data) -> createMergeSource(data))
            .behavior(Behaviors.REMOVE, (context, data) -> createRemove(data));
    }

    private ResultProcessor createCopyFromSource(@Nullable ConfigurationSection data) {
        NamespacedKey key = parseKey(data);
        PersistentDataType<?, ?> type = parseType(data);
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/copy_from_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            resultItem.edit(resultMeta -> {
                PersistentDataContainer sourcePdc = sourceMeta.getPersistentDataContainer();
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                if (key == null || type == null) {
                    sourcePdc.copyTo(resultPdc, true);
                    return;
                }
                if (sourcePdc.has(key, type)) {
                    copyPdcValue(sourcePdc, resultPdc, key, type);
                }
            });
        });
    }

    private ResultProcessor createAdd(@Nullable ConfigurationSection data) {
        if (data == null || data.getKeys(false).isEmpty()) {
            //配置缺 data 时只降级这一个组件并告警, 不影响同配方的其它组件
            CrypticLib.warn(COMPONENT_NAME + " add requires data with entries, this processor will be skipped");
            return ProcessorUtils.noop(COMPONENT_NAME);
        }
        return ProcessorUtils.processor(COMPONENT_NAME + "/add", (sourceItem, resultItem, player) ->
            resultItem.edit(resultMeta -> writeEntries(resultMeta.getPersistentDataContainer(), data))
        );
    }

    /**
     * 清空结果物品的全部持久数据后写入
     */
    private ResultProcessor createSet(@Nullable ConfigurationSection data) {
        return ProcessorUtils.processor(COMPONENT_NAME + "/set", (sourceItem, resultItem, player) ->
            resultItem.edit(resultMeta -> {
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                for (NamespacedKey key : List.copyOf(resultPdc.getKeys())) {
                    resultPdc.remove(key);
                }
                if (data != null) {
                    writeEntries(resultPdc, data);
                }
            })
        );
    }

    private void writeEntries(PersistentDataContainer pdc, ConfigurationSection data) {
        for (String keyStr : data.getKeys(false)) {
            NamespacedKey entryKey = NamespacedKey.fromString(keyStr);
            if (entryKey == null) continue;
            Object value = data.get(keyStr);
            if (value instanceof String s) {
                pdc.set(entryKey, PersistentDataType.STRING, s);
            } else if (value instanceof Integer i) {
                pdc.set(entryKey, PersistentDataType.INTEGER, i);
            } else if (value instanceof Long l) {
                pdc.set(entryKey, PersistentDataType.LONG, l);
            } else if (value instanceof Double d) {
                pdc.set(entryKey, PersistentDataType.DOUBLE, d);
            } else if (value instanceof Float f) {
                pdc.set(entryKey, PersistentDataType.FLOAT, f);
            } else if (value instanceof Byte b) {
                pdc.set(entryKey, PersistentDataType.BYTE, b);
            } else if (value instanceof Boolean b) {
                pdc.set(entryKey, PersistentDataType.BOOLEAN, b);
            } else {
                pdc.set(entryKey, PersistentDataType.STRING, String.valueOf(value));
            }
        }
    }

    private ResultProcessor createMergeSource(@Nullable ConfigurationSection data) {
        NamespacedKey key = parseKey(data);
        PersistentDataType<?, ?> type = parseType(data);
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/merge_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            resultItem.edit(resultMeta -> {
                PersistentDataContainer sourcePdc = sourceMeta.getPersistentDataContainer();
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                if (key != null && type != null) {
                    if (!resultPdc.has(key, type) && sourcePdc.has(key, type)) {
                        copyPdcValue(sourcePdc, resultPdc, key, type);
                    }
                    return;
                }
                sourcePdc.getKeys().forEach(k -> {
                    if (!resultPdc.has(k)) {
                        copyKnownValue(sourcePdc, resultPdc, k);
                    }
                });
            });
        });
    }

    private ResultProcessor createRemove(@Nullable ConfigurationSection data) {
        List<NamespacedKey> targets = parseRemoveTargets(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/remove", (sourceItem, resultItem, player) ->
            resultItem.edit(resultMeta -> {
                PersistentDataContainer resultPdc = resultMeta.getPersistentDataContainer();
                if (targets == null) {
                    for (NamespacedKey key : List.copyOf(resultPdc.getKeys())) {
                        resultPdc.remove(key);
                    }
                    return;
                }
                for (NamespacedKey key : targets) {
                    resultPdc.remove(key);
                }
            })
        );
    }

    /**
     * @return null 表示没有写 data, 即清空全部
     */
    private @Nullable List<NamespacedKey> parseRemoveTargets(@Nullable ConfigurationSection data) {
        if (data == null || data.getKeys(false).isEmpty()) {
            return null;
        }
        // value 为统一写法, keys 与 key 为旧写法保留
        List<String> names = data.getStringList("value");
        if (names.isEmpty()) {
            names = data.getStringList("keys");
        }
        if (names.isEmpty()) {
            String singleKey = data.getString("key");
            if (singleKey != null) {
                names = List.of(singleKey);
            }
        }
        List<NamespacedKey> targets = new ArrayList<>(names.size());
        for (String name : names) {
            NamespacedKey key = NamespacedKey.fromString(name);
            if (key == null) {
                CrypticLib.warn(COMPONENT_NAME + "/remove has invalid key '" + name + "', skipped");
                continue;
            }
            targets.add(key);
        }
        return targets;
    }

    @Nullable
    private NamespacedKey parseKey(@Nullable ConfigurationSection data) {
        if (data == null) return null;
        String keyStr = data.getString("key");
        if (keyStr != null) {
            return NamespacedKey.fromString(keyStr);
        }
        return null;
    }

    @Nullable
    private PersistentDataType<?, ?> parseType(@Nullable ConfigurationSection data) {
        if (data == null) return null;
        String typeStr = data.getString("type");
        if (typeStr != null) {
            return parseDataType(typeStr);
        }
        return null;
    }

    private PersistentDataType<?, ?> parseDataType(String typeStr) {
        String upperTypeStr = typeStr.toUpperCase(Locale.ROOT);
        Class<PersistentDataType> dataTypeClass = PersistentDataType.class;
        try {
            Field field = ReflectionHelper.getField(dataTypeClass, upperTypeStr);
            if (field == null) {
                CrypticLib.warn("Unknown PersistentDataType: " + typeStr);
                return null;
            }
            Object dataType = ReflectionHelper.getFieldObj(field, null);
            if (dataType == null) {
                CrypticLib.warn("Unknown PersistentDataType: " + typeStr);
                return null;
            }
            return (PersistentDataType<?, ?>) dataType;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T, Z> void copyPdcValue(PersistentDataContainer from, PersistentDataContainer to, NamespacedKey key, PersistentDataType<T, Z> type) {
        Z value = from.get(key, type);
        if (value != null) {
            to.set(key, type, value);
        }
    }

    private void copyKnownValue(PersistentDataContainer from, PersistentDataContainer to, NamespacedKey key) {
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
        } else if (from.has(key, PersistentDataType.BYTE_ARRAY)) {
            to.set(key, PersistentDataType.BYTE_ARRAY, from.get(key, PersistentDataType.BYTE_ARRAY));
        } else if (from.has(key, PersistentDataType.INTEGER_ARRAY)) {
            to.set(key, PersistentDataType.INTEGER_ARRAY, from.get(key, PersistentDataType.INTEGER_ARRAY));
        } else if (from.has(key, PersistentDataType.LONG_ARRAY)) {
            to.set(key, PersistentDataType.LONG_ARRAY, from.get(key, PersistentDataType.LONG_ARRAY));
        } else if (from.has(key, PersistentDataType.TAG_CONTAINER)) {
            to.set(key, PersistentDataType.TAG_CONTAINER, from.get(key, PersistentDataType.TAG_CONTAINER));
        }
    }

}
