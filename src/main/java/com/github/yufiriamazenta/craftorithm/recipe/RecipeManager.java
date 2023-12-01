package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.config.wrapper.YamlConfigWrapper;
import crypticlib.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionBrewer;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecipeManager {

    private static final YamlConfigWrapper removedRecipeConfig;
    private static final File recipeFileFolder;
    private static final Map<String, YamlConfigWrapper> recipeConfigWrapperMap;
    private static final Map<String, List<NamespacedKey>> recipeGroupMap;
    private static final Map<String, Integer> recipeSortIdMap;
    private static final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private static final Map<String, List<PotionMixRecipe>> potionMixGroupMap;
    private static final List<NamespacedKey> serverRecipeCache;
    private static final List<Recipe> removedRecipeRecycleBin;
    private static final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";
    private static boolean supportPotionMix;

    static {
        removedRecipeConfig = new YamlConfigWrapper(Craftorithm.instance(), "removed_recipes.yml");
        recipeFileFolder = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
        recipeGroupMap = new ConcurrentHashMap<>();
        recipeConfigWrapperMap = new ConcurrentHashMap<>();
        serverRecipeCache = new CopyOnWriteArrayList<>();
        recipeUnlockMap = new ConcurrentHashMap<>();
        recipeSortIdMap = new ConcurrentHashMap<>();
        removedRecipeRecycleBin = new CopyOnWriteArrayList<>();
        potionMixGroupMap = new ConcurrentHashMap<>();


        try {
            Class.forName("io.papermc.paper.potion.PotionMix");
            supportPotionMix = true;
        } catch (Exception e) {
            supportPotionMix = false;
        }
    }

    public static void loadRecipeFiles() {
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

    public static void reloadRecipeManager() {
        resetRecipes();
        loadRecipeFiles();
        reloadCraftorithmRecipes();
        reloadRemovedRecipes();
        reloadServerRecipeCache();
    }

    public static void reloadCraftorithmRecipes() {
        for (String fileName : recipeConfigWrapperMap.keySet()) {
            try {
                YamlConfigWrapper configWrapper = recipeConfigWrapperMap.get(fileName);
                YamlConfiguration config = configWrapper.config();
                boolean multiple = config.getBoolean("multiple", false);
                Recipe[] recipes;
                if (multiple) {
                    recipes = RecipeFactory.newMultipleRecipe(config, fileName);
                } else {
                    recipes = RecipeFactory.newRecipe(config, fileName);
                }
                if (!supportPotionMix()) {
                    regRecipes(fileName, Arrays.asList(recipes), configWrapper);
                    continue;
                }

                RecipeType recipeType = RecipeType.valueOf(config.getString("type", "shaped").toUpperCase());
                if (recipeType.equals(RecipeType.POTION)) {
                    regPotionMix(fileName, Arrays.asList(recipes), configWrapper);
                } else {
                    regRecipes(fileName, Arrays.asList(recipes), configWrapper);
                }
            } catch (Throwable e) {
                LangUtil.info(Languages.loadRecipeLoadException.value(), CollectionsUtil.newStringHashMap("<recipe_name>", fileName));
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
            Bukkit.addRecipe(recipe);
            boolean defUnlockCondition = PluginConfigs.allRecipeUnlocked.value();
            if (config.contains("unlock")) {
                recipeUnlockMap.put(recipeKey, config.getBoolean("unlock", defUnlockCondition));
            } else {
                recipeUnlockMap.put(recipeKey, defUnlockCondition);
            }
        }
        recipeGroupMap.put(recipeName, recipeKeyList);
        recipeSortIdMap.put(recipeName, config.getInt("sort_id", 0));
    }

    public static void regPotionMix(String potionMixName, List<Recipe> recipes, YamlConfigWrapper configWrapper) {
        PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
        List<PotionMixRecipe> potionMixes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (!(recipe instanceof PotionMixRecipe)) {
                continue;
            }
            potionBrewer.addPotionMix(((PotionMixRecipe) recipe).potionMix());
            potionMixes.add(((PotionMixRecipe) recipe));
        }
        potionMixGroupMap.put(potionMixName, potionMixes);
        recipeSortIdMap.put(potionMixName, configWrapper.config().getInt("sort_id", 0));
    }

    private static void reloadRemovedRecipes() {
        removedRecipeConfig.reloadConfig();
        List<String> removedRecipes = removedRecipeConfig.config().getStringList("recipes");
        if (PluginConfigs.removeAllVanillaRecipe.value()) {
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
        disableOtherPluginsRecipe(removedRecipeKeys, false);
    }

    public static void resetRecipes() {
        //删除Craftorithm的配方
        recipeGroupMap.forEach((key, recipes) -> {
            removeCraftorithmRecipe(key, false);
        });

        if (supportPotionMix()) {
            potionMixGroupMap.clear();
            Bukkit.getPotionBrewer().resetPotionMixes();
        }

        //先将已经删除的配方还原
        for (Recipe recipe : removedRecipeRecycleBin) {
            Bukkit.addRecipe(recipe);
        }
        removedRecipeRecycleBin.clear();
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
        else if (recipe instanceof PotionMixRecipe)
            return RecipeType.POTION;
        else
            return RecipeType.UNKNOWN;
    }

    /**
     * 删除酿造台配方
     * @param potionMixRecipes 酿造台配方的key
     */
    public static void removePotionMix(List<PotionMixRecipe> potionMixRecipes) {
        for (PotionMixRecipe recipe : potionMixRecipes) {
            Bukkit.getPotionBrewer().removePotionMix(recipe.key());
        }
    }

    /**
     * 删除配方的基础方法
     * @param recipeKeys 要删除的配方
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
        if (!recipeGroupMap.containsKey(recipeName)) {
            if (potionMixGroupMap.containsKey(recipeName)) {
                removePotionMix(potionMixGroupMap.get(recipeName));
                YamlConfigWrapper recipeConfig = recipeConfigWrapperMap.get(recipeName);
                if (recipeConfig != null && deleteFile) {
                    recipeConfig.configFile().delete();
                }
                recipeSortIdMap.remove(recipeName);
                return true;
            } else {
                return false;
            }
        } else {
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
    }

    public static boolean disableOtherPluginsRecipe(List<NamespacedKey> recipeKeys, boolean save) {
        if (save)
            addKeyToRemovedConfig(recipeKeys);
        addRecipeToRemovedRecipeRecycleBin(recipeKeys);
        return removeRecipes(recipeKeys) > 0;
    }

    private static void addRecipeToRemovedRecipeRecycleBin(List<NamespacedKey> recipeKeys) {
        if (CrypticLib.minecraftVersion() < 11600) {
            //因为1.16以下没有getRecipe，故直接返回
            return;
        }
        for (NamespacedKey recipeKey : recipeKeys) {
            Recipe recipe = Bukkit.getRecipe(recipeKey);
            if (recipe == null)
                continue;
            removedRecipeRecycleBin.add(recipe);
        }
    }

    private static void addKeyToRemovedConfig(List<NamespacedKey> keys) {
        List<String> removedList = removedRecipeConfig.config().getStringList("recipes");
        for (NamespacedKey key : keys) {
            if (key.getNamespace().equals(NamespacedKey.MINECRAFT) && PluginConfigs.removeAllVanillaRecipe.value())
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
            return ((CustomRecipe) recipe).key();
        }
        return ((Keyed) recipe).getKey();
    }

    public static List<Recipe> getCraftorithmRecipe(String recipeName) {
        //因为1.16以下没有getRecipe，故直接返回
        if (CrypticLib.minecraftVersion() < 11600)
            return new ArrayList<>();
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


    public static File recipeFileFolder() {
        return recipeFileFolder;
    }

    public static Map<String, List<NamespacedKey>> recipeGroupMap() {
        return recipeGroupMap;
    }

    public static Map<String, List<PotionMixRecipe>> potionMixGroupMap() {
        return potionMixGroupMap;
    }

    public static YamlConfigWrapper removedRecipeConfig() {
        return removedRecipeConfig;
    }

    public static List<NamespacedKey> serverRecipeCache() {
        return serverRecipeCache;
    }

    public static Map<NamespacedKey, Boolean> recipeUnlockMap() {
        return recipeUnlockMap;
    }

    public static Map<String, YamlConfigWrapper> recipeConfigWrapperMap() {
        return recipeConfigWrapperMap;
    }

    public static Map<String, Integer> recipeSortIdMap() {
        return recipeSortIdMap;
    }

    public static boolean supportPotionMix() {
        return supportPotionMix;
    }

    private static void saveDefConfigFile(List<File> allFiles) {
        if (!PluginConfigs.releaseDefaultRecipes.value())
            return;
        Craftorithm.instance().saveResource("recipes/example_shaped.yml", false);
        Craftorithm.instance().saveResource("recipes/example_shapeless.yml", false);
        allFiles.add(new File(recipeFileFolder, "example_shaped.yml"));
        allFiles.add(new File(recipeFileFolder, "example_shapeless.yml"));
        if (CrypticLib.minecraftVersion() >= 11300) {
            Craftorithm.instance().saveResource("recipes/example_cooking.yml", false);
            allFiles.add(new File(recipeFileFolder, "example_cooking.yml"));
        }
        if (CrypticLib.minecraftVersion() >= 11400) {
            Craftorithm.instance().saveResource("recipes/example_smithing.yml", false);
            Craftorithm.instance().saveResource("recipes/example_stone_cutting.yml", false);
            allFiles.add(new File(recipeFileFolder, "example_smithing.yml"));
            allFiles.add(new File(recipeFileFolder, "example_stone_cutting.yml"));
        }
        if (CrypticLib.minecraftVersion() >= 11700) {
            Craftorithm.instance().saveResource("recipes/example_random_cooking.yml", false);
            allFiles.add(new File(recipeFileFolder, "example_random_cooking.yml"));
        }
        if (supportPotionMix()) {
            Craftorithm.instance().saveResource("recipes/example_potion.yml", false);
            allFiles.add(new File(recipeFileFolder, "example_potion.yml"));
        }
    }

}
