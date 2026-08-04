package pers.yufiria.craftorithm.item;

import crypticlib.CrypticLibPlugin;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.util.IOHelper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@LifecycleTaskSettings(
    rules = {
        @LifecycleRule(lifeCycle = Lifecycle.ENABLE),
        @LifecycleRule(lifeCycle = Lifecycle.RELOAD)
    }
)
public enum CraftorithmItemProvider implements ItemProvider, LifecycleTask {

    INSTANCE;
    public final File ITEM_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder(), "items");
    private final Map<String, ItemStack> itemMap;
    private final Map<String, BukkitConfigWrapper> itemConfigFileMap;

    CraftorithmItemProvider() {
        itemConfigFileMap = new HashMap<>();
        itemMap = new ConcurrentHashMap<>();
    }

    @Override
    public @NotNull String namespace() {
        return "items";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        for (Map.Entry<String, ItemStack> itemStackEntry : itemMap.entrySet()) {
            ItemStack item = itemStackEntry.getValue();
            if (item.isSimilar(itemStack)) {
                NamespacedItemId namespacedItemId = new NamespacedItemId(namespace(), itemStackEntry.getKey());
                if (ignoreAmount) {
                    return new NamespacedItemIdStack(namespacedItemId);
                } else {
                    return new NamespacedItemIdStack(namespacedItemId, itemStack.getAmount());
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        ItemStack item = itemMap.get(itemId);
        if (item == null)
            return null;
        return item.clone();
    }



    private void loadItemFiles() {
        itemConfigFileMap.clear();
        if (!ITEM_FILE_FOLDER.exists()) {
            boolean mkdirResult = ITEM_FILE_FOLDER.mkdir();
            if (!mkdirResult)
                throw new RuntimeException("Create item folder failed");
        }
        List<File> allFiles = IOHelper.allYamlFiles(ITEM_FILE_FOLDER);
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
                    LangUtils.info(Languages.ITEM_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<item_name>", itemKey));
                    e.printStackTrace();
                }
            }
        }
    }


    public NamespacedItemIdStack regCraftorithmItem(String namespace, String itemName, ItemStack item) {
        BukkitConfigWrapper itemConfigWrapper;
        if (!itemConfigFileMap.containsKey(namespace)) {
            File itemFile = new File(ITEM_FILE_FOLDER, namespace + ".yml");
            if (!itemFile.exists()) {
                IOHelper.createNewFile(itemFile);
            }
            itemConfigWrapper = new BukkitConfigWrapper(itemFile);
            itemConfigFileMap.put(namespace, itemConfigWrapper);
        } else {
            itemConfigWrapper = itemConfigFileMap.get(namespace);
        }
        itemConfigWrapper.set(itemName, item);
        itemConfigWrapper.saveConfig();
        String key = namespace + ":" + itemName;
        itemMap.put(key, item);
        return new NamespacedItemIdStack(
            new NamespacedItemId(
                namespace(),
                key
            ),
            item.getAmount()
        );
    }

    public Map<String, ItemStack> itemMap() {
        return new HashMap<>(itemMap);
    }

    public Map<String, BukkitConfigWrapper> itemConfigFileMap() {
        return new HashMap<>(itemConfigFileMap);
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        loadItemFiles();
        loadItems();
    }
}
