package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ByteArrayNbtTag implements IPluginNbtTag<byte[]> {

    private byte[] value;
    private static final Map<String, String> getValueMethodNameMap;
    private static final Map<String, String> nmsByteArrayNbtClassNameMap;
    private static Constructor<?> nmsByteArrayNbtConstructor = null;
    private static Method getValueMethod = null;

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "e");
        getValueMethodNameMap.put("v1_19_R1", "d");

        nmsByteArrayNbtClassNameMap = new HashMap<>();
        nmsByteArrayNbtClassNameMap.put("v1_19_R2", "net.minecraft.nbt.NBTTagByteArray");
        nmsByteArrayNbtClassNameMap.put("v1_19_R1", "net.minecraft.nbt.NBTTagByteArray");
    }

    public ByteArrayNbtTag(Object nmsNbtObj) {
        try {
            if (getValueMethod == null) {
                Class<?> nmsNbtObjClass = nmsNbtObj.getClass();
                String getValueMethodName = getValueMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "g");
                getValueMethod = nmsNbtObjClass.getMethod(getValueMethodName);
            }
            this.value = (byte[]) getValueMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //提示版本不兼容
            value = null;
            e.printStackTrace();
        }
    }

    public ByteArrayNbtTag(byte[] byteArray) {
        this.value = byteArray;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.BYTE_ARRAY;
    }

    @Override
    public Object toNmsNbt() {
        Object nmsNbtObj = null;
        try {
            String nmsByteArrayNbtClassName = nmsByteArrayNbtClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), "net.minecraft.nbt.NBTTagByteArray");
            Class<?> nmsByteArrayNbtClass = Class.forName(nmsByteArrayNbtClassName);
            if (nmsByteArrayNbtConstructor == null) {
                nmsByteArrayNbtConstructor = nmsByteArrayNbtClass.getDeclaredConstructor(byte[].class);
                nmsByteArrayNbtConstructor.setAccessible(true);
            }
            nmsNbtObj = nmsByteArrayNbtConstructor.newInstance(value);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
    }

    public String getFullValue() {
        StringJoiner str = new StringJoiner(",", "BYTE$[", "]");
        for (byte b : value) {
            str.add(b + "");
        }
        return str.toString();
    }

}
