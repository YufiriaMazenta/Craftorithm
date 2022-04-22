package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

public class SkullOwnerTag implements NBTTag {

    private static final SkullOwnerTag TAG = new SkullOwnerTag();

    private final String key;

    private SkullOwnerTag() { key = "skullOwner"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta) {
            OfflinePlayer offlinePlayer = ((SkullMeta) meta).getOwningPlayer();
            if (offlinePlayer != null) {
                config.set(itemName + ".skullOwner", offlinePlayer.getName());
            }
        }
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta) {
            String skullName = config.getString(itemName + ".skullOwner");
            if (skullName != null)
                ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(skullName));
        }
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

    public static NBTTag getInstance() {
        return TAG;
    }

}
