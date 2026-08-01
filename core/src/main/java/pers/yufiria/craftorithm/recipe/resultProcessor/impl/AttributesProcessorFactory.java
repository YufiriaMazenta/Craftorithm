package pers.yufiria.craftorithm.recipe.resultProcessor.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import java.util.List;
import java.util.Map;

import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processor;
import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processorRequireSource;

public class AttributesProcessorFactory implements ComponentProcessorFactory {

    public static final AttributesProcessorFactory INSTANCE = new AttributesProcessorFactory();

    public static final String COMPONENT_NAME = "attributes";

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
        return switch (strategy) {
            case COPY_FROM_SOURCE -> copyFromSource();
            case ADD -> add(data);
            case MERGE_SOURCE -> mergeSource();
            case REMOVE -> remove(data);
        };
    }

    private static ResultProcessor copyFromSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasAttributeModifiers()) {
                resultMeta.setAttributeModifiers(sourceMeta.getAttributeModifiers());
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor mergeSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem) -> {
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
        return processor(COMPONENT_NAME, (sourceItem, resultItem) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            applyAttributeModifiers(resultMeta, attrList);
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor remove(@Nullable ConfigurationSection data) {
        return processor(COMPONENT_NAME, (sourceItem, resultItem) -> {
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

    private static void applyAttributeModifiers(ItemMeta meta, List<Map<?, ?>> attrList) {
        for (Map<?, ?> attrEntry : attrList) {
            String attrName = (String) attrEntry.get("attribute");
            if (attrName == null) continue;
            Attribute attr = Registry.ATTRIBUTE.get(NamespacedKey.fromString(attrName));
            if (attr == null) continue;
            String slotStr = (String) attrEntry.get("slot");
            EquipmentSlotGroup slotGroup = slotStr != null
                ? EquipmentSlotGroup.getByName(slotStr.toLowerCase())
                : EquipmentSlotGroup.ANY;
            String opStr = (String) attrEntry.get("operation");
            AttributeModifier.Operation op = opStr != null
                ? AttributeModifier.Operation.valueOf(opStr.toUpperCase())
                : AttributeModifier.Operation.ADD_NUMBER;
            Object amountObj = attrEntry.get("amount");
            double amount = amountObj instanceof Number n ? n.doubleValue() : 0.0;
            NamespacedKey key = new NamespacedKey("craftorithm", attrName.toLowerCase().replace(":", "_"));
            AttributeModifier modifier = new AttributeModifier(key, amount, op, slotGroup);
            meta.addAttributeModifier(attr, modifier);
        }
    }

}
