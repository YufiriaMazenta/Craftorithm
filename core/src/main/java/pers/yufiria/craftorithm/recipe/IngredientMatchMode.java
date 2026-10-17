package pers.yufiria.craftorithm.recipe;

/**
 * 材料匹配模式
 */
public enum IngredientMatchMode {

    /**
     * 使用第一个识别到的物品id匹配材料（原有逻辑）
     */
    FIRST,
    /**
     * 收集物品所有可能的物品id后逐个尝试匹配
     */
    ALL

}
