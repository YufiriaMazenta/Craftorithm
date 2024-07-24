package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.config.ConfigWrapper;
import crypticlib.util.FileHelper;
import org.bukkit.OfflinePlayer;
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
    private final Map<String, BukkitConfigWrapper> itemConfigFileMap;

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

    @Override
    public @Nullable ItemStack getItem(String itemName, OfflinePlayer player) {
        return getItem(itemName);
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
        List<File> allFiles = FileHelper.allYamlFiles(ITEM_FILE_FOLDER);
        if (allFiles.isEmpty()) {
            Craftorithm.instance().saveResource("items/example_item.yml", false);
            allFiles.add(new File(ITEM_FILE_FOLDER, "example_item.yml"));
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(ITEM_FILE_FOLDER.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            itemConfigFileMap.put(key, new BukkitConfigWrapper(file));
        }
    }

    private void loadItems() {
        itemMap.clear();
        for (String namespace : itemConfigFileMap.keySet()) {
            BukkitConfigWrapper itemFile = itemConfigFileMap.get(namespace);
            Set<String> itemKeySet = itemFile.config().getKeys(false);
            for (String itemKey : itemKeySet) {
                try {
                    ItemStack item = itemFile.config().getItemStack(itemKey);
                    if (item == null) {
                        throw new NullPointerException("Item " + itemKey + " is null");
                    }
                    itemMap.put(namespace + ":" + itemKey, item);
                } catch (Exception e) {
                    LangUtil.info(Languages.LOAD_ITEM_LOAD_EXCEPTION, CollectionsUtil.newStringHashMap("<item_name>", itemKey));
                    e.printStackTrace();
                }
            }
        }
    }


    public String regCraftorithmItem(String namespace, String itemName, ItemStack item) {
        BukkitConfigWrapper itemConfigFile;
        if (!itemConfigFileMap.containsKey(namespace)) {
            File itemFile = new File(ITEM_FILE_FOLDER, namespace + ".yml");
            if (!itemFile.exists()) {
                FileHelper.createNewFile(itemFile);
            }
            itemConfigFile = new BukkitConfigWrapper(itemFile);
            itemConfigFileMap.put(namespace, itemConfigFile);
        } else {
            itemConfigFile = itemConfigFileMap.get(namespace);
        }
        itemConfigFile.set(itemName, item);
        itemConfigFile.saveConfig();
        String key = namespace + ":" + itemName;
        itemMap.put(key, item);
        return key;
    }

    public Map<String, ItemStack> itemMap() {
        return new HashMap<>(itemMap);
    }

    public Map<String, BukkitConfigWrapper> itemConfigFileMap() {
        return new HashMap<>(itemConfigFileMap);
    }

}
