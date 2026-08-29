package pers.yufiria.craftorithm.util;

import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.choice.CustomRecipeChoice;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IngredientUtils {
    static final Map<NamespacedKey, Tag<Material>> ITEMS_TAGS = new HashMap<>();
    static final Map<NamespacedKey, Tag<Material>> BLOCKS_TAGS = new HashMap<>();

    static {
        Iterable<Tag<Material>> blocksTags = Bukkit.getTags("blocks", Material.class);
        for (Tag<Material> blocksTag : blocksTags) {
            BLOCKS_TAGS.put(blocksTag.getKey(), blocksTag);
        }

        Iterable<Tag<Material>> itemsTags = Bukkit.getTags("items", Material.class);
        for (Tag<Material> itemsTag : itemsTags) {
            ITEMS_TAGS.put(itemsTag.getKey(), itemsTag);
        }
    }

    public static Optional<Tag<Material>> getTag(String tagKeyStr) {
        if (tagKeyStr.contains(":")) {
            NamespacedKey tagKey = NamespacedKey.fromString(tagKeyStr);
            return Optional.ofNullable(getTag(tagKey));
        }
        //兼容旧版写法, 也就是直接在Tag里反射
        String upperTagKey = tagKeyStr.toUpperCase();
        Tag<Material> reflectTag;
        try {
            Field field = Tag.class.getField(upperTagKey);
            reflectTag = (Tag<Material>) field.get(null);
            return Optional.ofNullable(reflectTag);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        NamespacedKey tagKey = NamespacedKey.fromString(tagKeyStr);
        return Optional.ofNullable(getTag(tagKey));
    }

    private static @Nullable Tag<Material> getTag(NamespacedKey tagKey) {
        if (ITEMS_TAGS.containsKey(tagKey)) {
            return ITEMS_TAGS.get(tagKey);
        }
        if (BLOCKS_TAGS.containsKey(tagKey)) {
            return BLOCKS_TAGS.get(tagKey);
        }
        return null;
    }

    public static RecipeChoice getBukkitChoice(RecipeChoice recipeChoice) {
        if (recipeChoice instanceof CustomRecipeChoice customRecipeChoice) {
            RecipeChoice bukkitChoice = customRecipeChoice.bukkitChoice();
            return getBukkitChoice(bukkitChoice);
        }
        return recipeChoice;
    }

    public static boolean testOptionalChoice(Optional<RecipeChoice> choice, ItemStack inputItem) {
        Optional<Boolean> result = choice.map(ingredient -> ingredient.test(inputItem));
        return result.orElseGet(() -> ItemHelper.isAir(inputItem));
    }

    /**
     * 回溯匹配
     * 用于无序配方判断材料是否足够
     * @param items
     * @param choices
     * @return
     */
    @ApiStatus.Internal
    public static boolean matchItemsToChoices(
        List<ItemStack> items,
        List<RecipeChoice> choices
    ) {
        // 使用一个 boolean 数组标记成分是否已被使用
        boolean[] used = new boolean[choices.size()];
        return backtrack(items, choices, used, 0);
    }

    private static boolean backtrack(
        List<ItemStack> items,
        List<RecipeChoice> choices,
        boolean[] used,
        int itemIndex
    ) {
        // 所有物品都已匹配成功
        if (itemIndex == items.size()) {
            return true;
        }
        ItemStack currentItem = items.get(itemIndex);
        // 尝试将当前物品匹配到任意一个未使用的成分
        for (int i = 0; i < choices.size(); i++) {
            if (!used[i] && choices.get(i).test(currentItem)) {
                used[i] = true;
                if (backtrack(items, choices, used, itemIndex + 1)) {
                    return true;
                }
                used[i] = false; // 回溯
            }
        }
        return false;
    }
}
