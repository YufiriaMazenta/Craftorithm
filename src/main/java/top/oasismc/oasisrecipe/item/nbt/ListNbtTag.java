package top.oasismc.oasisrecipe.item.nbt;

import org.bukkit.configuration.MemorySection;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ListNbtTag implements IPluginNbtTag<List<IPluginNbtTag<?>>> {

    private final List<IPluginNbtTag<?>> value;
    private final static Map<String, String> getListItemMethodNameMap;
    private final static Map<String, String> nmsNbtListClassNameMap;
    private final static Map<String, String> getNmsNbtTypeMethodNameMap;
    private static Method getNmsNbtTypeMethod;

    static {
        getListItemMethodNameMap = new HashMap<>();
        getListItemMethodNameMap.put("v1_19_R2", "k");

        nmsNbtListClassNameMap = new HashMap<>();
        nmsNbtListClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagList");

        getNmsNbtTypeMethodNameMap = new HashMap<>();
        getNmsNbtTypeMethodNameMap.put("v1_19_R2", "b");

        try {
            getNmsNbtTypeMethod = NbtHandler.getNmsNbtBaseClass().getMethod(getNmsNbtTypeMethodNameMap.get(NbtHandler.getNmsVersion()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
        List<IPluginNbtTag<?>> value = new ArrayList<>();
        for (Object listItem : objList) {
            String configObjClassName = listItem.getClass().getSimpleName();
            switch (configObjClassName) {
                case "Integer":
                    int intValue = (int) listItem;
                    value.add(new NumberNbtTag(intValue));
                    break;
                case "Double":
                    double doubleValue = (double) listItem;
                    value.add(new NumberNbtTag(doubleValue));
                    break;
                case "ArrayList":
                    List<?> deepObjList = (List<?>) listItem;
                    if (deepObjList.size() >= 1)
                        value.add(new ListNbtTag(deepObjList));
                    break;
                case "String":
                    value.add(new StringNbtTag((String) listItem, 0));
                    break;
                case "LinkedHashMap":
                    LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) listItem;
                    value.add(new CompoundNbtTag(map));
                    break;
            }
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

    @Override
    public Object toNmsNbt() {
        Class<?> nmsNbtListClass;
        Object nmsNbtObj = null;
        try {
            nmsNbtListClass = Class.forName(nmsNbtListClassNameMap.get(NbtHandler.getNmsVersion()));

            List<Object> nmsNbtList = new ArrayList<>();
            byte nmsNbtType = 0;
            for (IPluginNbtTag<?> pluginNbtTag : value) {
                Object nmsNbtTag = pluginNbtTag.toNmsNbt();
                nmsNbtList.add(pluginNbtTag.toNmsNbt());
                if (nmsNbtType == 0) {
                    nmsNbtType = (byte) getNmsNbtTypeMethod.invoke(nmsNbtTag);
                }
            }
            Constructor<?> constructor = nmsNbtListClass.getDeclaredConstructor(List.class, byte.class);
            constructor.setAccessible(true);
            nmsNbtObj = constructor.newInstance(nmsNbtList, nmsNbtType);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

}
