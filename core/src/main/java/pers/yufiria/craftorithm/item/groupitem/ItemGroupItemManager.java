package pers.yufiria.craftorithm.item.groupitem;

import crypticlib.CrypticLibPlugin;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 用于管理物品组的类
 * 物品组物品: 用于标识多个物品的物品
 * 例如物品Tag, 和插件自己的ItemPack
 */
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum ItemGroupItemManager implements LifecycleTask {

    INSTANCE;

    private final NamespacedKey ITEM_GROUP_TYPE_KEY = new NamespacedKey("craftorithm", "item_group_type");
    private final NamespacedKey ITEM_GROUP_ID_KEY = new NamespacedKey("craftorithm", "item_group_id");
    public static final String GROUP_TYPE_TAG = "tag", GROUP_TYPE_ITEM_PACK = "item_pack";
    /**
     * 物品组类型 -> 将裸id解析为物品组的解析器, 解析失败返回null
     * 占位符物品的构建也是基于解析结果, 因此只需要注册解析器
     */
    private final Map<String, Function<String, ItemGroup>> itemGroupResolverMap = new ConcurrentHashMap<>();

    ItemGroupItemManager() {
        registerParsers();
    }

    private void registerParsers() {
        itemGroupResolverMap.clear();
        itemGroupResolverMap.put(GROUP_TYPE_TAG, this::resolveTag);
        itemGroupResolverMap.put(GROUP_TYPE_ITEM_PACK, this::resolveItemPack);
    }

    /**
     * 将tag的裸id解析为物品组
     */
    private @Nullable ItemGroup resolveTag(String tagId) {
        Optional<Tag<Material>> tagOpt = IngredientUtils.getTag(tagId);
        if (tagOpt.isEmpty()) {
            return null;
        }
        List<NamespacedItemId> items = tagOpt.get().getValues().stream()
            .map(NamespacedItemId::fromMaterial)
            .toList();
        return new ItemGroup(GROUP_TYPE_TAG, tagId, items);
    }

    /**
     * 将物品组的裸id解析为物品组
     */
    private @Nullable ItemGroup resolveItemPack(String itemPackId) {
        ItemPack itemPack = ItemManager.INSTANCE.getItemPack(itemPackId);
        if (itemPack == null) {
            return null;
        }
        return new ItemGroup(GROUP_TYPE_ITEM_PACK, itemPackId, itemPack.itemIds());
    }

    /**
     * 将物品组类型与裸id解析为物品组
     * @return 解析出的物品组, 类型不支持或解析失败返回Optional.empty()
     */
    private Optional<ItemGroup> resolveItemGroup(@Nullable String groupType, @Nullable String groupId) {
        if (groupType == null || groupId == null) {
            return Optional.empty();
        }
        Function<String, ItemGroup> resolver = itemGroupResolverMap.get(groupType);
        return resolver == null ? Optional.empty() : Optional.ofNullable(resolver.apply(groupId));
    }

    /**
     * 构建物品组的占位符物品
     * 展示物品为组内第一个能解析出物品的条目, 在其原有lore之后追加物品组名称与物品列表lore
     * @return 构建出的占位符物品, 构建失败返回Optional.empty()
     */
    private Optional<ItemStack> buildItemGroupItem(ItemGroup itemGroup) {
        ItemStack itemStack = matchFirstItem(itemGroup.items());
        if (itemStack == null) {
            return Optional.empty();
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return Optional.empty();
        }
        itemMeta.setDisplayName(buildItemGroupItemName(itemGroup));
        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(Objects.requireNonNull(itemMeta.getLore())) : new ArrayList<>();
        lore.addAll(buildItemGroupItemLore(itemGroup));
        itemMeta.setLore(lore);
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(ITEM_GROUP_TYPE_KEY, PersistentDataType.STRING, itemGroup.groupType());
        persistentDataContainer.set(ITEM_GROUP_ID_KEY, PersistentDataType.STRING, itemGroup.groupId());
        itemStack.setItemMeta(itemMeta);
        return Optional.of(itemStack);
    }

    /**
     * 取物品组内第一个能解析为物品的条目
     * @return 该物品的克隆, 组内没有任何可解析的条目时返回null
     */
    private @Nullable ItemStack matchFirstItem(List<NamespacedItemId> itemIds) {
        for (NamespacedItemId itemId : itemIds) {
            Optional<ItemStack> itemOpt = ItemManager.INSTANCE.matchItem(itemId);
            //克隆后再写入名称与lore, 避免污染物品提供源可能返回的缓存实例
            if (itemOpt.isPresent()) {
                return itemOpt.get().clone();
            }
        }
        return null;
    }

    private String buildItemGroupItemName(ItemGroup itemGroup) {
        return BukkitTextProcessor.color(
            PluginConfigs.ITEM_GROUP_ITEM_NAME.value()
                .replace("<group_id>", itemGroup.groupId())
                .replace("<group_type>", itemGroup.groupType())
                .replace("<group_item_count>", String.valueOf(itemGroup.items().size()))
        );
    }

    private List<String> buildItemGroupItemLore(ItemGroup itemGroup) {
        List<NamespacedItemId> itemIds = itemGroup.items();
        int maxLoreSize = Math.max(0, PluginConfigs.ITEM_GROUP_ITEM_LORE_MAX_SIZE.value());
        boolean truncated = itemIds.size() > maxLoreSize;
        return Stream.concat(
            itemIds.stream()
                .limit(maxLoreSize)
                .map(itemId -> BukkitTextProcessor.color(
                    PluginConfigs.ITEM_GROUP_ITEM_LORE_ELEMENT.value().replace("<item_id>", itemId.toString())
                )),
            truncated
                ? Stream.of(BukkitTextProcessor.color(PluginConfigs.ITEM_GROUP_ITEM_LORE_END.value()))
                : Stream.empty()
        ).toList();
    }

    public Optional<ItemGroup> fromItemGroupItem(ItemStack itemStack) {
        if (ItemHelper.isAir(itemStack) || !itemStack.hasItemMeta()) {
            return Optional.empty();
        }
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return resolveItemGroup(
            persistentDataContainer.get(ITEM_GROUP_TYPE_KEY, PersistentDataType.STRING),
            persistentDataContainer.get(ITEM_GROUP_ID_KEY, PersistentDataType.STRING)
        );
    }

    /**
     * 构建一个物品组的占位符物品
     * @param groupType 物品组类型, 见{@link #GROUP_TYPE_TAG}与{@link #GROUP_TYPE_ITEM_PACK}
     * @param groupId 物品组的裸id, 如minecraft:planks或物品组id
     * @return 构建出的占位符物品, 构建失败返回Optional.empty()
     */
    public Optional<ItemStack> buildItemGroupItem(String groupType, String groupId) {
        return resolveItemGroup(groupType, groupId).flatMap(this::buildItemGroupItem);
    }

    /**
     * 根据配方材料ID构建物品组占位符物品
     * @param ingredientId 配方材料ID, 如tag:minecraft:planks或item_pack:my_pack
     * @return 构建出的占位符物品, 该ID不是物品组或构建失败时返回Optional.empty()
     */
    public Optional<ItemStack> buildItemGroupItem(String ingredientId) {
        return itemGroupSource(ingredientId)
            .flatMap(groupSource -> buildItemGroupItem(groupSource.groupType(), groupSource.groupId()));
    }

    /**
     * 判断一个配方材料ID是否为物品组引用
     * @param ingredientId 配方材料ID
     * @return 是物品组引用时返回其类型与裸id, 否则返回Optional.empty()
     */
    public static Optional<ItemGroup> itemGroupSource(String ingredientId) {
        if (ingredientId == null) {
            return Optional.empty();
        }
        if (ingredientId.startsWith(GROUP_TYPE_TAG + ":")) {
            return Optional.of(new ItemGroup(GROUP_TYPE_TAG, ingredientId.substring(GROUP_TYPE_TAG.length() + 1)));
        }
        if (ingredientId.startsWith(GROUP_TYPE_ITEM_PACK + ":")) {
            return Optional.of(new ItemGroup(GROUP_TYPE_ITEM_PACK, ingredientId.substring(GROUP_TYPE_ITEM_PACK.length() + 1)));
        }
        return Optional.empty();
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        itemGroupResolverMap.clear();
    }

}