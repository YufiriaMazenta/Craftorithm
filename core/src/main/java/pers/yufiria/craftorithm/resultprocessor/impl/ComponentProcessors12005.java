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
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;

import java.util.*;
import java.util.function.Function;

/**
 * 1.20.5 起可用的组件，低版本不注册。
 * <p>
 * 组件工厂在 LOAD 阶段自行注册到 {@link ResultProcessorManager}。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum ComponentProcessors12005 implements ComponentProcessorFactory, LifecycleTask {

    FOOD("food", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("food", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasFood()) {
                resultMeta.setFood(sourceMeta.getFood());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> ProcessorUtils.processor("food", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setFood(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    HIDE_TOOLTIP("hide_tooltip", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("hide_tooltip", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setHideTooltip(sourceMeta.isHideTooltip());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value");
            return ProcessorUtils.processor("hide_tooltip", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setHideTooltip(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("hide_tooltip", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setHideTooltip(false);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    ITEM_NAME("item_name", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("item_name", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasItemName()) {
                resultMeta.setItemName(sourceMeta.getItemName());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            String value = data.getString("value");
            if (value == null) {
                CrypticLib.warn("item_name: missing 'value'");
                return ProcessorUtils.noop("item_name");
            }
            return ProcessorUtils.processor("item_name", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                String parsed = player != null ? BukkitTextProcessor.placeholder(player, value) : value;
                resultMeta.setItemName(BukkitTextProcessor.color(parsed));
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("item_name", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setItemName(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    MAX_STACK_SIZE("max_stack_size", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("max_stack_size", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasMaxStackSize()) {
                resultMeta.setMaxStackSize(sourceMeta.getMaxStackSize());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            int value = data.getInt("value");
            return ProcessorUtils.processor("max_stack_size", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setMaxStackSize(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("max_stack_size", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setMaxStackSize(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    RARITY("rarity", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("rarity", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasRarity()) {
                resultMeta.setRarity(sourceMeta.getRarity());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            ItemRarity rarity = ProcessorUtils.parseEnum(ItemRarity.class, data.getString("value"));
            if (rarity == null) {
                CrypticLib.warn("rarity: invalid 'value', expected COMMON/UNCOMMON/RARE/EPIC");
                return ProcessorUtils.noop("rarity");
            }
            return ProcessorUtils.processor("rarity", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setRarity(rarity);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("rarity", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setRarity(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    /**
     * 防火。沿用 Bukkit 的 setFireResistant（已废弃，但 1.20.5 起到 26.x 都能用），
     * 更通用的抗伤害写法见 damage_resistant。
     */
    FIRE_RESISTANCE("fire_resistance", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("fire_resistance", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setFireResistant(sourceMeta.isFireResistant());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value", true);
            return ProcessorUtils.processor("fire_resistance", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setFireResistant(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("fire_resistance", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setFireResistant(false);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    /**
     * 最大耐久上限，独立于 damage 组件。
     */
    MAX_DAMAGE("max_damage", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("max_damage", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (!(sourceMeta instanceof Damageable sourceDamageable)
                || !(resultMeta instanceof Damageable resultDamageable)) {
                return;
            }
            if (!sourceDamageable.hasMaxDamage()) {
                return;
            }
            resultDamageable.setMaxDamage(sourceDamageable.getMaxDamage());
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            int value = Math.max(1, data.getInt("value", 1));
            return ProcessorUtils.processor("max_damage", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                if (!(resultMeta instanceof Damageable damageable)) {
                    CrypticLib.warn("max_damage: " + resultItem.getType() + " has no durability, skipped");
                    return;
                }
                damageable.setMaxDamage(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("max_damage", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (resultMeta instanceof Damageable damageable) {
                damageable.setMaxDamage(null);
                resultItem.setItemMeta(resultMeta);
            }
        })
    )),

    ENCHANTMENT_GLINT_OVERRIDE("enchantment_glint_override", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("enchantment_glint_override", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasEnchantmentGlintOverride()) {
                resultMeta.setEnchantmentGlintOverride(sourceMeta.getEnchantmentGlintOverride());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "add", data -> {
            boolean value = data.getBoolean("value");
            return ProcessorUtils.processor("enchantment_glint_override", (sourceItem, resultItem, player) -> {
                ItemMeta resultMeta = resultItem.getItemMeta();
                resultMeta.setEnchantmentGlintOverride(value);
                resultItem.setItemMeta(resultMeta);
            });
        },
        "remove", data -> ProcessorUtils.processor("enchantment_glint_override", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setEnchantmentGlintOverride(null);
            resultItem.setItemMeta(resultMeta);
        })
    )),

    /**
     * 药水的基础类型
     */
    POTION("potion", Map.of(
        "copy_from_source", data -> ProcessorUtils.processorRequireSource("potion", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta instanceof PotionMeta sourcePotion
                && resultMeta instanceof PotionMeta resultPotion
                && sourcePotion.hasBasePotionType()) {
                resultPotion.setBasePotionType(sourcePotion.getBasePotionType());
            }
            resultItem.setItemMeta(resultMeta);
        }),
        "remove", data -> ProcessorUtils.processor("potion", (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (resultMeta instanceof PotionMeta potionMeta) {
                potionMeta.setBasePotionType(null);
                resultItem.setItemMeta(resultMeta);
            }
        })
    ));

    private final String componentName;
    private final Map<String, Function<ConfigurationSection, ResultProcessor>> types;

    ComponentProcessors12005(String componentName, Map<String, Function<ConfigurationSection, ResultProcessor>> types) {
        this.componentName = componentName;
        this.types = new HashMap<>(types);
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

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = types.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(componentName + " does not support type: " + type);
        }
        return handler.apply(data == null ? ProcessorUtils.EMPTY_DATA : data);
    }

}
