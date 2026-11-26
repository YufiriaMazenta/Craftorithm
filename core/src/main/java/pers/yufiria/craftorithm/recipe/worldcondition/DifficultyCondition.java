package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.Difficulty;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * 世界难度
 */
public record DifficultyCondition(Set<Difficulty> difficulties) implements WorldCondition {

    public DifficultyCondition {
        difficulties = Set.copyOf(difficulties);
    }

    @Override
    public boolean test(World world) {
        return difficulties.contains(world.getDifficulty());
    }

    public static WorldCondition parse(ConfigurationSection section, String key, NamespacedKey recipeKey) {
        Set<Difficulty> parsed = EnumSet.noneOf(Difficulty.class);
        for (String value : ConditionValues.readStringList(section, key)) {
            parsed.add(switch (value.toLowerCase(Locale.ROOT)) {
                case "peaceful" -> Difficulty.PEACEFUL;
                case "easy" -> Difficulty.EASY;
                case "normal" -> Difficulty.NORMAL;
                case "hard" -> Difficulty.HARD;
                default -> throw ConditionValues.invalidEnum(
                    section, key, value, "peaceful / easy / normal / hard"
                );
            });
        }
        return new DifficultyCondition(parsed);
    }

}
