package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.cmd.subcmd.RemoveRecipeCommand;
import com.github.yufiriamazenta.craftorithm.config.YamlFileWrapper;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.FileUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeManager {

    private static final Map<String, YamlFileWrapper> recipeFileMap = new HashMap<>();
    private static final Map<NamespacedKey, YamlConfiguration> recipeKeyConfigMap = new ConcurrentHashMap<>();
    private static final File recipeFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "recipes");
    private static final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private static final Map<NamespacedKey, AnvilRecipe> anvilRecipeMap;
    private static final Map<Recipe, RecipeType> recipeTypeMap;
    private static final Map<NamespacedKey, Recipe> recipeKeyMap;

    static {
        recipeUnlockMap = new ConcurrentHashMap<>();

        anvilRecipeMap = new ConcurrentHashMap<>();

        recipeTypeMap = new ConcurrentHashMap<>();

        recipeKeyMap = new ConcurrentHashMap<>();
    }

    public static void loadRecipeManager() {
        loadRecipeFiles();
    }

    public static void loadRecipeFiles() {
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
            recipeFileMap.put(key, new YamlFileWrapper(file));
        }
    }

    public static void loadCraftorithmRecipes() {
        resetRecipes();
        for (String fileName : recipeFileMap.keySet()) {
            try {
                YamlConfiguration config = recipeFileMap.get(fileName).getConfig();
                boolean multiple = config.getBoolean("multiple", false);
                if (multiple) {
                    Recipe[] multipleRecipes = RecipeFactory.newMultipleRecipe(config, fileName);
                    for (Recipe recipe : multipleRecipes) {
                        NamespacedKey key = getRecipeKey(recipe);
                        regRecipe(key, recipe, config);
                    }
                } else {
                    Recipe recipe = RecipeFactory.newRecipe(config, fileName);
                    regRecipe(getRecipeKey(recipe), recipe, config);
                }
            } catch (Exception e) {
                LangUtil.info("load.recipe_load_exception", ContainerUtil.newHashMap("<recipe_name>", fileName));
                e.printStackTrace();
            }
        }
    }

    public static void resetRecipes() {
        CraftorithmAPI.INSTANCE.getPluginRegRecipeMap().forEach((plugin, recipes) -> {
            if (plugin.equals(NamespacedKey.MINECRAFT))
                return;
            List<String> recipeKeyList = new ArrayList<>();
            for (Recipe recipe : recipes) {
                recipeKeyList.add(getRecipeKey(recipe).toString());
            }
            ((RemoveRecipeCommand) RemoveRecipeCommand.INSTANCE).removeRecipes(recipeKeyList, false);
        });
        List<String> recipeKeyList = new ArrayList<>();
        for (NamespacedKey key : getPluginRecipeKeys()) {
            recipeKeyList.add(key.toString());
        }
        ((RemoveRecipeCommand) RemoveRecipeCommand.INSTANCE).removeRecipes(recipeKeyList, false);
        recipeTypeMap.clear();
        recipeKeyMap.clear();
        recipeUnlockMap.clear();
        anvilRecipeMap.clear();
        recipeKeyConfigMap.clear();
    }

    public static void regRecipe(NamespacedKey key, Recipe recipe, YamlConfiguration config) {
        if (recipe instanceof CustomRecipe) {
            switch (((CustomRecipe) recipe).getRecipeType()) {
                case ANVIL:
                    regAnvilRecipe((AnvilRecipe) recipe);
                    break;
                case POTION:
                    regPotionMixRecipe((PotionMixRecipe) recipe);
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
        putRecipeTypeMap(recipe);
        recipeKeyMap.put(key, recipe);
    }

    private static void putRecipeTypeMap(Recipe recipe) {
        if (recipe instanceof ShapedRecipe)
            recipeTypeMap.put(recipe, RecipeType.SHAPED);
        else if (recipe instanceof ShapelessRecipe)
            recipeTypeMap.put(recipe, RecipeType.SHAPELESS);
        else if (recipe instanceof CookingRecipe)
            recipeTypeMap.put(recipe, RecipeType.COOKING);
        else if (recipe instanceof SmithingRecipe)
            recipeTypeMap.put(recipe, RecipeType.SMITHING);
        else if (recipe instanceof StonecuttingRecipe)
            recipeTypeMap.put(recipe, RecipeType.STONE_CUTTING);
        else if (recipe instanceof AnvilRecipe)
            recipeTypeMap.put(recipe, RecipeType.ANVIL);
        else
            recipeTypeMap.put(recipe, RecipeType.UNKNOWN);
    }

    public static void loadRecipes() {
        loadCraftorithmRecipes();
        loadRecipeFromOtherPlugins();
        removeRecipes();
        ((RemoveRecipeCommand) RemoveRecipeCommand.INSTANCE).reloadRecipeMap();
    }

    private static void loadRecipeFromOtherPlugins() {
        Map<String, List<Recipe>> pluginRecipeMap = CraftorithmAPI.INSTANCE.getPluginRegRecipeMap();
        for (String plugin : pluginRecipeMap.keySet()) {
            if (plugin.equals(NamespacedKey.MINECRAFT) || plugin.equals(Craftorithm.getInstance().getName()))
                continue;
            for (Recipe recipe : pluginRecipeMap.get(plugin)) {
                Craftorithm.getInstance().getServer().addRecipe(recipe);
            }
        }
    }

    private static void removeRecipes() {
        List<String> removedRecipes = RemoveRecipeCommand.getRemovedRecipeConfig().getConfig().getStringList("recipes");
        if (Craftorithm.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe", false)) {
            for (NamespacedKey key : ((RemoveRecipeCommand) RemoveRecipeCommand.INSTANCE).getRecipeMap().keySet()) {
                if (key.getNamespace().equals("minecraft")) {
                    if (removedRecipes.contains(key.toString()))
                        continue;
                    removedRecipes.add(key.toString());
                }
            }
        }
        ((RemoveRecipeCommand) RemoveRecipeCommand.INSTANCE).removeRecipes(removedRecipes, false);
    }

    public static YamlConfiguration getRecipeConfig(Recipe recipe) {
        NamespacedKey key = getRecipeKey(recipe);
        return key != null ? recipeKeyConfigMap.get(key) : null;
    }

    public static Map<Recipe, RecipeType> getPluginRecipes() {
        return new ConcurrentHashMap<>(recipeTypeMap);
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

    public static Recipe getPluginRecipe(String key) {
        if (key.contains(":")) {
            return getPluginRecipe(NamespacedKey.fromString(key));
        } else {
            return getPluginRecipe(NamespacedKey.fromString(key, Craftorithm.getInstance()));
        }
    }

    public static Recipe getPluginRecipe(NamespacedKey namespacedKey) {
        return recipeKeyMap.get(namespacedKey);
    }

    public static List<NamespacedKey> getPluginRecipeKeys() {
        return new ArrayList<>(recipeKeyMap.keySet());
    }

    public static RecipeType getPluginRecipeType(Recipe recipe) {
        return recipeTypeMap.getOrDefault(recipe, RecipeType.UNKNOWN);
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

    public static File getRecipeFileFolder() {
        return recipeFileFolder;
    }

    public static void regAnvilRecipe(AnvilRecipe recipe) {
        anvilRecipeMap.put(recipe.getKey(), recipe);
    }

    private static void regPotionMixRecipe(PotionMixRecipe recipe) {
        //TODO
    }

    private static void saveDefConfigFile(List<File> allFiles) {
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
