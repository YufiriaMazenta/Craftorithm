package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NumberNbtTag implements IPluginNbtTag<Number> {

    private final Number value;
    private final NumberType numberType;
    private static final Map<String, String> getValueMethodNameMap;
    private static final Map<String, Map<NumberType, String>> nmsNumberNbtClassNameMap;

    static {
        getValueMethodNameMap = new HashMap<>();
        getValueMethodNameMap.put("v1_19_R2", "l");

        nmsNumberNbtClassNameMap = new HashMap<>();
        Map<NumberType, String> v1_19_R2nmsClassNameMap = new HashMap<>();
        v1_19_R2nmsClassNameMap.put(NumberType.BYTE, "net.minecraft.nbt.NBTTagByte");
        v1_19_R2nmsClassNameMap.put(NumberType.SHORT, "net.minecraft.nbt.NBTTagShort");
        v1_19_R2nmsClassNameMap.put(NumberType.INT, "net.minecraft.nbt.NBTTagInt");
        v1_19_R2nmsClassNameMap.put(NumberType.LONG, "net.minecraft.nbt.NBTTagLong");
        v1_19_R2nmsClassNameMap.put(NumberType.FLOAT, "net.minecraft.nbt.NBTTagFloat");
        v1_19_R2nmsClassNameMap.put(NumberType.DOUBLE, "net.minecraft.nbt.NBTTagDouble");
        nmsNumberNbtClassNameMap.put("v1_19_R2", v1_19_R2nmsClassNameMap);
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
        String nmsNumberNbtClassName = nmsNumberNbtClassNameMap.get(NbtHandler.getNmsVersion()).get(numberType);
        Class<?> nmsNumberNbtClass;
        Object nmsNbtObj = null;
        try {
            nmsNumberNbtClass = Class.forName(nmsNumberNbtClassName);
            Constructor<?> constructor = nmsNumberNbtClass.getDeclaredConstructor(numberType.typeClass);
            constructor.setAccessible(true);
            nmsNbtObj = constructor.newInstance(value);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nmsNbtObj;
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

        INT(int.class),
        SHORT(short.class),
        BYTE(byte.class),
        FLOAT(float.class),
        DOUBLE(double.class),
        LONG(long.class);

        private final Class<?> typeClass;

        NumberType(Class<?> typeClass) {
            this.typeClass = typeClass;
        }

    }

}
