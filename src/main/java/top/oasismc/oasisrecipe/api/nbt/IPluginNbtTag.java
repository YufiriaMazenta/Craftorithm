package top.oasismc.oasisrecipe.api.nbt;

import top.oasismc.oasisrecipe.item.nbt.NbtType;

/*
插件定义的NBT Tag接口
 */
public interface IPluginNbtTag<T> {

    T getValue();

    NbtType getType();

}
