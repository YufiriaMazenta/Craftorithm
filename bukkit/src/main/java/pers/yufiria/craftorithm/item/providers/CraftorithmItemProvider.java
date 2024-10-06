package pers.yufiria.craftorithm.item.providers;

import crypticlib.config.BukkitConfigWrapper;
import crypticlib.util.FileHelper;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.CraftorithmBukkit;
import pers.yufiria.craftorithm.item.ItemProvider;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum CraftorithmItemProvider implements ItemProvider {

    INSTANCE;
    public final File ITEM_FILE_FOLDER = new File(CraftorithmBukkit.instance().getDataFolder(), "items");
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
    public String matchItemId(ItemStack itemStack) {
        for (Map.Entry<String, ItemStack> itemStackEntry : itemMap.entrySet()) {
            ItemStack item = itemStackEntry.getValue();
            if (item.isSimilar(itemStack))
                return itemStackEntry.getKey();
        }
        return null;
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        ItemStack item = itemMap.get(itemId);
        if (item == null)
            return null;
        ItemStack clone = item.clone();
        clone.setAmount(1);
        return clone;
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, OfflinePlayer player) {
        return matchItem(itemId);
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
            BukkitConfigWrapper itemConfigWrapper = itemConfigFileMap.get(namespace);
            Set<String> itemKeySet = itemConfigWrapper.config().getKeys(false);
            for (String itemKey : itemKeySet) {
                try {
                    ItemStack item = itemConfigWrapper.config().getItemStack(itemKey);
                    if (item == null) {
                        throw new NullPointerException("Item " + itemKey + " is null");
                    }
                    itemMap.put(namespace + ":" + itemKey, item);
                } catch (Exception e) {
                    //TODO 提示消息
                    e.printStackTrace();
                }
            }
        }
    }


    public String regCraftorithmItem(String namespace, String itemName, ItemStack item) {
        BukkitConfigWrapper itemConfigWrapper;
        if (!itemConfigFileMap.containsKey(namespace)) {
            File itemFile = new File(ITEM_FILE_FOLDER, namespace + ".yml");
            if (!itemFile.exists()) {
                FileHelper.createNewFile(itemFile);
            }
            itemConfigWrapper = new BukkitConfigWrapper(itemFile);
            itemConfigFileMap.put(namespace, itemConfigWrapper);
        } else {
            itemConfigWrapper = itemConfigFileMap.get(namespace);
        }
        itemConfigWrapper.set(itemName, item);
        itemConfigWrapper.saveConfig();
        String id = namespace + ":" + itemName;
        itemMap.put(id, item);
        return id;
    }

    public Map<String, ItemStack> itemMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    public Map<String, BukkitConfigWrapper> itemConfigFileMap() {
        return new HashMap<>(itemConfigFileMap);
    }

}
