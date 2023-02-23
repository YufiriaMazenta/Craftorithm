package me.yufiria.craftorithm.item.nbt;

import me.yufiria.craftorithm.api.nbt.IPluginNbtTag;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class NbtHandler {

    private static final String nmsVersion;
    private static final Map<Byte, Function<Object, IPluginNbtTag<?>>> nbtObjTypeMap;
    private static final Map<String, String> setNbt2CompoundMethodNameMap;
    private final static Map<String, String> getNmsNbtTypeIdMethodNameMap;
    private static final Map<String, String> nbtBaseClassNameMap;
    private static Class<?> nmsNbtBaseClass = null;
    private static Method setNbt2CompoundMethod = null;
    private static Method getNmsNbtTypeIdMethod = null;

    static {
        nmsVersion = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);

        nbtObjTypeMap = new HashMap<>();
        loadNbtObjTypeMap();

        setNbt2CompoundMethodNameMap = new HashMap<>();
        loadSetNbt2CompoundMethodNameMap();

        nbtBaseClassNameMap = new HashMap<>();
        loadNbtBaseClassNameMap();

        getNmsNbtTypeIdMethodNameMap = new HashMap<>();
        loadGetNmsNbtTypeIdMethodNameMap();

        try {
            String nbtBaseClassName = nbtBaseClassNameMap.getOrDefault(nmsVersion, "net.minecraft.nbt.NBTBase");
            nmsNbtBaseClass = Class.forName(nbtBaseClassName);
            String getNmsNbtTypeMethodName = getNmsNbtTypeIdMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "b");
            getNmsNbtTypeIdMethod = NbtHandler.getNmsNbtBaseClass().getMethod(getNmsNbtTypeMethodName);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String getNmsVersion() {
        return nmsVersion;
    }

    /*
    将NMS的NBT转化为插件的NBT
     */
    public static IPluginNbtTag<?> nmsNbt2PluginNbtObj(Object nmsNbtObject) {
        try {
            Byte nbtTypeId = (Byte) getNmsNbtTypeIdMethod.invoke(nmsNbtObject);
            return nbtObjTypeMap.getOrDefault(nbtTypeId, StringNbtTag::new).apply(nmsNbtObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
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
    从配置文件读取并将NBT设置到NBTCompound对象
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
                return new NumberNbtTag(intValue, 0).toNmsNbt();
            case "Double":
                double doubleValue = (double) sourceObj;
                return new NumberNbtTag(doubleValue, 0).toNmsNbt();
            case "ArrayList":
                List<?> listValue = (List<?>) sourceObj;
                if (listValue.size() < 1)
                    return null;
                return new ListNbtTag(listValue, 0).toNmsNbt();
            case "String":
                return new StringNbtTag((String) sourceObj, 0).toNmsNbt();
            case "MemorySection":
                MemorySection sectionValue = (MemorySection) sourceObj;
                return new CompoundNbtTag(sectionValue, 0).toNmsNbt();
            case "LinkedHashMap":
                LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) sourceObj;
                return new CompoundNbtTag(map, 0).toNmsNbt();
            default:
                throw new IllegalArgumentException("Unsupported object type: " + sourceObjClassName);
        }
    }

    public static Map<Byte, Function<Object, IPluginNbtTag<?>>> getNbtObjTypeMap() {
        return nbtObjTypeMap;
    }

    public static Method getSetNbt2CompoundMethod() {
        return setNbt2CompoundMethod;
    }

    public static Class<?> getNmsNbtBaseClass() {
        return nmsNbtBaseClass;
    }

    public static Method getGetNmsNbtTypeIdMethod() {
        return getNmsNbtTypeIdMethod;
    }

    private static void loadNbtObjTypeMap() {
        nbtObjTypeMap.put((byte) 1, NumberNbtTag::new);
        nbtObjTypeMap.put((byte) 2, NumberNbtTag::new);
        nbtObjTypeMap.put((byte) 3, NumberNbtTag::new);
        nbtObjTypeMap.put((byte) 4, NumberNbtTag::new);
        nbtObjTypeMap.put((byte) 5, NumberNbtTag::new);
        nbtObjTypeMap.put((byte) 6, NumberNbtTag::new);
        nbtObjTypeMap.put((byte) 10, CompoundNbtTag::new);
        nbtObjTypeMap.put((byte) 8, StringNbtTag::new);
        nbtObjTypeMap.put((byte) 9, ListNbtTag::new);
        nbtObjTypeMap.put((byte) 7, ByteArrayNbtTag::new);
        nbtObjTypeMap.put((byte) 11, IntArrayNbtTag::new);
        nbtObjTypeMap.put((byte) 12, LongArrayNbtTag::new);
    }

    private static void loadSetNbt2CompoundMethodNameMap() {
        setNbt2CompoundMethodNameMap.put("v1_19_R2", "a");
        setNbt2CompoundMethodNameMap.put("v1_19_R1", "a");
        setNbt2CompoundMethodNameMap.put("v1_18_R2", "a");
        setNbt2CompoundMethodNameMap.put("v1_18_R1", "a");
        setNbt2CompoundMethodNameMap.put("v1_17_R1", "set");
        setNbt2CompoundMethodNameMap.put("v1_16_R3", "set");
        setNbt2CompoundMethodNameMap.put("v1_16_R2", "set");
        setNbt2CompoundMethodNameMap.put("v1_16_R1", "set");
        setNbt2CompoundMethodNameMap.put("v1_15_R1", "set");
        setNbt2CompoundMethodNameMap.put("v1_14_R1", "set");
        setNbt2CompoundMethodNameMap.put("v1_13_R2", "set");
        setNbt2CompoundMethodNameMap.put("v1_13_R1", "set");
    }

    private static void loadNbtBaseClassNameMap() {
        nbtBaseClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTBase");
        nbtBaseClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTBase");
        nbtBaseClassNameMap.put("v1_18_R2", "net.minecraft.nbt.NBTBase");
        nbtBaseClassNameMap.put("v1_18_R1", "net.minecraft.nbt.NBTBase");
        nbtBaseClassNameMap.put("v1_17_R1", "net.minecraft.nbt.NBTBase");
        nbtBaseClassNameMap.put("v1_16_R3", "net.minecraft.server.v1_16_R3.NBTBase");
        nbtBaseClassNameMap.put("v1_16_R2", "net.minecraft.server.v1_16_R2.NBTBase");
        nbtBaseClassNameMap.put("v1_16_R1", "net.minecraft.server.v1_16_R1.NBTBase");
        nbtBaseClassNameMap.put("v1_15_R1", "net.minecraft.server.v1_15_R1.NBTBase");
        nbtBaseClassNameMap.put("v1_14_R1", "net.minecraft.server.v1_14_R1.NBTBase");
        nbtBaseClassNameMap.put("v1_13_R2", "net.minecraft.server.v1_13_R2.NBTBase");
        nbtBaseClassNameMap.put("v1_13_R1", "net.minecraft.server.v1_13_R1.NBTBase");
    }

    private static void loadGetNmsNbtTypeIdMethodNameMap() {
        getNmsNbtTypeIdMethodNameMap.put("v1_19_R2", "b");
        getNmsNbtTypeIdMethodNameMap.put("v1_19_R1", "a");
        getNmsNbtTypeIdMethodNameMap.put("v1_18_R2", "a");
        getNmsNbtTypeIdMethodNameMap.put("v1_18_R1", "a");
        getNmsNbtTypeIdMethodNameMap.put("v1_17_R1", "getTypeId");
        getNmsNbtTypeIdMethodNameMap.put("v1_16_R3", "getTypeId");
        getNmsNbtTypeIdMethodNameMap.put("v1_16_R2", "getTypeId");
        getNmsNbtTypeIdMethodNameMap.put("v1_16_R1", "getTypeId");
        getNmsNbtTypeIdMethodNameMap.put("v1_15_R1", "getTypeId");
        getNmsNbtTypeIdMethodNameMap.put("v1_14_R1", "getTypeId");
        getNmsNbtTypeIdMethodNameMap.put("v1_13_R2", "getTypeId");
        getNmsNbtTypeIdMethodNameMap.put("v1_13_R1", "getTypeId");
    }

}
