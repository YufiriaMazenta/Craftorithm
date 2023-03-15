package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.ItemDisplayIcon;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class RecipeDisplayMenuHolder extends BukkitMenuHandler {

    private final Recipe recipe;
    private final InventoryType inventoryType;
    private final String invTitle;
    private RecipeListMenuHolder parentMenu;

    public RecipeDisplayMenuHolder(Recipe recipe) {
        this(recipe, null);
    }

    public RecipeDisplayMenuHolder(Recipe recipe, RecipeListMenuHolder parentMenu) {
        super();
        this.parentMenu = parentMenu;
        this.recipe = recipe;
        RecipeType recipeType = RecipeManager.getPluginRecipeType(recipe);
        switch (recipeType) {
            case SHAPED:
                setShapedRecipeMenu();
                inventoryType = InventoryType.WORKBENCH;
                invTitle = LangUtil.lang("menu.recipe_display.title.shaped");
                break;
            case SHAPELESS:
                setShapelessRecipeMenu();
                inventoryType = InventoryType.WORKBENCH;
                invTitle = LangUtil.lang("menu.recipe_display.title.shapeless");
                break;
            case COOKING:
            case RANDOM_COOKING:
                setCookingRecipeMenu();
                if (recipe instanceof FurnaceRecipe) {
                    inventoryType = InventoryType.FURNACE;
                    invTitle = LangUtil.lang("menu.recipe_display.title.furnace");
                }
                else if (recipe instanceof BlastingRecipe) {
                    inventoryType = InventoryType.BLAST_FURNACE;
                    invTitle = LangUtil.lang("menu.recipe_display.title.blasting");
                }
                else if (recipe instanceof SmokingRecipe) {
                    inventoryType = InventoryType.SMOKER;
                    invTitle = LangUtil.lang("menu.recipe_display.title.smoking");
                }
                else {
                    inventoryType = InventoryType.FURNACE;
                    invTitle = LangUtil.lang("menu.recipe_display.title.campfire");
                }
                break;
            case SMITHING:
                setSmithingRecipeMenu();
                inventoryType = InventoryType.SMITHING;
                invTitle = LangUtil.lang("menu.recipe_display.title.smithing");
                break;
            case STONE_CUTTING:
                setStoneCuttingRecipeMenu();
                inventoryType = InventoryType.CHEST;
                invTitle = LangUtil.lang("menu.recipe_display.title.stone_cutting");
                break;
            case ANVIL:
                setAnvilRecipeMenu();
                inventoryType = InventoryType.ANVIL;
                invTitle = LangUtil.lang("menu.recipe_display.title.anvil");
                break;
            default:
                invTitle = "Unknown Type";
                inventoryType = InventoryType.CHEST;
                getMenuIconMap().put(15, ItemDisplayIcon.icon(recipe.getResult()));
                break;
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, inventoryType, LangUtil.color(invTitle));
        for (Integer slot : getMenuIconMap().keySet()) {
            inventory.setItem(slot, getMenuIconMap().get(slot).getDisplay());
        }
        return inventory;
    }

    public void setShapedRecipeMenu() {
        ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
        String[] shape = shapedRecipe.getShape();
        Map<Character, ItemStack> itemMap = shapedRecipe.getIngredientMap();
        int columnStart = 1, line = 0;
        for (String shapeStr : shape) {
            for (int i = 0; i < shapeStr.length(); i++) {
                char c = shapeStr.charAt(i);
                if (itemMap.containsKey(c)) {
                    ItemStack item = itemMap.get(c).clone();
                    item.setAmount(1);
                    getMenuIconMap().put(columnStart, ItemDisplayIcon.icon(item));
                }
                columnStart ++;
            }
            line ++;
            columnStart = 1 + 3 * line;
        }
        getMenuIconMap().put(0, ItemDisplayIcon.icon(recipe.getResult()));
    }

    private void setShapelessRecipeMenu() {
        ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
        List<ItemStack> ingredientList = shapelessRecipe.getIngredientList();
        int slot = 1;
        for (ItemStack item : ingredientList) {
            ItemStack display = item.clone();
            display.setAmount(1);
            getMenuIconMap().put(slot, ItemDisplayIcon.icon(display));
            slot ++;
        }
        getMenuIconMap().put(0, ItemDisplayIcon.icon(recipe.getResult()));
    }

    private void setCookingRecipeMenu() {
        CookingRecipe<?> cookingRecipe = (CookingRecipe<?>) recipe;
        getMenuIconMap().put(2, ItemDisplayIcon.icon(recipe.getResult()));
        ItemStack input = cookingRecipe.getInput();
        input.setAmount(1);
        getMenuIconMap().put(0, ItemDisplayIcon.icon(input));
        getMenuIconMap().put(1, ItemDisplayIcon.icon(new ItemStack(Material.LAVA_BUCKET)));
    }

    private void setSmithingRecipeMenu() {
        SmithingRecipe smithingRecipe = (SmithingRecipe) recipe;
        getMenuIconMap().put(0, ItemDisplayIcon.icon(smithingRecipe.getBase().getItemStack()));
        getMenuIconMap().put(1, ItemDisplayIcon.icon(smithingRecipe.getAddition().getItemStack()));
        getMenuIconMap().put(2, ItemDisplayIcon.icon(smithingRecipe.getResult()));
    }

    private void setStoneCuttingRecipeMenu() {
        StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe) recipe;
        ItemStack input = stonecuttingRecipe.getInput();
        input.setAmount(1);
        getMenuIconMap().put(11, ItemDisplayIcon.icon(input));
        getMenuIconMap().put(15, ItemDisplayIcon.icon(stonecuttingRecipe.getResult()));
    }

    private void setAnvilRecipeMenu() {
        AnvilRecipe anvilRecipe = (AnvilRecipe) recipe;
        getMenuIconMap().put(0, ItemDisplayIcon.icon(anvilRecipe.getBase().getItem()));
        getMenuIconMap().put(1, ItemDisplayIcon.icon(anvilRecipe.getAddition().getItem()));
        getMenuIconMap().put(2, ItemDisplayIcon.icon(anvilRecipe.getResult()));
    }

    public RecipeListMenuHolder getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(RecipeListMenuHolder parentMenu) {
        this.parentMenu = parentMenu;
    }

}
