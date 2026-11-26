package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorldConditionTest {

    @Test
    void timeConditionComparesTimeOfDay() {
        TimeCondition condition = new TimeCondition(new IntRanges(List.of(new IntRange(13000, 23000))));

        assertTrue(condition.test(worldAt(13000, 0)));
        assertTrue(condition.test(worldAt(23000, 0)));
        assertFalse(condition.test(worldAt(12999, 0)));
        assertFalse(condition.test(worldAt(23001, 0)));
    }

    @Test
    void timeConditionIgnoresElapsedDays() {
        TimeCondition condition = new TimeCondition(new IntRanges(List.of(new IntRange(0, 1000))));

        assertTrue(condition.test(worldAt(500, 24000L * 1234)));
    }

    @Test
    void moonPhaseComesFromElapsedDays() {
        MoonPhaseCondition condition = new MoonPhaseCondition(new IntRanges(List.of(new IntRange(0, 0))));

        assertTrue(condition.test(worldAt(0, 0)));
        assertTrue(condition.test(worldAt(0, 24000L * 8)));
        assertFalse(condition.test(worldAt(0, 24000L * 1)));
    }

    @Test
    void difficultyConditionMatchesAnyConfiguredDifficulty() {
        DifficultyCondition condition = new DifficultyCondition(EnumSet.of(Difficulty.HARD, Difficulty.NORMAL));
        World world = mock(World.class);

        when(world.getDifficulty()).thenReturn(Difficulty.HARD);
        assertTrue(condition.test(world));

        when(world.getDifficulty()).thenReturn(Difficulty.EASY);
        assertFalse(condition.test(world));
    }

    @Test
    void weatherConditionTreatsThunderAsThunderOnly() {
        WeatherCondition rainOnly = new WeatherCondition(EnumSet.of(WeatherCondition.Weather.RAIN));
        WeatherCondition thunderOnly = new WeatherCondition(EnumSet.of(WeatherCondition.Weather.THUNDER));
        WeatherCondition clearOnly = new WeatherCondition(EnumSet.of(WeatherCondition.Weather.CLEAR));

        World storming = mock(World.class);
        when(storming.hasStorm()).thenReturn(true);

        World thundering = mock(World.class);
        when(thundering.hasStorm()).thenReturn(true);
        when(thundering.isThundering()).thenReturn(true);

        World clear = mock(World.class);

        assertTrue(rainOnly.test(storming));
        assertFalse(rainOnly.test(thundering));
        assertTrue(thunderOnly.test(thundering));
        assertTrue(clearOnly.test(clear));
        assertFalse(clearOnly.test(storming));
    }

    @Test
    void environmentConditionMatchesConfiguredDimensions() {
        EnvironmentCondition condition = new EnvironmentCondition(
            Set.of(World.Environment.NETHER, World.Environment.THE_END)
        );
        World world = mock(World.class);

        when(world.getEnvironment()).thenReturn(World.Environment.NETHER);
        assertTrue(condition.test(world));

        when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        assertFalse(condition.test(world));
    }

    @Test
    void nameConditionMatchesExactWorldName() {
        WorldNameCondition condition = new WorldNameCondition(Set.of("world"));

        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        assertTrue(condition.test(world));

        when(world.getName()).thenReturn("world_nether");
        assertFalse(condition.test(world));
    }

    @Test
    void ruleRequiresEveryCondition() {
        World world = worldAt(13000, 0);
        when(world.getDifficulty()).thenReturn(Difficulty.EASY);

        WorldRule matched = new WorldRule.Conditions(List.of(
            new TimeCondition(new IntRanges(List.of(new IntRange(13000, 23000)))),
            new DifficultyCondition(EnumSet.of(Difficulty.EASY))
        ));
        WorldRule unmatched = new WorldRule.Conditions(List.of(
            new TimeCondition(new IntRanges(List.of(new IntRange(13000, 23000)))),
            new DifficultyCondition(EnumSet.of(Difficulty.HARD))
        ));

        assertTrue(matched.test(world));
        assertFalse(unmatched.test(world));
        assertTrue(WorldRule.AllowAll.INSTANCE.test(world));
        assertFalse(WorldRule.DenyAll.INSTANCE.test(world));
    }

    private static World worldAt(long time, long fullTime) {
        World world = mock(World.class);
        when(world.getTime()).thenReturn(time);
        when(world.getFullTime()).thenReturn(fullTime);
        return world;
    }

}
