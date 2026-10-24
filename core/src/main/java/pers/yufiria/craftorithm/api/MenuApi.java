package pers.yufiria.craftorithm.api;

import crypticlib.ui.menu.Menu;
import crypticlib.util.TriFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.creator.RecipeCreatorManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.editor.RecipeEditorManager;

import java.util.function.BiFunction;

/**
 * 界面相关API。
 * <p>
 * 提供配方创建、编辑、展示三类界面的打开、注册与注销入口，方便其他插件接管或扩展对应界面。
 */
public enum MenuApi {

    INSTANCE;

    /**
     * 打开指定配方类型的创建界面
     *
     * @param recipeType     配方类型
     * @param player         打开界面的玩家
     * @param recipeId       预设的配方id，可为null
     * @param recipeFileName 预设的配方文件名，可为null
     */
    public void openCreator(RecipeType recipeType, Player player, String recipeId, String recipeFileName) {
        RecipeCreatorManager.INSTANCE.getCreatorFactory(recipeType)
            .ifPresent(creatorFactory -> creatorFactory.apply(player, recipeId, recipeFileName));
    }

    /**
     * 注册某个配方类型的创建界面
     *
     * @param recipeType     配方类型
     * @param creatorFactory 创建界面工厂：(玩家, 配方id, 配方文件名) -> 界面
     */
    public void registerCreator(RecipeType recipeType, TriFunction<Player, String, String, Menu> creatorFactory) {
        RecipeCreatorManager.INSTANCE.registerCreator(recipeType, creatorFactory);
    }

    /**
     * 注销某个配方类型的创建界面
     *
     * @param recipeType 配方类型
     */
    public void unregisterCreator(RecipeType recipeType) {
        RecipeCreatorManager.INSTANCE.removeCreator(recipeType);
    }

    /**
     * 打开指定配方类型的编辑界面
     *
     * @param recipeType 配方类型
     * @param player     打开界面的玩家
     * @param recipeKey  配方key
     * @param recipe     待编辑的配方
     */
    public void openEditor(RecipeType recipeType, Player player, NamespacedKey recipeKey, Recipe recipe) {
        RecipeEditorManager.INSTANCE.getEditorFactory(recipeType)
            .ifPresent(editorFactory -> editorFactory.apply(player, recipeKey, recipe));
    }

    /**
     * 注册某个配方类型的编辑界面
     *
     * @param recipeType    配方类型
     * @param editorFactory 编辑界面工厂：(玩家, 配方key, 配方) -> 界面
     */
    public void registerEditor(RecipeType recipeType, TriFunction<Player, NamespacedKey, Recipe, Menu> editorFactory) {
        RecipeEditorManager.INSTANCE.registerEditor(recipeType, editorFactory);
    }

    /**
     * 注销某个配方类型的编辑界面
     *
     * @param recipeType 配方类型
     */
    public void unregisterEditor(RecipeType recipeType) {
        RecipeEditorManager.INSTANCE.removeEditor(recipeType);
    }

    /**
     * 打开指定配方类型的展示界面
     *
     * @param recipeType 配方类型
     * @param player     打开界面的玩家
     * @param recipe     待展示的配方
     */
    public void openDisplay(RecipeType recipeType, Player player, Recipe recipe) {
        RecipeDisplayManager.INSTANCE.getRecipeDisplayFactory(recipeType)
            .ifPresent(displayFactory -> displayFactory.apply(player, recipe));
    }

    /**
     * 注册某个配方类型的展示界面
     *
     * @param recipeType     配方类型
     * @param displayFactory 展示界面工厂：(玩家, 配方) -> 界面
     */
    public void registerDisplay(RecipeType recipeType, BiFunction<Player, Recipe, Menu> displayFactory) {
        RecipeDisplayManager.INSTANCE.addRecipeDisplay(recipeType, displayFactory);
    }

    /**
     * 注销某个配方类型的展示界面
     *
     * @param recipeType 配方类型
     */
    public void unregisterDisplay(RecipeType recipeType) {
        RecipeDisplayManager.INSTANCE.removeRecipeDisplay(recipeType);
    }

}
