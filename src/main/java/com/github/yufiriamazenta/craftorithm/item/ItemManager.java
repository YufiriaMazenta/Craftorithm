package com.github.yufiriamazenta.craftorithm.item;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.YamlFileWrapper;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.FileUtil;
import com.github.yufiriamazenta.craftorithm.util.ItemUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
                    itemMap.put(fileKey + ":" + itemKey, ItemUtil.getItemFromConfig(itemFile.getConfig(), itemKey));
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
        List<File> allFiles = FileUtil.getAllFiles(itemFileFolder);
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

    /**
     * 获取一个物品的Craftorithm名字
     * @param item 传入的物品
     * @param ignoreAmount 是否忽略数量
     * @param regNew 如果不存在，是否将此物品注册
     * @param regName 注册的名字
     * @return 传入的物品名字
     */
    public static String getItemName(ItemStack item, boolean ignoreAmount, boolean regNew, String namespace, String regName) {
        if (ItemUtil.checkItemIsAir(item))
            return null;
        AtomicReference<String> itemName = new AtomicReference<>("");
        itemMap.forEach((key, savedItem) -> {
            if (ignoreAmount) {
                if (savedItem.isSimilar(item)) {
                    itemName.set(key);
                }
            } else {
                if (savedItem.equals(item)) {
                    itemName.set(key);
                }
            }
        });
        if (!itemName.get().isEmpty())
            return itemName.get();
        if (regNew) {
            addCraftorithmItem(namespace, regName, item);
            return namespace + ":" + regName;
        }
        return null;
    }

}
