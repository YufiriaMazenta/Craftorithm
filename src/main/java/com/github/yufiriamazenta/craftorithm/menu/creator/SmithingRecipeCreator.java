package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SmithingRecipeCreator extends UnlockableRecipeCreator {

    protected boolean copyNbt = true;

    public SmithingRecipeCreator(@NotNull Player player, @NotNull String groupName, @NotNull String recipeName) {
        super(player, RecipeType.SMITHING, groupName, recipeName);
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
                                "####B####"
                            );
                        } else {
                            return Arrays.asList(
                                "####F####",
                                "#***#%%%#",
                                "#   A% %#",
                                "#***#%%%#",
                                "####B####"
                            );
                        }
                    },
                    () -> {
                        Map<Character, Icon> layoutMap = new HashMap<>();
                        layoutMap.put('#', getFrameIcon());
                        layoutMap.put('*', new Icon(
                            Material.CYAN_STAINED_GLASS_PANE,
                            Languages.MENU_RECIPE_CREATOR_ICON_SMITHING_FRAME.value(player)
                        ));
                        layoutMap.put('%', getResultFrameIcon());
                        layoutMap.put('F', getUnlockIcon());
                        layoutMap.put('B', getCopyNbtIcon());
                        layoutMap.put('A', new Icon(
                            Material.SMITHING_TABLE,
                            Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player),
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
                                RecipeGroup recipeGroup = getRecipeGroup(groupName);
                                ConfigWrapper recipeConfig = recipeGroup.recipeGroupConfig();
                                ConfigurationSection recipeCfgSection = recipeConfig.config().createSection(recipeName);
                                recipeCfgSection.set("result", resultName);
                                recipeCfgSection.set("unlock", unlock());
                                recipeCfgSection.set("type", "smithing");
                                recipeCfgSection.set("source.base", baseName);
                                recipeCfgSection.set("source.addition", additionName);
                                recipeCfgSection.set("source.copy_nbt", copyNbt);
                                if (CrypticLib.minecraftVersion() >= 12000) {
                                    recipeCfgSection.set("source.type", "transform");
                                    recipeCfgSection.set("source.template", templateName);
                                }
                                recipeConfig.saveConfig();
                                recipeConfig.reloadConfig();
                                recipeGroup.updateAndLoadRecipeGroup();
                                event.getWhoClicked().closeInventory();
                                sendSuccessMsg();
                            })
                        );
                        return layoutMap;
                    }
                ))
        );
    }

    protected Icon getCopyNbtIcon() {
        Icon icon = new Icon(
            Material.NAME_TAG,
            Languages.MENU_RECIPE_CREATOR_ICON_SMITHING_COPY_NBT_TOGGLE
                .value(player)
                .replace("<enable>", String.valueOf(copyNbt)),
            event -> toggleCopyNbt(event.getSlot(), event)
        );
        if (copyNbt)
            ItemUtils.toggleItemGlowing(icon.display());
        return icon;
    }

    protected void toggleCopyNbt(int slot, InventoryClickEvent event) {
        super.toggleIconGlowing(slot, event);
        copyNbt = !copyNbt;
        ItemStack display = event.getCurrentItem();
        ItemUtil.setDisplayName(
            display,
            Languages.MENU_RECIPE_CREATOR_ICON_SMITHING_COPY_NBT_TOGGLE
                .value(player)
                .replace("<enable>", String.valueOf(copyNbt))
        );
    }

}
