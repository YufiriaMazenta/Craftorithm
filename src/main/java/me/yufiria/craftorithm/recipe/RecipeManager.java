package me.yufiria.craftorithm.recipe;

import me.yufiria.craftorithm.Craftorithm;
import me.yufiria.craftorithm.CraftorithmAPI;
import me.yufiria.craftorithm.cmd.subcmd.RemoveCommand;
import me.yufiria.craftorithm.config.YamlFileWrapper;
import me.yufiria.craftorithm.recipe.custom.AnvilRecipe;
import me.yufiria.craftorithm.recipe.custom.CustomRecipe;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.ContainerUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import static me.yufiria.craftorithm.util.FileUtil.getAllFiles;

public class RecipeManager {

    private static final Map<String, YamlFileWrapper> recipeFileMap = new HashMap<>();
    private static final Map<RecipeType, BiFunction<YamlConfiguration, String, Recipe>> recipeBuilderMap;
    private static final Map<RecipeType, BiFunction<YamlConfiguration, String, Recipe[]>> multipleRecipeBuilderMap;
    private static final Map<NamespacedKey, YamlConfiguration> recipeKeyConfigMap = new ConcurrentHashMap<>();
    private static final File recipeFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "recipes");
    private static final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private static final Map<NamespacedKey, AnvilRecipe> anvilRecipeMap;

    static {
        recipeBuilderMap = new HashMap<>();
        recipeBuilderMap.put(RecipeType.SHAPED, RecipeFactory::shapedRecipe);
        recipeBuilderMap.put(RecipeType.SHAPELESS, RecipeFactory::shapelessRecipe);
        recipeBuilderMap.put(RecipeType.COOKING, RecipeFactory::cookingRecipe);
        recipeBuilderMap.put(RecipeType.SMITHING, RecipeFactory::smithingRecipe);
        recipeBuilderMap.put(RecipeType.STONE_CUTTING, RecipeFactory::stoneCuttingRecipe);
        recipeBuilderMap.put(RecipeType.RANDOM_COOKING, RecipeFactory::cookingRecipe);
        recipeBuilderMap.put(RecipeType.ANVIL, RecipeFactory::anvilRecipe);

        multipleRecipeBuilderMap = new HashMap<>();
        multipleRecipeBuilderMap.put(RecipeType.SHAPED, RecipeFactory::multipleShapedRecipe);
        multipleRecipeBuilderMap.put(RecipeType.SHAPELESS, RecipeFactory::multipleShapelessRecipe);
        multipleRecipeBuilderMap.put(RecipeType.COOKING, RecipeFactory::multipleCookingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.SMITHING, RecipeFactory::multipleSmithingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.STONE_CUTTING, RecipeFactory::multipleStoneCuttingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.RANDOM_COOKING, RecipeFactory::multipleCookingRecipe);

        recipeUnlockMap = new ConcurrentHashMap<>();

        anvilRecipeMap = new ConcurrentHashMap<>();
    }

    public static void loadRecipeManager() {
        loadRecipeFiles();
        reloadRecipes();
    }

    public static void loadRecipeFiles() {
        recipeFileMap.clear();
        if (!recipeFileFolder.exists()) {
            boolean mkdirResult = recipeFileFolder.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = getAllFiles(recipeFileFolder);
        if (allFiles.size() < 1) {
            saveDefConfigFile(allFiles);
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(recipeFileFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            recipeFileMap.put(key, new YamlFileWrapper(file));
        }
    }

    public static void loadPluginRecipes() {
        Bukkit.resetRecipes();
        recipeKeyConfigMap.clear();
        for (String fileName : recipeFileMap.keySet()) {
            try {
                YamlConfiguration config = recipeFileMap.get(fileName).getConfig();
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
                LangUtil.info("load.recipe_load_exception", ContainerUtil.newHashMap("<recipe_name>", fileName));
                e.printStackTrace();
            }
        }
    }

    public static void regRecipe(NamespacedKey key, Recipe recipe, YamlConfiguration config) {
        if (recipe instanceof CustomRecipe) {
            switch (((CustomRecipe) recipe).getRecipeType()) {
                case ANVIL:
                    anvilRecipeMap.put(key, (AnvilRecipe) recipe);
                    break;
            }
        } else {
            Bukkit.addRecipe(recipe);
            boolean defUnlockCondition = Craftorithm.getInstance().getConfig().getBoolean("all_recipe_unlocked", false);
            if (config.contains("unlock")) {
                recipeUnlockMap.put(key, config.getBoolean("unlock", defUnlockCondition));
            } else {
                recipeUnlockMap.put(key, defUnlockCondition);
            }
        }
        recipeKeyConfigMap.put(key, config);
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
        Map<Plugin, List<Recipe>> pluginRecipeMap = CraftorithmAPI.INSTANCE.getPluginRegRecipeMap();
        for (Plugin plugin : pluginRecipeMap.keySet()) {
            for (Recipe recipe : pluginRecipeMap.get(plugin)) {
                Craftorithm.getInstance().getServer().addRecipe(recipe);
            }
        }
    }

    private static void removeRecipes() {
        List<String> removedRecipes = RemoveCommand.getRemovedRecipeConfig().getConfig().getStringList("recipes");
        if (Craftorithm.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe", false)) {
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
        if (recipe instanceof CustomRecipe) {
            return ((CustomRecipe) recipe).getKey();
        }
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

    public static Map<String, YamlFileWrapper> getRecipeFileMap() {
        return recipeFileMap;
    }

    public static Map<NamespacedKey, AnvilRecipe> getAnvilRecipeMap() {return anvilRecipeMap;}

    public static AnvilRecipe matchAnvilRecipe(ItemStack base, ItemStack addition) {
        if (base == null || addition == null)
            return null;
        AtomicReference<AnvilRecipe> anvilRecipe = new AtomicReference<>();
        anvilRecipeMap.forEach((key, recipe) -> {
            if (recipe.checkSource(base, addition)) {
                anvilRecipe.set(recipe);
            }
        });
        return anvilRecipe.get();
    }

    private static void saveDefConfigFile(List<File> allFiles) {
        Craftorithm.getInstance().saveResource("recipes/example_shaped.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_shapeless.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_cooking.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_smithing.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_stone_cutting.yml", false);
        Craftorithm.getInstance().saveResource("recipes/example_random_cooking.yml", false);
        allFiles.add(new File(recipeFileFolder, "example_shaped.yml"));
        allFiles.add(new File(recipeFileFolder, "example_shapeless.yml"));
        allFiles.add(new File(recipeFileFolder, "example_cooking.yml"));
        allFiles.add(new File(recipeFileFolder, "example_smithing.yml"));
        allFiles.add(new File(recipeFileFolder, "example_stone_cutting.yml"));
        allFiles.add(new File(recipeFileFolder, "example_random_cooking.yml"));
    }

}
