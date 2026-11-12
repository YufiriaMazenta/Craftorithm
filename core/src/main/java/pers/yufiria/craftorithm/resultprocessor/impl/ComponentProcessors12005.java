package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.behavior.ScalarBehaviors;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.function.BiConsumer;

/**
 * 1.20.5 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12005 implements ComponentProcessorFactory, LifecycleTask {

    FOOD("food") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, FoodComponent> setter = ItemMeta::setFood;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasFood() ? meta.getFood() : null, setter))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    HIDE_TOOLTIP("hide_tooltip") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, Boolean> setter = (meta, value) -> meta.setHideTooltip(Boolean.TRUE.equals(value));
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(ItemMeta::isHideTooltip, setter))
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    setter, data -> ValueProviders.bool(data, "value", false, "hide_tooltip/value")))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    ITEM_NAME("item_name") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, String> setter = ItemMeta::setItemName;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasItemName() ? meta.getItemName() : null, setter))
                .behavior(Behaviors.ADD, ScalarBehaviors.addRequired(
                    setter,
                    data -> ValueProviders.text(data, "value", "", "item_name/value"),
                    (text, player) -> text.isEmpty() ? null : parseText(text, player)))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    MAX_STACK_SIZE("max_stack_size") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, Integer> setter = ItemMeta::setMaxStackSize;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasMaxStackSize() ? meta.getMaxStackSize() : null, setter))
                //越界的数值会让 Bukkit 在合成期抛异常, 统一在运行期修正
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    setter,
                    data -> ValueProviders.integer(data, "value", 1, "max_stack_size/value"),
                    (value, player) -> ProcessorUtils.clampInt(value, 1, 99, "max_stack_size/value")))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    RARITY("rarity") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, ItemRarity> setter = ItemMeta::setRarity;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasRarity() ? meta.getRarity() : null, setter))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter))
                .behavior(Behaviors.ADD, (context, data) -> {
                    ItemRarity rarity = ProcessorUtils.parseEnum(
                        ItemRarity.class, data == null ? null : data.getString("value")
                    );
                    if (rarity == null) {
                        CrypticLib.warn("rarity: invalid 'value', expected COMMON/UNCOMMON/RARE/EPIC");
                        return null;
                    }
                    return context.processor((sourceItem, resultItem, player) ->
                        resultItem.edit(meta -> setter.accept(meta, rarity)));
                });
        }
    },

    /**
     * 防火。沿用 Bukkit 的 setFireResistant（已废弃，但 1.20.5 起到 26.x 都能用），
     * 更通用的抗伤害写法见 damage_resistant。
     */
    FIRE_RESISTANCE("fire_resistance") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, Boolean> setter = (meta, value) -> meta.setFireResistant(Boolean.TRUE.equals(value));
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(ItemMeta::isFireResistant, setter))
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    setter, data -> ValueProviders.bool(data, "value", true, "fire_resistance/value")))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    /**
     * 最大耐久上限，独立于 damage 组件。
     */
    MAX_DAMAGE("max_damage") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, Integer> setter = (meta, value) -> {
                //value 为 null 表示清除自定义上限, setMaxDamage 接受 null
                if (meta instanceof Damageable damageable) {
                    damageable.setMaxDamage(value);
                }
            };
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta instanceof Damageable damageable && damageable.hasMaxDamage() ? damageable.getMaxDamage() : null,
                    setter))
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    setter,
                    data -> ValueProviders.integer(data, "value", 1, "max_damage/value"),
                    (value, player) -> Math.max(1, value)))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    ENCHANTMENT_GLINT_OVERRIDE("enchantment_glint_override") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, Boolean> setter = ItemMeta::setEnchantmentGlintOverride;
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta.hasEnchantmentGlintOverride() ? meta.getEnchantmentGlintOverride() : null, setter))
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    setter, data -> ValueProviders.bool(data, "value", false, "enchantment_glint_override/value")))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    },

    /**
     * 药水的基础类型
     */
    POTION("potion") {
        @Override
        public void declareBehaviors(BehaviorRegistrar registrar) {
            BiConsumer<ItemMeta, PotionType> setter = (meta, value) -> {
                if (meta instanceof PotionMeta potionMeta) {
                    potionMeta.setBasePotionType(value);
                }
            };
            registrar.behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                    meta -> meta instanceof PotionMeta potionMeta && potionMeta.hasBasePotionType()
                        ? potionMeta.getBasePotionType() : null, setter))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter));
        }
    };

    private final String componentName;

    ComponentProcessors12005(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifecyclePhase) {
        if (MinecraftVersion.current().before(MinecraftVersion.V1_20_5)) {
            return;
        }
        for (ComponentProcessors12005 factory : values()) {
            ResultProcessorManager.INSTANCE.registerFactory(factory);
        }
    }

    @Override
    public String componentName() {
        return componentName;
    }

    private static String parseText(String text, @Nullable Player player) {
        String parsed = player != null ? BukkitTextProcessor.placeholder(player, text) : text;
        return BukkitTextProcessor.color(parsed);
    }

}
