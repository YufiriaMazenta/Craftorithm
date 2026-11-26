package pers.yufiria.craftorithm.recipe.worldcondition;

import crypticlib.CrypticLib;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 世界名白名单
 * <p>
 * 世界名使用 Bukkit 的世界名而非命名空间 ID；配置时已经不存在于服务器的世界名会被记录警告，
 * 但保留在白名单里，避免因为世界加载顺序不同而判错。
 */
public record WorldNameCondition(Set<String> names) implements WorldCondition {

    public WorldNameCondition {
        names = Set.copyOf(names);
    }

    @Override
    public boolean test(World world) {
        return names.contains(world.getName());
    }

    public static WorldCondition of(Collection<String> names, NamespacedKey recipeKey) {
        Set<String> checkedNames = new HashSet<>(names.size());
        for (String name : names) {
            if (name == null || name.isBlank()) {
                throw new RecipeLoadException("World name cannot be empty");
            }
            if (Bukkit.getWorld(name) == null) {
                CrypticLib.warn("World '" + name + "' does not exist in recipe: " + recipeKey);
            }
            checkedNames.add(name);
        }
        return new WorldNameCondition(checkedNames);
    }

    public static WorldCondition parse(ConfigurationSection section, String key, NamespacedKey recipeKey) {
        return of(ConditionValues.readStringList(section, key), recipeKey);
    }

}
