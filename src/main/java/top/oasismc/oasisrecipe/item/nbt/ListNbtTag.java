package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.item.nbt.NbtHandler;
import top.oasismc.oasisrecipe.item.nbt.NbtType;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ListNbtTag implements IPluginNbtTag<List<IPluginNbtTag<?>>> {

    private final List<IPluginNbtTag<?>> value;
    private final static Map<String, String> getListItemMethodNameMap;

    static {
        getListItemMethodNameMap = new HashMap<>();
        getListItemMethodNameMap.put("v1_19_R2", "k");
    }

    public ListNbtTag(Object nmsNbtObj) {
        List<IPluginNbtTag<?>> value = new ArrayList<>();
        int size = ((AbstractList<?>) nmsNbtObj).size();
        try {
            Class<?> nbtObjectClass = nmsNbtObj.getClass();
            String getListItemMethodName = getListItemMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getListItemMethod = nbtObjectClass.getMethod(getListItemMethodName, int.class);
            for (int i = 0; i < size; i++) {
                Object listValue = getListItemMethod.invoke(nmsNbtObj, i);
                IPluginNbtTag<?> pluginNbtTag = NbtHandler.nmsNbt2PluginNbtObj(listValue);
                value.add(pluginNbtTag);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.value = value;
    }

    @Override
    public List<IPluginNbtTag<?>> getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.LIST;
    }
}
