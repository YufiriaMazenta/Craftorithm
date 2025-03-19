package pers.yufiria.craftorithm.recipe.keepNbt;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KeepNbtRules {

    private final List<KeepNbtRule> keepNbtRules;

    public KeepNbtRules(List<KeepNbtRule> keepNbtRules) {
        this.keepNbtRules = keepNbtRules;
    }

    public ItemMeta processItemMeta(ItemMeta baseMeta, ItemMeta resultMeta) {
        for (KeepNbtRule keepNbtRule : keepNbtRules) {
            resultMeta = keepNbtRule.processItemMeta(baseMeta, resultMeta);
        }
        return resultMeta;
    }

}
