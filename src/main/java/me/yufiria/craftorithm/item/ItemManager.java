package me.yufiria.craftorithm.item;

import me.yufiria.craftorithm.Craftorithm;
import me.yufiria.craftorithm.config.YamlFileWrapper;
import me.yufiria.craftorithm.util.FileUtil;
import me.yufiria.craftorithm.util.ItemUtil;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.ContainerUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.yufiria.craftorithm.util.FileUtil.getAllFiles;

public class ItemManager {

    private static final Map<String, YamlFileWrapper> itemFileMap = new HashMap<>();
    private static final Map<String, ItemStack> itemMap = new HashMap<>();
    private static final File itemFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "items");

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
                    addCraftorithmItem(fileKey, itemKey, ItemUtil.getItemFromConfig(itemFile.getConfig(), itemKey));
                } catch (Exception e) {
                    LangUtil.info("load.item_load_exception", ContainerUtil.newHashMap("<item_name>", fileKey + ":" + itemKey));
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addCraftorithmItem(String itemFileName, String itemName, ItemStack item) {
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

    public static boolean isCraftorithmItem(String itemName) {
        return itemMap.containsKey(itemName);
    }

    public static ItemStack getCraftorithmItem(String itemName) {
        return itemMap.getOrDefault(itemName, new ItemStack(Material.AIR)).clone();
    }

    public static void loadItemFiles() {
        itemFileMap.clear();
        if (!itemFileFolder.exists()) {
            boolean mkdirResult = itemFileFolder.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = getAllFiles(itemFileFolder);
        if (allFiles.size() < 1) {
            Craftorithm.getInstance().saveResource("items/example_item.yml", false);
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

    public static ItemStack matchCraftorithmItem(String itemStr) {
        ItemStack item;
        int lastSpaceIndex = itemStr.lastIndexOf(" ");
        int amountScale = 1;
        if (lastSpaceIndex > 0) {
            amountScale = Integer.parseInt(itemStr.substring(lastSpaceIndex + 1));
            itemStr = itemStr.substring(0, lastSpaceIndex);
        }
        itemStr = itemStr.replace(" ", "");
        if (itemStr.startsWith("items:")) {
            itemStr = itemStr.substring("items:".length());
            item = getCraftorithmItem(itemStr);
        } else {
            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                throw new IllegalArgumentException(itemStr + " is a not exist item type");
            }
            item = new ItemStack(material);
        }
        item.setAmount(item.getAmount() * amountScale);
        return item;
    }
}
