package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.Color;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

public enum PotionColorTag implements NBTTag {

    INSTANCE;

    private final String key;

    PotionColorTag() { key = "potionColor"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta) {
            Color color = ((PotionMeta) meta).getColor();
            if (color != null) {
                config.set(itemName + "." + key, color.asRGB());
            }
        }
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta) {
            int color = config.getInt(itemName + "." + key, 0x000000);
            ((PotionMeta) meta).setColor(Color.fromRGB(color));
        }
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

}
