package pers.yufiria.craftorithm.migrator;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnableWorldsMigratorTest {

    @Test
    void singleWorldBecomesName() throws Exception {
        YamlConfiguration config = yaml("""
            type: vanilla_shaped
            enable_worlds: world
            """);

        assertTrue(EnableWorldsMigrator.convert(config));
        assertEquals("world", config.getString("world_conditions.name"));
        assertFalse(config.contains("enable_worlds"));
    }

    @Test
    void worldListBecomesNameList() throws Exception {
        YamlConfiguration config = yaml("""
            enable_worlds:
              - world
              - world_nether
            """);

        assertTrue(EnableWorldsMigrator.convert(config));
        assertEquals(List.of("world", "world_nether"), config.getStringList("world_conditions.name"));
        assertFalse(config.contains("enable_worlds"));
    }

    @Test
    void existingConditionsAreKept() throws Exception {
        YamlConfiguration config = yaml("""
            enable_worlds: [world_nether]
            world_conditions:
              difficulty: hard
            """);

        assertTrue(EnableWorldsMigrator.convert(config));
        assertEquals(List.of("world_nether"), config.getStringList("world_conditions.name"));
        assertEquals("hard", config.getString("world_conditions.difficulty"));
        assertFalse(config.contains("enable_worlds"));
    }

    @Test
    void configuredNameWinsOverLegacyValue() throws Exception {
        YamlConfiguration config = yaml("""
            enable_worlds: [world_nether]
            world_conditions:
              name: [world]
            """);

        assertTrue(EnableWorldsMigrator.convert(config));
        assertEquals(List.of("world"), config.getStringList("world_conditions.name"));
        assertFalse(config.contains("enable_worlds"));
    }

    @Test
    void placeholderEmptyConditionsSectionIsRebuilt() throws Exception {
        YamlConfiguration config = yaml("""
            enable_worlds: [world]
            world_conditions:
            """);

        assertTrue(EnableWorldsMigrator.convert(config));
        assertEquals(List.of("world"), config.getStringList("world_conditions.name"));
        assertFalse(config.contains("enable_worlds"));
    }

    @Test
    void configWithoutLegacyOptionIsUntouched() throws Exception {
        YamlConfiguration config = yaml("""
            type: vanilla_shapeless
            """);

        assertFalse(EnableWorldsMigrator.convert(config));
        assertFalse(config.contains("world_conditions"));
    }

    @Test
    void convertedConfigSurvivesSaveAndReload(@TempDir Path tempDir) throws Exception {
        Path recipeFile = tempDir.resolve("recipe.yml");
        Files.writeString(recipeFile, """
            type: vanilla_shaped
            result: minecraft:stone
            enable_worlds:
              - world
              - world_nether
            """);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(recipeFile.toFile());

        assertTrue(EnableWorldsMigrator.convert(config));
        config.save(recipeFile.toFile());

        YamlConfiguration reloaded = YamlConfiguration.loadConfiguration(recipeFile.toFile());
        assertEquals(List.of("world", "world_nether"), reloaded.getStringList("world_conditions.name"));
        assertEquals("minecraft:stone", reloaded.getString("result"));
        assertFalse(reloaded.contains("enable_worlds"));
    }

    private static YamlConfiguration yaml(String source) throws InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(source);
        return configuration;
    }

}
