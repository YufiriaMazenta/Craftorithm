package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.inventory.meta.components.ToolComponent;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.behavior.ComponentRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.behavior.ScalarBehaviors;

/**
 * 1.21 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12100 implements ComponentProcessorFactory, LifecycleTask {

    TOOL("tool") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(ToolComponent.class)
                .getter(meta -> meta.hasTool() ? meta.getTool() : null)
                .setter((meta, value) -> meta.setTool(value))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    };

    private final String componentName;

    ComponentProcessors12100(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifecyclePhase) {
        if (MinecraftVersion.current().before(MinecraftVersion.V1_21)) {
            return;
        }
        for (ComponentProcessors12100 factory : values()) {
            ResultProcessorManager.INSTANCE.registerFactory(factory);
        }
    }

    @Override
    public String componentName() {
        return componentName;
    }

}
