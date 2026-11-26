package pers.yufiria.craftorithm.recipe.worldcondition;

import crypticlib.CrypticLib;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class WorldConditionParserTest {

    private static final NamespacedKey RECIPE_KEY = new NamespacedKey("test", "recipe");

    private MockedStatic<CrypticLib> crypticLib;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    void setUp() {
        crypticLib = mockStatic(CrypticLib.class);
        bukkit = mockStatic(Bukkit.class);
    }

    @AfterEach
    void tearDown() {
        crypticLib.close();
        bukkit.close();
    }

    @Test
    void recipeWithoutWorldConditionsIsUnrestricted() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("type: vanilla_shaped"), RECIPE_KEY);
        assertSame(WorldRule.AllowAll.INSTANCE, rule);
    }

    @Test
    void emptyWorldConditionsIsUnrestricted() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("world_conditions: {}"), RECIPE_KEY);
        assertSame(WorldRule.AllowAll.INSTANCE, rule);
    }

    @Test
    void worldConditionsWithoutValueIsUnrestricted() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("world_conditions:"), RECIPE_KEY);
        assertSame(WorldRule.AllowAll.INSTANCE, rule);
    }

    @Test
    void worldConditionsMustBeASection() {
        assertThrows(
            RecipeLoadException.class,
            () -> WorldConditionParser.parse(yaml("world_conditions: world"), RECIPE_KEY)
        );
    }

    @Test
    void unknownConditionIsRejected() {
        assertThrows(
            RecipeLoadException.class,
            () -> WorldConditionParser.parse(yaml("""
                world_conditions:
                  weather_forecast: rain
                """), RECIPE_KEY)
        );
    }

    @Test
    void removedEnableWorldsOptionIsRejected() {
        assertThrows(
            RecipeLoadException.class,
            () -> WorldConditionParser.parse(yaml("""
                enable_worlds: [world]
                """), RECIPE_KEY)
        );
    }

    @Test
    void timeAcceptsSingleValue() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("""
            world_conditions:
              time: 13000
            """), RECIPE_KEY);

        assertTrue(rule.test(worldAtTime(13000)));
        assertFalse(rule.test(worldAtTime(12999)));
    }

    @Test
    void timeAcceptsMinAndMax() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("""
            world_conditions:
              time:
                min: 13000
                max: 23000
            """), RECIPE_KEY);

        assertTrue(rule.test(worldAtTime(13000)));
        assertTrue(rule.test(worldAtTime(23000)));
        assertFalse(rule.test(worldAtTime(12999)));
        assertFalse(rule.test(worldAtTime(23001)));
    }

    @Test
    void timeAcceptsSingleSidedRange() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("""
            world_conditions:
              time:
                min: 13000
            """), RECIPE_KEY);

        assertTrue(rule.test(worldAtTime(23999)));
        assertFalse(rule.test(worldAtTime(12999)));
    }

    @Test
    void timeAcceptsListOfValuesAndRanges() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("""
            world_conditions:
              time:
                - 0
                - min: 20000
                  max: 23000
            """), RECIPE_KEY);

        assertTrue(rule.test(worldAtTime(0)));
        assertTrue(rule.test(worldAtTime(21000)));
        assertFalse(rule.test(worldAtTime(10000)));
    }

    @Test
    void rangeWithMinGreaterThanMaxIsRejected() {
        assertThrows(
            RecipeLoadException.class,
            () -> WorldConditionParser.parse(yaml("""
                world_conditions:
                  time:
                    min: 23000
                    max: 13000
                """), RECIPE_KEY)
        );
    }

    @Test
    void rangeWithUnknownFieldIsRejected() {
        assertThrows(
            RecipeLoadException.class,
            () -> WorldConditionParser.parse(yaml("""
                world_conditions:
                  time:
                    from: 13000
                    to: 23000
                """), RECIPE_KEY)
        );
    }

    @Test
    void conditionsAreCombinedWithAnd() throws Exception {
        World world = worldAtTime(1000);
        when(world.getDifficulty()).thenReturn(org.bukkit.Difficulty.HARD);
        WorldRule rule = WorldConditionParser.parse(yaml("""
            world_conditions:
              time: 1000
              difficulty: hard
            """), RECIPE_KEY);

        assertTrue(rule.test(world));
        when(world.getDifficulty()).thenReturn(org.bukkit.Difficulty.EASY);
        assertFalse(rule.test(world));
    }

    @Test
    void sizeOfConditionListMatchesConfiguredKeys() throws Exception {
        WorldRule rule = WorldConditionParser.parse(yaml("""
            world_conditions:
              time: 1000
              difficulty: hard
              weather: rain
            """), RECIPE_KEY);

        assertTrue(rule instanceof WorldRule.Conditions);
        assertEquals(3, ((WorldRule.Conditions) rule).conditions().size());
    }

    @Test
    void everyBuiltInConditionCanBeConfiguredTogether() throws Exception {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        when(world.getTime()).thenReturn(1000L);
        when(world.getFullTime()).thenReturn(0L);
        when(world.getDifficulty()).thenReturn(org.bukkit.Difficulty.NORMAL);
        when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        bukkit.when(() -> Bukkit.getWorld("world")).thenReturn(world);

        WorldRule rule = WorldConditionParser.parse(yaml("""
            world_conditions:
              name: [world]
              time: 1000
              difficulty: normal
              weather: clear
              environment: normal
              moon_phase: 0
            """), RECIPE_KEY);

        assertTrue(rule instanceof WorldRule.Conditions);
        assertEquals(6, ((WorldRule.Conditions) rule).conditions().size());
        assertTrue(rule.test(world));
    }

    private static YamlConfiguration yaml(String source) throws InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(source);
        return configuration;
    }

    private static World worldAtTime(long time) {
        World world = mock(World.class);
        when(world.getTime()).thenReturn(time);
        return world;
    }

}
