package top.oasismc.oasisrecipe.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.ConfigFile;

import java.util.List;
import java.util.UUID;

import static top.oasismc.oasisrecipe.OasisRecipe.color;

public class ItemUtil {

    private static final ConfigFile itemFile = new ConfigFile("items.yml");
    private static final ConfigFile resultFile = new ConfigFile("results.yml");

    public static void addEnchants2Meta(ItemMeta meta, String itemName, ConfigFile config) {
        List<String> enchantStrList = config.getConfig().getStringList(itemName + ".enchants");
        for (String enchantStr : enchantStrList) {
            Enchantment enchantType;
            int enchantLevel;
            enchantStr = enchantStr.toLowerCase();
            int spaceIndex = enchantStr.indexOf(" ");
            if (spaceIndex == -1) {
                enchantType = Enchantment.getByKey(NamespacedKey.minecraft(enchantStr));
                enchantLevel = 1;
            } else {
                enchantType = Enchantment.getByKey(NamespacedKey.minecraft(enchantStr.substring(0, spaceIndex)));
                enchantLevel = Integer.parseInt(enchantStr.substring(spaceIndex + 1));
            }
            if (enchantType != null) {
                meta.addEnchant(enchantType, enchantLevel, true);
            }
        }
    }

    public static void addAttribute2Meta(ItemMeta meta, String recipeName, ConfigFile config) {
        List<String> attrs = config.getConfig().getStringList(recipeName + ".attributes");
        if (attrs.size() == 0) {
            return;
        }
        for (String attr : attrs) {
            try {
                int index1 = attr.indexOf(" ");
                int index2 = attr.indexOf(" ", index1 + 1);
                int index3 = attr.indexOf(" ", index2 + 1);
                String type = attr.substring(0, index1);
                double value = Double.parseDouble(attr.substring(index1 + 1, index2));
                String addType = attr.substring(index2 + 1, index3);
                String slot = attr.substring(index3 + 1);
                meta.addAttributeModifier(
                        Attribute.valueOf(type),
                        new AttributeModifier(
                                UUID.randomUUID(),
                                type,
                                value,
                                AttributeModifier.Operation.valueOf("ADD_" + addType),
                                EquipmentSlot.valueOf(slot)
                        )
                );
            } catch (Exception e) { e.printStackTrace(); }
        }
    }


    private static void addLore2Meta(ItemMeta meta, String itemName, ConfigFile config) {
        List<String> loreList = config.getConfig().getStringList(itemName + ".lore");
        if (loreList.size() == 0)
            return;
        for (int i = 0; i < loreList.size(); i++) {
            String coloredLore = color(loreList.get(i));
            loreList.set(i, coloredLore);
        }
        meta.setLore(loreList);
    }

    public static ConfigFile getItemFile() {
        return itemFile;
    }

    public static ConfigFile getResultFile() {
        return resultFile;
    }

    public static ItemStack getItemFromConfig(String itemName) {
        return getItemFromConfig(itemName, true);
    }

    public static ItemStack getItemFromConfig(String itemName, boolean isResult) {
        if (!itemName.startsWith("items:") && !itemName.startsWith("results:")) {
            Material material = Material.matchMaterial(itemName);
            if (material == null)
                throw new IllegalArgumentException(itemName + " is a non-existent item type");
            return new ItemStack(material);
        }//若不以items开头，则返回对应类型的物品

        ConfigFile config;
        if (itemName.startsWith("items:")) {
            itemName = itemName.substring(6);
            config = itemFile;
        } else {
            itemName = itemName.substring(8);
            config = resultFile;
        }

        if (config.getConfig().getString(itemName + ".material", "Null").equals("Null"))
            throw new IllegalArgumentException(itemName + " is not exist");
        String materialStr = config.getConfig().getString(itemName + ".material", "Null");
        Material material = Material.matchMaterial(materialStr);
        if (material == null)
            throw new IllegalArgumentException(materialStr + " is a non-existent item type");
        ItemStack itemStack = new ItemStack(material, config.getConfig().getInt(itemName + ".amount", 1));

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;
        String customName = config.getConfig().getString(itemName + ".name");
        if (customName != null && !customName.equals("")) {
            meta.setDisplayName(color(customName));
        }//设置合成物品的名字
        addLore2Meta(meta, itemName, config);
        addEnchants2Meta(meta, itemName, config);
        for (String flag : config.getConfig().getStringList(itemName + ".hides")) {
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_" + flag));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("HIDE_" + flag + " is not a valid tag");
            }
        }
        if (isResult) {
            addAttribute2Meta(meta, itemName, config);
            if (config.getConfig().getInt(itemName + ".customModelData") != 0)
                meta.setCustomModelData(config.getConfig().getInt(itemName + ".customModelData"));
        }
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(config.getConfig().getInt(itemName + ".durability", 0));
        }
        meta.setUnbreakable(config.getConfig().getBoolean(itemName + ".unbreakable"));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static RecipeChoice getChoiceFromStr(String str) {
        if (str.startsWith("items:") || str.startsWith("results:")) {
            ItemStack item = getItemFromConfig(str, false);
            return new RecipeChoice.ExactChoice(item);
        }
        Material material = Material.matchMaterial(str);
        if (material == null)
            return new RecipeChoice.MaterialChoice(Material.BEDROCK);
        else
            return new RecipeChoice.MaterialChoice(material);
    }

}
