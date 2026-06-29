package pers.yufiria.craftorithm.ui.custom;

import crypticlib.ui.display.MenuDisplay;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.script.ScriptEngine;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.RecipeDisplayLoader;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.List;

public class CustomMenuInfo implements RecipeDisplayLoader {

    private final @NotNull MenuDisplay menuDisplay;
    private final @Nullable String permission;
    private final @Nullable CompiledScript openAction;
    private final @Nullable CompiledScript closeAction;
    private static final IconParser iconParser = CustomMenuIconParser.INSTANCE;

    public CustomMenuInfo(@NotNull ConfigurationSection menuConfig) {
        this.menuDisplay = loadMenuDisplay(
            menuConfig.getString("title"),
            menuConfig.getStringList("layout"),
            menuConfig.getConfigurationSection("icons")
        );
        this.permission = menuConfig.getString("permission");
        this.openAction = parseActions(menuConfig.getStringList("open_actions"));
        this.closeAction = parseActions(menuConfig.getStringList("close_actions"));
    }

    public MenuDisplay menuDisplay() {
        return menuDisplay;
    }

    public String permission() {
        return permission;
    }

    public @Nullable CompiledScript openAction() {
        return openAction;
    }

    public @Nullable CompiledScript closeAction() {
        return closeAction;
    }

    private CompiledScript parseActions(List<String> actionStrList) {
        if (actionStrList == null || actionStrList.isEmpty()) {
            return null;
        }
        return ScriptEngine.INSTANCE.compile("custom_menu_" + menuDisplay.title(), String.join("\n", actionStrList));
    }

    @Override
    @NotNull
    public IconParser iconParser() {
        return iconParser;
    }

}
