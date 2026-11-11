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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.behavior.ComponentRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.behavior.ScalarBehaviors;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

/**
 * 1.20.5 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12005 implements ComponentProcessorFactory, LifecycleTask {

    FOOD("food") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(org.bukkit.inventory.meta.components.FoodComponent.class)
                .getter(meta -> meta.hasFood() ? meta.getFood() : null)
                .setter((meta, value) -> meta.setFood(value))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    HIDE_TOOLTIP("hide_tooltip") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Boolean.class)
                .getter(meta -> meta.isHideTooltip())
                .setter((meta, value) -> meta.setHideTooltip(value != null && value))
                .valueReader(data -> ValueProviders.bool(data, "value", false, "hide_tooltip/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.add())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    ITEM_NAME("item_name") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(String.class)
                .getter(meta -> meta.hasItemName() ? meta.getItemName() : null)
                .setter((meta, value) -> meta.setItemName(value))
                .valueReader(data -> ValueProviders.text(data, "value", "", "item_name/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.addRequired(
                    (text, player) -> text.isEmpty() ? null : parseText(text, player)
                ))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    MAX_STACK_SIZE("max_stack_size") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Integer.class)
                .getter(meta -> meta.hasMaxStackSize() ? meta.getMaxStackSize() : null)
                .setter((meta, value) -> meta.setMaxStackSize(value))
                .valueReader(data -> ValueProviders.integer(data, "value", 1, "max_stack_size/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                //越界的数值会让 Bukkit 在合成期抛异常, 统一在运行期修正
                .behavior(Behaviors.ADD, ScalarBehaviors.add(
                    (value, player) -> ProcessorUtils.clampInt(value, 1, 99, "max_stack_size/value")
                ))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    RARITY("rarity") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(ItemRarity.class)
                .getter(meta -> meta.hasRarity() ? meta.getRarity() : null)
                .setter((meta, value) -> meta.setRarity(value))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove())
                .behavior(Behaviors.ADD, (context, field, data) -> {
                    ItemRarity rarity = ProcessorUtils.parseEnum(
                        ItemRarity.class, data == null ? null : data.getString("value")
                    );
                    if (rarity == null) {
                        CrypticLib.warn("rarity: invalid 'value', expected COMMON/UNCOMMON/RARE/EPIC");
                        return null;
                    }
                    return field.processor(context.qualifiedName(), (sourceItem, resultItem, player) -> rarity);
                });
        }
    },

    /**
     * 防火。沿用 Bukkit 的 setFireResistant（已废弃，但 1.20.5 起到 26.x 都能用），
     * 更通用的抗伤害写法见 damage_resistant。
     */
    FIRE_RESISTANCE("fire_resistance") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Boolean.class)
                .getter(meta -> meta.isFireResistant())
                .setter((meta, value) -> meta.setFireResistant(value != null && value))
                .valueReader(data -> ValueProviders.bool(data, "value", true, "fire_resistance/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.add())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    /**
     * 最大耐久上限，独立于 damage 组件。
     */
    MAX_DAMAGE("max_damage") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Integer.class)
                .getter(meta -> meta instanceof Damageable damageable && damageable.hasMaxDamage() ? damageable.getMaxDamage() : null)
                .setter((meta, value) -> {
                    //value 为 null 表示清除自定义上限, setMaxDamage 接受 null
                    if (meta instanceof Damageable damageable) {
                        damageable.setMaxDamage(value);
                    }
                })
                .valueReader(data -> ValueProviders.integer(data, "value", 1, "max_damage/value"))
                .accepts(meta -> meta instanceof Damageable)
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.add((value, player) -> Math.max(1, value)))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    ENCHANTMENT_GLINT_OVERRIDE("enchantment_glint_override") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Boolean.class)
                .getter(meta -> meta.hasEnchantmentGlintOverride() ? meta.getEnchantmentGlintOverride() : null)
                .setter((meta, value) -> meta.setEnchantmentGlintOverride(value))
                .valueReader(data -> ValueProviders.bool(data, "value", false, "enchantment_glint_override/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.add())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    /**
     * 药水的基础类型
     */
    POTION("potion") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(PotionType.class)
                .getter(meta -> meta instanceof PotionMeta potionMeta && potionMeta.hasBasePotionType()
                    ? potionMeta.getBasePotionType() : null)
                .setter((meta, value) -> {
                    if (meta instanceof PotionMeta potionMeta) {
                        potionMeta.setBasePotionType(value);
                    }
                })
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
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
