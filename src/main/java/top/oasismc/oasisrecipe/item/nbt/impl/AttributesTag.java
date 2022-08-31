package top.oasismc.oasisrecipe.item.nbt.impl;

import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum AttributesTag implements NBTTag {

    INSTANCE;

    private final String key;

    AttributesTag() { key = "attributes"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        List<String> attributes = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attrMap = meta.getAttributeModifiers();
        if (attrMap != null) {
            attrMap.asMap().forEach((attr, attrModifiers) -> {
                for (AttributeModifier modifier : attrModifiers) {
                    attributes.add(attr.name() + " " + modifier.getAmount() + " " + modifier.getOperation() + " " + modifier.getSlot());
                }
            });
            config.set(itemName + "." + key, attributes);
        }
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        addAttribute2Meta(meta, itemName, config);
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

    private void addAttribute2Meta(ItemMeta meta, String itemName, YamlConfiguration config) {
        List<String> attrs = config.getStringList(itemName + "." + key);
        if (attrs.size() == 0) {
            return;
        }
        for (String attr : attrs) {
            try {
                int index1 = attr.indexOf(" ");
                int index2 = attr.indexOf(" ", index1 + 1);
                int index3 = attr.indexOf(" ", index2 + 1);
                String type = attr.substring(0, index1);
                double value = Double.parseDouble(attr.substring(index1 + 1, index2));
                String addType = attr.substring(index2 + 1, index3);
                String slot = attr.substring(index3 + 1);
                meta.addAttributeModifier(
                        Attribute.valueOf(type),
                        new AttributeModifier(
                                UUID.randomUUID(),
                                type,
                                value,
                                AttributeModifier.Operation.valueOf(addType),
                                EquipmentSlot.valueOf(slot)
                        )
                );
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}
