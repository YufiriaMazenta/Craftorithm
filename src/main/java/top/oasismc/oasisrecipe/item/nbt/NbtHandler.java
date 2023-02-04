package top.oasismc.oasisrecipe.item.nbt;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NbtHandler {

    private static final String nmsVersion;
    private static final Map<String, String> getNbtTagTypeMethodNameMap;
    private static final Map<String, Function<Object, IPluginNbtTag<?>>> nbtObjTypeMap;
    private static final Map<String, String> setNbt2CompoundMethodNameMap;
    private static final Map<String, String> nbtBaseClassNameMap;
    private static Method getNbtTagTypeMethod = null;
    private static Class<?> nmsNbtBaseClass = null;
    private static Method setNbt2CompoundMethod = null;

    static {
        nmsVersion = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
        getNbtTagTypeMethodNameMap = new HashMap<>();
        getNbtTagTypeMethodNameMap.put("v1_19_R2", "c");
        getNbtTagTypeMethodNameMap.put("v1_19_R1", "b");

        nbtObjTypeMap = new HashMap<>();
        nbtObjTypeMap.put("Byte", NumberNbtTag::new);
        nbtObjTypeMap.put("Short", NumberNbtTag::new);
        nbtObjTypeMap.put("Int", NumberNbtTag::new);
        nbtObjTypeMap.put("Long", NumberNbtTag::new);
        nbtObjTypeMap.put("Float", NumberNbtTag::new);
        nbtObjTypeMap.put("Double", NumberNbtTag::new);
        nbtObjTypeMap.put("Compound", CompoundNbtTag::new);
        nbtObjTypeMap.put("String", StringNbtTag::new);
        nbtObjTypeMap.put("List", ListNbtTag::new);
        nbtObjTypeMap.put("ByteArray", ByteArrayNbtTag::new);
        nbtObjTypeMap.put("IntArray", IntArrayNbtTag::new);
        nbtObjTypeMap.put("LongArray", LongArrayNbtTag::new);

        setNbt2CompoundMethodNameMap = new HashMap<>();
        setNbt2CompoundMethodNameMap.put("v1_19_R2", "a");
        setNbt2CompoundMethodNameMap.put("v1_19_R1", "a");

        nbtBaseClassNameMap = new HashMap<>();
        nbtBaseClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTBase");
        nbtBaseClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTBase");

        try {
            String nbtBaseClassName = nbtBaseClassNameMap.getOrDefault(nmsVersion, "net.minecraft.nbt.NBTBase");
            nmsNbtBaseClass = Class.forName(nbtBaseClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNmsVersion() {
        return nmsVersion;
    }

    public static String getGetNbtTagTypeMethodName() {
        return getNbtTagTypeMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "c");
    }

    /*
    将NMS的NBT转化为插件的NBT
     */
    public static IPluginNbtTag<?> nmsNbt2PluginNbtObj(Object nmsNbtObject) {
        Class<?> nmsNbtObjClass = nmsNbtObject.getClass();
        try {
            if (getNbtTagTypeMethod == null)
                getNbtTagTypeMethod = nmsNbtObjClass.getMethod(getGetNbtTagTypeMethodName());
            String nbtType = getNbtTagTypeMethod.invoke(nmsNbtObject).getClass().getName();
            int nbtTagStrIndex = nbtType.indexOf("NBTTag") + 6;
            int $StrIndex = nbtType.indexOf("$");
            nbtType = nbtType.substring(nbtTagStrIndex, $StrIndex);
            return nbtObjTypeMap.getOrDefault(nbtType, StringNbtTag::new).apply(nmsNbtObject);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    将NBT设置至配置文件，仅设置，不保存配置文件
     */
    public static void setPluginNbt2Config(FileConfiguration config, String configPath, CompoundNbtTag pluginCompoundNbtTag) {
        Map<String, IPluginNbtTag<?>> nbtTagMap = pluginCompoundNbtTag.getValue();
        Map<String, Object> configMap = new HashMap<>();
        for (String nbtKey : nbtTagMap.keySet()) {
            configMap.put(nbtKey, getNbtTagValueObj(nbtTagMap.get(nbtKey)));
        }
        config.set(configPath, configMap);
    }

    /*
    逐层解析NBT，并返回数据类型对象，用于存储至配置文件
     */
    public static Object getNbtTagValueObj(IPluginNbtTag<?> nbtTag) {
        switch (nbtTag.getType()) {
            case NUMBER:
                NumberNbtTag numberNbtTag = (NumberNbtTag) nbtTag;
                return numberNbtTag.getFullValue();
            case BYTE_ARRAY:
                ByteArrayNbtTag byteArrayNbtTag = (ByteArrayNbtTag) nbtTag;
                return byteArrayNbtTag.getFullValue();
            case LONG_ARRAY:
                LongArrayNbtTag longArrayNbtTag = (LongArrayNbtTag) nbtTag;
                return longArrayNbtTag.getFullValue();
            case INT_ARRAY:
                IntArrayNbtTag intArrayNbtTag = (IntArrayNbtTag) nbtTag;
                return intArrayNbtTag.getFullValue();
            case STRING:
                return nbtTag.getValue();
            case COMPOUND:
                Map<String, Object> objMap = new HashMap<>();
                Map<String, IPluginNbtTag<?>> nbtTagMap = ((CompoundNbtTag) nbtTag).getValue();
                for (String key : nbtTagMap.keySet()) {
                    objMap.put(key, getNbtTagValueObj(nbtTagMap.get(key)));
                }
                return objMap;
            case LIST:
                List<Object> objList = new ArrayList<>();
                List<IPluginNbtTag<?>> nbtTagList = ((ListNbtTag) nbtTag).getValue();
                for (IPluginNbtTag<?> iPluginNbtTag : nbtTagList) {
                    objList.add(getNbtTagValueObj(iPluginNbtTag));
                }
                return objList;
            default:
                return null;
        }
    }

    /*
    从配置文件读取并生成对应NMS NBT对象
     */
    public static void setNbt2NmsCompound(ConfigurationSection configSection, Object nmsNbtCompoundObj) {
        String setNbt2CompoundMethodName = setNbt2CompoundMethodNameMap.getOrDefault(nmsVersion, "a");
        if (setNbt2CompoundMethod == null) {
            try {
                setNbt2CompoundMethod = nmsNbtCompoundObj.getClass().getMethod(setNbt2CompoundMethodName, String.class, nmsNbtBaseClass);
            } catch (NoSuchMethodException e) {
                //提示版本不兼容
                e.printStackTrace();
            }

        }
        for (String key : configSection.getKeys(false)) {
            Object configObj = configSection.get(key);
            Object nmsNbtObj = generateNmsNbtObj(configObj);
            try {
                setNbt2CompoundMethod.invoke(nmsNbtCompoundObj, key, nmsNbtObj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    传入获取的配置文件，返回生成的NMS NBT对象
     */
    public static Object generateNmsNbtObj(Object sourceObj) {
        if (sourceObj == null)
            return null;
        String sourceObjClassName = sourceObj.getClass().getSimpleName();
        switch (sourceObjClassName) {
            case "Integer":
                int intValue = (int) sourceObj;
                return new NumberNbtTag(intValue).toNmsNbt();
            case "Double":
                double doubleValue = (double) sourceObj;
                return new NumberNbtTag(doubleValue).toNmsNbt();
            case "ArrayList":
                List<?> listValue = (List<?>) sourceObj;
                if (listValue.size() < 1)
                    return null;
                return new ListNbtTag(listValue).toNmsNbt();
            case "String":
                return new StringNbtTag((String) sourceObj, 0).toNmsNbt();
            case "MemorySection":
                MemorySection sectionValue = (MemorySection) sourceObj;
                return new CompoundNbtTag(sectionValue).toNmsNbt();
            default:
                throw new IllegalArgumentException("Unsupported object type: " + sourceObjClassName);
        }
    }

    public static Map<String, Function<Object, IPluginNbtTag<?>>> getNbtObjTypeMap() {
        return nbtObjTypeMap;
    }

    public static Method getSetNbt2CompoundMethod() {
        return setNbt2CompoundMethod;
    }

    public static Class<?> getNmsNbtBaseClass() {
        return nmsNbtBaseClass;
    }

}
