package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NumberNbtTag implements IPluginNbtTag<Number> {

    private final Number value;
    private final NumberType numberType;
    private static final Map<String, String> getValueMethodNameMap;

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "l");
    }

    public NumberNbtTag(Object nmsNbtObj) {
        Number value;
        NumberType numberType;
        try {
            Class<?> nmsNbtObjectClass = nmsNbtObj.getClass();
            String nbtObjClsName = nmsNbtObj.getClass().getSimpleName();
            int nbtTagStrIndex = nbtObjClsName.indexOf("NBTTag") + 6;
            switch (nbtObjClsName.substring(nbtTagStrIndex)) {
                case "Byte":
                    numberType = NumberType.BYTE;
                    break;
                case "Short":
                    numberType = NumberType.SHORT;
                    break;
                case "Int":
                    numberType = NumberType.INT;
                    break;
                case "Long":
                    numberType = NumberType.LONG;
                    break;
                case "Float":
                    numberType = NumberType.FLOAT;
                    break;
                case "Double":
                default:
                    numberType = NumberType.DOUBLE;
                    break;
            }
            String getValueMethodName = getValueMethodNameMap.get(NbtHandler.getNmsVersion());
            Method getValueMethod = nmsNbtObjectClass.getMethod(getValueMethodName);
            value = (Number) getValueMethod.invoke(nmsNbtObj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //提示版本不兼容
            e.printStackTrace();
            value = 0;
            numberType = NumberType.DOUBLE;
        }
        this.value = value;
        this.numberType = numberType;
    }

    public NumberNbtTag(int value) {
        this.value = value;
        this.numberType = NumberType.INT;
    }

    public NumberNbtTag(byte value) {
        this.value = value;
        this.numberType = NumberType.BYTE;
    }

    public NumberNbtTag(short value) {
        this.value = value;
        this.numberType = NumberType.SHORT;
    }

    public NumberNbtTag(long value) {
        this.value = value;
        this.numberType = NumberType.LONG;
    }

    public NumberNbtTag(float value) {
        this.value = value;
        this.numberType = NumberType.FLOAT;
    }

    public NumberNbtTag(double value) {
        this.value = value;
        this.numberType = NumberType.DOUBLE;
    }

    @Override
    public Number getValue() {
        return this.value;
    }

    @Override
    public NbtType getType() {
        return NbtType.NUMBER;
    }

    @Override
    public Object toNmsNbt() {
        return null;
    }

    public NumberType getNumberType() {
        return this.numberType;
    }

    /*
    获取NBT的完全值，包含类型和值
     */
    public Object getFullValue() {
        switch (numberType) {
            case INT:
            case DOUBLE:
            default:
                return this.value;
            case BYTE:
            case FLOAT:
            case SHORT:
            case LONG:
                return this.toString();
        }
    }

    @Override
    public String toString() {
        return this.numberType.name() + "@" + this.value;
    }

    enum NumberType {
        INT,
        SHORT,
        BYTE,
        FLOAT,
        DOUBLE,
        LONG
    }

}
