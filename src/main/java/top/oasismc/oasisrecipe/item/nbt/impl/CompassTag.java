package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

import java.util.StringJoiner;

public enum CompassTag implements NBTTag {

    INSTANCE;

    private final String key;

    CompassTag() { key = "location"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof CompassMeta))
            return;
        StringJoiner joiner = new StringJoiner("");
        Location loc = ((CompassMeta) meta).getLodestone();
        if (loc == null)
            return;
        joiner.add(loc.getWorld().getName())
                .add(", ").add(loc.getX() + "")
                .add(", ").add(loc.getY() + "")
                .add(loc.getZ() + "");
        config.set(itemName + "." + key, joiner.toString());
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof CompassMeta))
            return;
        String locStr = config.getString(itemName + "." + key, "");
        if (!locStr.contains(","))
            return;
        int index1 = locStr.indexOf(',');
        World world = Bukkit.getWorld(locStr.substring(0, index1));
        if (world == null)
            return;
        int index2 = locStr.indexOf(',', index1 + 1);
        double x = Double.parseDouble(locStr.substring(index1 + 1, index2));
        int index3 = locStr.indexOf(',', index2 + 1);
        double y = Double.parseDouble(locStr.substring(index2 + 1, index3));
        double z = Double.parseDouble(locStr.substring(index3));
        Location loc = new Location(world, x, y, z);
        ((CompassMeta) meta).setLodestone(loc);
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

}
