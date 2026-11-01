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
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;

import java.util.*;
import java.util.function.Function;

/**
 * 1.21.2 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12102 implements ComponentProcessorFactory, LifecycleTask {

    GLIDER("glider", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("glider", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setGlider(sourceMeta.isGlider());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value");
            return ProcessorUtils.processor("glider", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setGlider(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("glider", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setGlider(false);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    ENCHANTABLE("enchantable", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("enchantable", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasEnchantable()) {
                resultMeta.setEnchantable(sourceMeta.getEnchantable());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            //缺省/越界的数值会让 Bukkit 在合成期抛 IllegalArgumentException, 这里在加载期就修正
            int value = data != null ? data.getInt("value", 1) : 1;
            int enchantable = Math.max(1, value);
            if (enchantable != value) {
                CrypticLib.warn("enchantable value " + value + " must be >= 1, clamped to " + enchantable);
            }
            return ProcessorUtils.processor("enchantable", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setEnchantable(enchantable);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("enchantable", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setEnchantable(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    TOOLTIP_STYLE("tooltip_style", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("tooltip_style", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasTooltipStyle()) {
                resultMeta.setTooltipStyle(sourceMeta.getTooltipStyle());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            NamespacedKey value = ProcessorUtils.parseKey(data.getString("value"));
            if (value == null) {
                CrypticLib.warn("tooltip_style: invalid 'value', expected e.g. mypack:custom_tooltip");
                return ProcessorUtils.noop("tooltip_style");
            }
            return ProcessorUtils.processor("tooltip_style", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setTooltipStyle(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("tooltip_style", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setTooltipStyle(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    USE_COOLDOWN("use_cooldown", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("use_cooldown", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (!sourceMeta.hasUseCooldown()) {
                return;
            }
            UseCooldownComponent sourceComponent = sourceMeta.getUseCooldown();
            UseCooldownComponent resultComponent = resultMeta.getUseCooldown();
            resultComponent.setCooldownSeconds(sourceComponent.getCooldownSeconds());
            resultComponent.setCooldownGroup(sourceComponent.getCooldownGroup());
            resultMeta.setUseCooldown(resultComponent);
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            float seconds = (float) Math.max(0, data.getDouble("seconds", 1.0));
            NamespacedKey group = ProcessorUtils.parseKey(data.getString("group"));
            return ProcessorUtils.processor("use_cooldown", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                UseCooldownComponent component = resultMeta.getUseCooldown();
                component.setCooldownSeconds(seconds);
                component.setCooldownGroup(group);
                resultMeta.setUseCooldown(component);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("use_cooldown", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setUseCooldown(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    DAMAGE_RESISTANT("damage_resistant", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("damage_resistant", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasDamageResistant()) {
                resultMeta.setDamageResistant(sourceMeta.getDamageResistant());
                resultItem.setItemMeta(resultMeta);
            }
        }),
        "remove", data -> ProcessorUtils.processor("damage_resistant", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setDamageResistant(null);
            resultItem.setItemMeta(resultMeta);
        })
    ));

    private final String componentName;
    private final Map<String, Function<ConfigurationSection, ResultProcessor>> types;

    ComponentProcessors12102(String componentName, Map<String, Function<ConfigurationSection, ResultProcessor>> types) {
        this.componentName = componentName;
        this.types = new HashMap<>(types);
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifecyclePhase) {
        if (MinecraftVersion.current().before(MinecraftVersion.V1_21_2)) {
            return;
        }
        for (ComponentProcessors12102 factory : values()) {
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
