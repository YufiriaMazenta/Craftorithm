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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeManager {

    private static final YamlConfigWrapper removedRecipeConfig;
    private static final File recipeFileFolder;
    private static final Map<String, YamlConfigWrapper> recipeConfigWrapperMap;
    private static final Map<String, List<NamespacedKey>> craftorithmRecipeGroupMap;
    private static final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private static final Map<NamespacedKey, Integer> recipeSortIdMap;
    private static final Map<NamespacedKey, AnvilRecipe> anvilRecipeMap;
    private static final List<NamespacedKey> serverRecipeList;

    static {
        removedRecipeConfig = new YamlConfigWrapper(Craftorithm.getInstance(), "removed_recipes.yml");
        recipeFileFolder = new File(Craftorithm.getInstance().getDataFolder().getPath(), "recipes");
        craftorithmRecipeGroupMap = new ConcurrentHashMap<>();
        recipeConfigWrapperMap = new ConcurrentHashMap<>();
        serverRecipeList = new CopyOnWriteArrayList<>();
        recipeUnlockMap = new ConcurrentHashMap<>();
        recipeSortIdMap = new ConcurrentHashMap<>();
        anvilRecipeMap = new ConcurrentHashMap<>();
    }

    public static void loadRecipeManager() {
        loadRecipeFiles();
    }

    public static void loadRecipeFiles() {
        recipeConfigWrapperMap.clear();
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
            recipeConfigWrapperMap.put(key, new YamlConfigWrapper(file));
        }
    }

    public static void loadRecipes() {
        loadCraftorithmRecipes();
        loadOtherPluginsRecipes();
        loadRemovedRecipes();
        reloadServerRecipeMap();
    }

    public static void loadCraftorithmRecipes() {
        resetRecipes();
        for (String fileName : recipeConfigWrapperMap.keySet()) {
            try {
                YamlConfigWrapper configWrapper = recipeConfigWrapperMap.get(fileName);
                YamlConfiguration config = configWrapper.config();
                boolean multiple = config.getBoolean("multiple", false);
                if (multiple) {
                    Recipe[] multipleRecipes = RecipeFactory.newMultipleRecipe(config, fileName);
                    regRecipes(fileName, Arrays.asList(multipleRecipes), config);
                } else {
                    Recipe recipe = RecipeFactory.newRecipe(config, fileName);
                    regRecipes(fileName, Collections.singletonList(recipe), config);
                }
                recipeConfigWrapperMap.put(fileName, configWrapper);
            } catch (Exception e) {
                LangUtil.info("load.recipe_load_exception", ContainerUtil.newHashMap("<recipe_name>", fileName));
                e.printStackTrace();
            }
        }
    }

    public static void regRecipes(String recipeName, List<Recipe> recipes, YamlConfiguration config) {
        List<NamespacedKey> recipeKeyList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            NamespacedKey recipeKey = getRecipeKey(recipe);
            recipeKeyList.add(getRecipeKey(recipe));
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
                    recipeUnlockMap.put(recipeKey, config.getBoolean("unlock", defUnlockCondition));
                } else {
                    recipeUnlockMap.put(recipeKey, defUnlockCondition);
                }
            }
        }
        craftorithmRecipeGroupMap.put(recipeName, recipeKeyList);

        //TODO 是否要进行排序暂定
//        recipeSortIdMap.put(key, config.getInt("sort_id", 0));
    }

    private static void loadOtherPluginsRecipes() {
        Map<String, List<Recipe>> pluginRecipeMap = CraftorithmAPI.INSTANCE.getPluginRegRecipeMap();
        for (String plugin : pluginRecipeMap.keySet()) {
            if (plugin.equals(NamespacedKey.MINECRAFT) || plugin.equals(Craftorithm.getInstance().getName()))
                continue;
            for (Recipe recipe : pluginRecipeMap.get(plugin)) {
                Bukkit.addRecipe(recipe);
            }
        }
    }

    private static void loadRemovedRecipes() {
        List<String> removedRecipes = removedRecipeConfig.config().getStringList("recipes");
        if (Craftorithm.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe", false)) {
            for (NamespacedKey key : serverRecipeList) {
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
        craftorithmRecipeGroupMap.forEach((key, recipes) -> {
            removeCraftorithmRecipe(key);
        });
        anvilRecipeMap.clear();
        craftorithmRecipeGroupMap.clear();
        recipeConfigWrapperMap.clear();
        recipeUnlockMap.clear();
        recipeSortIdMap.clear();
        reloadServerRecipeMap();
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
                    //TODO 维护表
                }
                reloadServerRecipeMap();
            }
        }
        return removedRecipeNum;
    }

    public static boolean removeCraftorithmRecipe(String recipeName) {
        int i = removeRecipes(craftorithmRecipeGroupMap.getOrDefault(recipeName, new ArrayList<>()));
        YamlConfigWrapper recipeConfig = recipeConfigWrapperMap.get(recipeName);
        if (recipeConfig != null) {
            recipeConfig.configFile().delete();
        }
        recipeConfigWrapperMap.remove(recipeName);
        craftorithmRecipeGroupMap.remove(recipeName);

        return i > 0;
    }

    public static boolean disableOtherPluginsRecipe(List<NamespacedKey> recipeKeys) {
        addKeyToRemovedConfig(recipeKeys);
        return removeRecipes(recipeKeys) > 0;
    }

    public static void addKeyToRemovedConfig(List<NamespacedKey> keys) {
        List<String> removedList = removedRecipeConfig.config().getStringList("recipes");
        for (NamespacedKey key : keys) {
            String keyStr = key.toString();
            if (!removedList.contains(keyStr))
                removedList.add(keyStr);
        }
        removedRecipeConfig.config().set("recipes", removedList);
        removedRecipeConfig.saveConfig();
    }

    public static void reloadServerRecipeMap() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        serverRecipeList.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = RecipeManager.getRecipeKey(recipe);
            serverRecipeList.put(key, recipe);
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

    public static List<Recipe> getCraftorithmRecipe(String key) {
        List<Recipe> recipes = new ArrayList<>();
        if (key.contains(":")) {
            return getCraftorithmRecipe(NamespacedKey.fromString(key));
        } else {
            return getCraftorithmRecipe(NamespacedKey.fromString(key, Craftorithm.getInstance()));
        }
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

    public static Map<NamespacedKey, Recipe> getServerRecipeList() {
        return Collections.unmodifiableMap(serverRecipeList);
    }

    public static Map<NamespacedKey, Boolean> getRecipeUnlockMap() {
        return recipeUnlockMap;
    }

    public static Map<String, YamlConfigWrapper> getRecipeConfigWrapperMap() {
        return recipeConfigWrapperMap;
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
