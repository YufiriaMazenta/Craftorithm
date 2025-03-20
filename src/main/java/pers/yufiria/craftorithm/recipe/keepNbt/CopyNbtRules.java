package pers.yufiria.craftorithm.recipe.keepNbt;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CopyNbtRules {

    private final List<CopyNbtRule> copyNbtRules;

    public CopyNbtRules(List<CopyNbtRule> copyNbtRules) {
        this.copyNbtRules = copyNbtRules;
    }

    public ItemMeta processItemMeta(ItemMeta baseMeta, ItemMeta resultMeta) {
        for (CopyNbtRule copyNbtRule : copyNbtRules) {
            resultMeta = copyNbtRule.processItemMeta(baseMeta, resultMeta);
        }
        return resultMeta;
    }

}
