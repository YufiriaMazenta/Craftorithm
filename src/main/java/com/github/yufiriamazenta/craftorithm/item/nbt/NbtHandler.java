package com.github.yufiriamazenta.craftorithm.item.nbt;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NbtHandler {

    private static final String nmsVersion;
    private static final Map<Byte, Function<Object, IPluginNbtTag<?>>> nbtObjTypeMap;
    private static final Map<String, String> getNmsNbtTypeIdMethodNameMap;
    private static final Map<String, String> nbtBaseClassNameMap;
    private static Class<?> nmsNbtBaseClass = null;
    private static Method getNmsNbtTypeIdMethod = null;

    static {
        nmsVersion = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);

        nbtObjTypeMap = new HashMap<>();
        loadNbtObjTypeMap();

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

    public static Map<Byte, Function<Object, IPluginNbtTag<?>>> getNbtObjTypeMap() {
        return nbtObjTypeMap;
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


    private static void loadNbtBaseClassNameMap() {
        nbtBaseClassNameMap.put("v1_20_R1", "net.minecraft.nbt.NBTBase");
        nbtBaseClassNameMap.put("v1_19_R3", "net.minecraft.nbt.NBTBase");
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
        getNmsNbtTypeIdMethodNameMap.put("v1_20_R1", "b");
        getNmsNbtTypeIdMethodNameMap.put("v1_19_R3", "b");
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
