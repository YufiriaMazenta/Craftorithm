package pers.yufiria.craftorithm.item;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class NamespacedItemId {

    private final @NotNull String namespace;
    private final @NotNull String itemId;
    private final @NotNull String toString;
    private final int hashCode;

    private static final ConcurrentHashMap<String, NamespacedItemId> INSTANCE_CACHE = new ConcurrentHashMap<>();

    @ApiStatus.Internal
    public NamespacedItemId(@NotNull String namespace, @NotNull String itemId) {
        this.namespace = namespace;
        this.itemId = itemId;
        this.toString = namespace + ":" + itemId;
        this.hashCode = Objects.hashCode(this.toString);
    }

    public String namespace() {
        return namespace;
    }

    public String itemId() {
        return itemId;
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
        String key = namespace + ":" + itemId;
        return INSTANCE_CACHE.computeIfAbsent(key, k -> new NamespacedItemId(namespace, itemId));
    }

    public static @Nullable NamespacedItemId fromString(String string) {
        if (string == null) return null;
        NamespacedItemId cached = INSTANCE_CACHE.get(string);
        if (cached != null) return cached;
        int splitIndex = string.indexOf(':');
        if (splitIndex == -1) {
            Material material = MaterialHelper.matchMaterial(string);
            return material != null ? fromMaterial(material) : null;
        }
        String namespace = string.substring(0, splitIndex);
        String itemId = string.substring(splitIndex + 1);
        return INSTANCE_CACHE.computeIfAbsent(string, k -> new NamespacedItemId(namespace, itemId));
    }

    public static @NotNull NamespacedItemId fromMaterial(@NotNull Material material) {
        String key = material.getKey().getNamespace() + ":" + material.getKey().getKey();
        return INSTANCE_CACHE.computeIfAbsent(key, k -> new NamespacedItemId(material.getKey().getNamespace(), material.getKey().getKey()));
    }

}