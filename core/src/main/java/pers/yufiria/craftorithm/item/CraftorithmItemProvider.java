package pers.yufiria.craftorithm.item;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.util.IOHelper;
import crypticlib.util.ItemHelper;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ENABLE),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, priority = -1, isAsync = true),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE, priority = -1)
    }
)
public enum CraftorithmItemProvider implements ItemPluginHook, ItemProvider, LifecycleTask {

    INSTANCE;
    public final File ITEM_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder(), "items");
    //玩家通过界面创建物品时使用的物品命名空间
    public static final String PLUGIN_CREATED_NAMESPACE = "plugin_created";
    private final Map<ItemBucketKey, Map<String, ItemStack>> itemBuckets;
    private final Map<String, ItemStack> idItemMap;
    private final Map<String, BukkitConfigWrapper> itemConfigFileMap;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * 同一物品桶内存在多个相似物品时, 决定优先返回哪一个ID
     * <p>
     * 通过 {@code /craftorithm item save} 显式命名的物品优先于界面自动登记的 plugin_created 快照,
     * 同类之间按ID字典序排列, 保证同一物品每次解析出的ID都是稳定的
     */
    private static final Comparator<String> ITEM_ID_ORDER = Comparator
        .comparingInt((String itemId) -> itemId.startsWith(PLUGIN_CREATED_NAMESPACE + ":") ? 1 : 0)
        .thenComparing(Comparator.naturalOrder());

    /**
     * 创建物品桶, 桶内条目按 {@link #ITEM_ID_ORDER} 有序, 依赖其迭代顺序做ID匹配
     */
    private static Map<String, ItemStack> newItemBucketMap() {
        return new ConcurrentSkipListMap<>(ITEM_ID_ORDER);
    }

    CraftorithmItemProvider() {
        idItemMap = new ConcurrentHashMap<>();
        itemConfigFileMap = new ConcurrentHashMap<>();
        itemBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public @NotNull String namespace() {
        return "items";
    }

    @Override
    public @Nullable NamespacedItemId matchItemId(ItemStack itemStack) {
        ItemBucketKey bucketKey = ItemBucketKey.of(itemStack);
        Map<String, ItemStack> itemMap = this.itemBuckets.get(bucketKey);
        if (itemMap == null) {
            return null;
        }

        //桶内条目按 ITEM_ID_ORDER 有序, 命中即返回第一个即为优先级最高的ID
        for (Map.Entry<String, ItemStack> itemStackEntry : itemMap.entrySet()) {
            ItemStack item = itemStackEntry.getValue();
            if (item.isSimilar(itemStack)) {
                return NamespacedItemId.of(namespace(), itemStackEntry.getKey());
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
                        fp -> newItemBucketMap()
                    ).put(namespacedItemId, item);
                } catch (Exception e) {
                    LangUtils.info(Languages.ITEM_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<item_name>", itemKey));
                    CrypticLib.error("Failed to load item " + itemKey, e);
                }
            }
        }
    }

    /**
     * 保存物品并返回其物品id, 用于「保存结果物品全部组件」等需要完整保留物品数据的场景
     * <p>
     * 存在数据值的物品会被完整写入内置物品源, 若已有完全相同的物品则复用已有id;
     * 没有数据值的原版物品没有需要保留的组件, 直接返回原版物品id, 并且不会保存该物品
     *
     * @param item         待保存的物品
     * @param ignoreAmount 是否忽略数量, 为true时id栈的数量恒为1
     * @return 物品id栈, 物品为空气时返回null
     */
    public @Nullable NamespacedItemIdStack saveItem(ItemStack item, boolean ignoreAmount) {
        if (ItemHelper.isAir(item)) {
            return null;
        }
        int amount = ignoreAmount ? 1 : item.getAmount();
        if (!item.hasItemMeta()) {
            return new NamespacedItemIdStack(NamespacedItemId.fromMaterial(item.getType()), amount);
        }
        NamespacedItemId existedItemId = matchItemId(item);
        if (existedItemId != null) {
            return new NamespacedItemIdStack(existedItemId, amount);
        }
        String itemName = item.getType().getKey().getKey();
        if (matchItem(PLUGIN_CREATED_NAMESPACE + ":" + itemName) != null) {
            itemName += "_" + timeFormat.format(System.currentTimeMillis());
        }
        return new NamespacedItemIdStack(
            saveItem(PLUGIN_CREATED_NAMESPACE, itemName, item),
            amount
        );
    }

    /**
     * 强制以某个命名空间和物品id保存某个物品
     * 就算已经有完全相同的物品,也会保存
     * <p>
     * @param namespace 物品命名空间,对应items文件夹里的文件名
     * @param itemName 物品id
     * @param item 要保存的物品
     * @return 是否
     */
    public NamespacedItemId saveItem(String namespace, String itemName, ItemStack item) {
        String namespaceItemId = namespace + ":" + itemName;
        idItemMap.put(namespaceItemId, item);
        itemBuckets.computeIfAbsent(
            ItemBucketKey.of(item),
            fp -> newItemBucketMap()
        ).put(namespaceItemId, item);
        CrypticLibBukkit.scheduler().async(() -> {
            BukkitConfigWrapper itemConfigWrapper = itemConfigFileMap.computeIfAbsent(namespace, ns -> {
                File itemFile = new File(ITEM_FILE_FOLDER, ns + ".yml");
                if (!itemFile.exists()) {
                    IOHelper.createNewFile(itemFile);
                }
                return new BukkitConfigWrapper(itemFile);
            });
            itemConfigWrapper.set(itemName, item);
            itemConfigWrapper.saveConfig();
        });
        return NamespacedItemId.of(
            namespace(),
            namespaceItemId
        );
    }

    public Map<String, ItemStack> idItemMap() {
        return new HashMap<>(idItemMap);
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
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        switch (lifeCycle) {
            case ENABLE -> {
                ItemPluginHookManager.INSTANCE.addItemPluginHook(this);
                checkCraftorithmProvider();
            }
            case RELOAD -> {
                checkCraftorithmProvider();
                loadItemFiles();
                loadItems();
            }
            case DISABLE -> {
                itemBuckets.clear();
                idItemMap.clear();
                itemConfigFileMap.clear();
            }
        }
    }

    private void checkCraftorithmProvider() {
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
