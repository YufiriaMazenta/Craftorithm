package pers.yufiria.craftorithm.recipe.worldcondition;

/**
 * 闭区间
 */
public record IntRange(int min, int max) {

    public boolean contains(int value) {
        return value >= min && value <= max;
    }

}
