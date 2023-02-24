package me.yufiria.craftorithm.item.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class LongArrayNbtTag implements IPluginNbtTag<long[]> {

    private long[] value;
    private static final Map<String, String> getValueMethodNameMap;
    private static final Map<String, String> nmsLongArrayNbtClassNameMap;
    private static Method getValueMethod = null;
    private static Constructor<?> nmsLongArrayNbtConstructor = null;

    static {
        getValueMethodNameMap = new HashMap<>();
        loadGetValueMethodNameMap();

        nmsLongArrayNbtClassNameMap = new HashMap<>();
        loadNmsLongArrayNbtClassNameMap();
    }

    public LongArrayNbtTag(Object nmsNbtObj) {
        try {
            if (getValueMethod == null) {
                Class<?> nmsNbtObjClass = nmsNbtObj.getClass();
                String getValueMethodName = getValueMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "g");
                getValueMethod = nmsNbtObjClass.getMethod(getValueMethodName);
            }
            this.value = (long[]) getValueMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //提示版本不兼容
            value = null;
            e.printStackTrace();
        }
    }

    public LongArrayNbtTag(long[] longArray) {
        this.value = longArray;
    }

    @Override
    public long[] getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.LONG_ARRAY;
    }

    @Override
    public Object toNmsNbt() {
        Object nmsNbtObj = null;
        try {
            if (nmsLongArrayNbtConstructor == null) {
                String nmsLongArrayNbtClassName = nmsLongArrayNbtClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), "net.minecraft.nbt.NBTTagLongArray");
                Class<?> nmsLongArrayNbtClass = Class.forName(nmsLongArrayNbtClassName);
                nmsLongArrayNbtConstructor = nmsLongArrayNbtClass.getConstructor(long[].class);
            }
            nmsNbtObj = nmsLongArrayNbtConstructor.newInstance(value);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

    public String getFullValue() {
        StringJoiner str = new StringJoiner(",", "LONG$[", "]");
        for (long b : value) {
            str.add(b + "");
        }
        return str.toString();
    }

    private static void loadGetValueMethodNameMap() {
        getValueMethodNameMap.put("v1_19_R2", "g");
        getValueMethodNameMap.put("v1_19_R1", "f");
        getValueMethodNameMap.put("v1_18_R2", "f");
        getValueMethodNameMap.put("v1_18_R1", "f");
        getValueMethodNameMap.put("v1_17_R1", "getLongs");
        getValueMethodNameMap.put("v1_16_R3", "getLongs");
        getValueMethodNameMap.put("v1_16_R2", "getLongs");
        getValueMethodNameMap.put("v1_16_R1", "getLongs");
        getValueMethodNameMap.put("v1_15_R1", "getLongs");
        getValueMethodNameMap.put("v1_14_R1", "getLongs");
        getValueMethodNameMap.put("v1_13_R2", "d");
        getValueMethodNameMap.put("v1_13_R1", "d");
    }

    private static void loadNmsLongArrayNbtClassNameMap() {
        nmsLongArrayNbtClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_18_R2", "net.minecraft.nbt.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_18_R1", "net.minecraft.nbt.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_17_R1", "net.minecraft.nbt.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_16_R3", "net.minecraft.server.v1_16_R3.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_16_R2", "net.minecraft.server.v1_16_R2.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_16_R1", "net.minecraft.server.v1_16_R1.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_15_R1", "net.minecraft.server.v1_15_R1.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_14_R1", "net.minecraft.server.v1_14_R1.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_13_R2", "net.minecraft.server.v1_13_R2.NBTTagLongArray");
        nmsLongArrayNbtClassNameMap.put("v1_13_R1", "net.minecraft.server.v1_13_R1.NBTTagLongArray");
    }

}
