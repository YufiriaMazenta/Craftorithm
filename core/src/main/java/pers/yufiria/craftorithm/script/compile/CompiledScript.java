package pers.yufiria.craftorithm.script.compile;

import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.script.vm.ScriptVM;

import java.util.List;

/**
 * 编译后的脚本
 * 由 ScriptCompiler 从 AST 编译生成，可多次高效执行
 */
public class CompiledScript {

    private final String sourceName;
    private final List<Instruction> instructions;

    public CompiledScript(String sourceName, List<Instruction> instructions) {
        this.sourceName = sourceName;
        this.instructions = instructions;
    }

    /**
     * 执行脚本
     * @param context 执行上下文
     * @return 执行结果
     */
    public ScriptValue execute(ScriptContext context) {
        return new ScriptVM(this, context).execute();
    }

    /**
     * 执行脚本并返回布尔结果（用于条件判断）
     * @param context 执行上下文
     * @return 布尔结果
     */
    public boolean evaluate(ScriptContext context) {
        return execute(context).asBoolean();
    }

    public String sourceName() {
        return sourceName;
    }

    public List<Instruction> instructions() {
        return instructions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== " + sourceName + " ===\n");
        for (int i = 0; i < instructions.size(); i++) {
            sb.append(String.format("%3d: %s%n", i, instructions.get(i)));
        }
        return sb.toString();
    }
}
