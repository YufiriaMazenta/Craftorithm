package pers.yufiria.craftorithm.ui.display;

import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.Menu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.MenuDisplayLoader;
import pers.yufiria.craftorithm.ui.TranslatableMenu;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class RecipeDisplayMenu<T extends Recipe> extends TranslatableMenu implements BackableMenu, MenuDisplayLoader {

    private static final String ICON_TYPE_KEY = "icon_type";

    protected Menu parentMenu;
    protected final T recipe;

    public RecipeDisplayMenu(@NotNull Player player, T recipe) {
        super(player);
        this.recipe = recipe;
    }

    @Override
    public String parsedMenuTitle() {
        String title = super.parsedMenuTitle();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        title = title.replace("<recipe_key>", recipeKey != null ? recipeKey.getKey() : "<recipe_key>");
        return title;
    }

    @Override
    public Menu parentMenu() {
        return parentMenu;
    }

    @Override
    public void setParentMenu(@Nullable Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    /**
     * 按图标的 icon_type 设置其显示物品。
     * icon_type 为 {@link RecipeDisplayManager#ICON_TYPE_RESULT} 时统一显示配方结果,
     * 其余类型交由 {@code iconTypeHandlers} 处理, 未注册的类型不做处理。
     *
     * @param icon             待处理的图标, 非物品显示图标会被忽略
     * @param iconTypeHandlers icon_type -> 显示逻辑
     */
    protected void applyIconType(@NotNull Icon icon, @NotNull Map<String, Consumer<ItemDisplayIcon>> iconTypeHandlers) {
        if (!(icon instanceof ItemDisplayIcon itemDisplayIcon)) {
            return;
        }
        Optional<Object> iconTypeOpt = itemDisplayIcon.getData(ICON_TYPE_KEY);
        if (iconTypeOpt.isEmpty()) {
            return;
        }
        String iconType = iconTypeOpt.get().toString().toLowerCase();
        if (RecipeDisplayManager.ICON_TYPE_RESULT.equals(iconType)) {
            itemDisplayIcon.setDisplayItem(recipe.getResult());
            return;
        }
        Consumer<ItemDisplayIcon> handler = iconTypeHandlers.get(iconType);
        if (handler != null) {
            handler.accept(itemDisplayIcon);
        }
    }

}
