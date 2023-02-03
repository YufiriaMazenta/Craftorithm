package top.oasismc.oasisrecipe.item.nbt;

import org.bukkit.configuration.MemorySection;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CompoundNbtTag implements IPluginNbtTag<Map<String, IPluginNbtTag<?>>> {

    private final Map<String, IPluginNbtTag<?>> value;
    private static final Map<String, String> getKeySetMethodNameMap;
    private static final Map<String, String> getNbtBaseMethodNameMap;

    static {
        getKeySetMethodNameMap = new HashMap<>();
        getKeySetMethodNameMap.put("v1_19_R2", "e");

        getNbtBaseMethodNameMap = new HashMap<>();
        getNbtBaseMethodNameMap.put("v1_19_R2", "c");
    }

    public CompoundNbtTag(Object nmsNbtObj) {
        Set<String> tagKeySet = getCompoundKeySet(nmsNbtObj);
        this.value = getNbtValueMap(nmsNbtObj, tagKeySet);
    }

    public CompoundNbtTag(MemorySection section) {
        //TODO
        this.value = null;
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
        return null;
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
