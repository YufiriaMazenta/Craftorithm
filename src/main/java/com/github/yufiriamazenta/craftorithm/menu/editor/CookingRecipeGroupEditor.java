package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.registry.impl.CookingRecipeRegistry;
import crypticlib.config.ConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CookingRecipeGroupEditor extends UnlockableRecipeGroupEditor {

    private final List<CookingRecipeInfo> cookingRecipeInfoList = new CopyOnWriteArrayList<>();

    public CookingRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player, recipeGroup);
        loadCookingRecipeInfoList();
        setDisplay(
            new MenuDisplay(
                title,
                new MenuLayout(
                    Arrays.asList(
                        "ABCDEFGHI",
                        "#########"
                    ),
                    () -> {
                        Map<Character, Icon> layoutMap = new HashMap<>();
                        layoutMap.put('A', getSortIdEditIcon(0));
                        layoutMap.put('B', getUnlockIcon());
                        return layoutMap;
                    }
                )
            )
        );
        //TODO 烧炼配方编辑，考虑到可能会有多个配方，逻辑可能有问题
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

    protected class CookingRecipeInfo {
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

}
