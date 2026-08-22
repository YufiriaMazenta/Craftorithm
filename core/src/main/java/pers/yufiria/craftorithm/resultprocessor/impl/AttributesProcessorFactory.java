package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

import java.util.*;
import java.util.function.Function;

import static pers.yufiria.craftorithm.resultprocessor.impl.ProcessorUtils.processor;
import static pers.yufiria.craftorithm.resultprocessor.impl.ProcessorUtils.processorRequireSource;

public class AttributesProcessorFactory implements ComponentProcessorFactory {

    public static final AttributesProcessorFactory INSTANCE = new AttributesProcessorFactory();

    public static final String COMPONENT_NAME = "attributes";

    private static final Map<String, Function<ConfigurationSection, ResultProcessor>> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("copy_from_source", data -> copyFromSource());
        HANDLERS.put("add", AttributesProcessorFactory::add);
        HANDLERS.put("merge_source", data -> mergeSource());
        HANDLERS.put("remove", AttributesProcessorFactory::remove);
    }

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = HANDLERS.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(COMPONENT_NAME + " does not support type: " + type);
        }
        return handler.apply(data);
    }

    private static ResultProcessor copyFromSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasAttributeModifiers()) {
                resultMeta.setAttributeModifiers(sourceMeta.getAttributeModifiers());
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor mergeSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasAttributeModifiers()) {
                sourceMeta.getAttributeModifiers().entries().forEach(entry -> {
                    resultMeta.addAttributeModifier(entry.getKey(), entry.getValue());
                });
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor add(@Nullable ConfigurationSection data) {
        List<Map<?, ?>> attrList = data != null ? data.getMapList("value") : List.of();
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            applyAttributeModifiers(resultMeta, attrList);
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor remove(@Nullable ConfigurationSection data) {
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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
    }

    @SuppressWarnings("removal")
    private static void applyAttributeModifiers(ItemMeta meta, List<Map<?, ?>> attrList) {
        for (Map<?, ?> attrEntry : attrList) {
            String attrName = (String) attrEntry.get("attribute");
            if (attrName == null) continue;
            Attribute attr = Registry.ATTRIBUTE.get(NamespacedKey.fromString(attrName));
            if (attr == null) continue;
            String opStr = (String) attrEntry.get("operation");
            AttributeModifier.Operation op = opStr != null
                ? AttributeModifier.Operation.valueOf(opStr.toUpperCase())
                : AttributeModifier.Operation.ADD_NUMBER;
            Object amountObj = attrEntry.get("amount");
            double amount = amountObj instanceof Number n ? n.doubleValue() : 0.0;
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_5)) {
                String slotStr = (String) attrEntry.get("slot");
                EquipmentSlotGroup slotGroup = slotStr != null
                    ? EquipmentSlotGroup.getByName(slotStr.toLowerCase())
                    : EquipmentSlotGroup.ANY;
                NamespacedKey key = new NamespacedKey("craftorithm", attrName.toLowerCase().replace(":", "_"));
                AttributeModifier modifier = new AttributeModifier(key, amount, op, Objects.requireNonNull(slotGroup));
                meta.addAttributeModifier(attr, modifier);
            } else {
                String slotStr = (String) attrEntry.get("slot");
                EquipmentSlot slot = slotStr != null
                    ? EquipmentSlot.valueOf(slotStr.toUpperCase())
                    : null;
                AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attrName, amount, op, slot);
                meta.addAttributeModifier(attr, modifier);
            }

        }
    }

}
