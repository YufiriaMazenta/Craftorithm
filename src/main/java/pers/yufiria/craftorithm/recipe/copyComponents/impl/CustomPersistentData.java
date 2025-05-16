package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

/**
 * 用于自定义从PersistentDataContainer里复制内容的规则
 */
public class CustomPersistentData implements CopyComponentsRule {

    private String ruleName;

    @Override
    public String ruleName() {
        return ruleName;
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        //TODO 实现相关解析和处理
        return null;
    }

}
