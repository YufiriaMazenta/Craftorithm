package me.yufiria.craftorithm.item.nbt;

import me.yufiria.craftorithm.item.nbt.NbtType;

/*
插件定义的NBT Tag接口
 */
public interface IPluginNbtTag<T> {

    T getValue();

    NbtType getType();

    Object toNmsNbt();

}
