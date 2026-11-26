package pers.yufiria.craftorithm.recipe.worldcondition;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界条件的注册表
 * <p>
 * 键名就是配方里 {@code world_conditions} 下的配置键。内置条件在这里注册；
 * 其他插件可以调用 {@link #register} 把自己的条件接进来，配方即可直接使用该配置键。
 */
public enum WorldConditionRegistry {

    INSTANCE;

    private final Map<String, WorldConditionType> types = new ConcurrentHashMap<>();

    WorldConditionRegistry() {
        register("name", WorldNameCondition::parse);
        register("time", TimeCondition::parse);
        register("difficulty", DifficultyCondition::parse);
        register("weather", WeatherCondition::parse);
        register("environment", EnvironmentCondition::parse);
        register("moon_phase", MoonPhaseCondition::parse);
    }

    public void register(String key, WorldConditionType type) {
        types.put(key, type);
    }

    public @Nullable WorldConditionType get(String key) {
        return types.get(key);
    }

    public Set<String> keys() {
        return new TreeSet<>(types.keySet());
    }

}
