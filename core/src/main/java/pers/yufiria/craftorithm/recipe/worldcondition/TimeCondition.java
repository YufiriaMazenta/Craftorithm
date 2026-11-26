package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * 游戏内时间
 * <p>
 * 取世界的当日时间（0-23999），不含累计天数，因此每天都会在同一时间段重新命中。
 */
public record TimeCondition(IntRanges times) implements WorldCondition {

    private static final int TICKS_PER_DAY = 24000;

    @Override
    public boolean test(World world) {
        return times.contains((int) Math.floorMod(world.getTime(), TICKS_PER_DAY));
    }

    public static WorldCondition parse(ConfigurationSection section, String key, NamespacedKey recipeKey) {
        return new TimeCondition(IntRanges.parse(section, key));
    }

}
