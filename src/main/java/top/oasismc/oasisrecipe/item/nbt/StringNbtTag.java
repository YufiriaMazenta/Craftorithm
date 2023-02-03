package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StringNbtTag implements IPluginNbtTag<String> {

    private final String value;
    private static final Map<String, String> getValueMethodNameMap;
    private static final Map<String, String> nmsStringNbtClassNameMap;
    private static final List<String> numberNbtPrefixList;
    private static final List<String> numberArrayNbtPrefixList;

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "f_");

        nmsStringNbtClassNameMap = new HashMap<>();
        nmsStringNbtClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagString");

        numberNbtPrefixList = Arrays.asList("BYTE", "SHORT", "LONG", "FLOAT");
        numberArrayNbtPrefixList = Arrays.asList("BYTE", "LONG", "INT");
    }

    public StringNbtTag(Object nmsNbtObj) {
        String value;
        try {
            Class<?> nmsNbtObjectClass = nmsNbtObj.getClass();
            String getValueMethodName = getValueMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getValueMethod = nmsNbtObjectClass.getMethod(getValueMethodName);
            value = getValueMethod.invoke(nmsNbtObj).toString();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //提示版本不兼容
            value = null;
            e.printStackTrace();
        }
        this.value = value;
    }

    public StringNbtTag(String strObj, int flag) {
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
        Class<?> nmsStringNbtClass;
        Object nmsNbtObj = null;
        try {
            nmsStringNbtClass = Class.forName(nmsStringNbtClassNameMap.get(NbtHandler.getNmsVersion()));
            Constructor<?> constructor = nmsStringNbtClass.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            nmsNbtObj = constructor.newInstance(value);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

}
