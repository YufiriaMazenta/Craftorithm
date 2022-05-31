package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

import java.util.ArrayList;
import java.util.List;

public enum EnchantStorageTag implements NBTTag {

    INSTANCE;

    private final String key;

    EnchantStorageTag() { key = "enchantStorage"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof EnchantmentStorageMeta))
            return;
        List<String> enchants = new ArrayList<>();
        EnchantmentStorageMeta esMeta = ((EnchantmentStorageMeta) meta);
        if (esMeta.hasStoredEnchants()) {
            esMeta.getStoredEnchants().forEach((enchant, lvl) -> {
                String type = enchant.toString();
                type = type.substring(type.indexOf(", ") + 2, type.length() - 1);
                enchants.add(type + " " + lvl);
            });
        }
        if (enchants.size() > 0)
            config.set(itemName + "." + key, enchants);
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof EnchantmentStorageMeta))
            return;
        loadEnchantStorage((EnchantmentStorageMeta) meta, itemName, config);
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

    private void loadEnchantStorage(EnchantmentStorageMeta meta, String itemName, YamlConfiguration config) {
        List<String> enchantStrList = config.getStringList(itemName + "." + key);
        for (String str : enchantStrList) {
            String enchantTypeStr;
            int enchantLevel;
            str = str.toLowerCase();
            int spaceIndex = str.indexOf(" ");
            if (spaceIndex == -1) {
                enchantTypeStr = str;
                enchantLevel = 1;
            } else {
                enchantTypeStr = str.substring(0, spaceIndex);
                enchantLevel = Integer.parseInt(str.substring(spaceIndex + 1));
            }
            Enchantment enchantType = Enchantment.getByKey(NamespacedKey.minecraft(enchantTypeStr));
            if (enchantType == null)
                enchantType = Enchantment.getByName(str);
            if (enchantType == null) {
                OasisRecipe.info("&c" + enchantTypeStr + " is not a valid enchant type");
            }
            if (enchantType != null) {
                meta.addStoredEnchant(enchantType, enchantLevel, true);
            }
        }
    }

}
