package pers.yufiria.craftorithm.resultprocessor.value;

import crypticlib.CrypticLib;
import crypticlib.Invoker;
import crypticlib.log.LogLevel;
import crypticlib.log.Logger;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptEngine;
import crypticlib.script.ScriptException;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.MathScriptModule;
import crypticlib.script.object.ObjectScriptModule;
import crypticlib.script.object.ReflectPropertyResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ValueProvidersTest {

    @BeforeAll
    static void setup() {
        // 解析/校验失败会走 CrypticLib.warn, 需要先装上日志器
        CrypticLib.setLogger(new SilentLogger());
        ScriptEngine.INSTANCE.registerModule(ObjectScriptModule.INSTANCE);
        ScriptEngine.INSTANCE.registerModule(MathScriptModule.INSTANCE);
    }

    private static ConfigurationSection section(Object value) {
        MemoryConfiguration config = new MemoryConfiguration();
        config.set("value", value);
        return config;
    }

    private static ItemStack resultItem() {
        return mock(ItemStack.class);
    }

    /**
     * 脚本求值走 apply(上下文), 绕过需要服务端环境的 RootScriptContext
     */
    @SuppressWarnings("unchecked")
    private static <T> T evaluate(ValueProvider<T> provider) {
        return ((ScriptedValue<T>) provider).apply(new ScriptContext(mock(Invoker.class)));
    }

    @SuppressWarnings("unchecked")
    private static <T> ScriptedValue<T> scripted(ValueProvider<T> provider) {
        return (ScriptedValue<T>) provider;
    }

    @Test
    void keepsNumericLiteral() {
        ValueProvider<Integer> provider = ValueProviders.integer(section(5), "value", 0, "test/value");
        assertInstanceOf(FixedValue.class, provider);
        assertEquals(5, provider.resolve(null, null, resultItem()));
    }

    @Test
    void quotedNumberIsParsedAsLiteral() {
        ValueProvider<Integer> provider = ValueProviders.integer(section("5"), "value", 0, "test/value");
        assertInstanceOf(FixedValue.class, provider);
        assertEquals(5, provider.resolve(null, null, resultItem()));
    }

    @Test
    void missingKeyUsesDefault() {
        ValueProvider<Integer> provider = ValueProviders.integer(section(null), "value", 7, "test/value");
        assertEquals(7, provider.resolve(null, null, resultItem()));
    }

    @Test
    void readsAlternateKey() {
        MemoryConfiguration config = new MemoryConfiguration();
        config.set("damage", 3);
        ValueProvider<Integer> provider = ValueProviders.integer(config, "value", "damage", 0, "damage/value");
        assertEquals(3, provider.resolve(null, null, resultItem()));
    }

    @Test
    void evaluatesSingleLineScript() {
        ValueProvider<Integer> provider = ValueProviders.integer(section("<1 + 1>"), "value", 0, "test/value");
        assertInstanceOf(ScriptedValue.class, provider);
        assertEquals(2, evaluate(provider));
    }

    @Test
    void evaluatesMultiLineScript() {
        ValueProvider<Integer> provider = ValueProviders.integer(
            section("<var lv = 2\nreturn lv + 1>"), "value", 0, "test/value"
        );
        assertEquals(3, evaluate(provider));
    }

    @Test
    void stripsOnlyOuterAngleBrackets() {
        ValueProvider<Boolean> provider = ValueProviders.bool(section("<2 > 1>"), "value", false, "test/value");
        assertEquals(true, evaluate(provider));
    }

    @Test
    void emptyScriptFallsBackToDefault() {
        ValueProvider<Integer> provider = ValueProviders.integer(section("<>"), "value", 7, "test/value");
        assertInstanceOf(FixedValue.class, provider);
        assertEquals(7, provider.resolve(null, null, resultItem()));
    }

    @Test
    void invalidScriptFallsBackToDefault() {
        ValueProvider<Integer> provider = ValueProviders.integer(section("<1 +>"), "value", 7, "test/value");
        assertInstanceOf(FixedValue.class, provider);
        assertEquals(7, provider.resolve(null, null, resultItem()));
    }

    @Test
    void nestedSectionFallsBackToDefault() {
        MemoryConfiguration config = new MemoryConfiguration();
        config.createSection("value").set("script", "1 + 1");
        ValueProvider<Integer> provider = ValueProviders.integer(config, "value", 7, "test/value");
        assertEquals(7, provider.resolve(null, null, resultItem()));
    }

    @Test
    void booleanLiterals() {
        assertEquals(true, ValueProviders.bool(section(true), "value", false, "test/value").resolve(null, null, resultItem()));
        assertEquals(true, ValueProviders.bool(section("true"), "value", false, "test/value").resolve(null, null, resultItem()));
        assertEquals(false, ValueProviders.bool(section("false"), "value", true, "test/value").resolve(null, null, resultItem()));
    }

    @Test
    void numericBooleanLiteral() {
        assertEquals(true, ValueProviders.bool(section(1), "value", false, "test/value").resolve(null, null, resultItem()));
        assertEquals(false, ValueProviders.bool(section(0), "value", true, "test/value").resolve(null, null, resultItem()));
    }

    @Test
    void booleanScriptFromStringResult() {
        ValueProvider<Boolean> provider = ValueProviders.bool(section("<\"true\">"), "value", false, "test/value");
        assertEquals(true, evaluate(provider));
    }

    @Test
    void booleanScriptRejectsGarbageString() {
        ValueProvider<Boolean> provider = ValueProviders.bool(section("<\"yes\">"), "value", false, "test/value");
        assertThrows(ScriptException.class, () -> scripted(provider).apply(new ScriptContext(mock(Invoker.class))));
    }

    @Test
    void integerScriptRejectsOverflow() {
        ValueProvider<Integer> provider = ValueProviders.integer(section("<9999999999>"), "value", 0, "test/value");
        assertThrows(ScriptException.class, () -> scripted(provider).apply(new ScriptContext(mock(Invoker.class))));
    }

    @Test
    void decimalScript() {
        ValueProvider<Double> provider = ValueProviders.decimal(section("<0.5 * 3>"), "value", 0D, "test/amount");
        assertEquals(1.5D, evaluate(provider));
    }

    @Test
    void scriptReadsInjectedObject() {
        ValueProvider<Integer> provider = ValueProviders.integer(
            section("<source_item.get(\"amount\") * 2>"), "value", 0, "test/value"
        );
        ScriptContext context = new ScriptContext(mock(Invoker.class));
        context.setVariable("source_item", ScriptValue.of(new FakeItem(7), ReflectPropertyResolver.INSTANCE));
        assertEquals(14, scripted(provider).apply(context));
    }

    @Test
    void missingPropertyFailsConversion() {
        ValueProvider<Integer> provider = ValueProviders.integer(
            section("<source_item.get(\"nope\")>"), "value", 0, "test/value"
        );
        ScriptContext context = new ScriptContext(mock(Invoker.class));
        context.setVariable("source_item", ScriptValue.of(new FakeItem(7), ReflectPropertyResolver.INSTANCE));
        assertThrows(ScriptException.class, () -> scripted(provider).apply(context));
    }

    private static final class SilentLogger implements Logger {

        private LogLevel level = LogLevel.OFF;

        @Override
        public LogLevel level() {
            return level;
        }

        @Override
        public void setLevel(LogLevel level) {
            this.level = level;
        }

        @Override
        public void log(LogLevel level, String msg, Map<String, String> replacements, Throwable throwable) {
            //测试里不输出日志
        }

    }

    public static final class FakeItem {

        private final int amount;

        public FakeItem(int amount) {
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }

    }

}
