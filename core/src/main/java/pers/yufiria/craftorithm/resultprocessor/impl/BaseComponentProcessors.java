package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.behavior.ComponentRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.behavior.ScalarBehaviors;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.UUID;

/**
 * 所有 1.20 起就存在的组件。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum BaseComponentProcessors implements ComponentProcessorFactory, LifecycleTask {

    ALL("all") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.behavior(
                Behaviors.COPY_FROM_SOURCE,
                (context, data) -> context.requireSource(
                    (sourceItem, resultItem, player) -> resultItem.setMeta(sourceItem.meta())
                )
            );
        }
    },

    DISPLAY_NAME("display_name") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(String.class)
                .getter(meta -> meta.hasDisplayName() ? meta.getDisplayName() : null)
                .setter((meta, value) -> meta.setDisplayName(value))
                .valueReader(data -> ValueProviders.text(data, "value", "", "display_name/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.addRequired(
                    (text, player) -> text.isEmpty() ? null : parseText(text, player)
                ))
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    TRIM("trim") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(ArmorTrim.class)
                .getter(meta -> meta instanceof ArmorMeta armorMeta && armorMeta.hasTrim() ? armorMeta.getTrim() : null)
                .setter((meta, value) -> {
                    if (meta instanceof ArmorMeta armorMeta) {
                        armorMeta.setTrim(value);
                    }
                })
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove())
                .behavior(Behaviors.ADD, (context, field, data) -> {
                    TrimPattern pattern = ProcessorUtils.lookup(TrimPattern.class, data == null ? null : data.getString("pattern"));
                    TrimMaterial material = ProcessorUtils.lookup(TrimMaterial.class, data == null ? null : data.getString("material"));
                    if (pattern == null || material == null) {
                        CrypticLib.warn("trim: invalid 'pattern' or 'material'");
                        return null;
                    }
                    ArmorTrim trim = new ArmorTrim(material, pattern);
                    return field.processor(
                        context.qualifiedName(),
                        (sourceItem, resultItem, player) ->
                            resultItem.meta() instanceof ArmorMeta ? trim : null
                    );
                });
        }
    },

    UNBREAKABLE("unbreakable") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Boolean.class)
                .getter(ItemMeta::isUnbreakable)
                .setter((meta, value) -> meta.setUnbreakable(value != null && value))
                .valueReader(data -> ValueProviders.bool(data, "value", false, "unbreakable/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.add())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    CUSTOM_MODEL_DATA("custom_model_data") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Integer.class)
                .getter(meta -> meta.hasCustomModelData() ? meta.getCustomModelData() : null)
                .setter((meta, value) -> meta.setCustomModelData(value))
                .valueReader(data -> ValueProviders.integer(data, "value", 0, "custom_model_data/value"))
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.ADD, ScalarBehaviors.add())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove());
        }
    },

    /**
     * 结果物品自身的耐久损耗
     */
    DAMAGE("damage") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Integer.class)
                .getter(meta -> meta instanceof Damageable damageable && damageable.hasDamage() ? damageable.getDamage() : null)
                .setter((meta, value) -> {
                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(value == null ? 0 : value);
                    }
                })
                .valueReader(data -> ValueProviders.integer(data, "value", "damage", 0, "damage/value"))
                .accepts(meta -> meta instanceof Damageable)
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove())
                .behavior(Behaviors.ADD, (context, field, data) -> {
                    ValueProvider<Integer> value = field.valueReader(data);
                    boolean allowExceed = data != null && data.getBoolean("allow_exceed_max", false);
                    return field.processor(context.qualifiedName(), (sourceItem, resultItem, player) -> {
                        if (value == null) {
                            return null;
                        }
                        Integer resolved = value.resolve(player, sourceItem, resultItem);
                        if (resolved == null) {
                            return null;
                        }
                        int damage = Math.max(0, resolved);
                        if (allowExceed) {
                            return damage;
                        }
                        int max = effectiveMaxDamage(resultItem);
                        if (max > 0 && damage > max) {
                            ProcessorUtils.warnOnce(
                                "damage/value:max",
                                "damage: " + damage + " exceeds max damage " + max + ", clamped (set allow_exceed_max to bypass)"
                            );
                            damage = max;
                        }
                        return damage;
                    });
                });
        }
    },

    /**
     * 皮革染色，非皮革物品会被跳过
     */
    BASE_COLOR("base_color") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(Color.class)
                .getter(meta -> meta instanceof LeatherArmorMeta leatherMeta ? leatherMeta.getColor() : null)
                .setter((meta, value) -> {
                    if (meta instanceof LeatherArmorMeta leatherMeta) {
                        leatherMeta.setColor(value == null ? Bukkit.getItemFactory().getDefaultLeatherColor() : value);
                    }
                })
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove())
                .behavior(Behaviors.ADD, (context, field, data) -> {
                    Color color = ProcessorUtils.parseColor(data == null ? null : data.getString("value"));
                    if (color == null) {
                        CrypticLib.warn("base_color: invalid 'value', expected '#RRGGBB' or 'r,g,b'");
                        return null;
                    }
                    return field.processor(
                        context.qualifiedName(),
                        (sourceItem, resultItem, player) ->
                            resultItem.meta() instanceof LeatherArmorMeta ? color : null
                    );
                });
        }
    },

    /**
     * 头颅皮肤。copy_from_source 直接沿用源物品的皮肤，是最可靠的用法；
     * add 只写入名字/UUID，皮肤纹理需服务端自行解析，不保证即时生效。
     */
    SKULL_PROFILE("skull_profile") {
        @Override
        public void declareBehaviors(ComponentRegistrar registrar) {
            registrar.valueType(PlayerProfile.class)
                .getter(meta -> meta instanceof SkullMeta skullMeta ? skullMeta.getOwnerProfile() : null)
                .setter((meta, value) -> {
                    if (!(meta instanceof SkullMeta skullMeta)) {
                        return;
                    }
                    try {
                        skullMeta.setOwnerProfile(value);
                    } catch (IllegalArgumentException exception) {
                        //只写 uuid 时资料可能既没有名字也没有纹理, 服务端会拒绝这种资料
                        ProcessorUtils.warnOnce(
                            "skull_profile:invalid_profile",
                            "skull_profile: invalid profile (" + exception.getMessage() + "), add 'name' alongside 'uuid'"
                        );
                    }
                })
                .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource())
                .behavior(Behaviors.REMOVE, ScalarBehaviors.remove())
                .behavior(Behaviors.ADD, (context, field, data) -> {
                    ValueProvider<String> name = ValueProviders.text(data, "name", "", "skull_profile/name");
                    ValueProvider<String> uuid = ValueProviders.text(data, "uuid", "", "skull_profile/uuid");
                    return field.processor(context.qualifiedName(), (sourceItem, resultItem, player) -> {
                        if (!(resultItem.meta() instanceof SkullMeta)) {
                            return null;
                        }
                        String resolvedName = name.resolve(player, sourceItem, resultItem);
                        String resolvedUuid = uuid.resolve(player, sourceItem, resultItem);
                        if (resolvedName == null || resolvedUuid == null) {
                            return null;
                        }
                        return createPlayerProfile(resolvedName, resolvedUuid);
                    });
                });
        }
    };

    private final String componentName;

    BaseComponentProcessors(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifecyclePhase) {
        for (BaseComponentProcessors factory : values()) {
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

    /**
     * 根据解析后的 name / uuid 创建头颅资料, 至少要有其一
     */
    @SuppressWarnings("deprecation")
    private static @Nullable PlayerProfile createPlayerProfile(String nameRaw, String uuidRaw) {
        String name = nameRaw.trim();
        if (name.isEmpty()) {
            name = null;
        }
        String uuidText = uuidRaw.trim();
        UUID uuid = ProcessorUtils.parseUuid(uuidText);
        if (uuid == null && !uuidText.isEmpty()) {
            ProcessorUtils.warnOnce("skull_profile:uuid", "skull_profile: invalid 'uuid' '" + uuidText + "'");
            return null;
        }
        if (name == null && uuid == null) {
            ProcessorUtils.warnOnce("skull_profile:missing", "skull_profile: 'add' requires 'name' or 'uuid'");
            return null;
        }
        try {
            if (name != null && uuid != null) {
                return Bukkit.getServer().createPlayerProfile(uuid, name);
            }
            if (uuid != null) {
                return Bukkit.getServer().createPlayerProfile(uuid);
            }
            return Bukkit.getServer().createPlayerProfile(name);
        } catch (IllegalArgumentException exception) {
            ProcessorUtils.warnOnce(
                "skull_profile:create",
                "skull_profile: cannot create profile: " + exception.getMessage()
            );
            return null;
        }
    }

    /**
     * 计算结果物品当前的有效耐久上限，用于校验 damage 不越界
     */
    private static int effectiveMaxDamage(ItemWrapper resultItem) {
        ItemMeta resultMeta = resultItem.meta();
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_5)
            && resultMeta instanceof Damageable damageable
            && damageable.hasMaxDamage()) {
            return damageable.getMaxDamage();
        }
        short durability = resultItem.type().getMaxDurability();
        return Math.max(durability, 0);
    }

}
