package top.oasismc.oasisrecipe.item.nbt.api;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public interface NBTTag {

    void importTag(String itemName, ItemStack item, YamlConfiguration config);

    void loadTag(String itemName, ItemStack item, YamlConfiguration config);

    String getKey();

}
