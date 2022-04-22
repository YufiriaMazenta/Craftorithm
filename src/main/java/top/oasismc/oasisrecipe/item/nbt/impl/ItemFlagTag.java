package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

import java.util.ArrayList;
import java.util.List;

public class ItemFlagTag implements NBTTag {

    private static final ItemFlagTag TAG = new ItemFlagTag();

    private final String key;

    private ItemFlagTag() { key = "hides"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        List<String> flags = new ArrayList<>();
        for (ItemFlag flag : meta.getItemFlags()) {
            flags.add(flag.name().substring(5));
        }
        if (flags.size() != 0)
            config.set(itemName + ".hides", flags);
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        for (String flag : config.getStringList(itemName + ".hides")) {
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_" + flag));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("HIDE_" + flag + " is not a valid tag");
            }
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
