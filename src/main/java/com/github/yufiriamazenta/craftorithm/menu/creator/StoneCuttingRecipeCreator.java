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

import java.util.*;
import java.util.function.Supplier;

public class StoneCuttingRecipeCreator extends UnlockableRecipeCreator {

    public StoneCuttingRecipeCreator(@NotNull Player player, @NotNull String recipeName) {
        super(player, RecipeType.STONE_CUTTING, recipeName);
        setDisplay(
            new MenuDisplay(
                title(),
                new MenuLayout(Arrays.asList(
                    "#########",
                    "#       #",
                    "####A####",
                    "#       #",
                    "#########"
                ), () -> {
                    Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                    layoutMap.put('#', this::getFrameIcon);
                    layoutMap.put('F', this::getUnlockIcon);
                    layoutMap.put('A', () -> new Icon(
                            new IconDisplay(
                                Material.STONECUTTER,
                                Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player)
                            )
                        ).setClickAction(
                        event -> {
                            StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                            List<String> sourceList = new ArrayList<>();
                            List<String> resultList = new ArrayList<>();
                            for (int i = 10; i < 17; i++) {
                                ItemStack source = Objects.requireNonNull(creator).storedItems().get(i);
                                if (ItemHelper.isAir(source))
                                    continue;
                                sourceList.add(ItemUtils.matchItemNameOrCreate(source, true));
                            }
                            for (int i = 28; i < 35; i++) {
                                ItemStack result = creator.storedItems().get(i);
                                if (ItemHelper.isAir(result))
                                    continue;
                                resultList.add(ItemUtils.matchItemNameOrCreate(result, false));
                            }
                            if (sourceList.isEmpty()) {
                                LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                return;
                            }
                            if (resultList.isEmpty()) {
                                LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                return;
                            }
                            BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                            recipeConfig.set("multiple", true);
                            recipeConfig.set("result", resultList);
                            recipeConfig.set("type", "stone_cutting");
                            recipeConfig.set("source", sourceList);
                            recipeConfig.set("unlock", unlock());
                            recipeConfig.saveConfig();
                            recipeConfig.reloadConfig();
                            regRecipeGroup(recipeConfig);
                            event.getWhoClicked().closeInventory();
                            sendSuccessMsg(event.getWhoClicked(), recipeName);
                        }
                        )
                    );
                    return layoutMap;
                })
            )
        );
    }

}
