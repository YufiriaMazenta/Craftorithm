package pers.yufiria.craftorithm.script.vm;

import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptException;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.script.compile.Instruction;
import pers.yufiria.craftorithm.script.compile.OpCode;
import pers.yufiria.craftorithm.script.func.ScriptFunction;
import pers.yufiria.craftorithm.script.func.ScriptFunctionRegistry;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * 栈式虚拟机
 * 执行编译后的指令序列
 */
public class ScriptVM {

    private final CompiledScript script;
    private final ScriptContext context;
    private final Deque<ScriptValue> stack = new ArrayDeque<>();
    private int pc;
    private boolean returned;

    public ScriptVM(CompiledScript script, ScriptContext context) {
        this.script = script;
        this.context = context;
    }

    /**
     * 执行编译后的脚本
     * @return 执行结果（栈顶值）
     */
    public ScriptValue execute() {
        stack.clear();
        pc = 0;
        returned = false;
        List<Instruction> instructions = script.instructions();

        while (pc < instructions.size() && !returned) {
            Instruction inst = instructions.get(pc);
            pc++;

            switch (inst.opCode()) {
                case PUSH -> stack.push(inst.operand());
                case POP  -> stack.pop();
                case DUP  -> stack.push(stack.peek());

                case CMP_EQ  -> { ScriptValue r = stack.pop(), l = stack.pop(); stack.push(ScriptValue.of(l.compare(r) == 0)); }
                case CMP_NEQ -> { ScriptValue r = stack.pop(), l = stack.pop(); stack.push(ScriptValue.of(l.compare(r) != 0)); }
                case CMP_GT  -> { ScriptValue r = stack.pop(), l = stack.pop(); stack.push(ScriptValue.of(l.compare(r) > 0)); }
                case CMP_GTE -> { ScriptValue r = stack.pop(), l = stack.pop(); stack.push(ScriptValue.of(l.compare(r) >= 0)); }
                case CMP_LT  -> { ScriptValue r = stack.pop(), l = stack.pop(); stack.push(ScriptValue.of(l.compare(r) < 0)); }
                case CMP_LTE -> { ScriptValue r = stack.pop(), l = stack.pop(); stack.push(ScriptValue.of(l.compare(r) <= 0)); }

                case AND -> {
                    ScriptValue r = stack.pop(), l = stack.pop();
                    stack.push(ScriptValue.of(l.asBoolean() && r.asBoolean()));
                }
                case OR -> {
                    ScriptValue r = stack.pop(), l = stack.pop();
                    stack.push(ScriptValue.of(l.asBoolean() || r.asBoolean()));
                }
                case NOT -> {
                    stack.push(ScriptValue.of(!stack.pop().asBoolean()));
                }

                case CALL -> executeCall(inst);

                case JUMP -> pc = inst.jumpOffset();
                case JUMP_IF_FALSE -> {
                    if (!stack.pop().asBoolean()) {
                        pc = inst.jumpOffset();
                    }
                }

                case RETURN -> {
                    returned = true;
                }

                case NOP -> {}
            }
        }

        return stack.isEmpty() ? ScriptValue.nil() : stack.peek();
    }

    /**
     * 提前返回（供 return 函数调用）
     */
    public void doReturn(ScriptValue value) {
        stack.push(value);
        returned = true;
    }

    private void executeCall(Instruction inst) {
        String funcName = inst.funcName();
        int argCount = inst.jumpOffset();

        ScriptValue[] args = new ScriptValue[argCount];
        for (int i = argCount - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }

        ScriptFunction func = ScriptFunctionRegistry.INSTANCE.getFunction(funcName);
        if (func == null) {
            throw new ScriptException("Unknown function: " + funcName + " at line " + inst.line());
        }

        ScriptValue result = func.execute(context, args);
        stack.push(result == null ? ScriptValue.nil() : result);
    }
}
