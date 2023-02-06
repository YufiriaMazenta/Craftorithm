package top.oasismc.oasisrecipe.recipe;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;
import top.oasismc.oasisrecipe.recipe.object.vanilla.RecipeBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import static top.oasismc.oasisrecipe.util.FileUtil.getAllFiles;

public class RecipeManager {

    public static final Map<String, YamlFileWrapper> recipeFileMap = new HashMap<>();
    public static final File recipeFileFolder = new File(OasisRecipe.getInstance().getDataFolder().getPath(), "recipes");
    public static final Map<String, BiFunction<YamlConfiguration, NamespacedKey, Recipe>> recipeBuilderMap;

    static {
        recipeBuilderMap = new HashMap<>();
        recipeBuilderMap.put("shaped", RecipeBuilder::buildShapedRecipe);
    }

    public static void loadRecipeManager() {
        loadRecipeFiles();
        loadRecipes();
    }

    public static void loadRecipeFiles() {
        recipeFileMap.clear();
        if (!recipeFileFolder.exists()) {
            recipeFileFolder.mkdir();
        }
        List<File> allFiles = getAllFiles(recipeFileFolder);
        if (allFiles.size() < 1) {
            OasisRecipe.getInstance().saveResource("recipes/example_recipe.yml", false);
            allFiles.add(new File(recipeFileFolder, "example_recipe.yml"));
        }
        for (File file : allFiles) {
            String key = file.getName();
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            recipeFileMap.put(key, new YamlFileWrapper(file));
        }
    }

    public static void loadRecipes() {
        Bukkit.resetRecipes();
        for (String fileName : recipeFileMap.keySet()) {
            YamlConfiguration config = recipeFileMap.get(fileName).getConfig();
            Bukkit.addRecipe(newRecipe(config, fileName));
        }
    }

    public static Recipe newRecipe(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        String recipeType = config.getString("type", "shaped");
        return recipeBuilderMap.get(recipeType).apply(config, namespacedKey);
    }

}
