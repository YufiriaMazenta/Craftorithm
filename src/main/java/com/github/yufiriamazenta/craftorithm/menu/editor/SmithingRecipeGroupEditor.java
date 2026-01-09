package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SmithingRecipeGroupEditor extends UnlockableRecipeGroupEditor {

    public SmithingRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup, RecipeGroupListMenu parent) {
        super(player, recipeGroup, parent);
        setDisplay(
            new MenuDisplay(
                title,
                new MenuLayout(
                    Collections.singletonList(
                        "ABCDEFGHI"
                    ),
                    () -> {
                        Map<Character, Supplier<Icon>> iconMap = new HashMap<>();
                        iconMap.put('A', () -> getSortIdEditIcon(0));
                        iconMap.put('B', this::getUnlockIcon);
                        iconMap.put('I', this::getRemoveIcon);
                        return iconMap;
                    }
                )
            )
        );
    }

}
