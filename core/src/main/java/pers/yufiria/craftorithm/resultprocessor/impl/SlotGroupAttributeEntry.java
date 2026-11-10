package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

import java.util.Locale;

/**
 * 1.20.5 起的属性修饰器: 槽位是 {@link EquipmentSlotGroup}
 * <p>
 * 只在服务端版本达到 1.20.5 时才由工厂创建, 低版本不会加载到这个类
 */
record SlotGroupAttributeEntry(
    Attribute attribute,
    AttributeModifier.Operation operation,
    ValueProvider<Double> amount,
    EquipmentSlotGroup slotGroup,
    NamespacedKey modifierKey
) implements AttributesProcessorFactory.AttributeEntry {

    static SlotGroupAttributeEntry create(
        Attribute attribute,
        AttributeModifier.Operation operation,
        ValueProvider<Double> amount,
        @Nullable String slotName,
        String name
    ) {
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.ANY;
        if (slotName != null) {
            EquipmentSlotGroup parsedGroup = EquipmentSlotGroup.getByName(slotName.toLowerCase(Locale.ROOT));
            if (parsedGroup == null) {
                CrypticLib.warn("attributes has unknown slot '" + slotName + "' for attribute '" + name + "', using ANY");
            } else {
                slotGroup = parsedGroup;
            }
        }
        NamespacedKey modifierKey = new NamespacedKey(
            "craftorithm",
            name.toLowerCase(Locale.ROOT).replace(":", "_")
        );
        return new SlotGroupAttributeEntry(attribute, operation, amount, slotGroup, modifierKey);
    }

    @Override
    public void apply(ItemMeta meta, double amount) {
        meta.addAttributeModifier(attribute, new AttributeModifier(modifierKey, amount, operation, slotGroup));
    }

}
