package pers.yufiria.craftorithm.resultprocessor.behavior;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

/**
 * 一个可扩展的处理行为。
 * <p>
 * 行为只描述"做什么", 组件负责声明"我支持哪些行为"。第三方注册新行为时
 * 不需要修改 Craftorithm 的代码。
 * <p>
 * 返回 null 表示该行为在当前配置下不生效(配置有误/缺少必要字段), 调用方会跳过这一条,
 * 不会中断同配方的其它处理器。
 */
@FunctionalInterface
public interface ProcessorBehavior {

    @Nullable
    ResultProcessor create(BehaviorContext context, @Nullable ConfigurationSection data);

}
