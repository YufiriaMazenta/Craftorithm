package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.chat.TextProcessor;
import crypticlib.config.ConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import crypticlib.util.ItemUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RecipeGroupEditor extends Menu {

    private final RecipeGroup recipeGroup;

    public RecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player);
        this.recipeGroup = recipeGroup;
        Validate.notNull(recipeGroup);
        setDisplay(new MenuDisplay(
            Languages.MENU_RECIPE_EDITOR_TITLE.value(player).replace("<recipe_name>", recipeGroup.groupName()),
            new MenuLayout(
                Collections.singletonList(
                    "ABCDEFGHI"
                ),
                () -> {
                    Map<Character, Icon> iconMap = new HashMap<>();
                    iconMap.put('A', getSortIdEditIcon());
                    switch (recipeGroup.recipeType()) {
                        case SHAPED:
                        case SHAPELESS:
                        case SMITHING:
                            iconMap.put('B', getUnlockIcon());
//                            iconMap.put('C', getConditionIcon());
//                            iconMap.put('D', getActionIcon());
                            //TODO condition和action
                            break;
                        case STONE_CUTTING:
                            break;
                        case COOKING:
                        case RANDOM_COOKING:
                            iconMap.put('B', getUnlockIcon());
                            //TODO 不同种类烧炼配方时间和奖励编辑
                            break;
                        case POTION:
                            //TODO 无编辑
                            break;
                        case ANVIL:
                            //TODO 编辑所需经验
                            break;
                        case UNKNOWN:
                            throw new IllegalArgumentException();
                    }
                    return iconMap;
                }
            )
        ));
    }

    private Icon getSortIdEditIcon() {
        return new Icon(
            Material.OAK_SIGN,
            Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_NAME.value(player)
                .replace("<id>", RecipeManager.INSTANCE.getRecipeGroupSortId(recipeGroup.groupName()) + ""),
            Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_LORE.value(player),
            event -> {
                new Conversation(
                    Craftorithm.instance(),
                    player,
                    new SortIdEditPrompt()
                ).start();
                player.closeInventory();
            }
        );
    }

    private void updateSortIdEditIcon() {
        ItemStack sortIdEditIcon = openedInventory.getItem(0);
        ItemUtil.setDisplayName(
            sortIdEditIcon,
            TextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_NAME
                .value(player)
                .replace("<id>", RecipeManager.INSTANCE.getRecipeGroupSortId(recipeGroup.groupName()) + ""))
        );
    }

    class SortIdEditPrompt implements NumberPrompt {
        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            int sortId = number.intValue();
            RecipeManager.INSTANCE.setRecipeGroupSortId(recipeGroup.groupName(), sortId);
            ConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
            configWrapper.set("sort_id", sortId);
            configWrapper.saveConfig();
            updateSortIdEditIcon();
            openMenu();
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return TextProcessor.toComponent(TextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_INPUT_HINT.value(player)));
        }
    }

    private Icon getUnlockIcon() {
        Icon icon = new Icon(
            Material.KNOWLEDGE_BOOK,
            Languages.MENU_RECIPE_EDITOR_ICON_UNLOCK.value(player)
                .replace("<unlock>", String.valueOf(recipeGroup.unlock())),
            event -> {
                int slot = event.getSlot();
                boolean unlock = !recipeGroup.unlock();
                recipeGroup.setUnlock(unlock);
                recipeGroup.recipeGroupConfig().set("unlock", unlock);
                recipeGroup.recipeGroupConfig().saveConfig();
                updateUnlockIcon(slot);
            }
        );
        if (recipeGroup.unlock()) {
            ItemUtils.toggleItemGlowing(icon.display());
        }
        return icon;
    }

    private void updateUnlockIcon(int slot) {
        ItemStack unlockIcon = openedInventory.getItem(slot);
        if (ItemUtil.isAir(unlockIcon)) return;
        ItemUtils.toggleItemGlowing(unlockIcon);
        ItemUtil.setDisplayName(
            unlockIcon,
            Languages.MENU_RECIPE_EDITOR_ICON_UNLOCK.value(player)
                .replace("<unlock>", String.valueOf(recipeGroup.unlock()))
        );
    }

    private Icon getActionIcon() {
        //TODO
        return null;
    }

    private Icon getConditionIcon() {
        //TODO
        return null;
    }

}
