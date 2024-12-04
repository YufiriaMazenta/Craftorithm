package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.exception.RecipeLoadException;
import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitMsgSender;
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
    private final List<Recipe> disabledRecipesCache;
    private final Map<NamespacedKey, Recipe> serverRecipesCache;
    private final Map<String, RecipeGroup> recipeGroupMap = new ConcurrentHashMap<>();
    private boolean supportPotionMix;

    RecipeManager() {
        disabledRecipesCache = new CopyOnWriteArrayList<>();
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
        regRecipeType(SimpleRecipeTypes.UNKNOWN);
        regRecipeType(SimpleRecipeTypes.VANILLA_SHAPED);
        regRecipeType(SimpleRecipeTypes.VANILLA_SHAPELESS);
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
        for (Recipe recipe : disabledRecipesCache) {
            RecipeType recipeType = getRecipeType(recipe);
            recipeType.recipeRegister().registerRecipe(recipe);
        }
        disabledRecipesCache.clear();
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
                if (recipe == null) {
                    BukkitMsgSender.INSTANCE.info("&eLoad recipe " + recipeName + " failed");
                    continue;
                }
                RecipeRegister recipeRegister = recipeType.recipeRegister();
                boolean result = recipeRegister.registerRecipe(recipe);
                if (result) {
                    NamespacedKey recipeKey = getRecipeKey(recipe);
                    craftorithmRecipes.put(recipeKey, recipe);
                    recipeConfigWrapperMap.put(recipeKey, recipeConfigWrapper);
                    if (recipeConfig.contains("group")) {
                        String groupId = recipeConfig.getString("group");
                        if (recipeGroupMap.containsKey(groupId)) {
                            recipeGroupMap.get(groupId).addRecipe(recipe);
                        }
                    }
                } else {
                    BukkitMsgSender.INSTANCE.info("&eRegister recipe " + recipeName + " failed");
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

    /**
     * 根据给定NamespacedKey获取配方实例,会从插件配方和服务器配方中寻找
     */
    public @Nullable Recipe getRecipe(NamespacedKey namespacedKey) {
        Recipe recipe = craftorithmRecipes.get(namespacedKey);
        if (recipe == null) {
            return serverRecipesCache.get(namespacedKey);
        }
        return recipe;
    }

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
        addDisabledRecipeCache(recipeKey);
        return removeRecipe(recipeKey);
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

    private void addDisabledRecipeCache(NamespacedKey recipeKey) {
        Recipe recipe = getRecipe(recipeKey);
        if (recipe == null)
            return;
        disabledRecipesCache.add(recipe);
    }

    public boolean removeRecipe(NamespacedKey recipeKey) {
        Recipe recipe = getRecipe(recipeKey);
        RecipeType recipeType = getRecipeType(recipe);
        return recipeType.recipeRegister().unregisterRecipe(recipeKey);
    }

    public boolean removeCraftorithmRecipe(NamespacedKey recipeKey, boolean deleteFile) {
        Recipe recipe = getRecipe(recipeKey);
        RecipeType recipeType = getRecipeType(recipe);
        boolean result = recipeType.recipeRegister().unregisterRecipe(recipeKey);
        if (result) {
            if (recipeConfigWrapperMap.containsKey(recipeKey) && deleteFile) {
                BukkitConfigWrapper removed = recipeConfigWrapperMap.remove(recipeKey);
                removed.configFile().delete();
            }
        }
        return result   ;
    }

    public boolean containsRecipe(String recipeKeyStr) {
        NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeKeyStr);
        return containsRecipe(recipeKey);
    }

    public boolean containsRecipe(NamespacedKey recipeKey) {
        return craftorithmRecipes.containsKey(recipeKey);
    }

    public @Nullable RecipeGroup getRecipeGroup(String groupId) {
        return recipeGroupMap.get(groupId);
    }

    public @Nullable RecipeType getRecipeType(String typeId) {
        return recipeTypes.get(typeId);
    }

    /**
     * 获取配方相关的配置文件
     * 若不是由Craftorithm添加的配方,可能没有相关文件
     * @param recipeKey 配方key
     */
    public @Nullable YamlConfiguration getRecipeConfig(NamespacedKey recipeKey) {
        BukkitConfigWrapper recipeConfigWrapper = recipeConfigWrapperMap.get(recipeKey);
        if (recipeConfigWrapper == null)
            return null;
        return recipeConfigWrapper.config();
    }

    public List<String> getRecipeGroups() {
        return recipeGroupMap.keySet().stream().toList();
    }

    public Map<NamespacedKey, Recipe> serverRecipesCache() {
        return serverRecipesCache;
    }

    public boolean supportPotionMix() {
        return supportPotionMix;
    }

    public Map<NamespacedKey, Recipe> craftorithmRecipes() {
        return craftorithmRecipes;
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
