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
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.behavior.ScalarBehaviors;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.function.BiConsumer;

/**
 * 1.21.2 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12102 implements ComponentProcessorFactory, LifecycleTask {

    GLIDER("glider") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, Boolean> setter = (meta, value) -> meta.setGlider(Boolean.TRUE.equals(value));
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(ItemMeta::isGlider, setter))
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    setter, data -> ValueProviders.bool(data, "value", false, "glider/value")))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    ENCHANTABLE("enchantable") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, Integer> setter = ItemMeta::setEnchantable;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasEnchantable() ? meta.getEnchantable() : null, setter))
                //越界的数值会让 Bukkit 在合成期抛异常, 统一在运行期修正
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    setter,
                    data -> ValueProviders.integer(data, "value", 1, "enchantable/value"),
                    (value, player) -> ProcessorUtils.clampInt(value, 1, Integer.MAX_VALUE, "enchantable/value")))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    TOOLTIP_STYLE("tooltip_style") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, NamespacedKey> setter = ItemMeta::setTooltipStyle;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasTooltipStyle() ? meta.getTooltipStyle() : null, setter))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter))
                .behavior(Behaviors.ADD, (context, data) -> {
                    NamespacedKey style = ProcessorUtils.parseKey(data == null ? null : data.getString("value"));
                    if (style == null) {
                        CrypticLib.warn("tooltip_style: invalid 'value', expected e.g. mypack:custom_tooltip");
                        return null;
                    }
                    return context.processor((sourceItem, resultItem, player) ->
                        resultItem.edit(meta -> setter.accept(meta, style)));
                });
        }
    },

    USE_COOLDOWN("use_cooldown") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            registrar
                .behavior(Behaviors.COPY_FROM_SOURCE, (context, data) -> context.requireSource((sourceItem, resultItem, player) -> {
                    ItemMeta sourceMeta = sourceItem.meta();
                    if (!sourceMeta.hasUseCooldown()) {
                        return;
                    }
                    UseCooldownComponent sourceComponent = sourceMeta.getUseCooldown();
                    resultItem.edit(resultMeta -> {
                        UseCooldownComponent resultComponent = resultMeta.getUseCooldown();
                        resultComponent.setCooldownSeconds(sourceComponent.getCooldownSeconds());
                        resultComponent.setCooldownGroup(sourceComponent.getCooldownGroup());
                        resultMeta.setUseCooldown(resultComponent);
                    });
                }))
                .behavior(Behaviors.ADD, (context, data) -> {
                    ValueProvider<Double> seconds = ValueProviders.decimal(data, "seconds", 1.0D, "use_cooldown/seconds");
                    NamespacedKey group = ProcessorUtils.parseKey(data == null ? null : data.getString("group"));
                    return context.processor((sourceItem, resultItem, player) -> {
                        Double resolved = seconds.resolve(player, sourceItem, resultItem);
                        if (resolved == null) {
                            return;
                        }
                        float cooldownSeconds = (float) Math.max(0, resolved);
                        resultItem.edit(resultMeta -> {
                            UseCooldownComponent component = resultMeta.getUseCooldown();
                            component.setCooldownSeconds(cooldownSeconds);
                            component.setCooldownGroup(group);
                            resultMeta.setUseCooldown(component);
                        });
                    });
                })
                .behavior(Behaviors.REMOVE, (context, data) -> context.processor((sourceItem, resultItem, player) ->
                    resultItem.edit(resultMeta -> resultMeta.setUseCooldown(null))
                ));
        }
    },

    DAMAGE_RESISTANT("damage_resistant") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            registrar
                .behavior(Behaviors.COPY_FROM_SOURCE, (context, data) -> context.requireSource((sourceItem, resultItem, player) -> {
                    ItemMeta sourceMeta = sourceItem.meta();
                    if (!sourceMeta.hasDamageResistant()) {
                        return;
                    }
                    resultItem.edit(resultMeta -> resultMeta.setDamageResistant(sourceMeta.getDamageResistant()));
                }))
                .behavior(Behaviors.REMOVE, (context, data) -> context.processor((sourceItem, resultItem, player) ->
                    resultItem.edit(resultMeta -> resultMeta.setDamageResistant(null))
                ));
        }
    };

    private final String componentName;

    ComponentProcessors12102(String componentName) {
        this.componentName = componentName;
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

}
