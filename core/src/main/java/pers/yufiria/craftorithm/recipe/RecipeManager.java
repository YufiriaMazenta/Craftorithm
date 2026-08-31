package pers.yufiria.craftorithm.recipe;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.scheduler.CrypticLibRunnable;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.parser.RecipeParser;
import pers.yufiria.craftorithm.recipe.register.RecipeRegister;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ENABLE),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, priority = 2)
    }
)
public enum RecipeManager implements LifecycleTask {

    INSTANCE;
    public final File RECIPE_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
    public final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";
    private final BukkitConfigWrapper disabledRecipesConfigWrapper = new BukkitConfigWrapper(Craftorithm.instance(), "disabled_recipes.yml");
    private final Map<String, RecipeType> recipeTypes = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, Recipe> craftorithmRecipes = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, BukkitConfigWrapper> recipeConfigWrapperMap = new ConcurrentHashMap<>();
    private final Map<String, NamespacedKey> recipeFileNameToKeyMap = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, String> recipeKeyToFileNameMap = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, Long> recipeCreateTimeMap = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, Recipe> disabledRecipes = new ConcurrentHashMap<>();
    private final Set<NamespacedKey> serverRecipeKeys = ConcurrentHashMap.newKeySet();
    private final Map<String, RecipeGroup> recipeGroupMap = new ConcurrentHashMap<>();
    private Boolean supportPotionMix;
    private final AtomicBoolean isReloadingRecipeManager = new AtomicBoolean(false);
    private volatile CompletableFuture<Void> reloadCompletion;

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
        regRecipeType(SimpleRecipeTypes.VANILLA_SMITHING_TRIM);
        regRecipeType(SimpleRecipeTypes.VANILLA_STONECUTTING);
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            regRecipeType(SimpleRecipeTypes.ANVIL);
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
        if (isReloadingRecipeManager.get()) {
            return;
        }
        isReloadingRecipeManager.set(true);
        reloadCompletion = new CompletableFuture<>();
        resetRecipes();
        loadRecipesFromConfig(() -> {
            loadServerRecipeKeys();
            reloadDisabledRecipes();
            CrypticLibBukkit.scheduler().syncLater(() -> {
                isReloadingRecipeManager.set(false);
                //所有操作进行完毕后，为玩家更新配方信息
                CraftorithmRecipeRegistry.findImpl().updateRecipes();
                reloadCompletion.complete(null);
            }, 2L);
        });
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
            recipeType.recipeRegister().unregisterRecipe(recipeKey, false);
        });
        craftorithmRecipes.clear();
        serverRecipeKeys.clear();
        recipeCreateTimeMap.clear();
        recipeFileNameToKeyMap.clear();
        recipeKeyToFileNameMap.clear();
        recipeConfigWrapperMap.clear();
        recipeGroupMap.clear();

        //重置所有配方的结果处理器
        ResultProcessorManager.INSTANCE.resetRecipeProcessors();

        //还原被禁用的配方
        for (Recipe recipe : disabledRecipes.values()) {
            RecipeType recipeType = getRecipeType(recipe);
            recipeType.recipeRegister().registerRecipe(recipe, false);
        }
        disabledRecipes.clear();
    }

    private void loadRecipesFromConfig(Runnable callback) {
        CrypticLibBukkit.scheduler().async(() -> {
            if (!RECIPE_FILE_FOLDER.exists()) {
                boolean mkdirResult = RECIPE_FILE_FOLDER.mkdir();
                if (!mkdirResult)
                    return;
            }
            List<BukkitConfigWrapper> recipeConfigs = IOHelper.allYamlFiles(RECIPE_FILE_FOLDER)
                .stream()
                .map(BukkitConfigWrapper::new)
                .toList();
            long parseStart = System.currentTimeMillis();
            List<ParsedRecipe> parsedRecipes = new ArrayList<>();
            for (BukkitConfigWrapper configWrapper : recipeConfigs) {
                String recipeName = deriveRecipeName(configWrapper);
                try {
                    ParsedRecipe parsed = parseRecipeFromConfig(recipeName, configWrapper);
                    if (parsed != null) {
                        parsedRecipes.add(parsed);
                    }
                } catch (Throwable throwable) {
                    LangUtils.info(Languages.RECIPE_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<recipe_name>", recipeName));
                    throwable.printStackTrace();
                }
            }
            long parseElapsed = System.currentTimeMillis() - parseStart;
            CrypticLib.info("Parsed " + parsedRecipes.size() + " recipes in " + parseElapsed + "ms");
            new RecipeRegisterTask(
                RECIPE_FILE_FOLDER,
                callback,
                parsedRecipes
            ).start();
        });
    }

    private String deriveRecipeName(BukkitConfigWrapper configWrapper) {
        String recipeName = configWrapper.configFile().getPath().substring(RECIPE_FILE_FOLDER.getPath().length() + 1);
        recipeName = recipeName.replace("\\", "/");
        recipeName = recipeName.replace('-', '_');
        int lastDotIndex = recipeName.lastIndexOf(".");
        return recipeName.substring(0, lastDotIndex).toLowerCase();
    }

    public @Nullable ParsedRecipe parseRecipeFromConfig(String recipeFileName, BukkitConfigWrapper configWrapper) {
        YamlConfiguration recipeConfig = configWrapper.config();
        String recipeId;
        if (recipeConfig.contains("recipe_id")) {
            recipeId = recipeConfig.getString("recipe_id");
        } else {
            recipeId = recipeFileName;
        }
        String typeId = recipeConfig.getString("type");
        if (typeId == null) {
            throw new RecipeLoadException("Unknown recipe type of " + recipeFileName + "(" + recipeId + "): " + null);
        }
        RecipeType recipeType = recipeTypes.get(typeId);
        if (recipeType == null) {
            throw new RecipeLoadException("Unknown recipe type of " + recipeFileName + "(" + recipeId + "): " + typeId);
        }
        RecipeParser<?> recipeParser = recipeType.recipeParser();
        Recipe recipe = recipeParser.parse(recipeId, recipeConfig);
        if (recipe == null) {
            BukkitMsgSender.INSTANCE.info("&eLoad recipe " + recipeFileName + "(" + recipeId + ") failed");
            return null;
        }
        File recipeFile = configWrapper.configFile();
        long createTime = recipeFile.exists() ? recipeFile.lastModified() : System.currentTimeMillis();
        return new ParsedRecipe(recipeFileName, recipeId, recipe, recipeType, configWrapper, createTime);
    }

    public boolean registerParsedRecipe(ParsedRecipe parsed, boolean updateRecipes) {
        Recipe recipe = parsed.recipe();
        RecipeType recipeType = parsed.recipeType();
        BukkitConfigWrapper configWrapper = parsed.configWrapper();
        YamlConfiguration recipeConfig = configWrapper.config();
        String recipeFileName = parsed.recipeName();
        String recipeId = parsed.recipeId();

        RecipeRegister recipeRegister = recipeType.recipeRegister();
        NamespacedKey recipeKey = Objects.requireNonNull(getRecipeKey(recipe));
        RecipeLoadFromConfigEvent recipeLoadFromConfigEvent = new RecipeLoadFromConfigEvent(
            recipe,
            recipeKey,
            recipeConfig,
            recipeRegister
        );
        recipeLoadFromConfigEvent.call();
        if (recipeLoadFromConfigEvent.isCancelled()) {
            return false;
        }
        boolean result = recipeLoadFromConfigEvent.recipeRegister().registerRecipe(recipeLoadFromConfigEvent.recipe(), updateRecipes, recipeConfig);
        if (result) {
            craftorithmRecipes.put(recipeKey, recipe);
            serverRecipeKeys.add(recipeKey);
            recipeConfigWrapperMap.put(recipeKey, configWrapper);
            recipeFileNameToKeyMap.put(recipeFileName, recipeKey);
            recipeKeyToFileNameMap.put(recipeKey, recipeFileName);
            recipeCreateTimeMap.put(recipeKey, parsed.createTime());
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
            BukkitMsgSender.INSTANCE.info("&eRegister recipe " + recipeFileName + "(" + recipeId + ") failed");
        }
        return result;
    }

    /**
     * 从配置文件里加载并注册一个配方
     *
     * @param recipeFileName      配方文件的名字,当配方文件里不存在recipe_id这个配置时,会尝试使用文件名字作为配方id
     * @param recipeConfigWrapper 配方的配置文件
     * @param updateRecipes 是否向玩家更新配方列表
     * @return
     */
    public boolean loadRecipeFromConfig(String recipeFileName, BukkitConfigWrapper recipeConfigWrapper, boolean updateRecipes) {
        ParsedRecipe parsed = parseRecipeFromConfig(recipeFileName, recipeConfigWrapper);
        if (parsed == null) {
            return false;
        }
        return registerParsedRecipe(parsed, updateRecipes);
    }

    private void loadServerRecipeKeys() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        serverRecipeKeys.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey recipeKey = getRecipeKey(recipe);
            if (recipeKey != null)
                serverRecipeKeys.add(recipeKey);
        }
    }

    private void reloadDisabledRecipes() {
        disabledRecipesConfigWrapper.reloadConfig();
        List<String> disabledRecipes = disabledRecipesConfigWrapper.config().getStringList("recipes");
        if (PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value()) {
            for (NamespacedKey recipeKey : serverRecipeKeys) {
                if (recipeKey.getNamespace().equals("minecraft")) {
                    if (disabledRecipes.contains(recipeKey.toString()))
                        continue;
                    disabledRecipes.add(recipeKey.toString());
                }
            }
        }
        for (String recipeKeyStr : disabledRecipes) {
            NamespacedKey recipeKey = NamespacedKey.fromString(recipeKeyStr);
            disableRecipe(recipeKey, false);
        }
    }

    //配方管理相关

    /**
     * 根据给定NamespacedKey获取配方实例,会从插件配方和服务器配方中寻找
     */
    public @Nullable Recipe getRecipe(NamespacedKey recipeKey) {
        Recipe recipe = craftorithmRecipes.get(recipeKey);
        if (recipe == null) {
            return Bukkit.getRecipe(recipeKey);
        }
        return recipe;
    }

    @Contract("null -> null")
    public @Nullable NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null) {
            return null;
        }
        if (!(recipe instanceof Keyed)) {
            return null;
        }
        return ((Keyed) recipe).getKey();
    }

    /**
     * 禁用某配方
     * 会将指定配方从服务器里卸载,并存入配方垃圾箱
     * @param recipeKey
     * @param save
     * @return
     */
    public boolean disableRecipe(NamespacedKey recipeKey, boolean save) {
        if (save)
            addDisabledRecipes2Config(recipeKey);
        Recipe recipe = getRecipe(recipeKey);
        boolean result = removeRecipe(recipeKey);
        if (result) {
            disabledRecipes.put(recipeKey, recipe);
            serverRecipeKeys.remove(recipeKey);
        }
        return result;
    }

    /**
     * 重新启用一个已经被禁用的配方
     */
    public boolean restoreDisabledRecipe(NamespacedKey recipeKey) {
        if (!disabledRecipes.containsKey(recipeKey)) {
            return false;
        }
        Recipe recipe = disabledRecipes.get(recipeKey);
        boolean result = getRecipeType(recipe).recipeRegister().registerRecipe(recipe, true);
        disabledRecipes.remove(recipeKey);
        CrypticLibBukkit.scheduler().async(() -> {
            List<String> disabledRecipesConfig = disabledRecipesConfigWrapper.config().getStringList("recipes");
            String keyStr = recipeKey.toString();
            disabledRecipesConfig.remove(keyStr);
            disabledRecipesConfigWrapper.set("recipes", disabledRecipesConfig);
            disabledRecipesConfigWrapper.saveConfig();
        });
        return result;
    }

    /**
     * 保存一个被禁用的配方
     * @param recipeKey 被禁用的配方Key
     */
    private void addDisabledRecipes2Config(NamespacedKey recipeKey) {
        if (recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) && PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value())
            return;
        CrypticLibBukkit.scheduler().async(() -> {
            List<String> disabledRecipes = disabledRecipesConfigWrapper.config().getStringList("recipes");
            String keyStr = recipeKey.toString();
            if (!disabledRecipes.contains(keyStr))
                disabledRecipes.add(keyStr);
            disabledRecipesConfigWrapper.set("recipes", disabledRecipes);
            disabledRecipesConfigWrapper.saveConfig();
        });
    }

    /**
     * 删除一个配方,并通知玩家
     */
    public boolean removeRecipe(NamespacedKey recipeKey) {
        Recipe recipe = getRecipe(recipeKey);
        RecipeType recipeType = getRecipeType(recipe);
        return recipeType.recipeRegister().unregisterRecipe(recipeKey, true);
    }

    public boolean removeCraftorithmRecipe(String recipeId, boolean deleteFile) {
        NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeId);
        boolean result = removeRecipe(recipeKey);
        if (result) {
            craftorithmRecipes.remove(recipeKey);
            serverRecipeKeys.remove(recipeKey);
            recipeCreateTimeMap.remove(recipeKey);
            String removedFileName = recipeKeyToFileNameMap.remove(recipeKey);
            if (removedFileName != null) {
                recipeFileNameToKeyMap.remove(removedFileName);
            }
            if (recipeConfigWrapperMap.containsKey(recipeKey) && deleteFile) {
                BukkitConfigWrapper removed = recipeConfigWrapperMap.remove(recipeKey);
                CrypticLibBukkit.scheduler().async(removed::deleteConfigFile);
            }
        }
        return result;
    }

    /**
     * 查看某名字的配方是否存在
     * @param recipeName 需要查询的配方名字
     * @return 是否存在
     */
    public boolean containsRecipe(String recipeName) {
        NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
        return containsRecipe(recipeKey);
    }

    /**
     * 检查某配方是否存在
     * @param recipeKey
     * @return
     */
    public boolean containsRecipe(NamespacedKey recipeKey) {
        if (craftorithmRecipes.containsKey(recipeKey)) {
            return true;
        }
        return Bukkit.getRecipe(recipeKey) != null;
    }

    public @Nullable RecipeGroup getRecipeGroup(String groupId) {
        return recipeGroupMap.get(groupId);
    }

    /**
     * 通过配方文件名查找配方的NamespacedKey
     * @param recipeFileName 配方文件名
     * @return 配方的NamespacedKey, 如果不存在则返回null
     */
    public @Nullable NamespacedKey getRecipeKeyByFileName(String recipeFileName) {
        return recipeFileNameToKeyMap.get(recipeFileName);
    }

    /**
     * 通过配方的NamespacedKey查找配方文件名
     * @param recipeKey 配方的NamespacedKey
     * @return 配方文件名, 如果不存在则返回null
     */
    public @Nullable String getRecipeFileNameByKey(NamespacedKey recipeKey) {
        return recipeKeyToFileNameMap.get(recipeKey);
    }

    /**
     * 获取配方的创建时间
     * @param recipeKey 配方的NamespacedKey
     * @return 创建时间戳(毫秒), 如果不存在则返回null
     */
    public @Nullable Long getRecipeCreateTime(NamespacedKey recipeKey) {
        return recipeCreateTimeMap.get(recipeKey);
    }

    /**
     * 获取指定类型的所有Craftorithm配方
     * @param type 配方类型
     * @return 该类型的所有配方的NamespacedKey列表
     */
    public List<Map.Entry<NamespacedKey, Recipe>> getRecipesByType(RecipeType type) {
        return craftorithmRecipes.entrySet().stream()
            .filter(entry -> type.isThisType(entry.getValue()))
            .toList();
    }

    public Map<NamespacedKey, Recipe> craftorithmRecipes() {
        return craftorithmRecipes;
    }

    public @Nullable RecipeType getRecipeTypeByKey(String typeId) {
        return recipeTypes.get(typeId);
    }

    public List<RecipeType> getRecipeTypes() {
        return new ArrayList<>(recipeTypes.values());
    }

    public @Nullable BukkitConfigWrapper getRecipeConfigWrapper(NamespacedKey recipeKey) {
        return recipeConfigWrapperMap.get(recipeKey);
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

    public Set<NamespacedKey> serverRecipeKeys() {
        return serverRecipeKeys;
    }

    public Set<NamespacedKey> disableRecipeKeys() {
        return disabledRecipes.keySet();
    }

    public boolean supportPotionMix() {
        if (supportPotionMix == null) {
            try {
                Class.forName("io.papermc.paper.potion.PotionMix");
                supportPotionMix = true;
            } catch (Throwable throwable) {
                supportPotionMix = false;
            }
        }
        return supportPotionMix;
    }

    public boolean isReloadingRecipeManager() {
        return isReloadingRecipeManager.get();
    }

    public CompletableFuture<Void> getReloadCompletion() {
        return reloadCompletion;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        switch (lifeCycle) {
            case ENABLE -> {
                //注册各内置配方类型
                regDefaultRecipeTypes();
            }
            case RELOAD -> {
                reloadRecipeManager();
            }
        }
    }

    public class RecipeRegisterTask extends CrypticLibRunnable {

        private List<ParsedRecipe> parsedRecipes;
        private int useTick = 0;
        //配方加载完毕后执行的代码
        private final Runnable callback;
        private long useMilliseconds = 0;

        public RecipeRegisterTask(File folder, Runnable doneActions, List<ParsedRecipe> parsedRecipes) {
            this.callback = doneActions;
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException(folder.getAbsolutePath() + " is not a directory");
            }
            this.parsedRecipes = parsedRecipes;
        }

        public void start() {
            this.syncTimer(1L, 1L);
        }

        public void end() {
            this.cancel();
            BukkitMsgSender.INSTANCE.info("Loaded " + craftorithmRecipes.size() + " recipes in " + useTick + " ticks(" + useMilliseconds + "ms)");
            if (callback != null) {
                callback.run();
            }
        }

        @Override
        public void run() {
            if (isCancelled()) {
                return;
            }
            try {
                int maxRegRecipePerTick = PluginConfigs.MAX_REG_RECIPE_PER_TICK.value();
                if (parsedRecipes.size() <= maxRegRecipePerTick) {
                    registerRecipes(parsedRecipes);
                    end();
                } else {
                    List<ParsedRecipe> batch = parsedRecipes.subList(0, maxRegRecipePerTick);
                    parsedRecipes = parsedRecipes.subList(maxRegRecipePerTick, parsedRecipes.size());
                    registerRecipes(batch);
                }
            } catch (Throwable t) {
                CrypticLib.info("&cUnexpected error during recipe loading, aborting...");
                t.printStackTrace();
                end();
            }
        }

        public void registerRecipes(List<ParsedRecipe> batch) {
            long startTime = System.currentTimeMillis();
            int recipeNum = 0;
            for (ParsedRecipe parsed : batch) {
                try {
                    boolean result = registerParsedRecipe(parsed, false);
                    if (result) {
                        recipeNum ++;
                    }
                } catch (Throwable throwable) {
                    LangUtils.info(Languages.RECIPE_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<recipe_name>", parsed.recipeName()));
                    throwable.printStackTrace();
                }
            }
            long thisTickUseMs = System.currentTimeMillis() - startTime;
            CrypticLib.info("Registered " + recipeNum + " recipes in " + thisTickUseMs + "ms");
            useMilliseconds += thisTickUseMs;
            useTick ++;
        }

    }
}
