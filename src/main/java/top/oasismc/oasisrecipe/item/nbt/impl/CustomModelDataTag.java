package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

public enum CustomModelDataTag implements NBTTag {

    INSTANCE;

    private final String key;

    CustomModelDataTag() { key = "customModelData"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasCustomModelData())
            config.set(itemName + ".customModelData", meta.getCustomModelData());
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (config.getInt(itemName + ".customModelData") != 0)
            meta.setCustomModelData(config.getInt(itemName + ".customModelData"));
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

}
