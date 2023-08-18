package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.manager.IRecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.FileUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.item.Item;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DefRecipeManager implements IRecipeManager {

    INSTANCE;
    
    private final Map<String, YamlConfigWrapper> recipeFileMap = new HashMap<>();
    private final File recipeFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "recipes");

    DefRecipeManager() {
        loadRecipeFiles();
        addRecipe();
    }

    public void loadRecipeFiles() {
        recipeFileMap.clear();
        if (!recipeFileFolder.exists()) {
            boolean mkdirResult = recipeFileFolder.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = FileUtil.getAllFiles(recipeFileFolder);
        if (allFiles.size() < 1) {
            saveDefConfigFile(allFiles);
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(recipeFileFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            recipeFileMap.put(key, new YamlConfigWrapper(file));
        }
    }

    public void loadCraftorithmRecipes() {
    }

    public void resetRecipes() {
    }

    public void addRecipe() {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
        ItemStack test = new ItemStack(Material.STONE_AXE);
        Ingredient ingredient = Ingredient.of(CraftItemStack.asNMSCopy(test));
        for (int i = 0; i < 9; i++) {
            ingredients.set(i, ingredient);
        }
        Recipe recipe = new ShapedRecipe(
                new ResourceLocation(Craftorithm.getInstance().getName().toLowerCase(), "test"),
                "example",
                CraftingBookCategory.BUILDING,
                3,
                3,
                ingredients,
                CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_AXE))
        );
        MinecraftServer.getServer().getRecipeManager().addRecipe(recipe);
    }

    public void loadRecipes() {
    }

    private void removeRecipes() {

    }

    public NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null)
            return null;
        if (recipe instanceof CustomRecipe) {
            return ((CustomRecipe) recipe).getKey();
        }
        return ((Keyed) recipe).getKey();
    }

    public Map<String, YamlConfigWrapper> getRecipeFileMap() {
        return recipeFileMap;
    }

    public File getRecipeFileFolder() {
        return recipeFileFolder;
    }

    private void saveDefConfigFile(List<File> allFiles) {
        Craftorithm.getInstance().saveResource("recipes/example_shaped.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_shapeless.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_cooking.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_smithing.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_stone_cutting.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_random_cooking.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_anvil.yml", false);
        allFiles.add(new File(recipeFileFolder, "example_shaped.yml"));
        allFiles.add(new File(recipeFileFolder, "example_shapeless.yml"));
        allFiles.add(new File(recipeFileFolder, "example_cooking.yml"));
        allFiles.add(new File(recipeFileFolder, "example_smithing.yml"));
        allFiles.add(new File(recipeFileFolder, "example_stone_cutting.yml"));
        allFiles.add(new File(recipeFileFolder, "example_random_cooking.yml"));
        allFiles.add(new File(recipeFileFolder, "example_anvil.yml"));
    }

}
