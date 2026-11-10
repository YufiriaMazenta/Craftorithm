package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

import java.util.Locale;
import java.util.UUID;

/**
 * 1.20.5 以下的属性修饰器: 槽位是 {@link EquipmentSlot}
 * <p>
 * 独立成类而不是和 {@link SlotGroupAttributeEntry} 共用一个类型:
 * 共用时构造器签名里会带上 EquipmentSlotGroup, 低版本创建实例就会 NoClassDefFoundError
 */
@SuppressWarnings("removal")
record SlotAttributeEntry(
    Attribute attribute,
    AttributeModifier.Operation operation,
    ValueProvider<Double> amount,
    @Nullable EquipmentSlot slot,
    String name
) implements AttributesProcessorFactory.AttributeEntry {

    static SlotAttributeEntry create(
        Attribute attribute,
        AttributeModifier.Operation operation,
        ValueProvider<Double> amount,
        @Nullable String slotName,
        String name
    ) {
        EquipmentSlot slot = null;
        if (slotName != null) {
            try {
                slot = EquipmentSlot.valueOf(slotName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                CrypticLib.warn("attributes has unknown slot '" + slotName + "' for attribute '" + name + "', using no slot");
            }
        }
        return new SlotAttributeEntry(attribute, operation, amount, slot, name);
    }

    @Override
    public void apply(ItemMeta meta, double amount) {
        meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), name, amount, operation, slot));
    }

}
