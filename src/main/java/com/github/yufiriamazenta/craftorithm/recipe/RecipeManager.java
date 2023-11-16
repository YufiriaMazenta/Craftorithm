package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.FileUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.config.impl.YamlConfigWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeManager {

    private static final YamlConfigWrapper removedRecipeConfig = new YamlConfigWrapper(Craftorithm.getInstance(), "removed_recipes.yml");
    private static final File recipeFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "recipes");
    private static final Map<String, YamlConfigWrapper> recipeFileMap;
    private static final Map<NamespacedKey, YamlConfiguration> recipeKeyConfigMap;
    private static final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private static final Map<NamespacedKey, Integer> recipeSortIdMap;
    private static final Map<NamespacedKey, AnvilRecipe> anvilRecipeMap;
    private static final Map<Recipe, RecipeType> recipeTypeMap;
    private static final Map<NamespacedKey, Recipe> recipeKeyMap;
    private static final Map<NamespacedKey, Recipe> serverRecipeMap;

    static {
        recipeFileMap = new ConcurrentHashMap<>();
        recipeKeyConfigMap = new ConcurrentHashMap<>();
        serverRecipeMap = new ConcurrentHashMap<>();
        recipeUnlockMap = new ConcurrentHashMap<>();
        recipeSortIdMap = new ConcurrentHashMap<>();
        anvilRecipeMap = new ConcurrentHashMap<>();
        recipeTypeMap = new ConcurrentHashMap<>();
        recipeKeyMap = new ConcurrentHashMap<>();
    }

    public static void loadRecipes() {
        loadCraftorithmRecipes();
        loadRecipeFromOtherPlugins();
        loadRemovedRecipes();
        reloadServerRecipeMap();
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
        if (allFiles.isEmpty()) {
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

    public static void loadCraftorithmRecipes() {
        resetRecipes();
        for (String fileName : recipeFileMap.keySet()) {
            try {
                YamlConfiguration config = recipeFileMap.get(fileName).config();
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

    private static void loadRemovedRecipes() {
        List<String> removedRecipes = removedRecipeConfig.config().getStringList("recipes");
        if (Craftorithm.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe", false)) {
            for (NamespacedKey key : serverRecipeMap.keySet()) {
                if (key.getNamespace().equals("minecraft")) {
                    if (removedRecipes.contains(key.toString()))
                        continue;
                    removedRecipes.add(key.toString());
                }
            }
        }
        removeRecipes(removedRecipes, false);
    }

    public static void resetRecipes() {
        CraftorithmAPI.INSTANCE.getPluginRegRecipeMap().forEach((plugin, recipes) -> {
            if (plugin.equals(NamespacedKey.MINECRAFT))
                return;
            List<String> recipeKeyList = new ArrayList<>();
            for (Recipe recipe : recipes) {
                recipeKeyList.add(getRecipeKey(recipe).toString());
            }
            removeRecipes(recipeKeyList, false);
        });
        List<String> recipeKeyList = new ArrayList<>();
        for (NamespacedKey key : getPluginRecipeKeys()) {
            recipeKeyList.add(key.toString());
        }
        removeRecipes(recipeKeyList, false);
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
        recipeSortIdMap.put(key, config.getInt("sort_id", 0));
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



    public static boolean removeRecipe(String keyStr, boolean save2File) {
        NamespacedKey key = NamespacedKey.fromString(keyStr);
        if (key == null)
            return false;
        if (CrypticLib.minecraftVersion() >= 11500) {
            if (Bukkit.removeRecipe(key) && save2File)
                addKeyToRemovedConfig(key.toString());
            reloadServerRecipeMap();
        } else {
            Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                Recipe recipe1 = recipeIterator.next();
                NamespacedKey key1 = RecipeManager.getRecipeKey(recipe1);
                if (key.equals(key1)) {
                    recipeIterator.remove();
                    reloadServerRecipeMap();
                    if (save2File) {
                        addKeyToRemovedConfig(key.toString());
                    }
                    return true;
                }
            }
        }
        return false;
    }


    public static void removeRecipes(List<String> keyStrList, boolean save2File) {
        List<NamespacedKey> keyList = new ArrayList<>();
        for (String str : keyStrList) {
            NamespacedKey key = NamespacedKey.fromString(str);
            if (key != null)
                keyList.add(key);
        }
        if (keyList.isEmpty())
            return;
        if (CrypticLib.minecraftVersion() >= 11500) {
            for (NamespacedKey key : keyList) {
                Bukkit.removeRecipe(key);
                if (save2File) {
                    addKeyToRemovedConfig(key.toString());
                }
            }
        } else {
            Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                Recipe recipe1 = recipeIterator.next();
                NamespacedKey key1 = RecipeManager.getRecipeKey(recipe1);
                if (key1 == null)
                    continue;
                if (keyList.contains(key1)) {
                    recipeIterator.remove();
                    if (save2File) {
                        addKeyToRemovedConfig(key1.toString());
                    }
                    keyList.remove(key1);
                    if (keyList.isEmpty())
                        break;
                }
            }
        }
        reloadServerRecipeMap();
    }

    public static boolean removeCraftorithmRecipe(NamespacedKey recipeKey) {
        if (CrypticLib.minecraftVersion() >= 11500) {
            YamlConfiguration config = getRecipeConfig(recipeKey);
            //TODO
            Bukkit.removeRecipe(recipeKey);
        } else {
            //TODO
        }
        return true;
    }

    public static boolean removeOtherRecipe(NamespacedKey recipeKey) {
        //TODO
        return true;
    }

    public static void addKeyToRemovedConfig(String key) {
        List<String> removedList = removedRecipeConfig.config().getStringList("recipes");
        if (!removedList.contains(key))
            removedList.add(key);
        removedRecipeConfig.config().set("recipes", removedList);
        removedRecipeConfig.saveConfig();
    }

    public static void reloadServerRecipeMap() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        serverRecipeMap.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = RecipeManager.getRecipeKey(recipe);
            serverRecipeMap.put(key, recipe);
        }
    }



    public static YamlConfiguration getRecipeConfig(NamespacedKey recipeKey) {
        return recipeKey != null ? recipeKeyConfigMap.get(recipeKey) : null;
    }

    public static NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null)
            return null;
        if (recipe instanceof CustomRecipe) {
            return ((CustomRecipe) recipe).getKey();
        }
        return ((Keyed) recipe).getKey();
    }

    public static Recipe getCraftorithmRecipe(String key) {
        if (key.contains(":")) {
            return getCraftorithmRecipe(NamespacedKey.fromString(key));
        } else {
            return getCraftorithmRecipe(NamespacedKey.fromString(key, Craftorithm.getInstance()));
        }
    }

    public static Recipe getCraftorithmRecipe(NamespacedKey namespacedKey) {
        return recipeKeyMap.get(namespacedKey);
    }

    public static RecipeType getCraftorithmRecipeType(Recipe recipe) {
        return recipeTypeMap.getOrDefault(recipe, RecipeType.UNKNOWN);
    }

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


    public static YamlConfigWrapper getRemovedRecipeConfig() {
        return removedRecipeConfig;
    }

    public static Map<NamespacedKey, Recipe> getServerRecipeMap() {
        return Collections.unmodifiableMap(serverRecipeMap);
    }

    public static Map<NamespacedKey, Boolean> getRecipeUnlockMap() {
        return recipeUnlockMap;
    }

    public static Map<String, YamlConfigWrapper> getRecipeFileMap() {
        return recipeFileMap;
    }

    public static Map<NamespacedKey, AnvilRecipe> getAnvilRecipeMap() {return anvilRecipeMap;}

    public static List<NamespacedKey> getPluginRecipeKeys() {
        return new ArrayList<>(recipeKeyMap.keySet());
    }

    public static Map<Recipe, RecipeType> getPluginRecipeTypeMap() {
        return new ConcurrentHashMap<>(recipeTypeMap);
    }

    public static Map<NamespacedKey, Integer> getRecipeSortIdMap() {
        return new ConcurrentHashMap<>(recipeSortIdMap);
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
