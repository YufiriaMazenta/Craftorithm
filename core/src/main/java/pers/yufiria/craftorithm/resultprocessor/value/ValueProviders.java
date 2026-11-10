package pers.yufiria.craftorithm.resultprocessor.value;

import crypticlib.CrypticLib;
import crypticlib.script.ScriptEngine;
import crypticlib.script.compile.CompiledScript;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

/**
 * 解析结果处理器配置里的取值: 字面量保持原义, {@code <表达式>} 编译为脚本
 * <p>
 * 加载期就把能发现的问题(语法错误/空脚本/非法字面量)报出来, 并降级为默认值,
 * 避免一条写错的取值把整个组件的处理动作丢掉
 */
public final class ValueProviders {

    public static ValueProvider<Integer> integer(
        @Nullable ConfigurationSection data,
        String key,
        int defaultValue,
        String description
    ) {
        return integer(data, key, null, defaultValue, description);
    }

    /**
     * value 缺失时回退读取 alternateKey, 用于 damage 这种存在 value/damage 两种写法的字段
     */
    public static ValueProvider<Integer> integer(
        @Nullable ConfigurationSection data,
        String key,
        @Nullable String alternateKey,
        int defaultValue,
        String description
    ) {
        return parse(
            rawValue(data, key, alternateKey),
            defaultValue,
            ScriptConverter.INT,
            ValueProviders::parseIntegerLiteral,
            description
        );
    }

    public static ValueProvider<Double> decimal(
        @Nullable ConfigurationSection data,
        String key,
        double defaultValue,
        String description
    ) {
        return parse(
            rawValue(data, key, null),
            defaultValue,
            ScriptConverter.DOUBLE,
            ValueProviders::parseDecimalLiteral,
            description
        );
    }

    public static ValueProvider<Boolean> bool(
        @Nullable ConfigurationSection data,
        String key,
        boolean defaultValue,
        String description
    ) {
        return parse(
            rawValue(data, key, null),
            defaultValue,
            ScriptConverter.BOOLEAN,
            ValueProviders::parseBooleanLiteral,
            description
        );
    }

    public static ValueProvider<String> text(
        @Nullable ConfigurationSection data,
        String key,
        String defaultValue,
        String description
    ) {
        return textValue(rawValue(data, key, null), defaultValue, description);
    }

    /**
     * 列表型文本(如 lore 的每一行)没有 key, 直接按原始值创建
     */
    public static ValueProvider<String> textValue(
        @Nullable Object raw,
        String defaultValue,
        String description
    ) {
        return parse(
            raw,
            defaultValue,
            ScriptConverter.STRING,
            value -> value instanceof String text ? text : null,
            description
        );
    }

    private static <T> ValueProvider<T> parse(
        @Nullable Object raw,
        T defaultValue,
        ScriptConverter<T> converter,
        Function<Object, T> literalParser,
        String description
    ) {
        if (raw == null) {
            return new FixedValue<>(defaultValue);
        }
        if (isScriptMarked(raw)) {
            CompiledScript script = compileScript((String) raw, description);
            if (script != null) {
                return new ScriptedValue<>(script, converter, description);
            }
            return new FixedValue<>(defaultValue);
        }
        T literal = literalParser.apply(raw);
        if (literal != null) {
            return new FixedValue<>(literal);
        }
        warnInvalid(raw, description);
        return new FixedValue<>(defaultValue);
    }

    /**
     * 判定 &lt;...&gt; 包裹: 只认首尾这一对, 中间允许出现 &gt; (如 {@code <a > b>})
     */
    private static boolean isScriptMarked(Object raw) {
        if (!(raw instanceof String text)) {
            return false;
        }
        String trimmed = text.trim();
        return trimmed.length() >= 2 && trimmed.startsWith("<") && trimmed.endsWith(">");
    }

    @Nullable
    private static CompiledScript compileScript(String raw, String description) {
        String trimmed = raw.trim();
        String source = trimmed.substring(1, trimmed.length() - 1).trim();
        if (source.isEmpty()) {
            CrypticLib.warn("Script value '" + description + "' is empty, using the default value");
            return null;
        }
        try {
            return ScriptEngine.INSTANCE.compile(description, source);
        } catch (Throwable throwable) {
            //脚本语法错误只降级这一个取值, 同组件其它动作照常生效
            CrypticLib.warn("Invalid script for result processor '" + description + "': " + throwable.getMessage());
            return null;
        }
    }

    private static void warnInvalid(Object raw, String description) {
        if (raw instanceof ConfigurationSection || raw instanceof Map) {
            CrypticLib.warn("Result processor value '" + description + "' is a nested section, write the script as a string instead: '<expression>'");
            return;
        }
        CrypticLib.warn("Invalid value for result processor '" + description + "': " + raw);
    }

    @Nullable
    private static Object rawValue(@Nullable ConfigurationSection data, String key, @Nullable String alternateKey) {
        if (data == null) {
            return null;
        }
        Object raw = data.get(key);
        if (raw == null && alternateKey != null) {
            raw = data.get(alternateKey);
        }
        return raw;
    }

    @Nullable
    private static Integer parseIntegerLiteral(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (!(raw instanceof String text)) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            long value = new BigDecimal(trimmed).longValue();
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                return null;
            }
            return (int) value;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Nullable
    private static Double parseDecimalLiteral(Object raw) {
        if (raw instanceof Number number) {
            return number.doubleValue();
        }
        if (!(raw instanceof String text)) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(trimmed).doubleValue();
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Nullable
    private static Boolean parseBooleanLiteral(Object raw) {
        if (raw instanceof Boolean bool) {
            return bool;
        }
        if (raw instanceof Number number) {
            //与脚本里的布尔转换保持一致: 非零即真
            return number.doubleValue() != 0;
        }
        if (!(raw instanceof String text)) {
            return null;
        }
        String trimmed = text.trim();
        if ("true".equalsIgnoreCase(trimmed)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmed)) {
            return false;
        }
        return null;
    }

}
