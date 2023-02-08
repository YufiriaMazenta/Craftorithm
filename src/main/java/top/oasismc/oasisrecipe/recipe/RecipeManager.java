package top.oasismc.oasisrecipe.recipe;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.OasisRecipeAPI;
import top.oasismc.oasisrecipe.cmd.subcmd.RemoveCommand;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static top.oasismc.oasisrecipe.util.FileUtil.getAllFiles;

public class RecipeManager {

    public static final Map<String, YamlFileWrapper> recipeFileNameMap = new HashMap<>();
    public static final Map<String, BiFunction<YamlConfiguration, String, Recipe>> recipeBuilderMap;
    public static final Map<String, BiFunction<YamlConfiguration, String, Recipe[]>> multipleRecipeBuilderMap;
    public static final Map<NamespacedKey, YamlConfiguration> recipeKeyConfigMap = new ConcurrentHashMap<>();
    public static final File recipeFileFolder = new File(OasisRecipe.getInstance().getDataFolder().getPath(), "recipes");

    static {
        recipeBuilderMap = new HashMap<>();
        recipeBuilderMap.put("shaped", RecipeBuilder::buildShapedRecipe);
        recipeBuilderMap.put("shapeless", RecipeBuilder::buildShapelessRecipe);
        recipeBuilderMap.put("cooking", RecipeBuilder::buildCookingRecipe);
        recipeBuilderMap.put("smithing", RecipeBuilder::buildSmithingRecipe);
        recipeBuilderMap.put("stone_cutting", RecipeBuilder::buildStoneCuttingRecipe);
        recipeBuilderMap.put("random_cooking", RecipeBuilder::buildCookingRecipe);

        multipleRecipeBuilderMap = new HashMap<>();
        multipleRecipeBuilderMap.put("shaped", RecipeBuilder::buildMultipleShapedRecipe);
        multipleRecipeBuilderMap.put("shapeless", RecipeBuilder::buildMultipleShapelessRecipe);
        multipleRecipeBuilderMap.put("cooking", RecipeBuilder::buildMultipleCookingRecipe);
        multipleRecipeBuilderMap.put("smithing", RecipeBuilder::buildMultipleSmithingRecipe);
        multipleRecipeBuilderMap.put("stone_cutting", RecipeBuilder::buildMultipleStoneCuttingRecipe);
        multipleRecipeBuilderMap.put("random_cooking", RecipeBuilder::buildMultipleCookingRecipe);
    }

    public static void loadRecipeManager() {
        loadRecipeFiles();
        reloadRecipes();
    }

    public static void loadRecipeFiles() {
        recipeFileNameMap.clear();
        if (!recipeFileFolder.exists()) {
            recipeFileFolder.mkdir();
        }
        List<File> allFiles = getAllFiles(recipeFileFolder);
        if (allFiles.size() < 1) {
            OasisRecipe.getInstance().saveResource("recipes/example_shaped.yml", false);
            allFiles.add(new File(recipeFileFolder, "example_shaped.yml"));
        }
        for (File file : allFiles) {
            String key = file.getName();
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            recipeFileNameMap.put(key, new YamlFileWrapper(file));
        }
    }

    public static void loadPluginRecipes() {
        Bukkit.resetRecipes();
        recipeKeyConfigMap.clear();
        for (String fileName : recipeFileNameMap.keySet()) {
            YamlConfiguration config = recipeFileNameMap.get(fileName).getConfig();
            boolean multiple = config.getBoolean("multiple", false);
            if (multiple) {
                Recipe[] multipleRecipes = newMultipleRecipe(config, fileName);
                for (Recipe recipe : multipleRecipes) {
                    NamespacedKey key = getRecipeKey(recipe);
                    regRecipe(key, recipe, config);
                }
            } else {
                Recipe recipe = newRecipe(config, fileName);
                regRecipe(getRecipeKey(recipe), recipe, config);
            }
        }
    }

    public static void regRecipe(NamespacedKey key, Recipe recipe, YamlConfiguration config) {
        Bukkit.addRecipe(recipe);
        recipeKeyConfigMap.put(key, config);
    }

    public static Recipe newRecipe(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeType = config.getString("type", "shaped");
        return recipeBuilderMap.get(recipeType).apply(config, key);
    }

    public static Recipe[] newMultipleRecipe(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeType = config.getString("type", "shaped");
        return multipleRecipeBuilderMap.get(recipeType).apply(config, key);
    }

    public static void reloadRecipes() {
        loadPluginRecipes();
        loadRecipeFromOtherPlugins();
        removeRecipes();
    }

    private static void loadRecipeFromOtherPlugins() {
        Map<Plugin, List<Recipe>> pluginRecipeMap = OasisRecipeAPI.INSTANCE.getPluginRegRecipeMap();
        for (Plugin plugin : pluginRecipeMap.keySet()) {
            for (Recipe recipe : pluginRecipeMap.get(plugin)) {
                OasisRecipe.getInstance().getServer().addRecipe(recipe);
            }
        }
    }

    private static void removeRecipes() {
        List<String> removedRecipes = RemoveCommand.getRemovedRecipeConfig().getConfig().getStringList("recipes");
        if (OasisRecipe.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe", false)) {
            for (NamespacedKey key : ((RemoveCommand) RemoveCommand.INSTANCE).getRecipeMap().keySet()) {
                if (key.getNamespace().equals("minecraft")) {
                    if (removedRecipes.contains(key.toString()))
                        continue;
                    removedRecipes.add(key.toString());
                }
            }
        }
        ((RemoveCommand) RemoveCommand.INSTANCE).removeRecipes(removedRecipes);
    }

    public static YamlConfiguration getRecipeConfig(Recipe recipe) {
        NamespacedKey key = getRecipeKey(recipe);
        return recipeKeyConfigMap.get(key);
    }

    public static NamespacedKey getRecipeKey(Recipe recipe) {
        try {
            Class<?> recipeClass = Class.forName(recipe.getClass().getName());
            Method getKeyMethod = recipeClass.getMethod("getKey");
            return (NamespacedKey) getKeyMethod.invoke(recipe);
        } catch (Exception e) {
            return null;
        }

    }

}
