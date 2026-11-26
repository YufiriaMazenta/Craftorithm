package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.World;

import java.util.List;

/**
 * 配方的世界规则
 * <p>
 * 在配方注册时构造一次，之后只读，运行期由 {@link WorldConditionHandler#canRecipeUse} 直接调用。
 * 判定发生在配方匹配的热路径上，因此实现里不做任何缓存与对象分配。
 */
public sealed interface WorldRule {

    boolean test(World world);

    /**
     * 没有配置世界条件，任何世界都可用
     */
    enum AllowAll implements WorldRule {

        INSTANCE;

        @Override
        public boolean test(World world) {
            return true;
        }

    }

    /**
     * 配置有误，配方在任何世界都不可用
     */
    enum DenyAll implements WorldRule {

        INSTANCE;

        @Override
        public boolean test(World world) {
            return false;
        }

    }

    /**
     * 所有条件同时满足才可用
     */
    record Conditions(List<WorldCondition> conditions) implements WorldRule {

        public Conditions {
            conditions = List.copyOf(conditions);
        }

        @Override
        public boolean test(World world) {
            for (WorldCondition condition : conditions) {
                if (!condition.test(world)) {
                    return false;
                }
            }
            return true;
        }

    }

}
