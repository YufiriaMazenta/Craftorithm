package pers.yufiria.craftorithm.item;

import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.util.IOHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    private final Map<ItemBucketKey, Map<String, ItemStack>> itemBuckets;
    private final Map<String, ItemStack> idItemMap;
    private final Map<String, BukkitConfigWrapper> itemConfigFileMap;

    CraftorithmItemProvider() {
        idItemMap = new ConcurrentHashMap<>();
        itemConfigFileMap = new HashMap<>();
        itemBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public @NotNull String namespace() {
        return "items";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        ItemBucketKey bucketKey = ItemBucketKey.of(itemStack);
        Map<String, ItemStack> itemMap = this.itemBuckets.get(bucketKey);
        if (itemMap == null) {
            return null;
        }

        for (Map.Entry<String, ItemStack> itemStackEntry : itemMap.entrySet()) {
            ItemStack item = itemStackEntry.getValue();
            if (item.isSimilar(itemStack)) {
                NamespacedItemId namespacedItemId = new NamespacedItemId(namespace(), itemStackEntry.getKey());
                return new NamespacedItemIdStack(
                    namespacedItemId,
                    ignoreAmount ? 1 : itemStack.getAmount()
                );
            }
        }
        return null;
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        ItemStack item = idItemMap.get(itemId);
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
        itemBuckets.clear();
        for (String namespace : itemConfigFileMap.keySet()) {
            BukkitConfigWrapper itemFile = itemConfigFileMap.get(namespace);
            Set<String> itemKeySet = itemFile.config().getKeys(false);
            for (String itemKey : itemKeySet) {
                try {
                    ItemStack item = itemFile.config().getItemStack(itemKey);
                    if (item == null) {
                        throw new NullPointerException("Item " + itemKey + " is null");
                    }
                    String namespacedItemId = namespace + ":" + itemKey;
                    idItemMap.put(namespacedItemId, item);
                    itemBuckets.computeIfAbsent(
                        ItemBucketKey.of(item),
                        fp -> new ConcurrentHashMap<>()
                    ).put(namespacedItemId, item);
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
        String namespaceItemId = namespace + ":" + itemName;
        idItemMap.put(namespaceItemId, item);
        itemBuckets.computeIfAbsent(
            ItemBucketKey.of(item),
            fp -> new ConcurrentHashMap<>()
        ).put(namespaceItemId, item);
        return new NamespacedItemIdStack(
            new NamespacedItemId(
                namespace(),
                namespaceItemId
            ),
            item.getAmount()
        );
    }

    public Map<String, ItemStack> idItemMap() {
        return new HashMap<>(idItemMap);
    }

    public Map<ItemBucketKey, Map<String, ItemStack>> itemBuckets() {
        return itemBuckets;
    }

    public Map<String, BukkitConfigWrapper> itemConfigFileMap() {
        return new HashMap<>(itemConfigFileMap);
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        loadItemFiles();
        loadItems();
    }

    /**
     * 物品指纹，用于快速索引和预筛选
     * <p>
     * 由 Material + 版本相关的额外 key 组成，不同版本使用不同的字段：
     * <ul>
     *   <li>1.21.2+：item_model（NamespacedKey）</li>
     *   <li>1.21.2 以下：custom_model_data（int）</li>
     * </ul>
     */
    public record ItemBucketKey(@NotNull Material material, @Nullable Object extraKey) {

        private static final boolean USE_ITEM_MODEL = MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21_2);

        public static ItemBucketKey of(@NotNull ItemStack item) {
            return new ItemBucketKey(item.getType(), extractExtraKey(item));
        }

        private static @Nullable Object extractExtraKey(ItemStack item) {
            if (USE_ITEM_MODEL) {
                return extractItemModel(item);
            }
            return extractCustomModelData(item);
        }

        private static @Nullable NamespacedKey extractItemModel(ItemStack item) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return null;
            }
            try {
                return meta.getItemModel();
            } catch (NoSuchMethodError e) {
                return null;
            }
        }

        private static @Nullable Integer extractCustomModelData(ItemStack item) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasCustomModelData()) {
                return meta.getCustomModelData();
            }
            return null;
        }
    }
}
