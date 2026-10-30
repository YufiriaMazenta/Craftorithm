package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;

import java.util.*;
import java.util.function.Function;

/**
 * 所有 1.20 起就存在的组件。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum BaseComponentProcessors implements ComponentProcessorFactory, LifecycleTask {

    ALL("all", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("all", (sourceItem, resultItem, player) -> {
            resultItem.setItemMeta(sourceItem.getItemMeta());
        })
    )),

    DISPLAY_NAME("display_name", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("display_name", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasDisplayName()) {
                resultMeta.setDisplayName(sourceMeta.getDisplayName());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            String value = data.getString("value");
            if (value == null) {
                CrypticLib.warn("display_name: missing 'value'");
                return ProcessorUtils.noop("display_name");
            }
            return ProcessorUtils.processor("display_name", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                String parsed = player != null ? BukkitTextProcessor.placeholder(player, value) : value;
                resultMeta.setDisplayName(BukkitTextProcessor.color(parsed));
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("display_name", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setDisplayName(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    TRIM("trim", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("trim", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta instanceof ArmorMeta sourceArmorMeta
                && resultMeta instanceof ArmorMeta resultArmorMeta
                && sourceArmorMeta.hasTrim()) {
                resultArmorMeta.setTrim(sourceArmorMeta.getTrim());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            TrimPattern pattern = ProcessorUtils.lookup(TrimPattern.class, data.getString("pattern"));
            TrimMaterial material = ProcessorUtils.lookup(TrimMaterial.class, data.getString("material"));
            if (pattern == null || material == null) {
                CrypticLib.warn("trim: invalid 'pattern' or 'material'");
                return ProcessorUtils.noop("trim");
            }
            ArmorTrim trim = buildTrim(material, pattern);
            return ProcessorUtils.processor("trim", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                if (resultMeta instanceof ArmorMeta armorMeta) {
                    armorMeta.setTrim(trim);
                }
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("trim", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (resultMeta instanceof ArmorMeta armorMeta) {
                armorMeta.setTrim(null);
            }
            resultItem.setItemMeta(resultMeta);
        })
    )),

    UNBREAKABLE("unbreakable", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("unbreakable", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setUnbreakable(sourceMeta.isUnbreakable());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value");
            return ProcessorUtils.processor("unbreakable", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setUnbreakable(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("unbreakable", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setUnbreakable(false);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    CUSTOM_MODEL_DATA("custom_model_data", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("custom_model_data", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasCustomModelData()) {
                resultMeta.setCustomModelData(sourceMeta.getCustomModelData());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            int value = data.getInt("value");
            return ProcessorUtils.processor("custom_model_data", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setCustomModelData(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("custom_model_data", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setCustomModelData(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    /**
     * 结果物品自身的耐久损耗
     */
    DAMAGE("damage", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("damage", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (!(sourceMeta instanceof Damageable sourceDamageable)
                || !(resultMeta instanceof Damageable resultDamageable)) {
                return;
            }
            if (!sourceDamageable.hasDamage()) {
                return;
            }
            resultDamageable.setDamage(sourceDamageable.getDamage());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            int configured = Math.max(0, data.getInt("value", data.getInt("damage", 0)));
            boolean allowExceed = data.getBoolean("allow_exceed_max", false);
            return ProcessorUtils.processor("damage", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                if (!(resultMeta instanceof Damageable damageable)) {
                    CrypticLib.warn("damage: " + resultItem.getType() + " has no durability, skipped");
                    return;
                }
                int damage = configured;
                if (!allowExceed) {
                    int max = effectiveMaxDamage(damageable, resultItem.getType());
                    if (max > 0 && damage > max) {
                        CrypticLib.warn("damage: " + damage + " exceeds max damage " + max + ", clamped (set allow_exceed_max to bypass)");
                        damage = max;
                    }
                }
                damageable.setDamage(damage);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("damage", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (resultMeta instanceof Damageable damageable) {
                damageable.setDamage(0);
                resultItem.setItemMeta(resultMeta);
            }
        })
    )),

    /**
     * 皮革染色，非皮革物品会被跳过。
     */
    BASE_COLOR("base_color", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("base_color", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta instanceof LeatherArmorMeta sourceLeather
                && resultMeta instanceof LeatherArmorMeta resultLeather
                && sourceLeather.getColor() != null
            ) {
                resultLeather.setColor(sourceLeather.getColor());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            Color color = ProcessorUtils.parseColor(data.getString("value"));
            if (color == null) {
                CrypticLib.warn("base_color: invalid 'value', expected '#RRGGBB' or 'r,g,b'");
                return ProcessorUtils.noop("base_color");
            }
            return ProcessorUtils.processor("base_color", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                if (resultMeta instanceof LeatherArmorMeta leatherMeta) {
                    leatherMeta.setColor(color);
                    resultItem.setItemMeta(resultMeta);
                }
            });
        },
        "remove", data -> ProcessorUtils.processor("base_color", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (resultMeta instanceof LeatherArmorMeta leatherMeta) {
                leatherMeta.setColor(Bukkit.getItemFactory().getDefaultLeatherColor());
                resultItem.setItemMeta(resultMeta);
            }
        })
    )),

    /**
     * 头颅皮肤。copy_from_source 直接沿用源物品的皮肤，是最可靠的用法；
     * add 只写入名字/UUID，皮肤纹理需服务端自行解析，不保证即时生效。
     */
    SKULL_PROFILE("skull_profile", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("skull_profile", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta instanceof SkullMeta sourceSkull
                && resultMeta instanceof SkullMeta resultSkull
                && sourceSkull.getOwnerProfile() != null) {
                resultSkull.setOwnerProfile(sourceSkull.getOwnerProfile());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> ProcessorUtils.processor("skull_profile", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (resultMeta instanceof SkullMeta skullMeta) {
                skullMeta.setOwnerProfile(null);
                resultItem.setItemMeta(resultMeta);
            }
        })
    ));

    private final String componentName;
    private final Map<String, Function<ConfigurationSection, ResultProcessor>> types;

    BaseComponentProcessors(String componentName, Map<String, Function<ConfigurationSection, ResultProcessor>> types) {
        this.componentName = componentName;
        this.types = new HashMap<>(types);
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

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = types.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(componentName + " does not support type: " + type);
        }
        return handler.apply(data == null ? ProcessorUtils.EMPTY_DATA : data);
    }

    /**
     * 计算结果物品当前的有效耐久上限，用于校验 damage 不越界
     */
    private static int effectiveMaxDamage(Damageable damageable, Material material) {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_5) && damageable.hasMaxDamage()) {
            return damageable.getMaxDamage();
        }
        short durability = material.getMaxDurability();
        return Math.max(durability, 0);
    }

    private static ArmorTrim buildTrim(TrimMaterial material, TrimPattern pattern) {
        return new ArmorTrim(material, pattern);
    }

}
