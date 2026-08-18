package pers.yufiria.craftorithm.recipe.blockrule;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 配方key匹配器，支持正则和精确匹配
 */
public sealed interface RecipeKeyMatcher {

    boolean matches(String recipeKeyStr);

    /**
     * 从字符串创建匹配器，先尝试编译为正则，失败则回退到精确匹配
     */
    static RecipeKeyMatcher of(String patternStr) {
        try {
            Pattern pattern = Pattern.compile(patternStr);
            return new RegexMatcher(pattern);
        } catch (PatternSyntaxException e) {
            return new ExactMatcher(patternStr);
        }
    }

    /**
     * 批量创建匹配器
     */
    static List<RecipeKeyMatcher> ofList(List<String> patterns) {
        return patterns.stream().map(RecipeKeyMatcher::of).toList();
    }

    record RegexMatcher(Pattern pattern) implements RecipeKeyMatcher {
        @Override
        public boolean matches(String recipeKeyStr) {
            return pattern.matcher(recipeKeyStr).matches();
        }
    }

    record ExactMatcher(String expected) implements RecipeKeyMatcher {
        @Override
        public boolean matches(String recipeKeyStr) {
            return recipeKeyStr.equals(expected);
        }
    }
}
