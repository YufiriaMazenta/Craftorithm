package pers.yufiria.craftorithm.item;

import crypticlib.chat.BukkitTextProcessor;
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
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 物品组: 用于标识多个物品的物品, 例如原版Tag和插件自己的ItemPack
 * 既表示一个物品组的值, 也负责物品组占位符物品的解析与构建
 */
public record ItemGroup(
    String groupType,
    String groupId,
    List<NamespacedItemId> items
) {

    public static final String GROUP_TYPE_TAG = "tag";
    public static final String GROUP_TYPE_ITEM_PACK = "item_pack";
    private static final NamespacedKey ITEM_GROUP_TYPE_KEY = new NamespacedKey("craftorithm", "item_group_type");
    private static final NamespacedKey ITEM_GROUP_ID_KEY = new NamespacedKey("craftorithm", "item_group_id");
    /**
     * 物品组类型 -> 将裸id解析为物品组的解析器, 解析失败返回null
     * 占位符物品的构建也是基于解析结果, 因此只需要注册解析器
     */
    private static final Map<String, Function<String, ItemGroup>> RESOLVER_MAP = new ConcurrentHashMap<>();

    static {
        RESOLVER_MAP.put(GROUP_TYPE_TAG, ItemGroup::resolveTag);
        RESOLVER_MAP.put(GROUP_TYPE_ITEM_PACK, ItemGroup::resolveItemPack);
    }

    /**
     * 将tag的裸id解析为物品组
     */
    private static @Nullable ItemGroup resolveTag(String tagId) {
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
     * 将item_pack的裸id解析为物品组
     */
    private static @Nullable ItemGroup resolveItemPack(String itemPackId) {
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
    public static Optional<ItemGroup> of(@Nullable String groupType, @Nullable String groupId) {
        if (groupType == null || groupId == null) {
            return Optional.empty();
        }
        Function<String, ItemGroup> resolver = RESOLVER_MAP.get(groupType);
        return resolver == null ? Optional.empty() : Optional.ofNullable(resolver.apply(groupId));
    }

    /**
     * 判断一个配方材料ID是否为物品组引用
     * @param ingredientId 配方材料ID
     * @return 是物品组引用时返回其类型与裸id, 否则返回Optional.empty()
     */
    public static Optional<ItemGroup> fromIngredientId(String ingredientId) {
        if (ingredientId == null) {
            return Optional.empty();
        }
        if (ingredientId.startsWith(GROUP_TYPE_TAG + ":")) {
            return ItemGroup.of(GROUP_TYPE_TAG, ingredientId.substring(GROUP_TYPE_TAG.length() + 1));
        }
        if (ingredientId.startsWith(GROUP_TYPE_ITEM_PACK + ":")) {
            return ItemGroup.of(GROUP_TYPE_ITEM_PACK, ingredientId.substring(GROUP_TYPE_ITEM_PACK.length() + 1));
        }
        return Optional.empty();
    }

    /**
     * 从占位符物品识别出物品组
     * @return 识别出的物品组, 该物品不是占位符或解析失败返回Optional.empty()
     */
    public static Optional<ItemGroup> fromItemStack(ItemStack itemStack) {
        if (ItemHelper.isAir(itemStack) || !itemStack.hasItemMeta()) {
            return Optional.empty();
        }
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return of(
            persistentDataContainer.get(ITEM_GROUP_TYPE_KEY, PersistentDataType.STRING),
            persistentDataContainer.get(ITEM_GROUP_ID_KEY, PersistentDataType.STRING)
        );
    }

    /**
     * 该物品组在配方材料里引用的ID, 例如 tag:minecraft:planks 或 item_pack:my_pack
     */
    public String toIngredientId() {
        return groupType + ":" + groupId;
    }

    /**
     * 构建该物品组的占位符物品
     * 展示物品取组内第一个能解析出物品的条目, 在其原有lore之后追加物品组名称与物品列表lore
     * @return 构建出的占位符物品, 构建失败返回Optional.empty()
     */
    public Optional<ItemStack> toPlaceholderItem() {
        ItemStack itemStack = matchFirstItem();
        if (itemStack == null) {
            return Optional.empty();
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return Optional.empty();
        }
        itemMeta.setDisplayName(buildPlaceholderName());
        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        lore.addAll(buildPlaceholderLore());
        itemMeta.setLore(lore);
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(ITEM_GROUP_TYPE_KEY, PersistentDataType.STRING, groupType);
        persistentDataContainer.set(ITEM_GROUP_ID_KEY, PersistentDataType.STRING, groupId);
        itemStack.setItemMeta(itemMeta);
        return Optional.of(itemStack);
    }

    /**
     * 取组内第一个能解析为物品的条目
     * @return 该物品的克隆, 组内没有任何可解析的条目时返回null
     */
    private @Nullable ItemStack matchFirstItem() {
        for (NamespacedItemId itemId : items) {
            Optional<ItemStack> itemOpt = ItemManager.INSTANCE.matchItem(itemId);
            //克隆后再写入名称与lore, 避免污染物品提供源可能返回的缓存实例
            if (itemOpt.isPresent()) {
                return itemOpt.get().clone();
            }
        }
        return null;
    }

    private String buildPlaceholderName() {
        return BukkitTextProcessor.color(
            PluginConfigs.ITEM_GROUP_ITEM_NAME.value()
                .replace("<group_id>", groupId)
                .replace("<group_type>", groupType)
                .replace("<group_item_count>", String.valueOf(items.size()))
        );
    }

    private List<String> buildPlaceholderLore() {
        int maxLoreSize = Math.max(0, PluginConfigs.ITEM_GROUP_ITEM_LORE_MAX_SIZE.value());
        boolean truncated = items.size() > maxLoreSize;
        return Stream.concat(
            items.stream()
                .limit(maxLoreSize)
                .map(itemId -> BukkitTextProcessor.color(
                    PluginConfigs.ITEM_GROUP_ITEM_LORE_ELEMENT.value().replace("<item_id>", itemId.toString())
                )),
            truncated
                ? Stream.of(BukkitTextProcessor.color(PluginConfigs.ITEM_GROUP_ITEM_LORE_END.value()))
                : Stream.empty()
        ).toList();
    }

}