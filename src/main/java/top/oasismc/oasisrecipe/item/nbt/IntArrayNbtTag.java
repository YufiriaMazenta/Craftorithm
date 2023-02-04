package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class IntArrayNbtTag implements IPluginNbtTag<int[]> {

    private int[] value;
    private static final Map<String, String> getValueMethodNameMap;
    private static final Map<String, String> nmsIntArrayNbtClassNameMap;
    private static Method getValueMethod = null;
    private static Constructor<?> nmsIntArrayConstructor = null;

    static {
        getValueMethodNameMap = new HashMap<>();
        loadGetValueMethodNameMap();

        nmsIntArrayNbtClassNameMap = new HashMap<>();
        loadNmsIntArrayClassNameMap();
    }

    public IntArrayNbtTag(Object nmsNbtObj) {
        try {
            if (getValueMethod == null) {
                Class<?> nmsNbtObjClass = nmsNbtObj.getClass();
                String getValueMethodName = getValueMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "g");
                getValueMethod = nmsNbtObjClass.getMethod(getValueMethodName);
            }
            this.value = (int[]) getValueMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //提示版本不兼容
            value = null;
            e.printStackTrace();
        }
    }

    public IntArrayNbtTag(int[] intArray) {
        this.value = intArray;
    }

    @Override
    public int[] getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.INT_ARRAY;
    }

    @Override
    public Object toNmsNbt() {
        Object nmsNbtObj = null;
        try {
            if (nmsIntArrayConstructor == null) {
                String nmsIntArrayNbtClassName = nmsIntArrayNbtClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), "net.minecraft.nbt.NBTTagIntArray");
                Class<?> nmsIntArrayNbtClass = Class.forName(nmsIntArrayNbtClassName);
                nmsIntArrayConstructor = nmsIntArrayNbtClass.getConstructor(int[].class);
            }
            nmsNbtObj = nmsIntArrayConstructor.newInstance(value);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

    public String getFullValue() {
        StringJoiner str = new StringJoiner(",", "INT$[", "]");
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
        getValueMethodNameMap.put("v1_17_R1", "getInts");
    }

    private static void loadNmsIntArrayClassNameMap() {
        nmsIntArrayNbtClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagIntArray");
        nmsIntArrayNbtClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTTagIntArray");
        nmsIntArrayNbtClassNameMap.put("v1_18_R2", "net.minecraft.nbt.NBTTagIntArray");
        nmsIntArrayNbtClassNameMap.put("v1_18_R1", "net.minecraft.nbt.NBTTagIntArray");
        nmsIntArrayNbtClassNameMap.put("v1_17_R1", "net.minecraft.nbt.NBTTagIntArray");
    }

}
