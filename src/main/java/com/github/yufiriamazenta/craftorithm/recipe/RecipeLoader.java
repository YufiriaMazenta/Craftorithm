package com.github.yufiriamazenta.craftorithm.recipe;

/**
 *
 * @param <R> 配方的类型
 * @param <S> 加载源
 */
public interface RecipeLoader<R, S> {

    /**
     * 加载配方,并将配方注册
     * @param recipeSource 配方配方的加载源
     */
    R loadRecipe(S recipeSource);

}
