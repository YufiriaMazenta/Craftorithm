package pers.yufiria.craftorithm.script.compile;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.ScriptValue;

/**
 * 编译后的指令
 * @param opCode 操作码
 * @param operand 操作数（PUSH 时为 ScriptValue，CALL 时为函数名，JUMP 时为偏移量，CALL 时 argCount 通过 jumpOffset 传递）
 * @param line 源码行号（用于报错）
 */
public record Instruction(OpCode opCode, ScriptValue operand, String funcName, int jumpOffset, int line) {

    /** PUSH value */
    public static Instruction push(ScriptValue value, int line) {
        return new Instruction(OpCode.PUSH, value, null, 0, line);
    }

    /** 简单指令（无操作数） */
    public static Instruction of(OpCode opCode, int line) {
        return new Instruction(opCode, null, null, 0, line);
    }

    /** CALL funcName argCount */
    public static Instruction call(String funcName, int argCount, int line) {
        return new Instruction(OpCode.CALL, null, funcName, argCount, line);
    }

    /** JUMP / JUMP_IF_FALSE offset */
    public static Instruction jump(OpCode opCode, int offset, int line) {
        return new Instruction(opCode, null, null, offset, line);
    }

    @Override
    public @NotNull String toString() {
        return switch (opCode) {
            case PUSH -> "PUSH " + operand;
            case CALL -> "CALL " + funcName + " " + jumpOffset;
            case JUMP, JUMP_IF_FALSE -> opCode + " " + jumpOffset;
            default -> opCode.name();
        };
    }
}
