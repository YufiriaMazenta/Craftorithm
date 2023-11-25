package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftingRecipeCreatorMenu extends StoredMenu {

    private RecipeType recipeType;

    public CraftingRecipeCreatorMenu(Player player, RecipeType recipeType) {
        super(player, null);
        this.recipeType = recipeType;
        List<String> layout = Arrays.asList(
            "#########",
            "#   #***#",
            "#   A* *#",
            "#   #***#",
            "#########"
        );
        Map<Character, Icon> layoutMap = new HashMap<>();
        layoutMap.put('#', new Icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.frame")));
        layoutMap.put('*', new Icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.result_frame")));
        layoutMap.put('A', new Icon(
            Material.CRAFTING_TABLE,
            LangUtil.langMsg("menu.recipe_creator.icon.confirm"),
            event -> {
                Map<Integer, ItemStack> storedItems = this.storedItems();
                ItemStack result = storedItems.get(24);
                if (ItemUtil.isAir(result)) {
                    LangUtil.sendLang(event.getWhoClicked(), "command.create.null_result");
                    return;
                }
                String resultName = ItemUtils.getItemName(result, false);
                int[] sourceSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                List<String> sourceList = new ArrayList<>();
                for (int slot : sourceSlots) {
                    ItemStack source = storedItems.get(slot);
                    if (ItemUtil.isAir(source)) {
                        sourceList.add("");
                        continue;
                    }
                    String sourceName = ItemUtils.getItemName(source, true);
                    sourceList.add(sourceName);
                }
            }));
    }



}
