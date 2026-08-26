package pers.yufiria.craftorithm.item;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NamespacedItemId {

    private final @NotNull String namespace;
    private final @NotNull String itemId;
    private final @NotNull String toString;

    public NamespacedItemId(@NotNull String namespace, @NotNull String itemId) {
        this.namespace = namespace;
        this.itemId = itemId;
        this.toString = namespace + ":" + itemId;
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
        return Objects.hashCode(toString);
    }

    public static @Nullable NamespacedItemId fromString(String string) {
        if (string == null) return null;
        int spiltIndex = string.indexOf(':');
        if (spiltIndex == -1) {
            Material material = MaterialHelper.matchMaterial(string);
            return material != null ? fromMaterial(material) : null;
        }
        String namespace = string.substring(0, spiltIndex);
        String itemId = string.substring(spiltIndex + 1);
        return new NamespacedItemId(namespace, itemId);
    }

    public static @NotNull NamespacedItemId fromMaterial(@NotNull Material material) {
        return new NamespacedItemId(material.getKey().getNamespace(), material.getKey().getKey());
    }

    @Override
    public String toString() {
        return toString;
    }

}