package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;

import java.util.*;
import java.util.function.Function;

/**
 * 1.21.4 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12104 implements ComponentProcessorFactory, LifecycleTask {

    CUSTOM_MODEL_DATA_COMPONENT("custom_model_data_component", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("custom_model_data_component", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setCustomModelDataComponent(sourceMeta.getCustomModelDataComponent());
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> ProcessorUtils.processor("custom_model_data_component", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setCustomModelDataComponent(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    ITEM_MODEL("item_model", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("item_model", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasItemModel()) {
                resultMeta.setItemModel(sourceMeta.getItemModel());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            NamespacedKey value = ProcessorUtils.parseKey(data.getString("value"));
            if (value == null) {
                CrypticLib.warn("item_model: invalid 'value', expected e.g. mypack:custom_model");
                return ProcessorUtils.noop("item_model");
            }
            return ProcessorUtils.processor("item_model", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setItemModel(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("item_model", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setItemModel(null);
            resultItem.setItemMeta(resultMeta);
        })
    ));

    private final String componentName;
    private final Map<String, Function<ConfigurationSection, ResultProcessor>> types;

    ComponentProcessors12104(String componentName, Map<String, Function<ConfigurationSection, ResultProcessor>> types) {
        this.componentName = componentName;
        this.types = new HashMap<>(types);
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

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = types.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(componentName + " does not support type: " + type);
        }
        return handler.apply(data == null ? ProcessorUtils.EMPTY_DATA : data);
    }

}
