package pers.yufiria.craftorithm.permission;

/**
 * 插件权限节点集中定义。
 * <p>
 * 命名采用能力导向，格式为 {@code craftorithm.<域>.<能力>}，命令与 GUI 复用同一条权限。
 */
public final class Permissions {

    // 根命令
    public static final String COMMAND = "craftorithm.command";

    // 命令
    public static final String COMMAND_RELOAD = "craftorithm.command.reload";
    public static final String COMMAND_VERSION = "craftorithm.command.version";
    public static final String COMMAND_SCRIPT = "craftorithm.command.script";
    public static final String COMMAND_OPEN_MENU = "craftorithm.command.openmenu";
    public static final String COMMAND_RECIPE_BOOK = "craftorithm.command.recipebook";

    // 配方
    public static final String RECIPE_CREATE = "craftorithm.recipe.create";
    public static final String RECIPE_REMOVE = "craftorithm.recipe.remove";
    public static final String RECIPE_EDIT = "craftorithm.recipe.edit";
    public static final String RECIPE_DISPLAY = "craftorithm.recipe.display";
    public static final String RECIPE_DISABLE = "craftorithm.recipe.disable";
    public static final String RECIPE_RESTORE = "craftorithm.recipe.restore";
    public static final String RECIPE_DISCOVER = "craftorithm.recipe.discover";
    public static final String RECIPE_UNDISCOVER = "craftorithm.recipe.undiscover";

    // 物品域
    public static final String ITEM = "craftorithm.item";

}
