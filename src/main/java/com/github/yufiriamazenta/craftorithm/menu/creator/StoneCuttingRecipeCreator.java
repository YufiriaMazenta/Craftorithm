package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.config.ConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StoneCuttingRecipeCreator extends UnlockableRecipeCreator {

    public StoneCuttingRecipeCreator(@NotNull Player player, @NotNull String groupName, @NotNull String recipeName) {
        super(player, RecipeType.STONE_CUTTING, groupName, recipeName);
        setDisplay(
            new MenuDisplay(
                title(),
                new MenuLayout(Arrays.asList(
                    "#########",
                    "#### ####",
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
                            List<String> resultList = new ArrayList<>();
                            ItemStack ingredient = Objects.requireNonNull(creator).storedItems().get(13);
                            if (ItemUtil.isAir(ingredient)) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                return;
                            }
                            String ingredientStr = ItemUtils.matchItemNameOrCreate(ingredient, true);
                            for (int i = 28; i < 35; i++) {
                                ItemStack result = creator.storedItems().get(i);
                                if (ItemUtil.isAir(result))
                                    continue;
                                resultList.add(ItemUtils.matchItemNameOrCreate(result, false));
                            }
                            if (resultList.isEmpty()) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                return;
                            }
                            RecipeGroup recipeGroup = getRecipeGroup(groupName);
                            ConfigWrapper recipeConfig = recipeGroup.recipeGroupConfig();
                            for (int i = 0; i < resultList.size(); i++) {
                                String recipeFullName = recipeName + "_" + i;
                                ConfigurationSection recipeCfgSection = recipeConfig.config().createSection(recipeFullName);
                                recipeCfgSection.set("result", resultList.get(i));
                                recipeCfgSection.set("type", "stone_cutting");
                                recipeCfgSection.set("source.ingredient", ingredientStr);
                                recipeCfgSection.set("unlock", unlock());
                            }
                            recipeConfig.saveConfig();
                            recipeConfig.reloadConfig();
                            recipeGroup.updateAndLoadRecipeGroup();
                            event.getWhoClicked().closeInventory();
                            sendSuccessMsg();
                        })
                    );
                    return layoutMap;
                })
            )
        );
    }

}
