package top.oasismc.oasisrecipe.item.nbt;

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

    public ListNbtTag(List<?> objList) {
        List<IPluginNbtTag<?>> nbtList = new ArrayList<>();
        for (Object listItem : objList) {
            String configObjClassName = listItem.getClass().getSimpleName();
            switch (configObjClassName) {
                case "Integer":
                    int intValue = (int) listItem;
                    nbtList.add(new NumberNbtTag(intValue));
                case "Double":
                    double doubleValue = (double) listItem;
                    nbtList.add(new NumberNbtTag(doubleValue));
                case "ArrayList":
                    List<?> deepObjList = (List<?>) listItem;
                    if (deepObjList.size() >= 1)
                        nbtList.add(new ListNbtTag(deepObjList));
                case "String":
                    //TODO
                    break;
                case "MemorySection":
                    //TODO
                    break;
            }
        }
        this.value = nbtList;
    }

    @Override
    public List<IPluginNbtTag<?>> getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.LIST;
    }

    @Override
    public Object toNmsNbt() {
        //TODO
        return null;
    }

}
