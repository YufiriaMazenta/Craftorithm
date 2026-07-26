package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import crypticlib.MinecraftVersion;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

import java.util.function.BinaryOperator;

/**
 * 无参数的组件保留规则表
 * 每个条目由规则名, 生效的最低版本与处理逻辑组成
 * 需要构造参数的规则(如custom_persistent_data)单独实现
 */
public enum SimpleCopyComponentsRules implements CopyComponentsRule {

    ALL("all", null, (baseMeta, resultMeta) -> baseMeta),
    ATTRIBUTES("attributes", null, (baseMeta, resultMeta) -> {
        if (baseMeta.hasAttributeModifiers()) {
            resultMeta.setAttributeModifiers(baseMeta.getAttributeModifiers());
        }
        return resultMeta;
    }),
    CUSTOM_MODEL_DATA("custom_model_data", null, (baseMeta, resultMeta) -> {
        if (baseMeta.hasCustomModelData()) {
            resultMeta.setCustomModelData(baseMeta.getCustomModelData());
        }
        return resultMeta;
    }),
    DISPLAY_NAME("display_name", null, (baseMeta, resultMeta) -> {
        if (baseMeta.hasDisplayName()) {
            resultMeta.setDisplayName(baseMeta.getDisplayName());
        }
        return resultMeta;
    }),
    ENCHANTMENTS("enchantments", null, (baseMeta, resultMeta) -> {
        if (baseMeta.hasEnchants()) {
            baseMeta.getEnchants().forEach((enchant, level) -> {
                if (resultMeta.hasEnchant(enchant)) {
                    if (resultMeta.getEnchantLevel(enchant) > level) {
                        return;
                    }
                    resultMeta.removeEnchant(enchant);
                }
                resultMeta.addEnchant(enchant, level, true);
            });
        }
        return resultMeta;
    }),
    ITEM_FLAG("item_flag", null, (baseMeta, resultMeta) -> {
        resultMeta.removeItemFlags(org.bukkit.inventory.ItemFlag.values());
        baseMeta.getItemFlags().forEach(resultMeta::addItemFlags);
        return resultMeta;
    }),
    LORE("lore", null, (baseMeta, resultMeta) -> {
        if (baseMeta.hasLore()) {
            resultMeta.setLore(baseMeta.getLore());
        }
        return resultMeta;
    }),
    TRIM("trim", null, (baseMeta, resultMeta) -> {
        if (baseMeta instanceof ArmorMeta baseArmorMeta
            && resultMeta instanceof ArmorMeta resultArmorMeta
            && baseArmorMeta.hasTrim()) {
            resultArmorMeta.setTrim(baseArmorMeta.getTrim());
        }
        return resultMeta;
    }),
    UNBREAKABLE("unbreakable", null, (baseMeta, resultMeta) -> {
        resultMeta.setUnbreakable(baseMeta.isUnbreakable());
        return resultMeta;
    }),
    FIRE_RESISTANCE("fire_resistance", MinecraftVersion.V1_20_5, (baseMeta, resultMeta) -> {
        resultMeta.setFireResistant(baseMeta.isFireResistant());
        return resultMeta;
    }),
    FOOD("food", MinecraftVersion.V1_20_5, (baseMeta, resultMeta) -> {
        if (baseMeta.hasFood()) {
            resultMeta.setFood(baseMeta.getFood());
        }
        return resultMeta;
    }),
    HIDE_TOOLTIP("hide_tooltip", MinecraftVersion.V1_20_5, (baseMeta, resultMeta) -> {
        resultMeta.setHideTooltip(baseMeta.isHideTooltip());
        return resultMeta;
    }),
    ITEM_NAME("item_name", MinecraftVersion.V1_20_5, (baseMeta, resultMeta) -> {
        if (baseMeta.hasItemName()) {
            resultMeta.setItemName(baseMeta.getItemName());
        }
        return resultMeta;
    }),
    MAX_STACK_SIZE("max_stack_size", MinecraftVersion.V1_20_5, (baseMeta, resultMeta) -> {
        if (baseMeta.hasMaxStackSize()) {
            resultMeta.setMaxStackSize(baseMeta.getMaxStackSize());
        }
        return resultMeta;
    }),
    RARITY("rarity", MinecraftVersion.V1_20_5, (baseMeta, resultMeta) -> {
        if (baseMeta.hasRarity()) {
            resultMeta.setRarity(baseMeta.getRarity());
        }
        return resultMeta;
    }),
    TOOL("tool", MinecraftVersion.V1_21, (baseMeta, resultMeta) -> {
        if (baseMeta.hasTool()) {
            resultMeta.setTool(baseMeta.getTool());
        }
        return resultMeta;
    }),
    CUSTOM_MODEL_DATA_COMPONENT("custom_model_data_component", MinecraftVersion.V1_21_4, (baseMeta, resultMeta) -> {
        resultMeta.setCustomModelDataComponent(baseMeta.getCustomModelDataComponent());
        return resultMeta;
    }),
    ITEM_MODEL("item_model", MinecraftVersion.V1_21_4, (baseMeta, resultMeta) -> {
        if (baseMeta.hasItemModel()) {
            resultMeta.setItemModel(baseMeta.getItemModel());
        }
        return resultMeta;
    }),
    ;

    private final String ruleName;
    private final MinecraftVersion minVersion;
    private final BinaryOperator<ItemMeta> processor;

    SimpleCopyComponentsRules(String ruleName, @Nullable MinecraftVersion minVersion, BinaryOperator<ItemMeta> processor) {
        this.ruleName = ruleName;
        this.minVersion = minVersion;
        this.processor = processor;
    }

    /**
     * 当前服务端版本是否支持此规则
     */
    public boolean supportedByCurrentVersion() {
        return minVersion == null || MinecraftVersion.CURRENT.afterOrEquals(minVersion);
    }

    @Override
    public String ruleName() {
        return ruleName;
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        return processor.apply(baseMeta, resultMeta);
    }

}
