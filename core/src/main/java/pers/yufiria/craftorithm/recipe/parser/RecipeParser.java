package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.recipe.ingredient.RecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.function.Supplier;

/**
 *
 * @param <R> 配方的类型
 */
public interface RecipeParser<R extends Recipe> {

    @NotNull RecipeChoiceParser choiceParser();

    /**
     * 从配置文件里解析配方
     * @param recipeConfig 配方的加载源
     */
    @Nullable R parse(String recipeName, ConfigurationSection recipeConfig);

    /**
     * 从配置里解析出结果物品
     * @param recipeConfig 配方的加载源
     * @return 结果物品
     * @throws ItemNotFoundException 配置里的物品不存在时抛出
     */
    default @NotNull ItemStack requireResult(@NotNull ConfigurationSection recipeConfig) {
        String resultId = recipeConfig.getString("result");
        return ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId))
            .orElseThrow(() -> new ItemNotFoundException("Item not found: " + resultId));
    }

    /**
     * 执行解析动作, 并把非 {@link RecipeLoadException} 的异常统一包装为 {@link RecipeLoadException},
     * 避免各实现类重复书写同一段 try/catch。
     * @param parseAction 具体的解析动作
     * @return 解析结果
     */
    default <T> T parseSafely(@NotNull Supplier<T> parseAction) {
        try {
            return parseAction.get();
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
