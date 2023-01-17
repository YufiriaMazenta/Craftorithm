package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.item.nbt.NbtHandler;
import top.oasismc.oasisrecipe.item.nbt.NbtType;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NumberNbtTag implements IPluginNbtTag<Number> {

    private final Number value;
    private static final Map<String, String> getValueMethodNameMap;

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "l");
    }

    public NumberNbtTag(Object nmsNbtObj) {
        Number value;
        try {
            Class<?> nmsNbtObjectClass = nmsNbtObj.getClass();
            String getValueMethodName = getValueMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getValueMethod = nmsNbtObjectClass.getMethod(getValueMethodName);
            value = (Number) getValueMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //提示版本不兼容
            e.printStackTrace();
            value = 0;
        }
        this.value = value;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public NbtType getType() {
        return NbtType.NUMBER;
    }

}
