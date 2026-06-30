package pers.yufiria.craftorithm.ui.recipeBook;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.recipeBook.TypeSelectConfig;
import pers.yufiria.craftorithm.ui.MenuDisplayLoader;
import pers.yufiria.craftorithm.ui.TranslatableMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.List;

public class RecipeBookTypeSelectMenu extends TranslatableMenu implements MenuDisplayLoader {

    public RecipeBookTypeSelectMenu(@NotNull Player player) {
        super(player);
        String title = TypeSelectConfig.TITLE.value();
        List<String> layout = TypeSelectConfig.LAYOUT.value();
        this.display = loadMenuDisplay(title, layout, TypeSelectConfig.ICONS.value());
    }

    @Override
    public IconParser iconParser() {
        return RecipeBookTypeSelectIconParser.INSTANCE;
    }

}
