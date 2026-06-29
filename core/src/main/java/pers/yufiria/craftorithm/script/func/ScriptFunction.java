package pers.yufiria.craftorithm.script.func;

import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.script.vm.ScriptVM;

/**
 * 脚本函数接口
 * 所有内置函数和自定义函数都实现此接口
 */
@FunctionalInterface
public interface ScriptFunction {

    /**
     * 执行函数
     * @param context 执行上下文（含 Player, Recipe 等）
     * @param vm 虚拟机实例（可用于暂停/恢复执行）
     * @param args 参数列表
     * @return 返回值
     */
    ScriptValue execute(ScriptContext context, ScriptVM vm, ScriptValue... args);

}
