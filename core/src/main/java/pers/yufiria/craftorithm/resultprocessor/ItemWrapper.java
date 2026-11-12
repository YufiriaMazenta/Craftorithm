package pers.yufiria.craftorithm.resultprocessor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * 一次处理链内的物品包装。
 * <p>
 * meta 在首次访问时从物品取一次, 之后读写都落在这一份缓存上, 直到 {@link #apply()}
 * 才写回物品, 因此一条处理链无论跑多少个组件, 都只有一次读取和一次写回。
 * <p>
 * 源物品与结果物品都走这个包装:
 * <ul>
 *     <li>结果物品用 {@link #of(ItemStack)}, 处理链结束时统一 {@link #apply()}</li>
 *     <li>源物品用 {@link #readOnly(ItemStack)}, 只读不写回, 多个 copy 类行为共用同一份 meta</li>
 * </ul>
 * <p>
 * 生命周期仅限一次 {@code processItem}: 不要存到字段、静态变量或跨合成复用,
 * 否则缓存会一直停留在第一次的状态。
 */
public final class ItemWrapper {

    private final ItemStack item;
    private final boolean readOnly;
    private ItemMeta meta;
    private boolean loaded;
    private boolean dirty;

    private ItemWrapper(ItemStack item, boolean readOnly) {
        this.item = item;
        this.readOnly = readOnly;
    }

    /**
     * 可修改的物品草稿, 用于结果物品
     */
    public static ItemWrapper of(@NotNull ItemStack item) {
        return new ItemWrapper(item, false);
    }

    /**
     * 只读包装, 用于源物品
     * <p>
     * {@link #meta()} 同样只取一次并缓存, 供多个 copy 类行为共用;
     * 写入入口会抛 {@link UnsupportedOperationException}, 避免改动被静默丢弃。
     */
    public static ItemWrapper readOnly(@NotNull ItemStack item) {
        return new ItemWrapper(item, true);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public @NotNull Material type() {
        return item.getType();
    }

    public int amount() {
        return item.getAmount();
    }

    /**
     * 缓存的 meta, 首次调用时从物品读取, 之后都是同一份。
     * <p>
     * 只读用途: 改了它不会标脏, 写回前请用 {@link #edit(Consumer)} 或 {@link #setMeta(ItemMeta)}。
     */
    public @Nullable ItemMeta meta() {
        if (!loaded) {
            meta = item.getItemMeta();
            loaded = true;
        }
        return meta;
    }

    /**
     * 替换缓存中的 meta, null 表示清除
     */
    public void setMeta(@Nullable ItemMeta meta) {
        checkWritable("setMeta");
        this.meta = meta;
        this.loaded = true;
        this.dirty = true;
    }

    /**
     * 修改缓存中的 meta, 物品本身没有 meta 时跳过
     */
    public void edit(@NotNull Consumer<ItemMeta> editor) {
        checkWritable("edit");
        ItemMeta current = meta();
        if (current == null) {
            return;
        }
        editor.accept(current);
        dirty = true;
    }

    /**
     * 物品当前状态的副本, 带上还没写回的改动。给脚本、第三方 API 之类只需要查看的地方用
     */
    public @NotNull ItemStack snapshot() {
        ItemStack copy = item.clone();
        if (dirty) {
            copy.setItemMeta(meta == null ? null : meta.clone());
        }
        return copy;
    }

    /**
     * 把缓存写回物品, 没改过则什么都不做
     */
    public void apply() {
        checkWritable("apply");
        if (!dirty) {
            return;
        }
        item.setItemMeta(meta);
        dirty = false;
    }

    private void checkWritable(String action) {
        if (readOnly) {
            throw new UnsupportedOperationException("Source item is read-only, cannot " + action + " on it");
        }
    }

}
