package pers.yufiria.craftorithm.resultprocessor;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.resultprocessor.behavior.ComponentRegistrar;

public interface ComponentProcessorFactory {

    String componentName();

    /**
     * 声明该组件支持的行为。
     * <pre>{@code
     * registrar.valueType(String.class)
     *          .getter(meta -> ...)
     *          .setter((meta, value) -> ...)
     *          .behavior(Behaviors.ADD, ScalarBehaviors.add());
     * }</pre>
     * 标准行为由框架统一实现, 组件只需声明字段和启用的行为;
     * 第三方也可以用同样的 {@code behavior(key, 实现)} 往这个组件追加行为, 不需要修改组件实现。
     */
    void declareBehaviors(@NotNull ComponentRegistrar registrar);

}
