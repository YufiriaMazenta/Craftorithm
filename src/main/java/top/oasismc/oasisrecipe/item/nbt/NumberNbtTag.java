package top.oasismc.oasisrecipe.item.nbt;

import top.oasismc.oasisrecipe.OasisRecipe;
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
    private static final Map<NumberType, Constructor<?>> nmsNumberNbtConstructorMap;

    static {
        getValueMethodNameMap = new HashMap<>();
        loadGetValueMethodNameMap();

        nmsNumberNbtClassNameMap = new HashMap<>();
        loadNmsNumberNbtClassNameMap();

        nmsNumberNbtConstructorMap = new HashMap<>();
        for (NumberType type : NumberType.values()) {
            try {
                Map<NumberType, String> defaultNmsNumberNbtClassNameMap = nmsNumberNbtClassNameMap.get("default");
                String valueNmsClassName = nmsNumberNbtClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), defaultNmsNumberNbtClassNameMap).get(type);
                Class<?> valueNmsClass = Class.forName(valueNmsClassName);
                Constructor<?> constructor = valueNmsClass.getDeclaredConstructor(type.typeClass);
                constructor.setAccessible(true);
                nmsNumberNbtConstructorMap.put(type, constructor);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public NumberNbtTag(Object nmsNbtObj) {
        Number value;
        NumberType numberType;
        try {
            Byte nbtTypeId = (Byte) NbtHandler.getGetNmsNbtTypeIdMethod().invoke(nmsNbtObj);
            switch (nbtTypeId) {
                case 1:
                    numberType = NumberType.BYTE;
                    break;
                case 2:
                    numberType = NumberType.SHORT;
                    break;
                case 3:
                    numberType = NumberType.INT;
                    break;
                case 4:
                    numberType = NumberType.LONG;
                    break;
                case 5:
                    numberType = NumberType.FLOAT;
                    break;
                case 6:
                default:
                    numberType = NumberType.DOUBLE;
                    break;
            }
            Class<?> nmsNbtObjectClass = nmsNbtObj.getClass();
            String getValueMethodName = getValueMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "l");
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

    public NumberNbtTag(int value, int flag) {
        this.value = value;
        this.numberType = NumberType.INT;
    }

    public NumberNbtTag(byte value, int flag) {
        this.value = value;
        this.numberType = NumberType.BYTE;
    }

    public NumberNbtTag(short value, int flag) {
        this.value = value;
        this.numberType = NumberType.SHORT;
    }

    public NumberNbtTag(long value, int flag) {
        this.value = value;
        this.numberType = NumberType.LONG;
    }

    public NumberNbtTag(float value, int flag) {
        this.value = value;
        this.numberType = NumberType.FLOAT;
    }

    public NumberNbtTag(double value, int flag) {
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
        Object nmsNbtObj = null;
        try {
            Constructor<?> constructor = nmsNumberNbtConstructorMap.get(numberType);
            nmsNbtObj = constructor.newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
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

    private static void loadNmsNumberNbtClassNameMap() {
        Map<NumberType, String> highVersionNmsNumberNbtClassNameMap = new HashMap<>();
        highVersionNmsNumberNbtClassNameMap.put(NumberType.BYTE, "net.minecraft.nbt.NBTTagByte");
        highVersionNmsNumberNbtClassNameMap.put(NumberType.SHORT, "net.minecraft.nbt.NBTTagShort");
        highVersionNmsNumberNbtClassNameMap.put(NumberType.INT, "net.minecraft.nbt.NBTTagInt");
        highVersionNmsNumberNbtClassNameMap.put(NumberType.LONG, "net.minecraft.nbt.NBTTagLong");
        highVersionNmsNumberNbtClassNameMap.put(NumberType.FLOAT, "net.minecraft.nbt.NBTTagFloat");
        highVersionNmsNumberNbtClassNameMap.put(NumberType.DOUBLE, "net.minecraft.nbt.NBTTagDouble");
        nmsNumberNbtClassNameMap.put("default", highVersionNmsNumberNbtClassNameMap);
        nmsNumberNbtClassNameMap.put("v1_19_R2", highVersionNmsNumberNbtClassNameMap);
        nmsNumberNbtClassNameMap.put("v1_19_R1", highVersionNmsNumberNbtClassNameMap);
        nmsNumberNbtClassNameMap.put("v1_18_R2", highVersionNmsNumberNbtClassNameMap);
        nmsNumberNbtClassNameMap.put("v1_17_R1", highVersionNmsNumberNbtClassNameMap);
        if (OasisRecipe.getInstance().getVanillaVersion() < 17) {
            Map<NumberType, String> lowVersionNmsNumberNbtClassNameMap = new HashMap<>();
            lowVersionNmsNumberNbtClassNameMap.put(NumberType.BYTE, "net.minecraft.server." + NbtHandler.getNmsVersion() + ".NBTTagByte");
            lowVersionNmsNumberNbtClassNameMap.put(NumberType.SHORT, "net.minecraft.server." + NbtHandler.getNmsVersion() + ".NBTTagShort");
            lowVersionNmsNumberNbtClassNameMap.put(NumberType.INT, "net.minecraft.server." + NbtHandler.getNmsVersion() + ".NBTTagInt");
            lowVersionNmsNumberNbtClassNameMap.put(NumberType.LONG, "net.minecraft.server." + NbtHandler.getNmsVersion() + ".NBTTagLong");
            lowVersionNmsNumberNbtClassNameMap.put(NumberType.FLOAT, "net.minecraft.server." + NbtHandler.getNmsVersion() + ".NBTTagFloat");
            lowVersionNmsNumberNbtClassNameMap.put(NumberType.DOUBLE, "net.minecraft.server." + NbtHandler.getNmsVersion() + ".NBTTagDouble");
            nmsNumberNbtClassNameMap.put(NbtHandler.getNmsVersion(), lowVersionNmsNumberNbtClassNameMap);
        }
    }

    private static void loadGetValueMethodNameMap() {
        getValueMethodNameMap.put("v1_19_R2", "l");
        getValueMethodNameMap.put("v1_19_R1", "k");
        getValueMethodNameMap.put("v1_18_R2", "k");
        getValueMethodNameMap.put("v1_18_R1", "k");
        getValueMethodNameMap.put("v1_17_R1", "k");
        getValueMethodNameMap.put("v1_16_R3", "k");
        getValueMethodNameMap.put("v1_16_R2", "k");
        getValueMethodNameMap.put("v1_16_R1", "k");
        getValueMethodNameMap.put("v1_15_R1", "k");
        getValueMethodNameMap.put("v1_14_R1", "j");
        getValueMethodNameMap.put("v1_13_R2", "j");
        getValueMethodNameMap.put("v1_13_R1", "j");
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
