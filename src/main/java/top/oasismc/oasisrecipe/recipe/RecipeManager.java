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
import top.oasismc.oasisrecipe.util.LangUtil;
import top.oasismc.oasisrecipe.util.MapUtil;

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
    public static final Map<RecipeType, BiFunction<YamlConfiguration, String, Recipe>> recipeBuilderMap;
    public static final Map<RecipeType, BiFunction<YamlConfiguration, String, Recipe[]>> multipleRecipeBuilderMap;
    public static final Map<NamespacedKey, YamlConfiguration> recipeKeyConfigMap = new ConcurrentHashMap<>();
    public static final File recipeFileFolder = new File(OasisRecipe.getInstance().getDataFolder().getPath(), "recipes");
    public static final Map<NamespacedKey, Boolean> recipeUnlockMap;

    static {
        recipeBuilderMap = new HashMap<>();
        recipeBuilderMap.put(RecipeType.SHAPED, RecipeBuilder::buildShapedRecipe);
        recipeBuilderMap.put(RecipeType.SHAPELESS, RecipeBuilder::buildShapelessRecipe);
        recipeBuilderMap.put(RecipeType.COOKING, RecipeBuilder::buildCookingRecipe);
        recipeBuilderMap.put(RecipeType.SMITHING, RecipeBuilder::buildSmithingRecipe);
        recipeBuilderMap.put(RecipeType.STONE_CUTTING, RecipeBuilder::buildStoneCuttingRecipe);
        recipeBuilderMap.put(RecipeType.RANDOM_COOKING, RecipeBuilder::buildCookingRecipe);

        multipleRecipeBuilderMap = new HashMap<>();
        multipleRecipeBuilderMap.put(RecipeType.SHAPED, RecipeBuilder::buildMultipleShapedRecipe);
        multipleRecipeBuilderMap.put(RecipeType.SHAPELESS, RecipeBuilder::buildMultipleShapelessRecipe);
        multipleRecipeBuilderMap.put(RecipeType.COOKING, RecipeBuilder::buildMultipleCookingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.SMITHING, RecipeBuilder::buildMultipleSmithingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.STONE_CUTTING, RecipeBuilder::buildMultipleStoneCuttingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.RANDOM_COOKING, RecipeBuilder::buildMultipleCookingRecipe);

        recipeUnlockMap = new ConcurrentHashMap<>();
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
            String key = file.getPath().substring(recipeFileFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            recipeFileNameMap.put(key, new YamlFileWrapper(file));
        }
    }

    public static void loadPluginRecipes() {
        Bukkit.resetRecipes();
        recipeKeyConfigMap.clear();
        for (String fileName : recipeFileNameMap.keySet()) {
            try {
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
            } catch (Exception e) {
                LangUtil.info("load.recipe_load_exception", MapUtil.newHashMap("<recipe_name>", fileName));
                e.printStackTrace();
            }
        }
    }

    public static void regRecipe(NamespacedKey key, Recipe recipe, YamlConfiguration config) {
        Bukkit.addRecipe(recipe);
        recipeKeyConfigMap.put(key, config);
        boolean defUnlockCondition = OasisRecipe.getInstance().getConfig().getBoolean("all_recipe_unlocked", false);
        if (config.contains("unlock")) {
            recipeUnlockMap.put(key, config.getBoolean("unlock", defUnlockCondition));
        } else {
            recipeUnlockMap.put(key, defUnlockCondition);
        }
    }

    public static Recipe newRecipe(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeTypeStr = config.getString("type", "shaped");
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
        return recipeBuilderMap.get(recipeType).apply(config, key);
    }

    public static Recipe[] newMultipleRecipe(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeTypeStr = config.getString("type", "shaped");
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
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
        return key != null ? recipeKeyConfigMap.get(key) : null;
    }

    public static NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null)
            return null;
        try {
            Class<?> recipeClass = Class.forName(recipe.getClass().getName());
            Method getKeyMethod = recipeClass.getMethod("getKey");
            return (NamespacedKey) getKeyMethod.invoke(recipe);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<NamespacedKey, Boolean> getRecipeUnlockMap() {
        return recipeUnlockMap;
    }

}
