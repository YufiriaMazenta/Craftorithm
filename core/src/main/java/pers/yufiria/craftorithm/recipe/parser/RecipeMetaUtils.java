package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 配方元数据(配方组、配方书分类)的配置应用工具,
 * 供各 {@link RecipeParser} 复用, 避免每个解析器各写一遍。
 */
public final class RecipeMetaUtils {

    private static final String GROUP_KEY = "group";
    private static final String RECIPE_BOOK_CATEGORY_KEY = "recipe_book_category";

    /**
     * 应用合成类配方(有序/无序)的元数据
     */
    public static void applyCraftingMeta(CraftingRecipe recipe, ConfigurationSection recipeConfig) {
        applyGroup(recipeConfig, recipe::setGroup);
        applyCategory(recipeConfig, CraftingBookCategory::valueOf, recipe::setCategory);
    }

    /**
     * 应用烧炼类配方(熔炉/高炉/烟熏炉/营火)的元数据
     */
    public static void applyCookingMeta(CookingRecipe<?> recipe, ConfigurationSection recipeConfig) {
        applyGroup(recipeConfig, recipe::setGroup);
        applyCategory(recipeConfig, CookingBookCategory::valueOf, recipe::setCategory);
    }

    /**
     * 应用切石机配方的配方组
     */
    public static void applyGroup(StonecuttingRecipe recipe, ConfigurationSection recipeConfig) {
        applyGroup(recipeConfig, recipe::setGroup);
    }

    private static void applyGroup(ConfigurationSection recipeConfig, Consumer<String> groupSetter) {
        String group = recipeConfig.getString(GROUP_KEY);
        if (group != null) {
            groupSetter.accept(group);
        }
    }

    private static <C> void applyCategory(
        ConfigurationSection recipeConfig,
        Function<String, C> categoryParser,
        Consumer<C> categorySetter
    ) {
        if (!recipeConfig.contains(RECIPE_BOOK_CATEGORY_KEY)) {
            return;
        }
        String categoryStr = Objects.requireNonNull(recipeConfig.getString(RECIPE_BOOK_CATEGORY_KEY)).toUpperCase();
        categorySetter.accept(categoryParser.apply(categoryStr));
    }

}
