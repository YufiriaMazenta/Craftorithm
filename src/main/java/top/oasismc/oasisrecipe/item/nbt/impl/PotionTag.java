package top.oasismc.oasisrecipe.item.nbt.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum PotionTag implements NBTTag {

    INSTANCE;

    private final String key;

    PotionTag() { key = "potion"; }

    @Override
    public void importTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta) {
            List<String> potions = new ArrayList<>();
            for (PotionEffect effect : ((PotionMeta) meta).getCustomEffects()) {
                potions.add(effect.getType().getKey().getKey() + " " + effect.getDuration() + " " + effect.getAmplifier());
            }
            config.set(itemName + "." + key, potions);
        }
    }

    @Override
    public void loadTag(String itemName, ItemStack item, YamlConfiguration config) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta) {
            addPotionEffect(((PotionMeta) meta), itemName, config);
        }
        item.setItemMeta(meta);
    }

    @Override
    public String getKey() {
        return key;
    }

    private void addPotionEffect(PotionMeta meta, String itemName, YamlConfiguration config) {
        List<String> potions = config.getStringList(itemName + "." + key);
        if (potions.size() == 0) {
            return;
        }
        potions.forEach(potionStr -> {
            int spaceIndex1 = potionStr.indexOf(" ");
            String type = potionStr.substring(0, spaceIndex1).toLowerCase(Locale.ROOT);
            PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.minecraft(type));
            if (effectType == null) {
                OasisRecipe.info("&c" + type + " is not a valid effect type");
                return;
            }
            int spaceIndex2 = potionStr.indexOf(" ", spaceIndex1 + 1);
            int time = Integer.parseInt(potionStr.substring(spaceIndex1 + 1, spaceIndex2));
            int level = Integer.parseInt(potionStr.substring(spaceIndex2 + 1));
            PotionEffect effect = new PotionEffect(effectType, time * 20, level);
            meta.addCustomEffect(effect, true);
        });
    }

}
