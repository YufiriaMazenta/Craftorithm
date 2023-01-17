package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.item.nbt.NbtType;
import top.oasismc.oasisrecipe.item.nbt.NbtHandler;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ByteArrayNbtTag implements IPluginNbtTag<byte[]> {

    private byte[] value;
    private static final Map<String, String> getValueMethodNameMap;

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "g");
    }

    public ByteArrayNbtTag(Object nmsNbtObj) {
        try {
            Class<?> nmsNbtObjClass = nmsNbtObj.getClass();
            String getValueMethodName = getValueMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getValueMethod = nmsNbtObjClass.getMethod(getValueMethodName);
            this.value = (byte[]) getValueMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //提示版本不兼容
            value = null;
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.BYTE_ARRAY;
    }

}
