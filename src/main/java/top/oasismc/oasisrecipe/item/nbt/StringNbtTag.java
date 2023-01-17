package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.item.nbt.NbtType;
import top.oasismc.oasisrecipe.item.nbt.NbtHandler;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class StringNbtTag implements IPluginNbtTag<String> {

    private final String value;
    private static final Map<String, String> getValueMethodNameMap;

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "f_");
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

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.STRING;
    }

}
