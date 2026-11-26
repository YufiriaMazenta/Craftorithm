package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * 世界维度
 */
public record EnvironmentCondition(Set<World.Environment> environments) implements WorldCondition {

    public EnvironmentCondition {
        environments = Set.copyOf(environments);
    }

    @Override
    public boolean test(World world) {
        return environments.contains(world.getEnvironment());
    }

    public static WorldCondition parse(ConfigurationSection section, String key, NamespacedKey recipeKey) {
        Set<World.Environment> parsed = EnumSet.noneOf(World.Environment.class);
        for (String value : ConditionValues.readStringList(section, key)) {
            parsed.add(switch (value.toLowerCase(Locale.ROOT)) {
                case "normal" -> World.Environment.NORMAL;
                case "nether" -> World.Environment.NETHER;
                case "the_end" -> World.Environment.THE_END;
                case "custom" -> World.Environment.CUSTOM;
                default -> throw ConditionValues.invalidEnum(
                    section, key, value, "normal / nether / the_end / custom"
                );
            });
        }
        return new EnvironmentCondition(parsed);
    }

}
