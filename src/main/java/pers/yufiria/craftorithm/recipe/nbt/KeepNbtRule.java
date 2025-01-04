package pers.yufiria.craftorithm.recipe.nbt;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * 物品NBT的处理方式
 * 用于铁砧和锻造配方
 */
public interface KeepNbtRule {

    String ruleName();

    /**
     * 对材料和结果的ItemMeta进行处理,并返回处理完的结果
     * 应采用返回的结果进行下一步操作,否则将会导致一些问题
     * @param baseMeta 原材料的ItemMeta
     * @param resultMeta 结果的ItemMeta
     * @return 处理完后的ItemMeta
     */
    @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta);

}
