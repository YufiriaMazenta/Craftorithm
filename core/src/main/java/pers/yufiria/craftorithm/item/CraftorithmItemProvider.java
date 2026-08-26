package pers.yufiria.craftorithm.item;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.config.node.impl.bukkit.StringListConfig;
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
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.hook.item.ItemPluginHook;
import pers.yufiria.craftorithm.hook.item.ItemPluginHookManager;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@LifecycleTaskSettings(
    rules = {
        @LifecycleRule(lifeCycle = Lifecycle.ENABLE),
        @LifecycleRule(lifeCycle = Lifecycle.RELOAD, priority = -1)
    }
)
public enum CraftorithmItemProvider implements ItemPluginHook, ItemProvider, LifecycleTask {

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
                NamespacedItemId namespacedItemId = NamespacedItemId.of(namespace(), itemStackEntry.getKey());
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
        idItemMap.clear();
        for (Map.Entry<String, BukkitConfigWrapper> entry : itemConfigFileMap.entrySet()) {
            String namespace = entry.getKey();
            BukkitConfigWrapper itemFile = entry.getValue();
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
        BukkitConfigWrapper itemConfigWrapper = itemConfigFileMap.computeIfAbsent(namespace, ns -> {
            File itemFile = new File(ITEM_FILE_FOLDER, ns + ".yml");
            if (!itemFile.exists()) {
                IOHelper.createNewFile(itemFile);
            }
            return new BukkitConfigWrapper(itemFile);
        });
        itemConfigWrapper.set(itemName, item);
        itemConfigWrapper.saveConfig();
        String namespaceItemId = namespace + ":" + itemName;
        idItemMap.put(namespaceItemId, item);
        itemBuckets.computeIfAbsent(
            ItemBucketKey.of(item),
            fp -> new ConcurrentHashMap<>()
        ).put(namespaceItemId, item);
        return new NamespacedItemIdStack(
            NamespacedItemId.of(
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

    /* ItemPluginHook 相关 */

    @Override
    public ItemProvider itemProvider() {
        return this;
    }

    @Override
    public String pluginName() {
        return Craftorithm.instance().pluginName();
    }

    @Override
    public boolean hook() {
        return true;
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        if (lifeCycle == Lifecycle.ENABLE) {
            ItemPluginHookManager.INSTANCE.addItemPluginHook(this);
        }
        //检查物品源优先级的配置里是否包含Craftorithm的物品源，如果不包含则添加到末尾
        StringListConfig itemPluginHookPriorityConfig = PluginConfigs.ITEM_PLUGIN_HOOK_PRIORITY;
        List<String> originValue = itemPluginHookPriorityConfig.value();
        String pluginName = Craftorithm.instance().pluginName();
        if (!originValue.contains(pluginName)) {
            List<String> value = new ArrayList<>(originValue);
            value.add(pluginName);
            itemPluginHookPriorityConfig.setValue(value);
            itemPluginHookPriorityConfig.saveConfig();
            CrypticLib.info("Detected that the item hook priority configuration did not include Craftorithm, Automatically added");
        }

        loadItemFiles();
        loadItems();
    }

    /**
     * 物品分桶key，用于快速索引和预筛选
     * <p>
     * 由 Material + 版本相关的额外 key 组成，不同版本使用不同的字段：
     * <ul>
     *   <li>1.21.4+：item_model + custom_model_data_component</li>
     *   <li>1.21.2~1.21.3：item_model + custom_model_data</li>
     *   <li>1.21.2 以下：custom_model_data</li>
     * </ul>
     */
    private record ItemBucketKey(
        @NotNull Material material,
        @Nullable Object extraKey1,
        @Nullable Object extraKey2
    ) {

        private ItemBucketKey {
        }

        public static ItemBucketKey of(@NotNull ItemStack item) {
            return of(item.getType(), item.getItemMeta());
        }

        public static ItemBucketKey of(@NotNull Material material, @Nullable ItemMeta meta) {
            if (meta == null) {
                return new ItemBucketKey(material, null, null);
            }
            MinecraftVersion version = MinecraftVersion.current();
            if (version.afterOrEquals(MinecraftVersion.V1_21_4)) {
                return new ItemBucketKey(material, extractItemModel(meta), extractCustomModelDataComponent(meta));
            }
            if (version.afterOrEquals(MinecraftVersion.V1_21_2)) {
                return new ItemBucketKey(material, extractItemModel(meta), extractLegacyCustomModelData(meta));
            }
            return new ItemBucketKey(material, extractLegacyCustomModelData(meta), null);
        }

        private static @Nullable NamespacedKey extractItemModel(@NotNull ItemMeta meta) {
            return meta.getItemModel();
        }

        private static @Nullable Integer extractLegacyCustomModelData(@NotNull ItemMeta meta) {
            if (meta.hasCustomModelData()) {
                return meta.getCustomModelData();
            }
            return null;
        }

        private static @Nullable Object extractCustomModelDataComponent(@NotNull ItemMeta meta) {
            if (meta.hasCustomModelDataComponent()) {
                return meta.getCustomModelDataComponent();
            }
            return null;
        }
    }
}
