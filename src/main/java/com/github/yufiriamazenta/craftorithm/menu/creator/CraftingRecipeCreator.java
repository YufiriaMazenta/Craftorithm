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
import java.util.function.Supplier;

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
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('*', this::getResultFrameIcon);
                layoutMap.put('F', this::getUnlockIcon);
                layoutMap.put('A', () -> new Icon(
                    Material.CRAFTING_TABLE,
                    Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player)
                    ).setClickAction(event -> {
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
                        boolean allEmpty = true;
                        for (String choice : sourceList) {
                            if (!choice.isEmpty()) {
                                allEmpty = false;
                                break;
                            }
                        }
                        if (allEmpty) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                            return;
                        }
                        ConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                        switch (recipeType()) {
                            case SHAPED:
                                Map<String, Character> itemRepeatMap = new HashMap<>();
                                List<String> shape = new ArrayList<>();
                                Map<Character, String> itemNameMap = new HashMap<>();
                                char[] tmp = "         ".toCharArray(); //9个空格
                                char c = 'a';
                                for(int i = 0; i < sourceList.size(); i++){
                                    String sourceItem = sourceList.get(i);
                                    if (sourceItem.isEmpty()) {
                                        continue;
                                    }
                                    if (!itemRepeatMap.containsKey(sourceItem)){
                                        itemRepeatMap.put(sourceList.get(i),c);
                                        c++;
                                    }
                                    tmp[i] = itemRepeatMap.get(sourceItem);
                                }
                                for (int i = 0; i < 9; i += 3) {
                                    shape.add(new String(tmp,i,3));
                                }
                                c = 'a';
                                for (String key : itemRepeatMap.keySet()){
                                    itemNameMap.put(c,key);
                                    c++;
                                }
                                shape.removeIf(s -> s.trim().isEmpty());
                                removeEmptyColumn(shape);

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

    private void removeEmptyColumn(List<String> shape) {
        boolean[] empty = new boolean[3];
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            empty[i] = shape.stream().allMatch(s -> s.charAt(finalI) == ' ');
        }
        if (empty[0]) {
            if (empty[1]) {
                if (!empty[2]) {
                    shape.replaceAll(s -> s.substring(2));
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(1, 2));
                } else {
                    shape.replaceAll(s -> s.substring(1));
                }
            }
        } else {
            if (empty[1]) {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, 1));
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, 2));
                }
            }
        }
    }

}
