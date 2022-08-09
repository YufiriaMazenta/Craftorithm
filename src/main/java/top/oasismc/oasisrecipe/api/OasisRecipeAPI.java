package top.oasismc.oasisrecipe.api;

import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;
import top.oasismc.oasisrecipe.script.condition.ConditionDispatcher;

public enum OasisRecipeAPI {

    INSTANCE;

    public ActionDispatcher getActionDispatcher() {
        return OasisRecipe.getInstance().getActionDispatcher();
    }

    public ConditionDispatcher getConditionDispatcher() {
        return OasisRecipe.getInstance().getConditionDispatcher();
    }

}
