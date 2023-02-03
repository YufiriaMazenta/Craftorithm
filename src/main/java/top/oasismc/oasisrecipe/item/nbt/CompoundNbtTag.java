package top.oasismc.oasisrecipe.item.nbt;

import org.bukkit.configuration.MemorySection;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CompoundNbtTag implements IPluginNbtTag<Map<String, IPluginNbtTag<?>>> {

    private final Map<String, IPluginNbtTag<?>> value;
    private static final Map<String, String> getKeySetMethodNameMap;
    private static final Map<String, String> getNbtBaseMethodNameMap;
    private static final Map<String, String> nmsNbtCompoundClassNameMap;

    static {
        getKeySetMethodNameMap = new HashMap<>();
        getKeySetMethodNameMap.put("v1_19_R2", "e");

        getNbtBaseMethodNameMap = new HashMap<>();
        getNbtBaseMethodNameMap.put("v1_19_R2", "c");

        nmsNbtCompoundClassNameMap = new HashMap<>();
        nmsNbtCompoundClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagCompound");
    }

    public CompoundNbtTag(Object nmsNbtObj) {
        Set<String> tagKeySet = getCompoundKeySet(nmsNbtObj);
        this.value = getNbtValueMap(nmsNbtObj, tagKeySet);
    }

    public CompoundNbtTag(MemorySection section) {
        Map<String, IPluginNbtTag<?>> value = new HashMap<>();
        for (String key : section.getKeys(false)) {
            Object sectionObj = section.get(key);
            if (sectionObj == null)
                continue;
            String sectionObjClass = sectionObj.getClass().getSimpleName();
            switch (sectionObjClass) {
                case "Integer":
                    int intValue = (int) sectionObj;
                    value.put(key, new NumberNbtTag(intValue));
                    break;
                case "Double":
                    double doubleValue = (double) sectionObj;
                    value.put(key, new NumberNbtTag(doubleValue));
                    break;
                case "ArrayList":
                    List<?> deepObjList = (List<?>) sectionObj;
                    if (deepObjList.size() >= 1)
                        value.put(key, new ListNbtTag(deepObjList));
                    break;
                case "String":
                    value.put(key, new StringNbtTag((String) sectionObj, 0));
                    break;
                case "MemorySection":
                    MemorySection deepSection = (MemorySection) sectionObj;
                    value.put(key, new CompoundNbtTag(deepSection));
                    break;
            }
        }
        this.value = value;
    }

    public CompoundNbtTag(LinkedHashMap<?, ?> map) {
        Map<String, IPluginNbtTag<?>> value = new HashMap<>();
        for (Object key : map.keySet()) {
            Object sectionObj = map.get(key);
            if (sectionObj == null)
                continue;
            String sectionObjClass = sectionObj.getClass().getSimpleName();
            switch (sectionObjClass) {
                case "Integer":
                    int intValue = (int) sectionObj;
                    value.put((String) key, new NumberNbtTag(intValue));
                    break;
                case "Double":
                    double doubleValue = (double) sectionObj;
                    value.put((String) key, new NumberNbtTag(doubleValue));
                    break;
                case "ArrayList":
                    List<?> deepObjList = (List<?>) sectionObj;
                    if (deepObjList.size() >= 1)
                        value.put((String) key, new ListNbtTag(deepObjList));
                    break;
                case "String":
                    value.put((String) key, new StringNbtTag((String) sectionObj, 0));
                    break;
                case "MemorySection":
                    MemorySection deepSection = (MemorySection) sectionObj;
                    value.put((String) key, new CompoundNbtTag(deepSection));
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
        Class<?> nmsNbtCompoundClass;
        Object nmsNbtObj = null;
        try {
            nmsNbtCompoundClass = Class.forName(nmsNbtCompoundClassNameMap.get(NbtHandler.getNmsVersion()));
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
            Class<?> nbtTagClass = nmsNbtObj.getClass();
            String getTagKeySetMethodName = getKeySetMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getTagKeySetMethod = nbtTagClass.getMethod(getTagKeySetMethodName);
            return (Set<String>) getTagKeySetMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    private Map<String, IPluginNbtTag<?>> getNbtValueMap(Object nmsNbtObj, Set<String> tagKeySet) {
        Map<String, IPluginNbtTag<?>> nbtValueMap = new HashMap<>();
        try {
            Class<?> nmsNbtTagClass = nmsNbtObj.getClass();
            String getNbtBaseMethodName = getNbtBaseMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getNbtBaseMethod = nmsNbtTagClass.getMethod(getNbtBaseMethodName, String.class);

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

}
