package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.config.ConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
                    Map<Character, Icon> layoutMap = new HashMap<>();
                    layoutMap.put('#', getFrameIcon());
                    layoutMap.put('F', getUnlockIcon());
                    layoutMap.put('A', new Icon(
                        Material.STONECUTTER,
                        Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player),
                        event -> {
                            StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                            List<String> sourceList = new ArrayList<>();
                            List<String> resultList = new ArrayList<>();
                            for (int i = 10; i < 17; i++) {
                                ItemStack source = Objects.requireNonNull(creator).storedItems().get(i);
                                if (ItemUtil.isAir(source))
                                    continue;
                                sourceList.add(ItemUtils.matchItemNameOrCreate(source, true));
                            }
                            for (int i = 28; i < 35; i++) {
                                ItemStack result = creator.storedItems().get(i);
                                if (ItemUtil.isAir(result))
                                    continue;
                                resultList.add(ItemUtils.matchItemNameOrCreate(result, false));
                            }
                            if (sourceList.isEmpty()) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                return;
                            }
                            if (resultList.isEmpty()) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                return;
                            }
                            ConfigWrapper recipeConfig = createRecipeConfig(recipeName);
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
                        })
                    );
                    return layoutMap;
                })
            )
        );
    }

}
