package pers.yufiria.craftorithm.resultprocessor.value;

import crypticlib.script.ScriptException;
import crypticlib.script.ScriptValue;

import java.math.BigDecimal;

/**
 * 把脚本返回值转换成字段需要的类型
 * <p>
 * 转换失败一律抛 {@link ScriptException}, 由 {@link ScriptedValue} 统一告警并跳过该值
 */
@FunctionalInterface
public interface ScriptConverter<T> {

    T convert(ScriptValue value);

    ScriptConverter<Integer> INT = ScriptValue::asInt;

    ScriptConverter<Double> DOUBLE = ScriptValue::asNumber;

    ScriptConverter<Float> FLOAT = value -> (float) value.asNumber();

    /**
     * 文本: nil 会转成空串, 与脚本自身的字符串插值行为保持一致
     */
    ScriptConverter<String> STRING = ScriptValue::asString;

    /**
     * 布尔转换刻意不用 {@link ScriptValue#asBoolean()}:
     * 它对任意字符串都返回 false, papi() 取到空值或写错变量名时会静默变成 false, 比报错更难排查
     */
    ScriptConverter<Boolean> BOOLEAN = ScriptConverter::toBoolean;

    private static boolean toBoolean(ScriptValue value) {
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isNumber()) {
            return value.asBigDecimal().compareTo(BigDecimal.ZERO) != 0;
        }
        if (value.isString()) {
            String raw = value.asString().trim();
            if ("true".equalsIgnoreCase(raw)) {
                return true;
            }
            if ("false".equalsIgnoreCase(raw)) {
                return false;
            }
            throw new ScriptException("Expected a boolean value, got string \"" + raw + "\"");
        }
        throw new ScriptException("Expected a boolean value, got " + value);
    }

}
