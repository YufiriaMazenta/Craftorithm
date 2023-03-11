package com.github.yufiriamazenta.craftorithm.item.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringNbtTag implements IPluginNbtTag<String> {

    private final String value;
    private static final Map<String, String> getValueMethodNameMap;
    private static final Map<String, String> nmsStringNbtClassNameMap;
    private static final List<String> numberNbtPrefixList;
    private static final List<String> numberArrayNbtPrefixList;
    private static Constructor<?> nmsStringNbtConstructor = null;
    private static Method getValueMethod = null;

    static {
        getValueMethodNameMap = new HashMap<>();
        loadGetValueMethodNameMap();

        nmsStringNbtClassNameMap = new HashMap<>();
        loadNmsStringNbtClassNameMap();

        numberNbtPrefixList = Arrays.asList("BYTE", "SHORT", "LONG", "FLOAT");
        numberArrayNbtPrefixList = Arrays.asList("BYTE", "LONG", "INT");
    }

    public StringNbtTag(Object nmsNbtObj) {
        String value;
        try {
            if (getValueMethod == null) {
                Class<?> nmsNbtObjectClass = nmsNbtObj.getClass();
                String getValueMethodName = getValueMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "f_");
                getValueMethod = nmsNbtObjectClass.getMethod(getValueMethodName);
            }
            value = getValueMethod.invoke(nmsNbtObj).toString();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //提示版本不兼容
            value = null;
            e.printStackTrace();
        }
        this.value = value;
    }

    public StringNbtTag(String strObj) {
        this.value = strObj;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.STRING;
    }

    @Override
    public Object toNmsNbt() {
        if (value.contains("@")) {
            return parseNumberNbt();
        } else if (value.contains("$")) {
            return parseNumberArrayNbt();
        }
        return parseStringNbt();
    }

    private Object parseNumberArrayNbt() {
        int $index = value.indexOf("$");
        String prefix = value.substring(0, $index);
        if (!numberArrayNbtPrefixList.contains(prefix)) {
            return parseStringNbt();
        }
        String numberStr = value.substring($index + 2, value.length() - 1);
        String[] strList = numberStr.split(",");
        switch (prefix) {
            case "BYTE":
                byte[] byteArrVal = new byte[strList.length];
                for (int i = 0; i < strList.length; i++) {
                    byteArrVal[i] = Byte.parseByte(strList[i]);
                }
                return new ByteArrayNbtTag(byteArrVal).toNmsNbt();
            case "LONG":
                long[] longArrVal = new long[strList.length];
                for (int i = 0; i < strList.length; i++) {
                    longArrVal[i] = Long.parseLong(strList[i]);
                }
                return new LongArrayNbtTag(longArrVal).toNmsNbt();
            case "INT":
                int[] intArrVal = new int[strList.length];
                for (int i = 0; i < strList.length; i++) {
                    intArrVal[i] = Integer.parseInt(strList[i]);
                }
                return new IntArrayNbtTag(intArrVal).toNmsNbt();
            default:
                return parseStringNbt();
        }
    }

    private Object parseNumberNbt() {
        int atIndex = value.indexOf("@");
        String prefix = value.substring(0, atIndex);
        if (!numberNbtPrefixList.contains(prefix)) {
            return parseStringNbt();
        }
        String numberStr = value.substring(atIndex + 1);
        switch (prefix) {
            case "BYTE":
                byte byteVal = Byte.parseByte(numberStr);
                return new NumberNbtTag(byteVal).toNmsNbt();
            case "SHORT":
                short shortVal = Short.parseShort(numberStr);
                return new NumberNbtTag(shortVal).toNmsNbt();
            case "LONG":
                long longVal = Long.parseLong(numberStr);
                return new NumberNbtTag(longVal).toNmsNbt();
            case "FLOAT":
                float floatVal = Float.parseFloat(numberStr);
                return new NumberNbtTag(floatVal).toNmsNbt();
            default:
                return parseStringNbt();
        }
    }

    private Object parseStringNbt() {
        Object nmsNbtObj = null;
        try {
            if (nmsStringNbtConstructor == null) {
                String nmsStringNbtClassName = nmsStringNbtClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), "net.minecraft.nbt.NBTTagString");
                Class<?> nmsStringNbtClass = Class.forName(nmsStringNbtClassName);
                nmsStringNbtConstructor = nmsStringNbtClass.getDeclaredConstructor(String.class);
                nmsStringNbtConstructor.setAccessible(true);
            }

            nmsNbtObj = nmsStringNbtConstructor.newInstance(value);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

    private static void loadGetValueMethodNameMap() {
        getValueMethodNameMap.put("v1_19_R2", "f_");
        getValueMethodNameMap.put("v1_19_R1", "e_");
        getValueMethodNameMap.put("v1_18_R2", "e_");
        getValueMethodNameMap.put("v1_18_R1", "e_");
        getValueMethodNameMap.put("v1_17_R1", "asString");
        getValueMethodNameMap.put("v1_16_R3", "asString");
        getValueMethodNameMap.put("v1_16_R2", "asString");
        getValueMethodNameMap.put("v1_16_R1", "asString");
        getValueMethodNameMap.put("v1_15_R1", "asString");
        getValueMethodNameMap.put("v1_14_R1", "asString");
        getValueMethodNameMap.put("v1_13_R2", "asString");
        getValueMethodNameMap.put("v1_13_R1", "b_");
    }

    private static void loadNmsStringNbtClassNameMap() {
        nmsStringNbtClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_18_R2", "net.minecraft.nbt.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_17_R1", "net.minecraft.nbt.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_16_R3", "net.minecraft.server.v1_16_R3.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_16_R2", "net.minecraft.server.v1_16_R2.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_16_R1", "net.minecraft.server.v1_16_R1.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_15_R1", "net.minecraft.server.v1_15_R1.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_14_R1", "net.minecraft.server.v1_14_R1.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_13_R2", "net.minecraft.server.v1_13_R2.NBTTagString");
        nmsStringNbtClassNameMap.put("v1_13_R1", "net.minecraft.server.v1_13_R1.NBTTagString");
    }

}
