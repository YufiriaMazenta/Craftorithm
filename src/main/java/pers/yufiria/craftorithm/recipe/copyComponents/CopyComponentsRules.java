package pers.yufiria.craftorithm.recipe.copyComponents;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CopyComponentsRules {

    private final List<CopyComponentsRule> copyComponentsRules;

    public CopyComponentsRules(List<CopyComponentsRule> copyComponentsRules) {
        this.copyComponentsRules = copyComponentsRules;
    }

    public ItemMeta processItemMeta(ItemMeta baseMeta, ItemMeta resultMeta) {
        for (CopyComponentsRule copyComponentsRule : copyComponentsRules) {
            resultMeta = copyComponentsRule.processItemMeta(baseMeta, resultMeta);
        }
        return resultMeta;
    }

}
