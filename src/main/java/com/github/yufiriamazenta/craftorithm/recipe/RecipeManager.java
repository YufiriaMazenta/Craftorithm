package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeManager {

    private static final YamlConfigWrapper removedRecipeConfig;
    private static final File recipeFileFolder;
    private static final Map<String, YamlConfigWrapper> recipeConfigWrapperMap;
    private static final Map<String, List<NamespacedKey>> recipeGroupMap;
    private static final Map<String, Integer> recipeSortIdMap;
    private static final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private static final Map<NamespacedKey, AnvilRecipe> anvilRecipeMap;
    private static final List<NamespacedKey> serverRecipeCache;
    private static final List<Recipe> removedRecipeRecycleBin;
    private static final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";

    static {
        removedRecipeConfig = new YamlConfigWrapper(Craftorithm.getInstance(), "removed_recipes.yml");
        recipeFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "recipes");
        recipeGroupMap = new ConcurrentHashMap<>();
        recipeConfigWrapperMap = new ConcurrentHashMap<>();
        serverRecipeCache = new CopyOnWriteArrayList<>();
        recipeUnlockMap = new ConcurrentHashMap<>();
        recipeSortIdMap = new ConcurrentHashMap<>();
        anvilRecipeMap = new ConcurrentHashMap<>();
        removedRecipeRecycleBin = new CopyOnWriteArrayList<>();
    }

    public static void loadRecipeManager() {
        reloadRecipeFiles();
    }

    public static void reloadRecipeFiles() {
        recipeConfigWrapperMap.clear();
        if (!recipeFileFolder.exists()) {
            boolean mkdirResult = recipeFileFolder.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = FileUtil.allFiles(recipeFileFolder, FileUtil.YAML_FILE_PATTERN);
        if (allFiles.isEmpty()) {
            saveDefConfigFile(allFiles);
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(recipeFileFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            recipeConfigWrapperMap.put(key, new YamlConfigWrapper(file));
        }
    }

    public static void reloadRecipesManager() {
        resetRecipes();
        reloadRecipeFiles();
        reloadCraftorithmRecipes();
        reloadOtherPluginsRecipes();
        reloadRemovedRecipes();
        reloadServerRecipeCache();
    }

    public static void reloadCraftorithmRecipes() {
        for (String fileName : recipeConfigWrapperMap.keySet()) {
            try {
                YamlConfigWrapper configWrapper = recipeConfigWrapperMap.get(fileName);
                YamlConfiguration config = configWrapper.config();
                boolean multiple = config.getBoolean("multiple", false);
                if (multiple) {
                    Recipe[] multipleRecipes = RecipeFactory.newMultipleRecipe(config, fileName);
                    regRecipes(fileName, Arrays.asList(multipleRecipes), configWrapper);
                } else {
                    Recipe[] recipes = RecipeFactory.newRecipe(config, fileName);
                    regRecipes(fileName, Arrays.asList(recipes), configWrapper);
                }
            } catch (Exception e) {
                LangUtil.info("load.recipe_load_exception", ContainerUtil.newHashMap("<recipe_name>", fileName));
                e.printStackTrace();
            }
        }
    }

    public static void regRecipes(String recipeName, List<Recipe> recipes, YamlConfigWrapper configWrapper) {
        YamlConfiguration config = configWrapper.config();
        List<NamespacedKey> recipeKeyList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            NamespacedKey recipeKey = getRecipeKey(recipe);
            recipeKeyList.add(getRecipeKey(recipe));
            if (recipe instanceof CustomRecipe) {
                //TODO
                if (Objects.requireNonNull(((CustomRecipe) recipe).getRecipeType()) == RecipeType.ANVIL) {
                    regAnvilRecipe((AnvilRecipe) recipe);
                }
            } else {
                Bukkit.addRecipe(recipe);
                boolean defUnlockCondition = Craftorithm.getInstance().getConfig().getBoolean("all_recipe_unlocked", false);
                if (config.contains("unlock")) {
                    recipeUnlockMap.put(recipeKey, config.getBoolean("unlock", defUnlockCondition));
                } else {
                    recipeUnlockMap.put(recipeKey, defUnlockCondition);
                }
            }
        }
        recipeGroupMap.put(recipeName, recipeKeyList);
        recipeSortIdMap.put(recipeName, config.getInt("sort_id", 0));
    }

    private static void reloadOtherPluginsRecipes() {
        Map<String, List<Recipe>> pluginRecipeMap = CraftorithmAPI.INSTANCE.getPluginRegRecipeMap();
        for (String plugin : pluginRecipeMap.keySet()) {
            if (plugin.equals(NamespacedKey.MINECRAFT) || plugin.equals(Craftorithm.getInstance().getName()))
                continue;
            for (Recipe recipe : pluginRecipeMap.get(plugin)) {
                Bukkit.addRecipe(recipe);
            }
        }
    }

    private static void reloadRemovedRecipes() {
        removedRecipeConfig.reloadConfig();
        List<String> removedRecipes = removedRecipeConfig.config().getStringList("recipes");
        if (Craftorithm.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe", false)) {
            for (NamespacedKey key : serverRecipeCache) {
                if (key.getNamespace().equals("minecraft")) {
                    if (removedRecipes.contains(key.toString()))
                        continue;
                    removedRecipes.add(key.toString());
                }
            }
        }
        List<NamespacedKey> removedRecipeKeys = new ArrayList<>();
        for (String recipeKey : removedRecipes) {
            removedRecipeKeys.add(NamespacedKey.fromString(recipeKey));
        }
        disableOtherPluginsRecipe(removedRecipeKeys);
    }

    public static void resetRecipes() {
        //删除其他插件的配方
        CraftorithmAPI.INSTANCE.getPluginRegRecipeMap().forEach((plugin, recipes) -> {
            if (plugin.equals(NamespacedKey.MINECRAFT))
                return;
            List<NamespacedKey> recipeKeyList = new ArrayList<>();
            for (Recipe recipe : recipes) {
                recipeKeyList.add(getRecipeKey(recipe));
            }
            disableOtherPluginsRecipe(recipeKeyList);
        });

        //删除Craftorithm的配方
        recipeGroupMap.forEach((key, recipes) -> {
            removeCraftorithmRecipe(key, false);
        });

        //先将已经删除的配方还原
        for (Recipe recipe : removedRecipeRecycleBin) {
            Bukkit.addRecipe(recipe);
        }
        removedRecipeRecycleBin.clear();
        anvilRecipeMap.clear();
        recipeGroupMap.clear();
        recipeUnlockMap.clear();
        recipeSortIdMap.clear();
        reloadServerRecipeCache();
    }

    public static RecipeType getRecipeType(Recipe recipe) {
        if (recipe == null)
            return RecipeType.UNKNOWN;
        if (recipe instanceof ShapedRecipe)
            return RecipeType.SHAPED;
        else if (recipe instanceof ShapelessRecipe)
            return RecipeType.SHAPELESS;
        else if (recipe instanceof CookingRecipe)
            return RecipeType.COOKING;
        else if (recipe instanceof SmithingRecipe)
            return RecipeType.SMITHING;
        else if (recipe instanceof StonecuttingRecipe)
            return RecipeType.STONE_CUTTING;
        else if (recipe instanceof AnvilRecipe)
            return RecipeType.ANVIL;
        else
            return RecipeType.UNKNOWN;
    }

    /**
     * 删除配方的基础方法
     * @param recipeKeys 要删除配方的key
     * @return 删除的配方数量
     */
    private static int removeRecipes(List<NamespacedKey> recipeKeys) {
        if (recipeKeys == null || recipeKeys.isEmpty())
            return 0;
        //删除表里缓存的一些数据
        for (NamespacedKey recipeKey : recipeKeys) {
            recipeUnlockMap.remove(recipeKey);
        }

        //在服务器中缓存的数据
        int removedRecipeNum = 0;
        if (CrypticLib.minecraftVersion() >= 11500) {
            for (NamespacedKey recipeKey : recipeKeys) {
                if (Bukkit.removeRecipe(recipeKey))
                    removedRecipeNum ++;
            }
        } else {
            Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                Recipe iteratorRecipe = recipeIterator.next();
                NamespacedKey iteratorRecipeKey = RecipeManager.getRecipeKey(iteratorRecipe);
                if (recipeKeys.contains(iteratorRecipeKey)) {
                    recipeIterator.remove();
                    removedRecipeNum ++;
                }
            }
        }
        serverRecipeCache.removeAll(recipeKeys);
        return removedRecipeNum;
    }

    public static boolean removeCraftorithmRecipe(String recipeName, boolean deleteFile) {
        int i = removeRecipes(recipeGroupMap.getOrDefault(recipeName, new ArrayList<>()));
        YamlConfigWrapper recipeConfig = recipeConfigWrapperMap.get(recipeName);
        if (recipeConfig != null && deleteFile) {
            recipeConfig.configFile().delete();
        }
        recipeConfigWrapperMap.remove(recipeName);
        recipeGroupMap.remove(recipeName);
        recipeSortIdMap.remove(recipeName);

        return i > 0;
    }

    public static boolean disableOtherPluginsRecipe(List<NamespacedKey> recipeKeys) {
        addRecipeToRemovedRecipeRecycleMap(recipeKeys);
        addKeyToRemovedConfig(recipeKeys);
        return removeRecipes(recipeKeys) > 0;
    }

    private static void addRecipeToRemovedRecipeRecycleMap(List<NamespacedKey> recipeKeys) {
        for (NamespacedKey recipeKey : recipeKeys) {
            Recipe recipe = Bukkit.getRecipe(recipeKey);
            if (recipe == null)
                continue;
            removedRecipeRecycleBin.add(recipe);
        }
    }

    private static void addKeyToRemovedConfig(List<NamespacedKey> keys) {
        List<String> removedList = removedRecipeConfig.config().getStringList("recipes");
        boolean removeAllVanillaRecipe = Craftorithm.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe");
        for (NamespacedKey key : keys) {
            if (key.getNamespace().equals(NamespacedKey.MINECRAFT) && removeAllVanillaRecipe)
                continue;
            String keyStr = key.toString();
            if (!removedList.contains(keyStr))
                removedList.add(keyStr);
        }
        removedRecipeConfig.config().set("recipes", removedList);
        removedRecipeConfig.saveConfig();
    }

    public static void reloadServerRecipeCache() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        serverRecipeCache.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = RecipeManager.getRecipeKey(recipe);
            if (key.getNamespace().equals(PLUGIN_RECIPE_NAMESPACE))
                continue;
            serverRecipeCache.add(key);
        }
    }

    public static YamlConfiguration getRecipeConfig(NamespacedKey recipeKey) {
        for (String recipeName : recipeGroupMap.keySet()) {
            List<NamespacedKey> recipeKeys = recipeGroupMap.getOrDefault(recipeName, new ArrayList<>());
            if (recipeKeys.contains(recipeKey)) {
                return recipeConfigWrapperMap.get(recipeName).config();
            }
        }
        return null;
    }

    public static NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null)
            return null;
        if (recipe instanceof CustomRecipe) {
            return ((CustomRecipe) recipe).getKey();
        }
        return ((Keyed) recipe).getKey();
    }

    public static List<Recipe> getCraftorithmRecipe(String recipeName) {
        List<Recipe> recipes = new ArrayList<>();
        for (NamespacedKey key : recipeGroupMap.getOrDefault(recipeName, new ArrayList<>())) {
            Recipe recipe = Bukkit.getRecipe(key);
            if (recipe != null)
                recipes.add(recipe);
        }
        return recipes;
    }

    public static int getCraftorithmRecipeSortId(String recipeName) {
        return recipeSortIdMap.getOrDefault(recipeName, 0);
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

    public static void regAnvilRecipe(AnvilRecipe recipe) {
        anvilRecipeMap.put(recipe.getKey(), recipe);
    }

//    private static void regPotionMixRecipe(PotionMix recipe) {
//        //TODO
//    }



    public static File getRecipeFileFolder() {
        return recipeFileFolder;
    }

    public static Map<String, List<NamespacedKey>> getRecipeGroupMap() {
        return recipeGroupMap;
    }

    public static YamlConfigWrapper getRemovedRecipeConfig() {
        return removedRecipeConfig;
    }

    public static List<NamespacedKey> getServerRecipeCache() {
        return serverRecipeCache;
    }

    public static Map<NamespacedKey, Boolean> getRecipeUnlockMap() {
        return recipeUnlockMap;
    }

    public static Map<String, YamlConfigWrapper> getRecipeConfigWrapperMap() {
        return recipeConfigWrapperMap;
    }

    public static Map<NamespacedKey, AnvilRecipe> getAnvilRecipeMap() {return anvilRecipeMap;}
    public static Map<String, Integer> getRecipeSortIdMap() {
        return recipeSortIdMap;
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
