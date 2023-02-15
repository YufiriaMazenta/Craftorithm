package top.oasismc.oasisrecipe.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;
import top.oasismc.oasisrecipe.util.FileUtil;
import top.oasismc.oasisrecipe.util.ItemUtil;
import top.oasismc.oasisrecipe.util.MapUtil;
import top.oasismc.oasisrecipe.util.LangUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static top.oasismc.oasisrecipe.util.FileUtil.getAllFiles;

public class ItemManager {

    private static final Map<String, YamlFileWrapper> itemFileMap = new HashMap<>();
    private static final Map<String, ItemStack> itemMap = new HashMap<>();
    private static final File itemFileFolder = new File(OasisRecipe.getInstance().getDataFolder().getPath(), "items");

    public static void loadItemManager() {
        loadItemFiles();
        loadItems();
    }

    public static void loadItems() {
        itemMap.clear();
        for (String fileKey : itemFileMap.keySet()) {
            YamlFileWrapper itemFile = itemFileMap.get(fileKey);
            Set<String> itemKeySet = itemFile.getConfig().getKeys(false);
            for (String itemKey : itemKeySet) {
                try {
                    addOasisRecipeItem(fileKey, itemKey, ItemUtil.getItemFromConfig(itemFile.getConfig(), itemKey));
                } catch (Exception e) {
                    LangUtil.info("load.item_load_exception", MapUtil.newHashMap("<item_name>", fileKey + ":" + itemKey));
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addOasisRecipeItem(String itemFileName, String itemName, ItemStack item) {
        YamlFileWrapper yamlFileWrapper;
        if (!ItemManager.getItemFileMap().containsKey(itemFileName)) {
            File itemFile = new File(ItemManager.getItemFileFolder(), itemFileName + ".yml");
            if (!itemFile.exists()) {
                FileUtil.createNewFile(itemFile);
            }
            yamlFileWrapper = new YamlFileWrapper(itemFile);
            itemFileMap.put(itemName, yamlFileWrapper);
        } else {
            yamlFileWrapper = itemFileMap.get(itemFileName);
        }
        ItemUtil.saveItem2Config(item, yamlFileWrapper, itemName);
        itemMap.put(itemFileName + ":" + itemName, item);
    }

    public static boolean isOasisRecipeItem(String itemName) {
        return itemMap.containsKey(itemName);
    }

    public static ItemStack getOasisRecipeItem(String itemName) {
        return itemMap.getOrDefault(itemName, new ItemStack(Material.AIR)).clone();
    }

    public static void loadItemFiles() {
        itemFileMap.clear();
        if (!itemFileFolder.exists()) {
            itemFileFolder.mkdir();
        }
        List<File> allFiles = getAllFiles(itemFileFolder);
        if (allFiles.size() < 1) {
            OasisRecipe.getInstance().saveResource("items/example_item.yml", false);
            allFiles.add(new File(itemFileFolder, "example_item.yml"));
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(itemFileFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            itemFileMap.put(key, new YamlFileWrapper(file));
        }
    }

    public static Map<String, ItemStack> getItemMap() {
        return itemMap;
    }

    public static Map<String, YamlFileWrapper> getItemFileMap() {
        return itemFileMap;
    }

    public static File getItemFileFolder() {
        return itemFileFolder;
    }

}
