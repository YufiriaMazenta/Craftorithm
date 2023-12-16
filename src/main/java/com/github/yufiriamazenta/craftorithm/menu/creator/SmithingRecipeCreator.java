package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SmithingRecipeCreator extends UnlockableRecipeCreator {
    public SmithingRecipeCreator(@NotNull Player player, @NotNull String recipeName) {
        super(player, RecipeType.SMITHING, recipeName);
        setDisplay(
            new MenuDisplay(
                title(),
                new MenuLayout(
                    () -> {
                        if (CrypticLib.minecraftVersion() < 12000) {
                            return Arrays.asList(
                                "####F####",
                                "#***#%%%#",
                                "# * A% %#",
                                "#***#%%%#",
                                "#########"
                            );
                        } else {
                            return Arrays.asList(
                                "####F####",
                                "#***#%%%#",
                                "#   A% %#",
                                "#***#%%%#",
                                "#########"
                            );
                        }
                    },
                    () -> {
                        Map<Character, Icon> layoutMap = new HashMap<>();
                        layoutMap.put('#', getFrameIcon());
                        layoutMap.put('*', new Icon(Material.CYAN_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_SMITHING_FRAME.value()));
                        layoutMap.put('%', getResultFrameIcon());
                        layoutMap.put('F', getUnlockIcon());
                        layoutMap.put('A', new Icon(
                            Material.SMITHING_TABLE,
                            Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                            event -> {
                                StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                                ItemStack result = Objects.requireNonNull(creator).storedItems().get(24);
                                if (ItemUtil.isAir(result)) {
                                    LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                    return;
                                }
                                String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                                ItemStack base, addition, template;
                                String baseName, additionName, templateName = null;
                                if (CrypticLib.minecraftVersion() < 12000) {
                                    base = creator.storedItems().get(19);
                                    addition = creator.storedItems().get(21);
                                } else {
                                    template = creator.storedItems().get(19);
                                    base = creator.storedItems().get(20);
                                    addition = creator.storedItems().get(21);
                                    templateName = ItemUtils.matchItemNameOrCreate(template, true);
                                    if (ItemUtil.isAir(template)) {
                                        LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                        return;
                                    }
                                }
                                if (ItemUtil.isAir(base) || ItemUtil.isAir(addition)) {
                                    LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                    return;
                                }
                                baseName = ItemUtils.matchItemNameOrCreate(base, true);
                                additionName = ItemUtils.matchItemNameOrCreate(addition, true);
                                ConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                                recipeConfig.set("result", resultName);
                                recipeConfig.set("source.base", baseName);
                                recipeConfig.set("source.addition", additionName);
                                recipeConfig.set("type", "smithing");
                                if (CrypticLib.minecraftVersion() >= 12000) {
                                    recipeConfig.set("source.type", "transform");
                                    recipeConfig.set("source.template", templateName);
                                }
                                recipeConfig.set("unlock", unlock());
                                recipeConfig.saveConfig();
                                recipeConfig.reloadConfig();
                                for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                                    recipeRegistry.register();
                                }
                                RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                                event.getWhoClicked().closeInventory();
                                sendSuccessMsg(event.getWhoClicked(), recipeName);
                            })
                        );
                        return layoutMap;
                    }
                ))
        );
    }
}
