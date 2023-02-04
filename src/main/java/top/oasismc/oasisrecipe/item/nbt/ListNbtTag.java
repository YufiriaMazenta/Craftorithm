package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ListNbtTag implements IPluginNbtTag<List<IPluginNbtTag<?>>> {

    private final List<IPluginNbtTag<?>> value;
    private final static Map<String, String> getListItemMethodNameMap;
    private final static Map<String, String> nmsNbtListClassNameMap;
    private final static Map<String, String> getNmsNbtTypeIdMethodNameMap;
    private static Method getListItemMethod = null;
    private static Method getNmsNbtTypeIdMethod = null;
    private static Constructor<?> nmsNbtListConstructor = null;

    static {
        getListItemMethodNameMap = new HashMap<>();
        loadGetListItemMethodNameMap();

        nmsNbtListClassNameMap = new HashMap<>();
        loadNmsNbtListClassNameMap();

        getNmsNbtTypeIdMethodNameMap = new HashMap<>();
        loadGetNmsNbtTypeIdMethodNameMap();

        try {
            String getNmsNbtTypeMethodName = getNmsNbtTypeIdMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "b");
            getNmsNbtTypeIdMethod = NbtHandler.getNmsNbtBaseClass().getMethod(getNmsNbtTypeMethodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public ListNbtTag(Object nmsNbtObj) {
        List<IPluginNbtTag<?>> value = new ArrayList<>();
        int size = ((AbstractList<?>) nmsNbtObj).size();
        try {
            if (getListItemMethod == null) {
                Class<?> nbtObjectClass = nmsNbtObj.getClass();
                String getListItemMethodName = getListItemMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "k");
                getListItemMethod = nbtObjectClass.getMethod(getListItemMethodName, int.class);
            }
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
        Object nmsNbtObj = null;
        try {

            List<Object> nmsNbtList = new ArrayList<>();
            byte nmsNbtType = 0;
            for (IPluginNbtTag<?> pluginNbtTag : value) {
                Object nmsNbtTag = pluginNbtTag.toNmsNbt();
                nmsNbtList.add(pluginNbtTag.toNmsNbt());
                if (nmsNbtType == 0) {
                    nmsNbtType = (byte) getNmsNbtTypeIdMethod.invoke(nmsNbtTag);
                }
            }
            if (nmsNbtListConstructor == null) {
                String nmsNbtListClassName = nmsNbtListClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), "net.minecraft.nbt.NBTTagList");
                Class<?> nmsNbtListClass = Class.forName(nmsNbtListClassName);
                nmsNbtListConstructor = nmsNbtListClass.getDeclaredConstructor(List.class, byte.class);
                nmsNbtListConstructor.setAccessible(true);
            }
            nmsNbtObj = nmsNbtListConstructor.newInstance(nmsNbtList, nmsNbtType);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

    private static void loadGetListItemMethodNameMap() {
        getListItemMethodNameMap.put("v1_19_R2", "k");
        getListItemMethodNameMap.put("v1_19_R1", "k");
        getListItemMethodNameMap.put("v1_18_R2", "k");
        getListItemMethodNameMap.put("v1_18_R1", "k");
        getListItemMethodNameMap.put("v1_17_R1", "get");
    }

    private static void loadNmsNbtListClassNameMap() {
        nmsNbtListClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagList");
        nmsNbtListClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTTagList");
        nmsNbtListClassNameMap.put("v1_18_R2", "net.minecraft.nbt.NBTTagList");
        nmsNbtListClassNameMap.put("v1_18_R1", "net.minecraft.nbt.NBTTagList");
        nmsNbtListClassNameMap.put("v1_17_R1", "net.minecraft.nbt.NBTTagList");
    }

    private static void loadGetNmsNbtTypeIdMethodNameMap() {
        getNmsNbtTypeIdMethodNameMap.put("v1_19_R2", "b");
        getNmsNbtTypeIdMethodNameMap.put("v1_19_R1", "a");
        getNmsNbtTypeIdMethodNameMap.put("v1_18_R2", "a");
        getNmsNbtTypeIdMethodNameMap.put("v1_18_R1", "a");
        getNmsNbtTypeIdMethodNameMap.put("v1_17_R1", "getTypeId");
    }

}
