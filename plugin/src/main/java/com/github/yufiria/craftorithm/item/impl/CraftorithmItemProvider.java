package com.github.yufiria.craftorithm.item.impl;

import com.github.yufiria.craftorithm.Craftorithm;
import com.github.yufiria.craftorithm.item.ItemProvider;
import crypticlib.config.ConfigWrapper;
import crypticlib.nms.item.ItemFactory;
import crypticlib.nms.item.NbtItem;
import crypticlib.util.FileUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum CraftorithmItemProvider implements ItemProvider {

    INSTANCE;
    public final File ITEM_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder(), "items");
    private final Map<String, ItemStack> itemMap;
    private final Map<String, ConfigWrapper> itemConfigFileMap;

    CraftorithmItemProvider() {
        itemConfigFileMap = new HashMap<>();
        itemMap = new ConcurrentHashMap<>();
        loadItemFiles();
        loadItems();
    }

    @Override
    public @NotNull String namespace() {
        return "items";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        for (Map.Entry<String, ItemStack> itemStackEntry : itemMap.entrySet()) {
            ItemStack item = itemStackEntry.getValue();
            if (ignoreAmount) {
                if (item.isSimilar(itemStack))
                    return itemStackEntry.getKey();
            } else {
                if (item.equals(itemStack)) {
                    return itemStackEntry.getKey();
                }
            }

        }
        return null;
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        ItemStack item = itemMap.get(itemName);
        if (item == null)
            return null;
        return item.clone();
    }

    public void reloadItemProvider() {
        loadItemFiles();
        loadItems();
    }

    private void loadItemFiles() {
        itemConfigFileMap.clear();
        if (!ITEM_FILE_FOLDER.exists()) {
            boolean mkdirResult = ITEM_FILE_FOLDER.mkdir();
            if (!mkdirResult)
                throw new RuntimeException("Create item folder failed");
        }
        List<File> allFiles = FileUtil.allYamlFiles(ITEM_FILE_FOLDER);
        if (allFiles.isEmpty()) {
            Craftorithm.instance().saveResource("items/example_item.yml", false);
            allFiles.add(new File(ITEM_FILE_FOLDER, "example_item.yml"));
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(ITEM_FILE_FOLDER.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            ConfigWrapper configWrapper = new ConfigWrapper(file);
            itemConfigFileMap.put(key, configWrapper);
        }
    }

    private void loadItems() {
        itemMap.clear();
        for (String namespace : itemConfigFileMap.keySet()) {
            ConfigWrapper itemFile = itemConfigFileMap.get(namespace);
            Set<String> itemKeySet = itemFile.config().getKeys(false);
            for (String itemKey : itemKeySet) {
                ConfigurationSection config = itemFile.config().getConfigurationSection(itemKey);
                loadItem(namespace + ":" + itemKey, config);
            }
        }
    }

    private void loadItem(String itemKey, ConfigurationSection config) {
        try {
            NbtItem item = ItemFactory.item(config);
            ItemStack bukkitItem = item.saveNbtToItem();
            itemMap.put(itemKey, bukkitItem);
        } catch (Exception e) {
//            LangUtil.info(Languages.LOAD_ITEM_LOAD_EXCEPTION, CollectionsUtil.newStringHashMap("<item_name>", itemKey));
            e.printStackTrace();
        }
    }

    public String regCraftorithmItem(String namespace, String itemName, ItemStack item) {
        ConfigWrapper itemConfigFile;
        if (!itemConfigFileMap.containsKey(namespace)) {
            File itemFile = new File(ITEM_FILE_FOLDER, namespace + ".yml");
            if (!itemFile.exists()) {
                FileUtil.createNewFile(itemFile);
            }
            itemConfigFile = new ConfigWrapper(itemFile);
            itemConfigFileMap.put(namespace, itemConfigFile);
        } else {
            itemConfigFile = itemConfigFileMap.get(namespace);
        }
        NbtItem nbtItem = ItemFactory.item(item);
        ConfigurationSection itemCfgSection = itemConfigFile.config().createSection(itemName);
        nbtItem.toMap().forEach(itemCfgSection::set);
        itemConfigFile.saveConfig();
        String key = namespace + ":" + itemName;
        itemMap.put(key, item);
        return key;
    }

    public Map<String, ItemStack> itemMap() {
        return new HashMap<>(itemMap);
    }

    public Map<String, ConfigWrapper> itemConfigFileMap() {
        return new HashMap<>(itemConfigFileMap);
    }

}
