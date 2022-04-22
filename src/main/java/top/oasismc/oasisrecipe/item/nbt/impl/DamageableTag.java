package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

public class DamageableTag implements NBTTag {

    private static final DamageableTag TAG = new DamageableTag();

    private final String key;

    private DamageableTag() {this.key = "durability"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable)
            config.set(itemName + ".durability", ((Damageable) meta).getDamage());
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(config.getInt(itemName + ".durability", 0));
        }
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

    public static NBTTag getInstance() { return TAG; }

}
