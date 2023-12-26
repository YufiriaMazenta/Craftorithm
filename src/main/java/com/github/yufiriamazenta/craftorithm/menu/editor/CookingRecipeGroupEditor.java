package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.registry.impl.CookingRecipeRegistry;
import crypticlib.chat.TextProcessor;
import crypticlib.config.ConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class CookingRecipeGroupEditor extends UnlockableRecipeGroupEditor {

    private final List<CookingRecipeInfo> cookingRecipeInfoList = new CopyOnWriteArrayList<>();

    public CookingRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player, recipeGroup);
        loadCookingRecipeInfoList();
        loadElements();
        setDisplay(
            new MenuDisplay(
                title,
                new MenuLayout(
                    Arrays.asList(
                        "ABCDEFGHI",
                        "%%%%%%%%%",
                        "X#######Y"
                    ),
                    () -> {
                        Map<Character, Icon> layoutMap = new HashMap<>();
                        layoutMap.put('A', getSortIdEditIcon(0));
                        layoutMap.put('B', getUnlockIcon());
                        layoutMap.put('#', getFrameIcon());
                        layoutMap.put('X', getPreviousIcon());
                        layoutMap.put('Y', getNextIcon());
                        return layoutMap;
                    }
                )
            )
        );
        //TODO 烧炼配方编辑，考虑到可能会有多个配方，逻辑可能有问题
    }

    private void loadElements() {
        List<Icon> elements = new ArrayList<>();
        for (int i = 0; i < cookingRecipeInfoList.size(); i++) {
            CookingRecipeInfo cookingRecipeInfo = cookingRecipeInfoList.get(i);
            Icon icon = new CookingIcon(cookingRecipeInfo.ingredient, i, cookingRecipeInfo);
            elements.add(icon);
        }
        setElements(elements);
    }

    private void loadCookingRecipeInfoList() {
        ConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
        Validate.notNull(configWrapper);
        if (!configWrapper.config().isList("source")) {
            for (Map<?, ?> map : configWrapper.config().getMapList("source")) {
                CookingRecipeRegistry.CookingBlock cookingBlock = CookingRecipeRegistry.CookingBlock.valueOf(map.get("block").toString());
                float exp;
                if (map.containsKey("exp")) {
                    exp = (float) map.get("exp");
                } else {
                    if (configWrapper.contains("exp")) {
                        exp = (float) configWrapper.config().getDouble("exp");
                    }
                    else {
                        exp = 0;
                    }
                }
                int time;
                if (map.containsKey("time")) {
                    time = (int) map.get("time");
                } else {
                    if (configWrapper.contains("time")) {
                        time = configWrapper.config().getInt("time");
                    } else {
                        time = 200;
                    }
                }
                ItemStack ingredient = ItemManager.INSTANCE.matchItem(map.get("source").toString());
                ingredient.setAmount(1);
                cookingRecipeInfoList.add(new CookingRecipeInfo(time, exp, ingredient, cookingBlock));
            }
        } else {
            CookingRecipeRegistry.CookingBlock cookingBlock = CookingRecipeRegistry.CookingBlock.valueOf(configWrapper.config().getString("source.block"));
            float exp = (float) configWrapper.config().getDouble("exp");
            int time = configWrapper.config().getInt("time");
            ItemStack ingredient = ItemManager.INSTANCE.matchItem(Objects.requireNonNull(configWrapper.config().getString("source.item")));
            cookingRecipeInfoList.add(new CookingRecipeInfo(time, exp, ingredient, cookingBlock));
        }
    }

    protected class CookingIcon extends Icon {

        private CookingRecipeInfo cookingRecipeInfo;
        private int recipeId;

        public CookingIcon(@NotNull ItemStack display, int recipeId, CookingRecipeInfo cookingRecipeInfo) {
            super(display);
            this.cookingRecipeInfo = cookingRecipeInfo;
            this.recipeId = recipeId;
            List<String> lore = new ArrayList<>(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_LORE.value(player));
            lore.replaceAll(it -> it
                .replace("<time>", cookingRecipeInfo.cookingTime + "")
                .replace("<exp>", cookingRecipeInfo.exp + "")
            );
            ItemUtil.setLore(display(), lore);
            setClickAction(event -> {
                switch (event.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        new Conversation(
                            Craftorithm.instance(),
                            player,
                            new CookingTimePrompt(recipeId)
                        ).start();
                        player.closeInventory();
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        //TODO 修改奖励经验
                        break;
                    default:
                        event.setCancelled(true);
                        break;
                }
            });
        }

        public CookingRecipeInfo cookingRecipeInfo() {
            return cookingRecipeInfo;
        }

        public CookingIcon setCookingRecipeInfo(CookingRecipeInfo cookingRecipeInfo) {
            this.cookingRecipeInfo = cookingRecipeInfo;
            return this;
        }

        public int recipeId() {
            return recipeId;
        }

        public void setRecipeId(int recipeId) {
            this.recipeId = recipeId;
        }

    }

    class CookingRecipeInfo {
        private int cookingTime;
        private float exp;
        private ItemStack ingredient;
        private CookingRecipeRegistry.CookingBlock cookingBlock;

        public CookingRecipeInfo(int cookingTime, float exp, ItemStack ingredient, CookingRecipeRegistry.CookingBlock cookingBlock) {
            this.cookingTime = cookingTime;
            this.exp = exp;
            this.ingredient = ingredient;
            this.cookingBlock = cookingBlock;
        }

        public int cookingTime() {
            return cookingTime;
        }

        public CookingRecipeInfo setCookingTime(int cookingTime) {
            this.cookingTime = cookingTime;
            return this;
        }

        public float exp() {
            return exp;
        }

        public CookingRecipeInfo setExp(float exp) {
            this.exp = exp;
            return this;
        }

        public ItemStack source() {
            return ingredient;
        }

        public CookingRecipeInfo setIngredient(ItemStack ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public CookingRecipeRegistry.CookingBlock cookingBlock() {
            return cookingBlock;
        }

        public CookingRecipeInfo setCookingBlock(CookingRecipeRegistry.CookingBlock cookingBlock) {
            this.cookingBlock = cookingBlock;
            return this;
        }

    }

    class CookingTimePrompt implements NumberPrompt {

        private int cookingRecipeId;

        public CookingTimePrompt(int cookingRecipeId) {
            this.cookingRecipeId = cookingRecipeId;
        }

        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            int cookingTime = number.intValue();
            //TODO 更新配方
            YamlConfiguration config = recipeGroup.recipeGroupConfig().config();
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return TextProcessor.toComponent(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_INPUT_COOKING_TIME_HINT.value(player));
        }
    }

}
