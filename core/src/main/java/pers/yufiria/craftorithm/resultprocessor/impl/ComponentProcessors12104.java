package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.behavior.ScalarBehaviors;

import java.util.function.BiConsumer;

/**
 * 1.21.4 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12104 implements ComponentProcessorFactory, LifecycleTask {

    CUSTOM_MODEL_DATA_COMPONENT("custom_model_data_component") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, CustomModelDataComponent> setter = ItemMeta::setCustomModelDataComponent;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    ItemMeta::getCustomModelDataComponent, setter))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    ITEM_MODEL("item_model") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, NamespacedKey> setter = ItemMeta::setItemModel;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasItemModel() ? meta.getItemModel() : null, setter))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter))
                .behavior(Behaviors.ADD, (context, data) -> {
                    NamespacedKey model = ProcessorUtils.parseKey(data == null ? null : data.getString("value"));
                    if (model == null) {
                        CrypticLib.warn("item_model: invalid 'value', expected e.g. mypack:custom_model");
                        return null;
                    }
                    return context.processor((sourceItem, resultItem, player) ->
                        resultItem.edit(meta -> setter.accept(meta, model)));
                });
        }
    };

    private final String componentName;

    ComponentProcessors12104(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifecyclePhase) {
        if (MinecraftVersion.current().before(MinecraftVersion.V1_21_4)) {
            return;
        }
        for (ComponentProcessors12104 factory : values()) {
            ResultProcessorManager.INSTANCE.registerFactory(factory);
        }
    }

    @Override
    public String componentName() {
        return componentName;
    }

}
