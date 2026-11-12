package pers.yufiria.craftorithm.resultprocessor;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;

public interface ComponentProcessorFactory {

    String componentName();

    /**
     * 声明该组件支持的行为。
     * <pre>{@code
     * BiConsumer<ItemMeta, String> setter = ItemMeta::setDisplayName;
     * registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
     *            meta -> meta.hasDisplayName() ? meta.getDisplayName() : null, setter))
     *          .behavior(Behaviors.ADD, ScalarBehaviors.add(
     *            setter, data -> ValueProviders.text(data, "value", "", "display_name/value")));
     * }</pre>
     * 读、写、取值都由行为自己声明; 第三方也可以用同样的 {@code behavior(key, 实现)}
     * 往这个组件追加行为, 不需要修改组件实现。
     */
    void declareBehaviors(@NotNull BehaviorRegistrar registrar);

}
