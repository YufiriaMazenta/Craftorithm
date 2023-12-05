package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.config.yaml.YamlConfigWrapper;
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

    private static final YamlConfigWrapper YAML_CONFIG_WRAPPER;
    private static final File RECIPE_FILE_FOLDER;
    private static final Map<String, YamlConfigWrapper> RECIPE_CONFIG_WRAPPER_MAP;
    private static final Map<String, List<NamespacedKey>> RECIPE_GROUP_MAP;
    private static final Map<String, Integer> RECIPE_SORT_ID_MAP;
    private static final Map<NamespacedKey, Boolean> RECIPE_UNLOCK_MAP;
    private static final Map<String, List<PotionMixRecipe>> POTION_MIX_GROUP_MAP;
    private static final List<NamespacedKey> SERVER_RECIPE_CACHE;
    private static final List<Recipe> REMOVED_RECIPE_RECYCLE_BIN;
    private static final Map<NamespacedKey, AnvilRecipe> ANVIL_RECIPE_MAP;
    private static final Map<String, List<NamespacedKey>> ANVIL_RECIPE_GROUP_MAP;
    private static final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";
    private static boolean supportPotionMix;

    static {
        YAML_CONFIG_WRAPPER = new YamlConfigWrapper(Craftorithm.instance(), "removed_recipes.yml");
        RECIPE_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
        RECIPE_GROUP_MAP = new ConcurrentHashMap<>();
        RECIPE_CONFIG_WRAPPER_MAP = new ConcurrentHashMap<>();
        SERVER_RECIPE_CACHE = new CopyOnWriteArrayList<>();
        RECIPE_UNLOCK_MAP = new ConcurrentHashMap<>();
        RECIPE_SORT_ID_MAP = new ConcurrentHashMap<>();
        REMOVED_RECIPE_RECYCLE_BIN = new CopyOnWriteArrayList<>();
        POTION_MIX_GROUP_MAP = new ConcurrentHashMap<>();
        ANVIL_RECIPE_MAP = new ConcurrentHashMap<>();
        ANVIL_RECIPE_GROUP_MAP = new ConcurrentHashMap<>();

        try {
            Class.forName("io.papermc.paper.potion.PotionMix");
            supportPotionMix = true;
        } catch (Exception e) {
            supportPotionMix = false;
        }
    }

    public static void loadRecipeFiles() {
        RECIPE_CONFIG_WRAPPER_MAP.clear();
        if (!RECIPE_FILE_FOLDER.exists()) {
            boolean mkdirResult = RECIPE_FILE_FOLDER.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = FileUtil.allFiles(RECIPE_FILE_FOLDER, FileUtil.YAML_FILE_PATTERN);
        if (allFiles.isEmpty()) {
            saveDefConfigFile(allFiles);
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(RECIPE_FILE_FOLDER.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            RECIPE_CONFIG_WRAPPER_MAP.put(key, new YamlConfigWrapper(file));
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
        for (String fileName : RECIPE_CONFIG_WRAPPER_MAP.keySet()) {
            try {
                YamlConfigWrapper configWrapper = RECIPE_CONFIG_WRAPPER_MAP.get(fileName);
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
                RECIPE_UNLOCK_MAP.put(recipeKey, config.getBoolean("unlock", defUnlockCondition));
            } else {
                RECIPE_UNLOCK_MAP.put(recipeKey, defUnlockCondition);
            }
        }
        RECIPE_GROUP_MAP.put(recipeName, recipeKeyList);
        RECIPE_SORT_ID_MAP.put(recipeName, config.getInt("sort_id", 0));
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
        POTION_MIX_GROUP_MAP.put(potionMixName, potionMixes);
        RECIPE_SORT_ID_MAP.put(potionMixName, configWrapper.config().getInt("sort_id", 0));
    }

    private static void reloadRemovedRecipes() {
        YAML_CONFIG_WRAPPER.reloadConfig();
        List<String> removedRecipes = YAML_CONFIG_WRAPPER.config().getStringList("recipes");
        if (PluginConfigs.removeAllVanillaRecipe.value()) {
            for (NamespacedKey key : SERVER_RECIPE_CACHE) {
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
        RECIPE_GROUP_MAP.forEach((key, recipes) -> {
            removeCraftorithmRecipe(key, false);
        });

        if (supportPotionMix()) {
            POTION_MIX_GROUP_MAP.clear();
            Bukkit.getPotionBrewer().resetPotionMixes();
        }

        //先将已经删除的配方还原
        for (Recipe recipe : REMOVED_RECIPE_RECYCLE_BIN) {
            Bukkit.addRecipe(recipe);
        }
        REMOVED_RECIPE_RECYCLE_BIN.clear();
        RECIPE_GROUP_MAP.clear();
        RECIPE_UNLOCK_MAP.clear();
        RECIPE_SORT_ID_MAP.clear();
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
            RECIPE_UNLOCK_MAP.remove(recipeKey);
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
        SERVER_RECIPE_CACHE.removeAll(recipeKeys);
        return removedRecipeNum;
    }

    public static boolean removeCraftorithmRecipe(String recipeName, boolean deleteFile) {
        if (!RECIPE_GROUP_MAP.containsKey(recipeName)) {
            if (POTION_MIX_GROUP_MAP.containsKey(recipeName)) {
                removePotionMix(POTION_MIX_GROUP_MAP.get(recipeName));
                YamlConfigWrapper recipeConfig = RECIPE_CONFIG_WRAPPER_MAP.get(recipeName);
                if (recipeConfig != null && deleteFile) {
                    recipeConfig.configFile().delete();
                }
                RECIPE_SORT_ID_MAP.remove(recipeName);
                return true;
            } else {
                return false;
            }
        } else {
            int i = removeRecipes(RECIPE_GROUP_MAP.getOrDefault(recipeName, new ArrayList<>()));
            YamlConfigWrapper recipeConfig = RECIPE_CONFIG_WRAPPER_MAP.get(recipeName);
            if (recipeConfig != null && deleteFile) {
                recipeConfig.configFile().delete();
            }
            RECIPE_CONFIG_WRAPPER_MAP.remove(recipeName);
            RECIPE_GROUP_MAP.remove(recipeName);
            RECIPE_SORT_ID_MAP.remove(recipeName);

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
            REMOVED_RECIPE_RECYCLE_BIN.add(recipe);
        }
    }

    private static void addKeyToRemovedConfig(List<NamespacedKey> keys) {
        List<String> removedList = YAML_CONFIG_WRAPPER.config().getStringList("recipes");
        for (NamespacedKey key : keys) {
            if (key.getNamespace().equals(NamespacedKey.MINECRAFT) && PluginConfigs.removeAllVanillaRecipe.value())
                continue;
            String keyStr = key.toString();
            if (!removedList.contains(keyStr))
                removedList.add(keyStr);
        }
        YAML_CONFIG_WRAPPER.config().set("recipes", removedList);
        YAML_CONFIG_WRAPPER.saveConfig();
    }

    public static void reloadServerRecipeCache() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        SERVER_RECIPE_CACHE.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = RecipeManager.getRecipeKey(recipe);
            if (key.getNamespace().equals(PLUGIN_RECIPE_NAMESPACE))
                continue;
            SERVER_RECIPE_CACHE.add(key);
        }
    }

    public static YamlConfiguration getRecipeConfig(NamespacedKey recipeKey) {
        for (String recipeName : RECIPE_GROUP_MAP.keySet()) {
            List<NamespacedKey> recipeKeys = RECIPE_GROUP_MAP.getOrDefault(recipeName, new ArrayList<>());
            if (recipeKeys.contains(recipeKey)) {
                return RECIPE_CONFIG_WRAPPER_MAP.get(recipeName).config();
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

    public static AnvilRecipe matchAnvilRecipe(ItemStack base, ItemStack addition) {
        //TODO 返回铁砧配方
        return null;
    }

    @Deprecated
    public static List<Recipe> getCraftorithmRecipe(String recipeName) {
        //因为1.16以下没有getRecipe，故直接返回
        if (CrypticLib.minecraftVersion() < 11600)
            return new ArrayList<>();
        List<Recipe> recipes = new ArrayList<>();
        for (NamespacedKey key : RECIPE_GROUP_MAP.getOrDefault(recipeName, new ArrayList<>())) {
            Recipe recipe = Bukkit.getRecipe(key);
            if (recipe != null)
                recipes.add(recipe);
        }
        return recipes;
    }

    public static int getCraftorithmRecipeSortId(String recipeName) {
        return RECIPE_SORT_ID_MAP.getOrDefault(recipeName, 0);
    }


    public static File recipeFileFolder() {
        return RECIPE_FILE_FOLDER;
    }

    public static Map<String, List<NamespacedKey>> recipeGroupMap() {
        return RECIPE_GROUP_MAP;
    }

    public static Map<String, List<PotionMixRecipe>> potionMixGroupMap() {
        return POTION_MIX_GROUP_MAP;
    }

    public static YamlConfigWrapper removedRecipeConfig() {
        return YAML_CONFIG_WRAPPER;
    }

    public static List<NamespacedKey> serverRecipeCache() {
        return SERVER_RECIPE_CACHE;
    }

    public static Map<NamespacedKey, Boolean> recipeUnlockMap() {
        return RECIPE_UNLOCK_MAP;
    }

    public static Map<String, YamlConfigWrapper> recipeConfigWrapperMap() {
        return RECIPE_CONFIG_WRAPPER_MAP;
    }

    public static Map<String, Integer> recipeSortIdMap() {
        return RECIPE_SORT_ID_MAP;
    }

    public static boolean supportPotionMix() {
        return supportPotionMix;
    }

    private static void saveDefConfigFile(List<File> allFiles) {
        if (!PluginConfigs.releaseDefaultRecipes.value())
            return;
        Craftorithm.instance().saveResource("recipes/example_shaped.yml", false);
        Craftorithm.instance().saveResource("recipes/example_shapeless.yml", false);
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_shaped.yml"));
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_shapeless.yml"));
        if (CrypticLib.minecraftVersion() >= 11300) {
            Craftorithm.instance().saveResource("recipes/example_cooking.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_cooking.yml"));
        }
        if (CrypticLib.minecraftVersion() >= 11400) {
            Craftorithm.instance().saveResource("recipes/example_smithing.yml", false);
            Craftorithm.instance().saveResource("recipes/example_stone_cutting.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_smithing.yml"));
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_stone_cutting.yml"));
        }
        if (CrypticLib.minecraftVersion() >= 11700) {
            Craftorithm.instance().saveResource("recipes/example_random_cooking.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_random_cooking.yml"));
        }
        if (supportPotionMix()) {
            Craftorithm.instance().saveResource("recipes/example_potion.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_potion.yml"));
        }
    }

}
