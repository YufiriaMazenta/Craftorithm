package pers.yufiria.craftorithm.item;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Lore阻止规则，预编译配方匹配模式
 * @param lore 去除颜色代码后的Lore文本
 * @param matchers 预编译的配方key匹配器列表
 */
record LoreBlockRule(String lore, List<RecipeKeyMatcher> matchers) {
    /**
     * 检查给定的lore行和配方key是否命中此规则
     */
    boolean matches(String strippedLoreLine, String recipeKeyStr) {
        if (!lore.equals(strippedLoreLine))
            return false;
        for (RecipeKeyMatcher matcher : matchers) {
            if (matcher.matches(recipeKeyStr))
                return true;
        }
        return false;
    }

    /**
     * 配方key匹配器，支持正则和精确匹配
     */
    sealed interface RecipeKeyMatcher {
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
}
