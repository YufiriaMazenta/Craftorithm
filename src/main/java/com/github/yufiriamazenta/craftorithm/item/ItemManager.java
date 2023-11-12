package com.github.yufiriamazenta.craftorithm.item;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.FileUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.nms.item.Item;
import crypticlib.util.ItemUtil;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ItemManager {

    private static final Map<String, YamlConfigWrapper> itemFileMap = new HashMap<>();
    private static final Map<String, ItemStack> itemMap = new HashMap<>();
    private static final File itemFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "items");

    public static void loadItemManager() {
        loadItemFiles();
        loadItems();
    }

    public static void loadItems() {
        itemMap.clear();
        for (String fileKey : itemFileMap.keySet()) {
            YamlConfigWrapper itemFile = itemFileMap.get(fileKey);
            Set<String> itemKeySet = itemFile.config().getKeys(false);
            for (String itemKey : itemKeySet) {
                try {
                    Item item = crypticlib.nms.item.ItemManager.item(itemFile.config().getConfigurationSection(itemKey));
                    itemMap.put(fileKey + ":" + itemKey, item.buildBukkit());
                } catch (Exception e) {
                    LangUtil.info("load.item_load_exception", ContainerUtil.newHashMap("<item_name>", fileKey + ":" + itemKey));
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addCraftorithmItem(String itemFileName, String itemName, ItemStack item) {
        YamlConfigWrapper yamlConfig;
        if (!ItemManager.getItemFileMap().containsKey(itemFileName)) {
            File itemFile = new File(ItemManager.getItemFileFolder(), itemFileName + ".yml");
            if (!itemFile.exists()) {
                FileUtil.createNewFile(itemFile);
            }
            yamlConfig = new YamlConfigWrapper(itemFile);
            itemFileMap.put(itemName, yamlConfig);
        } else {
            yamlConfig = itemFileMap.get(itemFileName);
        }
        Item libItem = crypticlib.nms.item.ItemManager.item(item);
        yamlConfig.set(itemName, libItem.toMap());
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
            itemFileMap.put(key, new YamlConfigWrapper(file));
        }
    }

    public static Map<String, ItemStack> getItemMap() {
        return itemMap;
    }

    public static Map<String, YamlConfigWrapper> getItemFileMap() {
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
        if (itemStr.contains(":")) {
            String namespace = itemStr.substring(0, itemStr.indexOf(":")), key = itemStr.substring(itemStr.indexOf(":") + 1);
            switch (namespace) {
                case "items":
                    item = getCraftorithmItem(key);
                    break;
                case "items_adder":
                    item = getItemsAdderItem(key);
                    break;
                case "oraxen":
                    item = getOraxenItem(key);
                    break;
                case "mythic_mobs":
                    item = getMythicMobsItem(key);
                    break;
                default:
                    throw new IllegalArgumentException(namespace + " is not a valid item namespace");
            }
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
        if (ItemUtil.isItemInvalidate(item))
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

    public static ItemStack getItemsAdderItem(String itemStr) {
        CustomStack customStack = CustomStack.getInstance(itemStr);
        if (customStack == null) {
            throw new IllegalArgumentException(itemStr + " is a not exist ItemsAdder item");
        }
        return customStack.getItemStack();
    }

    public static ItemStack getOraxenItem(String itemStr) {
        if (!OraxenItems.exists(itemStr)) {
            throw new IllegalArgumentException(itemStr + " is a not exist Oraxen item");
        }
        return OraxenItems.getItemById(itemStr).build();
    }

    public static ItemStack getMythicMobsItem(String itemStr) {
        ItemExecutor executor = MythicBukkit.inst().getItemManager();
        Optional<MythicItem> itemOptional = executor.getItem(itemStr);
        if (!itemOptional.isPresent()) {
            throw new IllegalArgumentException(itemStr + " is not a valid MythicMobs item");
        }
        MythicItem mythicItem = itemOptional.get();
        int amount = mythicItem.getAmount();
        return BukkitAdapter.adapt(itemOptional.get().generateItemStack(amount));
    }

}
