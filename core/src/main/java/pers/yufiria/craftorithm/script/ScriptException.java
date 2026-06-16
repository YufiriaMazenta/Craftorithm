package pers.yufiria.craftorithm.script;

/**
 * 脚本运行时异常
 */
public class ScriptException extends RuntimeException {

    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
