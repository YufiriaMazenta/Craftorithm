package me.yufiria.craftorithm.item.nbt;

import me.yufiria.craftorithm.api.nbt.IPluginNbtTag;
import org.bukkit.configuration.MemorySection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CompoundNbtTag implements IPluginNbtTag<Map<String, IPluginNbtTag<?>>> {

    private final Map<String, IPluginNbtTag<?>> value;
    private static final Map<String, String> getKeySetMethodNameMap;
    private static final Map<String, String> getNbtBaseMethodNameMap;
    private static final Map<String, String> nmsNbtCompoundClassNameMap;
    private static Method getKeySetMethod = null;
    private static Method getNbtBaseMethod = null;
    private static Class<?> nmsNbtCompoundClass = null;

    static {
        getKeySetMethodNameMap = new HashMap<>();
        loadGetKeySetMethodNameMap();

        getNbtBaseMethodNameMap = new HashMap<>();
        loadGetNbtBaseMethodNameMap();

        nmsNbtCompoundClassNameMap = new HashMap<>();
        loadNmsNbtCompoundClassNameMap();
    }

    public CompoundNbtTag() {
        this.value = new HashMap<>();
    }

    public CompoundNbtTag(Object nmsNbtObj) {
        Set<String> tagKeySet = getCompoundKeySet(nmsNbtObj);
        this.value = getNbtValueMap(nmsNbtObj, tagKeySet);
    }

    public CompoundNbtTag(MemorySection section, int flag) {
        Map<String, IPluginNbtTag<?>> value = new HashMap<>();
        for (String key : section.getKeys(false)) {
            Object sectionObj = section.get(key);
            if (sectionObj == null)
                continue;
            String sectionObjClass = sectionObj.getClass().getSimpleName();
            switch (sectionObjClass) {
                case "Integer":
                    int intValue = (int) sectionObj;
                    value.put(key, new NumberNbtTag(intValue, 0));
                    break;
                case "Double":
                    double doubleValue = (double) sectionObj;
                    value.put(key, new NumberNbtTag(doubleValue, 0));
                    break;
                case "ArrayList":
                    List<?> deepObjList = (List<?>) sectionObj;
                    if (deepObjList.size() >= 1)
                        value.put(key, new ListNbtTag(deepObjList, 0));
                    break;
                case "String":
                    value.put(key, new StringNbtTag((String) sectionObj, 0));
                    break;
                case "MemorySection":
                    MemorySection deepSection = (MemorySection) sectionObj;
                    value.put(key, new CompoundNbtTag(deepSection, 0));
                    break;
                case "LinkedHashMap":
                    LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) sectionObj;
                    value.put(key, new CompoundNbtTag(map, 0));
                    break;
            }
        }
        this.value = value;
    }

    public CompoundNbtTag(LinkedHashMap<?, ?> map, int flag) {
        Map<String, IPluginNbtTag<?>> value = new HashMap<>();
        for (Object key : map.keySet()) {
            Object sectionObj = map.get(key);
            if (sectionObj == null)
                continue;
            String sectionObjClass = sectionObj.getClass().getSimpleName();
            switch (sectionObjClass) {
                case "Integer":
                    int intValue = (int) sectionObj;
                    value.put((String) key, new NumberNbtTag(intValue, 0));
                    break;
                case "Double":
                    double doubleValue = (double) sectionObj;
                    value.put((String) key, new NumberNbtTag(doubleValue, 0));
                    break;
                case "ArrayList":
                    List<?> deepObjList = (List<?>) sectionObj;
                    if (deepObjList.size() >= 1)
                        value.put((String) key, new ListNbtTag(deepObjList, 0));
                    break;
                case "String":
                    value.put((String) key, new StringNbtTag((String) sectionObj, 0));
                    break;
                case "MemorySection":
                    MemorySection deepSection = (MemorySection) sectionObj;
                    value.put((String) key, new CompoundNbtTag(deepSection, 0));
                    break;
                case "LinkedHashMap":
                    LinkedHashMap<?, ?> deepMap = (LinkedHashMap<?, ?>) sectionObj;
                    value.put((String) key, new CompoundNbtTag(deepMap, 0));
                    break;
            }
        }
        this.value = value;
    }

    @Override
    public Map<String, IPluginNbtTag<?>> getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.COMPOUND;
    }

    @Override
    public Object toNmsNbt() {
        Object nmsNbtObj = null;
        try {
            if (nmsNbtCompoundClass == null) {
                String nmsNbtCompoundClassName = nmsNbtCompoundClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), "net.minecraft.nbt.NBTTagCompound");
                nmsNbtCompoundClass = Class.forName(nmsNbtCompoundClassName);
            }
            nmsNbtObj = nmsNbtCompoundClass.newInstance();
            Method method = NbtHandler.getSetNbt2CompoundMethod();
            for (String key : value.keySet()) {
                method.invoke(nmsNbtObj, key, value.get(key).toNmsNbt());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

    private Set<String> getCompoundKeySet(Object nmsNbtObj) {
        try {
            if (getKeySetMethod == null) {
                Class<?> nbtTagClass = nmsNbtObj.getClass();
                String getTagKeySetMethodName = getKeySetMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "e");
                getKeySetMethod = nbtTagClass.getMethod(getTagKeySetMethodName);
            }
            return (Set<String>) getKeySetMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    private Map<String, IPluginNbtTag<?>> getNbtValueMap(Object nmsNbtObj, Set<String> tagKeySet) {
        Map<String, IPluginNbtTag<?>> nbtValueMap = new HashMap<>();
        try {
            if (getNbtBaseMethod == null) {
                Class<?> nmsNbtTagClass = nmsNbtObj.getClass();
                String getNbtBaseMethodName = getNbtBaseMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "c");
                getNbtBaseMethod = nmsNbtTagClass.getMethod(getNbtBaseMethodName, String.class);
            }
            for (String compoundKey : tagKeySet) {
                Object compoundValue = getNbtBaseMethod.invoke(nmsNbtObj, compoundKey);
                IPluginNbtTag<?> nbtTag = NbtHandler.nmsNbt2PluginNbtObj(compoundValue);
                nbtValueMap.put(compoundKey, nbtTag);
            }
            return nbtValueMap;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private static void loadGetKeySetMethodNameMap() {
        getKeySetMethodNameMap.put("v1_19_R2", "e");
        getKeySetMethodNameMap.put("v1_19_R1", "d");
        getKeySetMethodNameMap.put("v1_18_R2", "d");
        getKeySetMethodNameMap.put("v1_18_R1", "d");
        getKeySetMethodNameMap.put("v1_17_R1", "getKeys");
        getKeySetMethodNameMap.put("v1_16_R3", "getKeys");
        getKeySetMethodNameMap.put("v1_16_R2", "getKeys");
        getKeySetMethodNameMap.put("v1_16_R1", "getKeys");
        getKeySetMethodNameMap.put("v1_15_R1", "getKeys");
        getKeySetMethodNameMap.put("v1_14_R1", "getKeys");
        getKeySetMethodNameMap.put("v1_13_R2", "getKeys");
        getKeySetMethodNameMap.put("v1_13_R1", "getKeys");
    }

    private static void loadGetNbtBaseMethodNameMap() {
        getNbtBaseMethodNameMap.put("v1_19_R2", "c");
        getNbtBaseMethodNameMap.put("v1_19_R1", "c");
        getNbtBaseMethodNameMap.put("v1_18_R2", "c");
        getNbtBaseMethodNameMap.put("v1_18_R1", "c");
        getNbtBaseMethodNameMap.put("v1_17_R1", "get");
        getNbtBaseMethodNameMap.put("v1_16_R3", "get");
        getNbtBaseMethodNameMap.put("v1_16_R2", "get");
        getNbtBaseMethodNameMap.put("v1_16_R1", "get");
        getNbtBaseMethodNameMap.put("v1_15_R1", "get");
        getNbtBaseMethodNameMap.put("v1_14_R1", "get");
        getNbtBaseMethodNameMap.put("v1_13_R2", "get");
        getNbtBaseMethodNameMap.put("v1_13_R1", "get");
    }

    private static void loadNmsNbtCompoundClassNameMap() {
        nmsNbtCompoundClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_18_R2", "net.minecraft.nbt.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_18_R1", "net.minecraft.nbt.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_17_R1", "net.minecraft.nbt.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_16_R3", "net.minecraft.server.v1_16_R3.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_16_R2", "net.minecraft.server.v1_16_R2.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_16_R1", "net.minecraft.server.v1_16_R1.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_15_R1", "net.minecraft.server.v1_15_R1.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_14_R1", "net.minecraft.server.v1_14_R1.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_13_R2", "net.minecraft.server.v1_13_R2.NBTTagCompound");
        nmsNbtCompoundClassNameMap.put("v1_13_R1", "net.minecraft.server.v1_13_R1.NBTTagCompound");
    }

}
