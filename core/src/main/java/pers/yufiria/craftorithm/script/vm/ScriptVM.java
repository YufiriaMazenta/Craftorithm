package pers.yufiria.craftorithm.script.vm;

import crypticlib.CrypticLibBukkit;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptException;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.script.compile.Instruction;
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

    /** 默认最大指令执行次数，防止无限循环 */
    public static final int DEFAULT_MAX_INSTRUCTIONS = 100_000;

    private final CompiledScript script;
    private final ScriptContext context;
    private final int maxInstructions;
    private final Deque<ScriptValue> stack = new ArrayDeque<>();
    private int pc;
    private boolean returned;
    private boolean paused;
    private int executedCount;

    public ScriptVM(CompiledScript script, ScriptContext context) {
        this(script, context, DEFAULT_MAX_INSTRUCTIONS);
    }

    public ScriptVM(CompiledScript script, ScriptContext context, int maxInstructions) {
        this.script = script;
        this.context = context;
        this.maxInstructions = maxInstructions;
    }

    /**
     * 执行编译后的脚本
     * @return 执行结果（栈顶值）
     */
    public ScriptValue execute() {
        stack.clear();
        pc = 0;
        returned = false;
        paused = false;
        executedCount = 0;
        return runLoop();
    }

    /**
     * 从当前状态恢复执行（用于 delay 后续恢复）
     * @return 执行结果（栈顶值）
     */
    public ScriptValue resume() {
        returned = false;
        paused = false;
        return runLoop();
    }

    /**
     * 核心执行循环
     */
    private ScriptValue runLoop() {
        List<Instruction> instructions = script.instructions();

        while (pc < instructions.size() && !returned && !paused) {
            if (++executedCount > maxInstructions) {
                throw new ScriptException("Script execution exceeded maximum instruction limit (" + maxInstructions + "): " + script.sourceName());
            }

            Instruction inst = instructions.get(pc);
            pc++;

            switch (inst.opCode()) {
                case PUSH -> stack.push(inst.operand());
                case POP  -> popStack("POP");
                case DUP  -> {
                    ScriptValue top = peekStack("DUP");
                    stack.push(top);
                }

                case CMP_EQ  -> { ScriptValue r = popStack("CMP_EQ"), l = popStack("CMP_EQ"); stack.push(ScriptValue.of(l.compare(r) == 0)); }
                case CMP_NEQ -> { ScriptValue r = popStack("CMP_NEQ"), l = popStack("CMP_NEQ"); stack.push(ScriptValue.of(l.compare(r) != 0)); }
                case CMP_GT  -> { ScriptValue r = popStack("CMP_GT"), l = popStack("CMP_GT"); stack.push(ScriptValue.of(l.compare(r) > 0)); }
                case CMP_GTE -> { ScriptValue r = popStack("CMP_GTE"), l = popStack("CMP_GTE"); stack.push(ScriptValue.of(l.compare(r) >= 0)); }
                case CMP_LT  -> { ScriptValue r = popStack("CMP_LT"), l = popStack("CMP_LT"); stack.push(ScriptValue.of(l.compare(r) < 0)); }
                case CMP_LTE -> { ScriptValue r = popStack("CMP_LTE"), l = popStack("CMP_LTE"); stack.push(ScriptValue.of(l.compare(r) <= 0)); }

                case AND -> {
                    ScriptValue r = popStack("AND"), l = popStack("AND");
                    stack.push(ScriptValue.of(l.asBoolean() && r.asBoolean()));
                }
                case OR -> {
                    ScriptValue r = popStack("OR"), l = popStack("OR");
                    stack.push(ScriptValue.of(l.asBoolean() || r.asBoolean()));
                }
                case NOT -> {
                    stack.push(ScriptValue.of(!popStack("NOT").asBoolean()));
                }

                case ADD -> {
                    ScriptValue r = popStack("ADD"), l = popStack("ADD");
                    if (l.isString() || r.isString()) {
                        stack.push(ScriptValue.of(l.asString() + r.asString()));
                    } else {
                        stack.push(ScriptValue.of(l.asNumber() + r.asNumber()));
                    }
                }
                case SUB -> {
                    ScriptValue r = popStack("SUB"), l = popStack("SUB");
                    stack.push(ScriptValue.of(l.asNumber() - r.asNumber()));
                }
                case MUL -> {
                    ScriptValue r = popStack("MUL"), l = popStack("MUL");
                    stack.push(ScriptValue.of(l.asNumber() * r.asNumber()));
                }
                case DIV -> {
                    ScriptValue r = popStack("DIV"), l = popStack("DIV");
                    double divisor = r.asNumber();
                    if (divisor == 0) {
                        throw new ScriptException("Division by zero at line " + inst.line() + " in script: " + script.sourceName());
                    }
                    stack.push(ScriptValue.of(l.asNumber() / divisor));
                }
                case NEG -> {
                    ScriptValue operand = popStack("NEG");
                    stack.push(ScriptValue.of(-operand.asNumber()));
                }

                case CALL -> executeCall(inst);

                case JUMP -> pc = inst.jumpOffset();
                case JUMP_IF_FALSE -> {
                    if (!popStack("JUMP_IF_FALSE").asBoolean()) {
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

    private ScriptValue popStack(String opName) {
        if (stack.isEmpty()) {
            throw new ScriptException("Stack underflow at operation " + opName + " (line " + (pc > 0 ? pc - 1 : 0) + ") in script: " + script.sourceName());
        }
        return stack.pop();
    }

    private ScriptValue peekStack(String opName) {
        if (stack.isEmpty()) {
            throw new ScriptException("Stack underflow at operation " + opName + " (line " + (pc > 0 ? pc - 1 : 0) + ") in script: " + script.sourceName());
        }
        return stack.peek();
    }

    /**
     * 中断执行并调度延迟恢复
     * @param delayTicks 延迟 tick 数
     */
    public void pauseAndScheduleResume(long delayTicks) {
        paused = true;
        CrypticLibBukkit.scheduler().syncLater(this::resume, delayTicks);
    }

    /**
     * 提前返回（供 return 函数调用）
     */
    public void doReturn(ScriptValue value) {
        stack.push(value);
        returned = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public Deque<ScriptValue> stack() {
        return stack;
    }

    public ScriptContext context() {
        return context;
    }

    private void executeCall(Instruction inst) {
        String funcName = inst.funcName();
        int argCount = inst.jumpOffset();

        ScriptValue[] args = new ScriptValue[argCount];
        for (int i = argCount - 1; i >= 0; i--) {
            args[i] = popStack("CALL:" + funcName);
        }

        ScriptFunction func = ScriptFunctionRegistry.INSTANCE.getFunction(funcName);
        if (func == null) {
            throw new ScriptException("Unknown function: " + funcName + " at line " + inst.line());
        }

        ScriptValue result = func.execute(context, this, args);
        if (!paused) {
            stack.push(result == null ? ScriptValue.nil() : result);
        }
    }
}
