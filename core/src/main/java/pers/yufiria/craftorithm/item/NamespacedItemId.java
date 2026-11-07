package pers.yufiria.craftorithm.item;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NamespacedItemId {

    private final @NotNull String namespace;
    private final @NotNull String itemId;
    private final @NotNull String toString;
    private final boolean vanillaItem;
    private final int hashCode;

    /**
     * 原版材料到物品id的映射, 以 Material 的序号作为索引
     */
    private static final NamespacedItemId[] VANILLA_CACHE = new NamespacedItemId[Material.values().length];

    @ApiStatus.Internal
    public NamespacedItemId(@NotNull String namespace, @NotNull String itemId) {
        this.namespace = namespace;
        this.itemId = itemId;
        this.toString = namespace + ":" + itemId;
        this.hashCode = Objects.hashCode(this.toString);
        this.vanillaItem = namespace.equals(NamespacedKey.MINECRAFT);
    }

    public String namespace() {
        return namespace;
    }

    public String itemId() {
        return itemId;
    }

    public boolean isVanillaItem() {
        return vanillaItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespacedItemId that)) return false;

        return toString.equals(that.toString);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return toString;
    }

    public static @NotNull NamespacedItemId of(@NotNull String namespace, @NotNull String itemId) {
        return new NamespacedItemId(namespace, itemId);
    }

    public static @Nullable NamespacedItemId fromString(String string) {
        if (string == null) return null;
        int splitIndex = string.indexOf(':');
        if (splitIndex == -1) {
            Material material = MaterialHelper.matchMaterial(string);
            return material != null ? fromMaterial(material) : null;
        }
        return new NamespacedItemId(string.substring(0, splitIndex), string.substring(splitIndex + 1));
    }

    public static @NotNull NamespacedItemId fromMaterial(@NotNull Material material) {
        NamespacedItemId cached = VANILLA_CACHE[material.ordinal()];
        if (cached != null) {
            return cached;
        }
        NamespacedKey key = material.getKey();
        NamespacedItemId created = new NamespacedItemId(key.getNamespace(), key.getKey());
        VANILLA_CACHE[material.ordinal()] = created;
        return created;
    }

}