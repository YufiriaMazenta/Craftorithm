package pers.yufiria.craftorithm.resultprocessor.behavior;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

final class SimpleFieldBinding<T> implements FieldBinding<T> {

    private final Class<T> valueType;
    private final Function<ItemMeta, T> getter;
    private final BiConsumer<ItemMeta, T> setter;
    private final Function<ConfigurationSection, ValueProvider<T>> valueReader;
    private final @Nullable Predicate<ItemMeta> acceptance;

    SimpleFieldBinding(
        Class<T> valueType,
        Function<ItemMeta, T> getter,
        BiConsumer<ItemMeta, T> setter,
        Function<ConfigurationSection, ValueProvider<T>> valueReader,
        @Nullable Predicate<ItemMeta> acceptance
    ) {
        this.valueType = valueType;
        this.getter = getter;
        this.setter = setter;
        this.valueReader = valueReader;
        this.acceptance = acceptance;
    }

    @Override
    public Class<T> valueType() {
        return valueType;
    }

    @Override
    public @Nullable T get(@NotNull ItemWrapper item) {
        ItemMeta meta = item.meta();
        return meta == null ? null : getter.apply(meta);
    }

    @Override
    public void set(@NotNull ItemWrapper item, @Nullable T value) {
        item.edit(meta -> setter.accept(meta, value));
    }

    @Override
    public @Nullable ValueProvider<T> valueReader(@Nullable ConfigurationSection data) {
        return valueReader.apply(data);
    }

    @Override
    public boolean accepts(@NotNull ItemWrapper item) {
        if (acceptance == null) {
            //没有额外条件时不必取 meta
            return true;
        }
        ItemMeta meta = item.meta();
        return meta != null && acceptance.test(meta);
    }

}
