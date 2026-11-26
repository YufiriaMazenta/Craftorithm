package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.World;

/**
 * 世界条件
 * <p>
 * 一个条件判断世界的一个属性，多个条件由 {@link pers.yufiria.craftorithm.recipe.worldcondition.WorldRule.Conditions}
 * 串联为「同时满足」。判定发生在配方匹配的热路径上，实现不得有副作用，也不应产生对象分配。
 */
public interface WorldCondition {

    boolean test(World world);

}
