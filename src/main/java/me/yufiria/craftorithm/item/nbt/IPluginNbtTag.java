package me.yufiria.craftorithm.item.nbt;

/*
插件定义的NBT Tag接口
 */
public interface IPluginNbtTag<T> {

    T getValue();

    NbtType getType();

    Object toNmsNbt();

}
