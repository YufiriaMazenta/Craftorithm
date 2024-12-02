package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.exception.RecipeLoadException;
import crypticlib.CrypticLibBukkit;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.platform.Platform;
import crypticlib.util.FileHelper;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE, priority = 2),
        @TaskRule(lifeCycle = LifeCycle.RELOAD, priority = 2)
    }
)
public enum RecipeManager implements BukkitLifeCycleTask {

    INSTANCE;
    public final File RECIPE_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
    public final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";
    private final BukkitConfigWrapper disabledRecipesConfigWrapper = new BukkitConfigWrapper(Craftorithm.instance(), "disabled_recipes.yml");
    private final Map<String, RecipeType> recipeTypes = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, Recipe> craftorithmRecipes = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, BukkitConfigWrapper> recipeConfigWrapperMap = new ConcurrentHashMap<>();
    private final List<Recipe> disabledRecipes;
    private final Map<NamespacedKey, Recipe> serverRecipesCache;
    private boolean supportPotionMix;

    RecipeManager() {
        disabledRecipes = new CopyOnWriteArrayList<>();
        serverRecipesCache = new ConcurrentHashMap<>();
    }

    //配方类型相关

    public boolean regRecipeType(RecipeType type) {
        return regRecipeType(type, false);
    }

    public boolean regRecipeType(RecipeType type, boolean force) {
        if (type == null) {
            return false;
        }
        if (recipeTypes.containsKey(type.typeId())) {
            if (force) {
                recipeTypes.put(type.typeId(), type);
                return true;
            } else {
                return false;
            }
        }
        recipeTypes.put(type.typeId(), type);
        return true;
    }

    private void regDefaultRecipeTypes() {
        regRecipeType(SimpleRecipeTypes.VANILLA_SHAPED);
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {

        }
        if (!CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
            supportPotionMix = true;
        }
    }

    public RecipeType getRecipeType(Recipe recipe) {
        for (RecipeType recipeType : recipeTypes.values()) {
            if (recipeType.isThisType(recipe)) {
                return recipeType;
            }
        }
        return SimpleRecipeTypes.UNKNOWN;
    }

    //配方加载相关

    public void reloadRecipeManager() {
        resetRecipes();
        loadRecipesFromConfig();
        loadServerRecipeCache();
        reloadDisabledRecipes();
    }


    /**
     * 重置配方
     * 将会删除所有由本插件及使用本插件提供的API添加的配方
     * 同时还原被删除的其他配方
     */
    public void resetRecipes() {
        //删除所有由插件添加的配方
        craftorithmRecipes.forEach((recipeKey, recipe) -> {
            RecipeType recipeType = getRecipeType(recipe);
            recipeType.recipeRegister().unregisterRecipe(recipeKey);
        });

        //还原被禁用的配方
        for (Recipe recipe : disabledRecipes) {
            RecipeType recipeType = getRecipeType(recipe);
            recipeType.recipeRegister().registerRecipe(recipe);
        }
        disabledRecipes.clear();
    }

    private void loadRecipesFromConfig() {
        if (!RECIPE_FILE_FOLDER.exists()) {
            boolean mkdirResult = RECIPE_FILE_FOLDER.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = FileHelper.allYamlFiles(RECIPE_FILE_FOLDER);
        for (File file : allFiles) {
            try {
                String recipeName = file.getPath().substring(RECIPE_FILE_FOLDER.getPath().length() + 1);
                recipeName = recipeName.replace("\\", "/");
                recipeName = recipeName.replace('-', '_');
                int lastDotIndex = recipeName.lastIndexOf(".");
                recipeName = recipeName.substring(0, lastDotIndex).toLowerCase();
                BukkitConfigWrapper recipeConfigWrapper = new BukkitConfigWrapper(file);
                YamlConfiguration recipeConfig = recipeConfigWrapper.config();
                String typeStr = recipeConfig.getString("type");
                RecipeType recipeType;
                if (typeStr == null) {
                    throw new RecipeLoadException("Unknown recipe type of " + recipeName);
                }
                recipeType = recipeTypes.get(typeStr);
                if (recipeType == null) {
                    throw new RecipeLoadException("Unknown recipe type of " + recipeName);
                }
                RecipeLoader<?> recipeLoader = recipeType.recipeLoader();
                Recipe recipe = recipeLoader.loadRecipe(recipeName, recipeConfig);
                RecipeRegister recipeRegister = recipeType.recipeRegister();
                boolean result = recipeRegister.registerRecipe(recipe);
                if (result) {
                    NamespacedKey recipeKey = getRecipeKey(recipe);
                    craftorithmRecipes.put(recipeKey, recipe);
                    recipeConfigWrapperMap.put(recipeKey, recipeConfigWrapper);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public void loadServerRecipeCache() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        serverRecipesCache.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = getRecipeKey(recipe);
            if (key != null)
                serverRecipesCache.put(key, recipe);
        }
    }

    private void reloadDisabledRecipes() {
        disabledRecipesConfigWrapper.reloadConfig();
        List<String> disabledRecipes = disabledRecipesConfigWrapper.config().getStringList("recipes");
        if (PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value()) {
            serverRecipesCache.forEach((key, recipe) -> {
                if (key.getNamespace().equals("minecraft")) {
                    if (disabledRecipes.contains(key.toString()))
                        return;
                    disabledRecipes.add(key.toString());
                }
            });
        }
        for (String recipeKey : disabledRecipes) {
            NamespacedKey key = NamespacedKey.fromString(recipeKey);
            disableRecipe(key, false);
        }
    }

    //配方管理相关

    public @Nullable NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null) {
            return null;
        }
        if (!(recipe instanceof Keyed)) {
            return null;
        }
        return ((Keyed) recipe).getKey();
    }

    public boolean disableRecipe(NamespacedKey recipeKey, boolean save) {
        if (save)
            saveDisabledRecipesData(recipeKey);
        addRecipeToRemovedRecipeRecycleBin(recipeKey);
        return removeRecipe(recipeKey);
    }

    public boolean removeCraftorithmRecipe(String recipeGroupName, boolean deleteFile) {
        for (Map.Entry<RecipeType, Map<String, RecipeGroup>> recipeTypeMapEntry : pluginRecipeMap.entrySet()) {
            RecipeType recipeType = recipeTypeMapEntry.getKey();
            Map<String, RecipeGroup> recipeGroupMap = recipeTypeMapEntry.getValue();
            if (recipeGroupMap.containsKey(recipeGroupName)) {
                RecipeGroup recipeGroup = recipeGroupMap.get(recipeGroupName);
                if (recipeGroup == null)
                    return false;
                recipeRemoverMap.get(recipeType).accept(recipeGroup.groupRecipeKeys());
                BukkitConfigWrapper recipeConfig = recipeGroupMap.get(recipeGroupName).recipeGroupConfig();
                if (deleteFile) {
                    recipeConfig.configFile().delete();
                }
                recipeGroupMap.remove(recipeGroupName);
                return true;
            }
        }
        return false;
    }

    /**
     * 保存一个被禁用的配方
     * @param recipeKey 被禁用的配方Key
     */
    private void saveDisabledRecipesData(NamespacedKey recipeKey) {
        if (recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) && PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value())
            return;
        List<String> disabledRecipes = disabledRecipesConfigWrapper.config().getStringList("recipes");
        String keyStr = recipeKey.toString();
        if (!disabledRecipes.contains(keyStr))
            disabledRecipes.add(keyStr);
        disabledRecipes.add(recipeKey.toString());
        disabledRecipesConfigWrapper.set("recipes", disabledRecipes);
        disabledRecipesConfigWrapper.saveConfig();
    }

    private void addRecipeToRemovedRecipeRecycleBin(List<NamespacedKey> recipeKeys) {
        for (NamespacedKey recipeKey : recipeKeys) {
            Recipe recipe = getRecipe(recipeKey);
            if (recipe == null)
                continue;
            disabledRecipes.add(recipe);
        }
    }

    public boolean isCraftorithmRecipe(String recipeName) {
        //todo 意义不明方法,待删除
        return getRecipeGroups().contains(recipeName);
    }

    @Nullable
    public RecipeGroup getRecipeGroup(String groupName) {
        for (Map.Entry<SimpleRecipeTypes, Map<String, RecipeGroup>> recipeTypeMapEntry : pluginRecipeMap.entrySet()) {
            Map<String, RecipeGroup> recipeGroupMap = recipeTypeMapEntry.getValue();
            if (recipeGroupMap.containsKey(groupName)) {
                return recipeGroupMap.get(groupName);
            }
        }
        return null;
    }

    public YamlConfiguration getRecipeConfig(NamespacedKey recipeKey) {
        if (!recipeKey.getNamespace().equals(PLUGIN_RECIPE_NAMESPACE))
            return null;

        for (Map.Entry<SimpleRecipeTypes, Map<String, RecipeGroup>> recipeTypeMapEntry : pluginRecipeMap.entrySet()) {
            Map<String, RecipeGroup> recipeGroupMap = recipeTypeMapEntry.getValue();
            for (String recipeGroupName : recipeGroupMap.keySet()) {
                RecipeGroup recipeGroup = recipeGroupMap.get(recipeGroupName);
                if (recipeGroup.contains(recipeKey)) {
                    BukkitConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
                    return configWrapper == null ? null : configWrapper.config();
                }
            }
        }
        return null;
    }

    public boolean removeRecipe(Recipe recipe) {

    }

    public boolean removeRecipe(NamespacedKey recipeKey) {
        Recipe recipe =
    }

    public List<String> getRecipeGroups() {
        //TODO 可能需要缓存一下
        List<String> recipes = new ArrayList<>();
        for (Map<String, RecipeGroup> value : pluginRecipeMap.values()) {
            recipes.addAll(value.keySet());
        }
        return recipes;
    }

    public Map<NamespacedKey, Recipe> serverRecipesCache() {
        return serverRecipesCache;
    }

    public boolean supportPotionMix() {
        return supportPotionMix;
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        if (lifeCycle.equals(LifeCycle.ENABLE)) {
            //设置各类型配方的注册方法
            regDefaultRecipeTypes();
        } else {
            reloadRecipeManager();
        }
    }

}
