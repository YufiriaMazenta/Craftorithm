package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.github.yufiriamazenta.craftorithm.recipe.RecipeType.SHAPED;
import static com.github.yufiriamazenta.craftorithm.recipe.RecipeType.SHAPELESS;

public class CraftingRecipeCreator extends UnlockableRecipeCreator {

    public CraftingRecipeCreator(@NotNull Player player, RecipeType recipeType, @NotNull String groupName, @NotNull String recipeName) {
        super(player, recipeType, groupName, recipeName);
        Preconditions.checkArgument(
            recipeType.equals(SHAPED) || recipeType.equals(SHAPELESS),
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
                        RecipeGroup recipeGroup = getRecipeGroup(groupName);
                        ConfigWrapper recipeConfig = recipeGroup.recipeGroupConfig();
                        ConfigurationSection recipeCfgSection = recipeConfig.config().createSection(recipeName);
                        RecipeType type = recipeType();
                        if (type.equals(SHAPED)) {
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
                            recipeCfgSection.set("type", "shaped");
                            recipeCfgSection.set("source.shape", shape);
                            recipeCfgSection.set("source.ingredients", itemNameMap);
                        } else if (type.equals(SHAPELESS)) {
                            sourceList.removeIf(String::isEmpty);
                            recipeCfgSection.set("type", "shapeless");
                            recipeCfgSection.set("source.ingredients", sourceList);
                        }
                        recipeCfgSection.set("unlock", unlock());
                        recipeCfgSection.set("result", resultName);
                        recipeConfig.saveConfig();
                        recipeConfig.reloadConfig();
                        recipeGroup.updateAndLoadRecipeGroup();
                        event.getWhoClicked().closeInventory();
                        sendSuccessMsg();
                    })
                );
                return layoutMap;
            })));
    }

}
