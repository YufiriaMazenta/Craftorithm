package pers.yufiria.craftorithm.recipe.ingredient.restriction;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 配方key匹配器，支持正则和精确匹配
 * <p>
 * 部分配方（原版酿造、原版铁砧等）在事件里拿不到 key，调用方会传 null。
 * 这类「未知配方」只应被「匹配所有配方」的规则命中，由 {@link #matchesAny()} 判定，
 * 避免规则因为正则碰巧命中某个占位字符串而误拦。
 */
public sealed interface RecipeKeyMatcher {

    boolean matches(String recipeKeyStr);

    /**
     * 是否匹配所有配方。
     * 只有通配写法（{@code *} 与 {@code .*} 这类）返回 true。
     */
    default boolean matchesAny() {
        return false;
    }

    /**
     * 从字符串创建匹配器，先尝试编译为正则，失败则回退到精确匹配
     */
    static RecipeKeyMatcher of(String patternStr) {
        if ("*".equals(patternStr.trim())) {
            return MatchAll.INSTANCE;
        }
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

    /**
     * 通配符 {@code *}，匹配所有配方
     */
    record MatchAll() implements RecipeKeyMatcher {

        static final MatchAll INSTANCE = new MatchAll();

        @Override
        public boolean matches(String recipeKeyStr) {
            return true;
        }

        @Override
        public boolean matchesAny() {
            return true;
        }
    }

    record RegexMatcher(Pattern pattern) implements RecipeKeyMatcher {

        /**
         * 等价于「匹配所有配方」的常见正则写法
         */
        private static final Set<String> MATCH_ALL_PATTERNS = Set.of(".*", ".+", "^.*$", "^.+$");

        @Override
        public boolean matches(String recipeKeyStr) {
            return pattern.matcher(recipeKeyStr).matches();
        }

        @Override
        public boolean matchesAny() {
            return MATCH_ALL_PATTERNS.contains(pattern.pattern().trim());
        }
    }

    record ExactMatcher(String expected) implements RecipeKeyMatcher {
        @Override
        public boolean matches(String recipeKeyStr) {
            return recipeKeyStr.equals(expected);
        }
    }
}
