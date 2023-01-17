package top.oasismc.oasisrecipe.item.nbt;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;
import top.oasismc.oasisrecipe.item.nbt.impl.*;

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

    static {
        nmsVersion = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
        getNbtTagTypeMethodNameMap = new HashMap<>();
        getNbtTagTypeMethodNameMap.put("v1_19_R2", "c");

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
    }

    public static String getNmsVersion() {
        return nmsVersion;
    }

    public static String getGetNbtTagTypeMethodName() {
        return getNbtTagTypeMethodNameMap.get(NbtHandler.getNmsVersion());
    }

    /*
    将NMS的NBT转化为插件的NBT
     */
    @Nullable
    public static IPluginNbtTag<?> nmsNbt2PluginNbtObj(Object nmsNbtObject) {
        Class<?> nmsNbtObjClass = nmsNbtObject.getClass();
        try {
            Method getNBTTagTypeMethod = nmsNbtObjClass.getMethod(getGetNbtTagTypeMethodName());
            String nbtType = getNBTTagTypeMethod.invoke(nmsNbtObject).getClass().getName();
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
    逐层解析NBT，并返回其数据
     */
    public static Object getNbtTagValueObj(IPluginNbtTag<?> nbtTag) {
        switch (nbtTag.getType()) {
            case NUMBER:
            case STRING:
            case BYTE_ARRAY:
            case INT_ARRAY:
            case LONG_ARRAY:
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

    public static Map<String, Function<Object, IPluginNbtTag<?>>> getNbtObjTypeMap() {
        return nbtObjTypeMap;
    }

}
