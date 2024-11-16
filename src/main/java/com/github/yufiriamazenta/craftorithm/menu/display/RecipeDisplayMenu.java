package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecipeDisplayMenu extends Menu {

    private final Recipe recipe;
    private final InventoryType inventoryType;
    private final String invTitle;
    private final Menu parentMenu;

    public RecipeDisplayMenu(Player player, Recipe recipe, Menu parentMenu) {
        super(player);

        this.parentMenu = parentMenu;
        this.recipe = recipe;
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(this.recipe);
        switch (recipeType) {
            case SHAPED:
                setShapedRecipeMenu();
                inventoryType = InventoryType.WORKBENCH;
                invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_SHAPED.value(player);
                break;
            case SHAPELESS:
                setShapelessRecipeMenu();
                inventoryType = InventoryType.WORKBENCH;
                invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_SHAPELESS.value(player);
                break;
            case COOKING:
            case RANDOM_COOKING:
                setCookingRecipeMenu();
                switch (recipe) {
                    case FurnaceRecipe furnaceRecipe -> {
                        inventoryType = InventoryType.FURNACE;
                        invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_FURNACE.value(player);
                    }
                    case BlastingRecipe blastingRecipe -> {
                        inventoryType = InventoryType.BLAST_FURNACE;
                        invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_BLASTING.value(player);
                    }
                    case SmokingRecipe smokingRecipe -> {
                        inventoryType = InventoryType.SMOKER;
                        invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_SMOKING.value(player);
                    }
                    case null, default -> {
                        inventoryType = InventoryType.FURNACE;
                        invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_CAMPFIRE.value(player);
                    }
                }
                break;
            case SMITHING:
                setSmithingRecipeMenu();
                inventoryType = InventoryType.SMITHING;
                invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_SMITHING.value(player);
                break;
            case STONE_CUTTING:
                setStoneCuttingRecipeMenu();
                inventoryType = InventoryType.CHEST;
                invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_STONE_CUTTING.value(player);
                break;
            case POTION:
                setPotionMixRecipeMenu();
                inventoryType = InventoryType.BREWING;
                invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_POTION.value(player);
                break;
            case ANVIL:
                setAnvilRecipeMenu();
                inventoryType = InventoryType.ANVIL;
                invTitle = Languages.MENU_RECIPE_DISPLAY_TITLE_ANVIL.value(player);
                break;
            default:
                invTitle = "Unknown Type";
                inventoryType = InventoryType.CHEST;
                slotMap.put(15, new Icon(recipe.getResult()));
                break;
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, inventoryType, BukkitTextProcessor.color(invTitle));
        for (Integer slot : slotMap.keySet()) {
            inventory.setItem(slot, slotMap.get(slot).display());
        }
        return inventory;
    }

    @Override
    public Icon onClick(int slot, InventoryClickEvent event) {
        BukkitMsgSender.INSTANCE.debug(event.getClick().name());
        return super.onClick(slot, event);
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
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
                    ItemStack item = itemMap.get(c);
                    if (item == null)
                        item = new ItemStack(Material.AIR);
                    else
                        item = item.clone();
                    slotMap.put(columnStart, new Icon(item));
                }
                columnStart ++;
            }
            line ++;
            columnStart = 1 + 3 * line;
        }
        slotMap.put(0, new Icon(recipe.getResult()));
    }

    private void setShapelessRecipeMenu() {
        ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
        List<ItemStack> ingredientList = shapelessRecipe.getIngredientList();
        int slot = 1;
        for (ItemStack item : ingredientList) {
            ItemStack display = item.clone();
            display.setAmount(1);
            slotMap.put(slot, new Icon(display));
            slot ++;
        }
        slotMap.put(0, new Icon(recipe.getResult()));
    }

    private void setCookingRecipeMenu() {
        CookingRecipe<?> cookingRecipe = (CookingRecipe<?>) recipe;
        slotMap.put(2, new Icon(recipe.getResult()));
        ItemStack input = cookingRecipe.getInput();
        input.setAmount(1);
        slotMap.put(0, new Icon(input));
        slotMap.put(1, new Icon(new ItemStack(Material.LAVA_BUCKET)));
    }

    private void setSmithingRecipeMenu() {
        SmithingRecipe smithingRecipe = (SmithingRecipe) recipe;
        slotMap.put(1, new Icon(smithingRecipe.getBase().getItemStack()));
        slotMap.put(2, new Icon(smithingRecipe.getAddition().getItemStack()));
        slotMap.put(3, new Icon(smithingRecipe.getResult()));
        if (recipe instanceof SmithingTransformRecipe) {
            slotMap.put(0, new Icon(((SmithingTransformRecipe) recipe).getTemplate().getItemStack()));
        } else if (recipe instanceof SmithingTrimRecipe){
            slotMap.put(0, new Icon(((SmithingTrimRecipe) recipe).getTemplate().getItemStack()));
        }
    }

    private void setStoneCuttingRecipeMenu() {
        StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe) recipe;
        ItemStack input = stonecuttingRecipe.getInput();
        input.setAmount(1);
        slotMap.put(11, new Icon(input));
        slotMap.put(15, new Icon(stonecuttingRecipe.getResult()));
    }

    private void setPotionMixRecipeMenu() {
        PotionMixRecipe potionMixRecipe = (PotionMixRecipe) recipe;
        ItemStack input = potionMixRecipe.input().getItemStack();
        ItemStack ingredient = potionMixRecipe.ingredient().getItemStack();
        ItemStack result = potionMixRecipe.getResult();
        slotMap.put(0, new Icon(input));
        slotMap.put(3, new Icon(ingredient));
        slotMap.put(2, new Icon(result));
    }

    private void setAnvilRecipeMenu() {
        AnvilRecipe anvilRecipe = (AnvilRecipe) recipe;
        ItemStack base = anvilRecipe.base();
        ItemStack addition = anvilRecipe.addition();
        ItemStack result = anvilRecipe.getResult();
        slotMap.put(0, new Icon(base));
        slotMap.put(1, new Icon(addition));
        slotMap.put(2, new Icon(result));
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (parentMenu != null) {
            CrypticLibBukkit.scheduler().runTask(
                Craftorithm.instance(),
                () -> {
                    InventoryType type = event.getPlayer().getOpenInventory().getType();
                    List<InventoryType> typeWhenNotOpenInv = Arrays.asList(InventoryType.CRAFTING, InventoryType.CREATIVE);
                    if (!typeWhenNotOpenInv.contains(type))
                        return;
                    parentMenu.openMenu();
                }
            );
        }
    }

}
