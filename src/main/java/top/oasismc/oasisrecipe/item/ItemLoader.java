package top.oasismc.oasisrecipe.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.nbt.NBTManager;

public class ItemLoader {

    private static final ConfigFile itemFile = new ConfigFile("items.yml");
    private static final ConfigFile resultFile = new ConfigFile("results.yml");

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
        }

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
        NBTManager.loadNBT2Item(itemName, itemStack, config);
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
