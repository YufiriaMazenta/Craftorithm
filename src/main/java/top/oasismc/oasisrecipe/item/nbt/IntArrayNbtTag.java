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

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "g");

        nmsIntArrayNbtClassNameMap = new HashMap<>();
        nmsIntArrayNbtClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagIntArray");
    }

    public IntArrayNbtTag(Object nmsNbtObj) {
        try {
            Class<?> nmsNbtObjClass = nmsNbtObj.getClass();
            String getValueMethodName = getValueMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getValueMethod = nmsNbtObjClass.getMethod(getValueMethodName);
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
        Class<?> nmsIntArrayNbtClass;
        Object nmsNbtObj = null;
        try {
            nmsIntArrayNbtClass = Class.forName(nmsIntArrayNbtClassNameMap.get(NbtHandler.getNmsVersion()));
            Constructor<?> constructor = nmsIntArrayNbtClass.getConstructor(int[].class);
            nmsNbtObj = constructor.newInstance(value);
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

}
