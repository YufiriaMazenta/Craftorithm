package top.oasismc.oasisrecipe.item;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.nbt.NBTManager;

public enum ItemLoader implements Listener {

    INSTANCE;

    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        itemsAdderLoaded = true;
    }

    private static boolean itemsAdderLoaded = false;
    private static final ConfigFile itemFile = new ConfigFile("items.yml");
    private static final ConfigFile resultFile = new ConfigFile("results.yml");

    public static ConfigFile getItemFile() {
        return itemFile;
    }

    public static ConfigFile getResultFile() {
        return resultFile;
    }

    public static ItemStack getItemFromConfig(String itemName) {
        if (!itemName.startsWith("items:") && !itemName.startsWith("results:")) {
            Material material = Material.matchMaterial(itemName);
            if (material == null)
                throw new IllegalArgumentException(itemName + " is a non-existent item type");
            return new ItemStack(material);
        }

        ConfigFile config;
        if (itemName.startsWith("items:")) {
            config = itemFile;
        } else {
            config = resultFile;
        }
        itemName = itemName.substring(itemName.indexOf(":") + 1);

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
            ItemStack item = getItemFromConfig(str);
            return new RecipeChoice.ExactChoice(item);
        }

        if (str.startsWith("ItemsAdder:")) {
            if (!itemsAdderLoaded)
                throw new IllegalArgumentException("ItemsAdder plugin not loading");
            str = str.substring(str.indexOf(":") + 1);
            CustomStack stack = CustomStack.getInstance(str);
            if (stack != null) {
                return new RecipeChoice.ExactChoice(stack.getItemStack());
            } else {
                throw new IllegalArgumentException(str + " is not a valid ItemsAdder item");
            }
        }

        Material material = Material.matchMaterial(str);
        if (material == null)
            return new RecipeChoice.MaterialChoice(Material.BEDROCK);
        else
            return new RecipeChoice.MaterialChoice(material);
    }

}
