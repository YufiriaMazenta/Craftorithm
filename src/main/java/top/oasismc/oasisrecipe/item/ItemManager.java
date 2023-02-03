package top.oasismc.oasisrecipe.item;

import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;
import top.oasismc.oasisrecipe.util.ItemUtil;

import java.io.File;
import java.util.*;

import static top.oasismc.oasisrecipe.util.FileUtil.getAllFiles;

public class ItemManager {

    public static final Map<String, YamlFileWrapper> itemFileMap = new HashMap<>();
    public static final Map<String, ItemStack> itemMap = new HashMap<>();
    public static final File itemFileFolder = new File(OasisRecipe.getInstance().getDataFolder().getPath(), "items");

    public static void loadItems() {
//        loadItemFiles();
//        for (String fileKey : itemFileMap.keySet()) {
//            YamlFileWrapper itemFile = itemFileMap.get(fileKey);
//            Set<String> itemKeySet = itemFile.getConfig().getKeys(false);
//            for (String itemKey : itemKeySet) {
//                ItemUtil.getItemFromConfig(itemFile.getConfig(), itemKey);
//            }
//        }
    }

    public static ItemStack getPluginItemStack(String itemName) {
        //
        return null;
    }

    private static void loadItemFiles() {
        if (!itemFileFolder.exists()) {
            itemFileFolder.mkdir();
        }
        List<File> allFiles = getAllFiles(itemFileFolder);
        if (allFiles.size() < 1) {
            OasisRecipe.getInstance().saveResource("items/example_item.yml", false);
        }
        for (File file : allFiles) {
            String key = file.getPath();
            key = key.substring(28).toLowerCase(Locale.ROOT);
            key = key.substring(0, key.indexOf("."));
            key = key.replace('\\', '/');
            itemFileMap.put(key, new YamlFileWrapper(file));
        }
    }

}
