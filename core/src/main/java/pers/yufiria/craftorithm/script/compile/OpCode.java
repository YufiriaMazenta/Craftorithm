package pers.yufiria.craftorithm.script.compile;

/**
 * 操作码
 * 栈式虚拟机的指令类型
 */
public enum OpCode {
    // 栈操作
    PUSH,           // PUSH value → 将值压栈
    POP,            // POP → 弹出栈顶
    DUP,            // DUP → 复制栈顶

    // 运算
    CMP_EQ,         // a == b → Boolean
    CMP_NEQ,        // a != b → Boolean
    CMP_GT,         // a > b  → Boolean
    CMP_GTE,        // a >= b → Boolean
    CMP_LT,         // a < b  → Boolean
    CMP_LTE,        // a <= b → Boolean
    AND,            // a && b → Boolean
    OR,             // a || b → Boolean
    NOT,            // !a     → Boolean

    // 函数调用
    CALL,           // CALL funcName argCount → 调用函数

    // 控制流
    JUMP,           // JMP offset → 无条件跳转
    JUMP_IF_FALSE,  // JMP_IF offset → 条件为false时跳转

    // 特殊
    RETURN,         // RETURN → 结束执行，栈顶为返回值
    NOP,            // 空操作
}
