package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * 世界天气
 * <p>
 * 雷暴优先于降雨：打雷时即使同时在下雨也只算 {@link Weather#THUNDER}。
 */
public record WeatherCondition(Set<Weather> weathers) implements WorldCondition {

    public WeatherCondition {
        weathers = Set.copyOf(weathers);
    }

    @Override
    public boolean test(World world) {
        return weathers.contains(Weather.of(world));
    }

    public static WorldCondition parse(ConfigurationSection section, String key, NamespacedKey recipeKey) {
        Set<Weather> parsed = EnumSet.noneOf(Weather.class);
        for (String value : ConditionValues.readStringList(section, key)) {
            parsed.add(switch (value.toLowerCase(Locale.ROOT)) {
                case "clear" -> Weather.CLEAR;
                case "rain" -> Weather.RAIN;
                case "thunder" -> Weather.THUNDER;
                default -> throw ConditionValues.invalidEnum(
                    section, key, value, "clear / rain / thunder"
                );
            });
        }
        return new WeatherCondition(parsed);
    }

    public enum Weather {

        CLEAR,
        RAIN,
        THUNDER;

        public static Weather of(World world) {
            if (world.isThundering()) {
                return THUNDER;
            }
            if (world.hasStorm()) {
                return RAIN;
            }
            return CLEAR;
        }

    }

}
