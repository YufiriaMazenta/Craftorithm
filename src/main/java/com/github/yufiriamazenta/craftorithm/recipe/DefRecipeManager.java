package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.exception.UnsupportedVersionException;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.config.yaml.YamlConfigWrapper;
import crypticlib.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public enum DefRecipeManager {

    INSTANCE;
    public final File RECIPE_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
    private final Map<RecipeType, Map<String, List<NamespacedKey>>> craftorithmRecipeMap;
    private final Map<RecipeType, Consumer<Recipe>> recipeRegisterMap;
    private final Map<String, YamlConfigWrapper> recipeConfigWrapperMap;
    private boolean supportPotionMix;

    DefRecipeManager() {
        craftorithmRecipeMap = new ConcurrentHashMap<>();
        recipeConfigWrapperMap = new ConcurrentHashMap<>();
        recipeRegisterMap = new ConcurrentHashMap<>();
        recipeRegisterMap.put(RecipeType.SHAPED, Bukkit::addRecipe);
        recipeRegisterMap.put(RecipeType.SHAPELESS, Bukkit::addRecipe);
        recipeRegisterMap.put(RecipeType.COOKING, Bukkit::addRecipe);
        recipeRegisterMap.put(RecipeType.STONE_CUTTING, Bukkit::addRecipe);
        recipeRegisterMap.put(RecipeType.SMITHING, Bukkit::addRecipe);
        recipeRegisterMap.put(RecipeType.ANVIL, recipe -> {});

        //TODO 对版本需求的加上限制
        try {
            Class.forName("io.papermc.paper.potion.PotionMix");
            supportPotionMix = true;
        } catch (Exception e) {
            supportPotionMix = false;
        }
        if (supportPotionMix)
            recipeRegisterMap.put(RecipeType.POTION, recipe -> Bukkit.getPotionBrewer().addPotionMix(((PotionMixRecipe) recipe).potionMix()));
        loadRecipeFiles();
        loadRecipes();
    }

    public void reloadRecipeManager() {
        //TODO reset
        loadRecipeFiles();
        loadRecipes();
    }

    private void loadRecipes() {
        for (String fileName : recipeConfigWrapperMap.keySet()) {
            try {
                YamlConfigWrapper configWrapper = recipeConfigWrapperMap.get(fileName);
                YamlConfiguration config = configWrapper.config();
                for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(config, fileName)) {
                    recipeRegistry.register();
                }
            } catch (Throwable e) {
                LangUtil.info(Languages.LOAD_RECIPE_LOAD_EXCEPTION.value(), CollectionsUtil.newStringHashMap("<recipe_name>", fileName));
                e.printStackTrace();
            }
        }
    }

    private void loadRecipeFiles() {
        recipeConfigWrapperMap.clear();
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
            recipeConfigWrapperMap.put(key, new YamlConfigWrapper(file));
        }
    }

    public void regRecipe(String recipeGroup, Recipe recipe, RecipeType recipeType) {
        if (!craftorithmRecipeMap.containsKey(recipeType))
            craftorithmRecipeMap.put(recipeType, new ConcurrentHashMap<>());
        Map<String, List<NamespacedKey>> recipeMap = craftorithmRecipeMap.get(recipeType);
        if (!recipeMap.containsKey(recipeGroup))
            recipeMap.put(recipeGroup, new ArrayList<>());
        List<NamespacedKey> recipes = recipeMap.get(recipeGroup);
        recipes.add(getRecipeKey(recipe));
        recipeRegisterMap.getOrDefault(recipeType, recipe1 -> {
            throw new UnsupportedVersionException("Can not register " + recipeType.name().toLowerCase() + " recipe");
        }).accept(recipe);
    }

    public Map<RecipeType, Map<String, List<NamespacedKey>>> recipeMap() {
        return craftorithmRecipeMap;
    }

    public Recipe getRecipe(NamespacedKey namespacedKey) {
        //TODO
        return null;
    }

    public NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null)
            return null;
        if (recipe instanceof CustomRecipe) {
            return ((CustomRecipe) recipe).key();
        }
        return ((Keyed) recipe).getKey();
    }


    public boolean supportPotionMix() {
        return supportPotionMix;
    }

    private void saveDefConfigFile(List<File> allFiles) {
        if (!PluginConfigs.RELEASE_DEFAULT_RECIPES.value())
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
