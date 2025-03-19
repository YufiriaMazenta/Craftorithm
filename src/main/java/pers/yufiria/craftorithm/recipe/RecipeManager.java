package pers.yufiria.craftorithm.recipe;

import crypticlib.scheduler.CrypticLibRunnable;
import crypticlib.util.IOHelper;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtManager;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
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
    private final List<Recipe> disabledRecipesCache = new CopyOnWriteArrayList<>();
    private final Map<NamespacedKey, Recipe> serverRecipesCache = new ConcurrentHashMap<>();
    private final Map<String, RecipeGroup> recipeGroupMap = new ConcurrentHashMap<>();
    private Boolean supportPotionMix;

    //配方类型相关

    public boolean regRecipeType(RecipeType type) {
        return regRecipeType(type, false);
    }

    public boolean regRecipeType(RecipeType type, boolean force) {
        if (type == null) {
            return false;
        }
        if (recipeTypes.containsKey(type.typeKey())) {
            if (force) {
                recipeTypes.put(type.typeKey(), type);
                return true;
            } else {
                return false;
            }
        }
        recipeTypes.put(type.typeKey(), type);
        return true;
    }

    private void regDefaultRecipeTypes() {
        regRecipeType(SimpleRecipeTypes.UNKNOWN);
        regRecipeType(SimpleRecipeTypes.VANILLA_SHAPED);
        regRecipeType(SimpleRecipeTypes.VANILLA_SHAPELESS);
        regRecipeType(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE);
        regRecipeType(SimpleRecipeTypes.VANILLA_SMELTING_BLAST);
        regRecipeType(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER);
        regRecipeType(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE);
        regRecipeType(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM);
        regRecipeType(SimpleRecipeTypes.VANILLA_STONECUTTING);
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            regRecipeType(SimpleRecipeTypes.ANVIL);
        }
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20)) {
            regRecipeType(SimpleRecipeTypes.VANILLA_SMITHING_TRIM);
        }
        if (supportPotionMix()) {
            regRecipeType(SimpleRecipeTypes.VANILLA_BREWING);
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

        //删除所有配方的Nbt保留规则
        KeepNbtManager.INSTANCE.resetRecipeKeepNbtRules();

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
        new RecipeLoadTask(RECIPE_FILE_FOLDER).start();
    }

    public boolean loadRecipeFromConfig(String recipeName, BukkitConfigWrapper recipeConfigWrapper) {
        YamlConfiguration recipeConfig = recipeConfigWrapper.config();
        String typeId = recipeConfig.getString("type");
        RecipeType recipeType;
        if (typeId == null) {
            throw new RecipeLoadException("Unknown recipe type of " + recipeName + ": " + null);
        }
        recipeType = recipeTypes.get(typeId);
        if (recipeType == null) {
            throw new RecipeLoadException("Unknown recipe type of " + recipeName + ": " + typeId);
        }
        RecipeLoader<?> recipeLoader = recipeType.recipeLoader();
        Recipe recipe = recipeLoader.loadRecipe(recipeName, recipeConfig);
        if (recipe == null) {
            BukkitMsgSender.INSTANCE.info("&eLoad recipe " + recipeName + " failed");
            return false;
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
                } else {
                    RecipeGroup recipeGroup = new RecipeGroup(Objects.requireNonNull(groupId));
                    recipeGroup.addRecipe(recipe);
                    recipeGroupMap.put(groupId, recipeGroup);
                }
            }
        } else {
            BukkitMsgSender.INSTANCE.info("&eRegister recipe " + recipeName + " failed");
        }
        return result;
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
        boolean result = removeRecipe(recipeKey);
        if (result) {
            addDisabledRecipeCache(recipeKey);
        }
        return result;
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
        return result;
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
        if (supportPotionMix == null) {
            supportPotionMix = CrypticLibBukkit.isPaper();
        }
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

    public class RecipeLoadTask extends CrypticLibRunnable {

        private List<File> recipeFiles;
        private int useTick = 0;

        public RecipeLoadTask(File folder) {
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException(folder.getAbsolutePath() + " is not a directory");
            }
            this.recipeFiles = IOHelper.allYamlFiles(folder);
        }

        public void start() {
            this.syncTimer(1L, 1L);
        }

        public void end() {
            this.cancel();
            BukkitMsgSender.INSTANCE.debug("Loaded " + craftorithmRecipes.size() + " recipes ,took " + useTick + " ticks");
        }

        @Override
        public void run() {
            int maxRegRecipePerTick = PluginConfigs.MAX_REG_RECIPE_PER_TICK.value();
            if (recipeFiles.size() <= maxRegRecipePerTick) {
                loadRecipes(recipeFiles);
                end();
            } else {
                List<File> loadFiles = recipeFiles.subList(0, maxRegRecipePerTick);
                recipeFiles = recipeFiles.subList(maxRegRecipePerTick, recipeFiles.size());
                loadRecipes(loadFiles);
            }
        }

        public void loadRecipes(List<File> files) {
            long startTime = System.currentTimeMillis();
            int recipeNum = 0;
            for (File file : files) {
                String recipeName = file.getPath().substring(RECIPE_FILE_FOLDER.getPath().length() + 1);
                recipeName = recipeName.replace("\\", "/");
                recipeName = recipeName.replace('-', '_');
                int lastDotIndex = recipeName.lastIndexOf(".");
                recipeName = recipeName.substring(0, lastDotIndex).toLowerCase();
                BukkitConfigWrapper recipeConfigWrapper = new BukkitConfigWrapper(file);
                try {
                    boolean result = loadRecipeFromConfig(recipeName, recipeConfigWrapper);
                    if (result) {
                        recipeNum ++;
                    }
                } catch (Throwable throwable) {
                    LangUtils.info(Languages.LOAD_RECIPE_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<recipe_name>", recipeName));
                    throwable.printStackTrace();
                }
            }
            BukkitMsgSender.INSTANCE.debug("Tick " + useTick + ": Loaded " + recipeNum + " recipes, took " + (System.currentTimeMillis() - startTime) + "ms");
            useTick ++;
        }

    }
}
