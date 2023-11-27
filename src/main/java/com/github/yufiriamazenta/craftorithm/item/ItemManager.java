package com.github.yufiriamazenta.craftorithm.item;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.nms.item.Item;
import crypticlib.nms.item.ItemFactory;
import crypticlib.util.FileUtil;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ItemManager {

    private static final Map<String, YamlConfigWrapper> itemFileMap = new HashMap<>();
    private static final Map<String, ItemStack> itemMap = new HashMap<>();
    private static final File itemFileFolder = new File(Craftorithm.instance().getDataFolder(), "items");

    public static void reloadItemManager() {
        reloadItemFiles();
        reloadItems();
    }

    public static void reloadItemFiles() {
        itemFileMap.clear();
        if (!itemFileFolder.exists()) {
            boolean mkdirResult = itemFileFolder.mkdir();
            if (!mkdirResult)
                throw new RuntimeException("Create item folder failed");
        }
        List<File> allFiles = FileUtil.allFiles(itemFileFolder, FileUtil.YAML_FILE_PATTERN);
        if (allFiles.isEmpty()) {
            Craftorithm.instance().saveResource("items/example_item.yml", false);
            allFiles.add(new File(itemFileFolder, "example_item.yml"));
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(itemFileFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            itemFileMap.put(key, new YamlConfigWrapper(file));
        }
    }

    public static void reloadItems() {
        itemMap.clear();
        for (String namespace : itemFileMap.keySet()) {
            YamlConfigWrapper itemFile = itemFileMap.get(namespace);
            Set<String> itemKeySet = itemFile.config().getKeys(false);
            for (String itemKey : itemKeySet) {
                ConfigurationSection config = itemFile.config().getConfigurationSection(itemKey);
                loadItem(namespace + ":" + itemKey, config);
            }
        }
    }

    private static void loadItem(String itemKey, ConfigurationSection config) {
        try {
            Item item = ItemFactory.item(config);
            ItemStack bukkitItem = item.buildBukkit();
            itemMap.put(itemKey, bukkitItem);
        } catch (Exception e) {
            LangUtil.info("load.item_load_exception", CollectionsUtil.newStringHashMap("<item_name>", itemKey));
            e.printStackTrace();
        }
    }


    public static void addCraftorithmItem(String namespace, String itemName, ItemStack bukkit) {
        YamlConfigWrapper itemConfigFile;
        if (!itemFileMap.containsKey(namespace)) {
            File itemFile = new File(itemFileFolder, namespace + ".yml");
            if (!itemFile.exists()) {
                FileUtil.createNewFile(itemFile);
            }
            itemConfigFile = new YamlConfigWrapper(itemFile);
            itemFileMap.put(namespace, itemConfigFile);
        } else {
            itemConfigFile = itemFileMap.get(namespace);
        }
        Item item = ItemFactory.item(bukkit);
        itemConfigFile.set(itemName, item.toMap());
        itemConfigFile.saveConfig();
        itemMap.put(namespace + ":" + itemName, bukkit);
    }

    public static boolean isCraftorithmItem(String itemName) {
        return itemMap.containsKey(itemName);
    }

    public static ItemStack getCraftorithmItem(String itemName) {
        return itemMap.getOrDefault(itemName, new ItemStack(Material.AIR)).clone();
    }

    public static Map<String, ItemStack> itemMap() {
        return itemMap;
    }

    public static Map<String, YamlConfigWrapper> itemFileMap() {
        return itemFileMap;
    }

    public static File itemFileFolder() {
        return itemFileFolder;
    }

    public static ItemStack matchItem(String itemName) {
        ItemStack item;
        int lastSpaceIndex = itemName.lastIndexOf(" ");
        int amountScale = 1;
        if (lastSpaceIndex > 0) {
            amountScale = Integer.parseInt(itemName.substring(lastSpaceIndex + 1));
            itemName = itemName.substring(0, lastSpaceIndex);
        }
        itemName = itemName.replace(" ", "");
        if (itemName.contains(":")) {
            int index = itemName.indexOf(":");
            String namespace = itemName.substring(0, index), key = itemName.substring(index + 1);
            switch (namespace) {
                case "items":
                    item = getCraftorithmItem(key);
                    break;
                case "items_adder":
                    item = PluginHookUtil.getItemsAdderItem(key);
                    break;
                case "oraxen":
                    item = PluginHookUtil.getOraxenItem(key);
                    break;
                case "mythic_mobs":
                    item = PluginHookUtil.getMythicMobsItem(key);
                    break;
                default:
                    throw new IllegalArgumentException(namespace + " is not a valid item namespace");
            }
        } else {
            Material material = Material.matchMaterial(itemName);
            if (material == null) {
                throw new IllegalArgumentException(itemName + " is a not exist item type");
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
        if (ItemUtil.isAir(item))
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
