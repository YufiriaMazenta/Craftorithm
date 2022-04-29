package top.oasismc.oasisrecipe.item.nbt;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.nbt.api.NBTTag;
import top.oasismc.oasisrecipe.item.nbt.impl.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NBTManager {

    private static final Map<String, NBTTag> nbtTagMap;

    static {
        nbtTagMap = new ConcurrentHashMap<>();
        regDefTags();
    }

    public static Map<String, NBTTag> getNbtTagMap() {
        return nbtTagMap;
    }

    public static void regTag(NBTTag tagImpl) {
        nbtTagMap.put(tagImpl.getKey(), tagImpl);
    }

    private static void regDefTags() {
        regTag(CustomNameTag.INSTANCE);
        regTag(AttributesTag.INSTANCE);
        if (OasisRecipe.getPlugin().getVanillaVersion() >= 14)
            regTag(CustomModelDataTag.INSTANCE);
        regTag(DamageableTag.INSTANCE);
        regTag(EnchantsTag.INSTANCE);
        regTag(ItemFlagTag.INSTANCE);
        regTag(LoreTag.INSTANCE);
        regTag(PotionColorTag.INSTANCE);
        regTag(PotionTag.INSTANCE);
        regTag(SkullOwnerTag.INSTANCE);
        regTag(UnbreakableTag.INSTANCE);
    }

    public static void loadNBT2Item(String itemName, ItemStack item, ConfigFile config) {
        if (item.getItemMeta() == null)
            return;
        Set<String> keys = config.getConfig().getKeys(true);
        Set<String> tags = keys.stream().filter(key -> key.startsWith(itemName)).collect(Collectors.toSet());
        for (String tag : tags) {
            String key = tag.substring(tag.indexOf(".") + 1);
            if (nbtTagMap.containsKey(key))
                nbtTagMap.get(key).loadTag(itemName, item, (YamlConfiguration) config.getConfig());
        }
    }

    public static void importItem(String itemName, ItemStack item, ConfigFile config) {
        config.getConfig().set(itemName + ".material", item.getType().name());
        config.getConfig().set(itemName + ".amount", item.getAmount());
        if (item.getItemMeta() != null) {
            Set<String> tagSet = nbtTagMap.keySet();
            if (config.getPath().equals("items.yml"))
                tagSet.remove(AttributesTag.INSTANCE.getKey());
            for (String tag : tagSet) {
                nbtTagMap.get(tag).importTag(itemName, item, (YamlConfiguration) config.getConfig());
            }
        }
        config.saveConfig();
    }

}
