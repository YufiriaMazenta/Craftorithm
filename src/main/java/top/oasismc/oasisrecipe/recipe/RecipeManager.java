package top.oasismc.oasisrecipe.recipe;

import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static top.oasismc.oasisrecipe.util.FileUtil.getAllFiles;

public class RecipeManager {

    public static final Map<String, YamlFileWrapper> recipeFileMap = new HashMap<>();
    public static final File recipeFileFolder = new File(OasisRecipe.getInstance().getDataFolder().getPath(), "recipes");

    public static void loadRecipes() {
        loadRecipeFiles();
    }

    private static void loadRecipeFiles() {
        if (!recipeFileFolder.exists()) {
            recipeFileFolder.mkdir();
        }
        List<File> allFiles = getAllFiles(recipeFileFolder);
        if (allFiles.size() < 1) {
            OasisRecipe.getInstance().saveResource("recipes/example_recipe.yml", false);
        }
        for (File file : allFiles) {
            String key = file.getPath();
            key = key.substring(28).toLowerCase(Locale.ROOT);
            key = key.substring(0, key.indexOf("."));
            key = key.replace('\\', '/');
            recipeFileMap.put(key, new YamlFileWrapper(file));
        }
    }

}
