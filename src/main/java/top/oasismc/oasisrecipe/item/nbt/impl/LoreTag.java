package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

import java.util.List;

import static top.oasismc.oasisrecipe.OasisRecipe.color;

public enum LoreTag implements NBTTag {

    INSTANCE;

    private final String key;

    LoreTag() { key = "lore"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore != null)
            config.set(itemName + ".lore", lore);
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        addLore2Meta(meta, itemName, config);
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

    private void addLore2Meta(ItemMeta meta, String itemName, YamlConfiguration config) {
        List<String> loreList = config.getStringList(itemName + ".lore");
        if (loreList.size() == 0)
            return;
        for (int i = 0; i < loreList.size(); i++) {
            String coloredLore = color(loreList.get(i));
            loreList.set(i, coloredLore);
        }
        meta.setLore(loreList);
    }

}
