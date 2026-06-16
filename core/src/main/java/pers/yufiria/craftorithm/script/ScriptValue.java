package pers.yufiria.craftorithm.script;

/**
 * 脚本值的类型安全封装
 * 所有脚本内部运算都通过此类进行，避免 ClassCastException
 */
public sealed interface ScriptValue permits ScriptValue.Str, ScriptValue.Num, ScriptValue.Bool, ScriptValue.NullValue {

    // ---- 工厂方法 ----
    static ScriptValue of(String value) { return new Str(value); }
    static ScriptValue of(double value) { return new Num(value); }
    static ScriptValue of(boolean value) { return Bool.of(value); }
    static ScriptValue nil() { return NullValue.INSTANCE; }

    // ---- 类型判断 ----
    default boolean isString()  { return this instanceof Str; }
    default boolean isNumber()  { return this instanceof Num; }
    default boolean isBoolean() { return this instanceof Bool; }
    default boolean isNull()    { return this instanceof NullValue; }
    default boolean isTruthy()  { return !isNull() && !(this instanceof Bool(boolean value) && !value); }

    // ---- 取值 ----
    default String asString() {
        return switch (this) {
            case Str s   -> s.value();
            case Num n   -> String.valueOf(n.value());
            case Bool b  -> String.valueOf(b.value());
            case NullValue nv -> "null";
        };
    }
    default double asNumber() {
        return switch (this) {
            case Num n   -> n.value();
            case Str s   -> { try { yield Double.parseDouble(s.value()); } catch (Exception e) { yield 0; } }
            case Bool b  -> b.value() ? 1 : 0;
            case NullValue nv -> 0;
        };
    }
    default boolean asBoolean() {
        return switch (this) {
            case Bool b  -> b.value();
            case Num n   -> n.value() != 0;
            case Str s   -> Boolean.parseBoolean(s.value());
            case NullValue nv -> false;
        };
    }

    // ---- 比较 ----
    default int compare(ScriptValue other) {
        if (this.isNumber() || other.isNumber())
            return Double.compare(this.asNumber(), other.asNumber());
        return this.asString().compareTo(other.asString());
    }

    // ---- 具体类型 ----
    record Str(String value) implements ScriptValue {}
    record Num(double value) implements ScriptValue {}
    record Bool(boolean value) implements ScriptValue {
        private static final Bool TRUE  = new Bool(true);
        private static final Bool FALSE = new Bool(false);
        static Bool of(boolean v) { return v ? TRUE : FALSE; }
    }
    enum NullValue implements ScriptValue { INSTANCE }
}
