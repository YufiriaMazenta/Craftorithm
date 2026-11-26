package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * 月相
 * <p>
 * 由累计时间推算，取值 0-7，0 为满月。
 */
public record MoonPhaseCondition(IntRanges phases) implements WorldCondition {

    private static final int TICKS_PER_DAY = 24000;
    private static final int PHASE_COUNT = 8;

    @Override
    public boolean test(World world) {
        int days = (int) Math.floorDiv(world.getFullTime(), TICKS_PER_DAY);
        return phases.contains(Math.floorMod(days, PHASE_COUNT));
    }

    public static WorldCondition parse(ConfigurationSection section, String key, NamespacedKey recipeKey) {
        return new MoonPhaseCondition(IntRanges.parse(section, key));
    }

}
