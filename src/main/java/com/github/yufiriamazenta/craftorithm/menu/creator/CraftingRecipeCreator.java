package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.google.common.base.Preconditions;
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

public class CraftingRecipeCreator extends UnlockableRecipeCreator {

    public CraftingRecipeCreator(@NotNull Player player, RecipeType recipeType, @NotNull String recipeName) {
        super(player, recipeType, recipeName);
        Preconditions.checkArgument(
            recipeType.equals(RecipeType.SHAPED) || recipeType.equals(RecipeType.SHAPELESS),
            "Crafting recipe only allow shaped and shapeless type"
        );
        setDisplay(new MenuDisplay(
            title(),
            new MenuLayout(Arrays.asList(
                "####F####",
                "#   #***#",
                "#   A* *#",
                "#   #***#",
                "#########"
            ), () -> {
                Map<Character, Icon> layoutMap = new HashMap<>();
                layoutMap.put('#', getFrameIcon());
                layoutMap.put('*', getResultFrameIcon());
                layoutMap.put('F', getUnlockIcon());
                layoutMap.put('A', new Icon(
                    Material.CRAFTING_TABLE,
                    Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player),
                    event -> {
                        StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                        Map<Integer, ItemStack> storedItems = Objects.requireNonNull(creator).storedItems();
                        ItemStack result = storedItems.get(24);
                        if (ItemUtil.isAir(result)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                            return;
                        }
                        String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                        int[] sourceSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                        List<String> sourceList = new ArrayList<>();
                        for (int slot : sourceSlots) {
                            ItemStack source = storedItems.get(slot);
                            if (ItemUtil.isAir(source)) {
                                sourceList.add("");
                                continue;
                            }
                            String sourceName = ItemUtils.matchItemNameOrCreate(source, true);
                            sourceList.add(sourceName);
                        }
                        ConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                        switch (recipeType()) {
                            case SHAPED:
                                List<String> shape = new ArrayList<>(Arrays.asList("abc", "def", "ghi"));
                                Map<Character, String> itemNameMap = new HashMap<>();
                                char[] tmp = "abcdefghi".toCharArray();
                                for (int i = 0; i < sourceList.size(); i++) {
                                    if (sourceList.get(i).isEmpty()) {
                                        continue;
                                    }
                                    itemNameMap.put(tmp[i], sourceList.get(i));
                                }
                                //删除无映射的字符
                                for (int i = 0; i < shape.size(); i++) {
                                    String s = shape.get(i);
                                    for (char c : s.toCharArray()) {
                                        if (!itemNameMap.containsKey(c)) {
                                            s = s.replace(c, ' ');
                                        }
                                    }
                                    shape.set(i, s);
                                }
                                shape.removeIf(s -> s.trim().isEmpty());
                                recipeConfig.set("type", "shaped");
                                recipeConfig.set("shape", shape);
                                recipeConfig.set("source", itemNameMap);
                                break;
                            case SHAPELESS:
                                sourceList.removeIf(String::isEmpty);
                                recipeConfig.set("type", "shapeless");
                                recipeConfig.set("source", sourceList);
                                break;
                        }
                        recipeConfig.set("unlock", unlock());
                        recipeConfig.set("result", resultName);
                        recipeConfig.saveConfig();
                        recipeConfig.reloadConfig();
                        regRecipeGroup(recipeConfig);
                        event.getWhoClicked().closeInventory();
                        sendSuccessMsg(event.getWhoClicked(), recipeName);
                    })
                );
                return layoutMap;
            })));
    }

}
