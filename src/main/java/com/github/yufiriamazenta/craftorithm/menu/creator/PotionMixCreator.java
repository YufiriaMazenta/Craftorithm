package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class PotionMixCreator extends RecipeCreator {

    public PotionMixCreator(@NotNull Player player, @NotNull String recipeName) {
        super(player, RecipeType.POTION, recipeName);
        setDisplay(
            new MenuDisplay(
                title(),
                new MenuLayout(Arrays.asList(
                    "#########",
                    "#***#%%%#",
                    "# * A% %#",
                    "#***#%%%#",
                    "#########"
                ), () -> {
                    Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                    layoutMap.put('#', this::getFrameIcon);
                    layoutMap.put('%', this::getResultFrameIcon);
                    layoutMap.put('*', () -> new Icon(
                        new IconDisplay(
                            Material.CYAN_STAINED_GLASS_PANE,
                            Languages.MENU_RECIPE_CREATOR_ICON_POTION_FRAME.value(player)
                        )
                    ));
                    layoutMap.put('A', () -> new Icon(
                        new IconDisplay(
                            Material.BREWING_STAND,
                            Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player)
                        )).setClickAction(
                        event -> {
                            StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                            ItemStack result = Objects.requireNonNull(creator).storedItems().get(24);
                            ItemStack input = creator.storedItems().get(19);
                            ItemStack ingredient = creator.storedItems().get(21);
                            if (ItemHelper.isAir(result)) {
                                LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                return;
                            }
                            if (ItemHelper.isAir(ingredient) || ItemHelper.isAir(input)) {
                                LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                return;
                            }
                            String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                            String inputName = ItemUtils.matchItemNameOrCreate(input, true);
                            String ingredientName = ItemUtils.matchItemNameOrCreate(ingredient, true);
                            BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                            recipeConfig.set("type", "potion");
                            recipeConfig.set("source.input", inputName);
                            recipeConfig.set("source.ingredient", ingredientName);
                            recipeConfig.set("result", resultName);
                            recipeConfig.saveConfig();
                            recipeConfig.reloadConfig();
                            regRecipeGroup(recipeConfig);
                            event.getWhoClicked().closeInventory();
                            sendSuccessMsg(player, recipeName);
                        }
                        )
                    );
                    return layoutMap;
                })
            )
        );
    }

}
