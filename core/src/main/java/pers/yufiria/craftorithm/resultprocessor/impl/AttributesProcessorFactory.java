package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import crypticlib.MinecraftVersion;
import crypticlib.util.BukkitConfigHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.*;
import java.util.function.Function;

public enum AttributesProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "attributes";

    private final Map<String, Function<ConfigurationSection, ResultProcessor>> handlers = new HashMap<>();

    AttributesProcessorFactory() {
        handlers.put("copy_from_source", data -> copyFromSource());
        handlers.put("add", this::add);
        handlers.put("set", this::set);
        handlers.put("merge_source", data -> mergeSource());
        handlers.put("remove", this::remove);
    }

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = handlers.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(COMPONENT_NAME + " does not support type: " + type);
        }
        return handler.apply(data);
    }

    public Map<String, Function<ConfigurationSection, ResultProcessor>> handlers() {
        return handlers;
    }

    private ResultProcessor copyFromSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasAttributeModifiers()) {
                resultMeta.setAttributeModifiers(sourceMeta.getAttributeModifiers());
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private ResultProcessor mergeSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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

    private ResultProcessor add(@Nullable ConfigurationSection data) {
        List<ParsedAttribute> parsedAttributes = parseAttributes(data);
        return ProcessorUtils.processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            applyAttributeModifiers(resultMeta, parsedAttributes, player, sourceItem, resultItem);
            resultItem.setItemMeta(resultMeta);
        });
    }

    /**
     * 清空结果物品的属性修饰器后写入
     */
    private ResultProcessor set(@Nullable ConfigurationSection data) {
        List<ParsedAttribute> parsedAttributes = parseAttributes(data);
        return ProcessorUtils.processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setAttributeModifiers(null);
            applyAttributeModifiers(resultMeta, parsedAttributes, player, sourceItem, resultItem);
            resultItem.setItemMeta(resultMeta);
        });
    }

    private ResultProcessor remove(@Nullable ConfigurationSection data) {
        return ProcessorUtils.processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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

    /**
     * 加载期把属性列表解析并校验完毕, 合成期只做套用:
     * 让 valueOf/强转/requireNonNull 的失败落在加载期, 而不是每次合成都抛一次异常
     */
    private List<ParsedAttribute> parseAttributes(@Nullable ConfigurationSection data) {
        List<Map<?, ?>> attrList = data != null ? data.getMapList("value") : List.of();
        List<ParsedAttribute> parsedAttributes = new ArrayList<>(attrList.size());
        for (Map<?, ?> attrEntry : attrList) {
            ConfigurationSection attrConfig = BukkitConfigHelper.map2ConfigSection(attrEntry);
            ParsedAttribute parsedAttribute = parseAttribute(attrConfig);
            if (parsedAttribute != null) {
                parsedAttributes.add(parsedAttribute);
            }
        }
        return parsedAttributes;
    }

    @SuppressWarnings("removal")
    private @Nullable ParsedAttribute parseAttribute(ConfigurationSection attrConfig) {
        String attrName = attrConfig.getString("attribute");
        if (attrName == null || attrName.isBlank()) {
            CrypticLib.warn(COMPONENT_NAME + " entry is missing 'attribute', skipped.");
            return null;
        }
        NamespacedKey attrKey = NamespacedKey.fromString(attrName);
        Attribute attr = attrKey != null ? Registry.ATTRIBUTE.get(attrKey) : null;
        if (attr == null) {
            CrypticLib.warn(COMPONENT_NAME + " has unknown attribute '" + attrName + "', skipped");
            return null;
        }
        AttributeModifier.Operation operation;
        String operationStr = attrConfig.getString("operation", "add_number");
        try {
            operation = AttributeModifier.Operation.valueOf(operationStr.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            CrypticLib.warn(COMPONENT_NAME + " has unknown operation '" + operationStr + "' for attribute '" + attrName + "', skipped");
            return null;
        }
        ValueProvider<Double> amountProvider = ValueProviders.decimal(
            attrConfig,
            "amount",
            0D,
            COMPONENT_NAME + "/" + attrName + "/amount"
        );
        String slotStr = attrConfig.getString("slot");
        NamespacedKey modifierKey = new NamespacedKey(
            "craftorithm",
            attrName.toLowerCase(Locale.ROOT).replace(":", "_")
        );
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_5)) {
            EquipmentSlotGroup slotGroup = EquipmentSlotGroup.ANY;
            if (slotStr != null) {
                EquipmentSlotGroup parsedGroup = EquipmentSlotGroup.getByName(slotStr.toLowerCase(Locale.ROOT));
                if (parsedGroup == null) {
                    CrypticLib.warn(COMPONENT_NAME + " has unknown slot '" + slotStr + "' for attribute '" + attrName + "', using ANY");
                } else {
                    slotGroup = parsedGroup;
                }
            }
            return new ParsedAttribute(attr, operation, amountProvider, slotGroup, null, modifierKey, attrName);
        } else {
            EquipmentSlot slot = null;
            if (slotStr != null) {
                try {
                    slot = EquipmentSlot.valueOf(slotStr.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    CrypticLib.warn(COMPONENT_NAME + " has unknown slot '" + slotStr + "' for attribute '" + attrName + "', using no slot");
                }
            }
            return new ParsedAttribute(attr, operation, amountProvider, null, slot, modifierKey, attrName);
        }
    }

    @SuppressWarnings("removal")
    private void applyAttributeModifiers(
        ItemMeta meta,
        List<ParsedAttribute> parsedAttributes,
        @Nullable Player player,
        @Nullable ItemStack sourceItem,
        ItemStack resultItem
    ) {
        for (ParsedAttribute parsed : parsedAttributes) {
            Double amount = parsed.amount().resolve(player, sourceItem, resultItem);
            if (amount == null) {
                //脚本求值失败: 只跳过这一条属性, 其余照常写入
                continue;
            }
            AttributeModifier modifier = parsed.slotGroup() != null
                ? new AttributeModifier(parsed.modifierKey(), amount, parsed.operation(), parsed.slotGroup())
                : new AttributeModifier(UUID.randomUUID(), parsed.name(), amount, parsed.operation(), parsed.slot());
            meta.addAttributeModifier(parsed.attribute(), modifier);
        }
    }

    private record ParsedAttribute(
        Attribute attribute,
        AttributeModifier.Operation operation,
        ValueProvider<Double> amount,
        @Nullable EquipmentSlotGroup slotGroup,
        @Nullable EquipmentSlot slot,
        NamespacedKey modifierKey,
        String name
    ) {
    }

}
