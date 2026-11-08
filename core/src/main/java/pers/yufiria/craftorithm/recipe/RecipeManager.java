package pers.yufiria.craftorithm.recipe;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.scheduler.CrypticLibRunnable;
import crypticlib.util.IOHelper;
import crypticlib.util.Lazy;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.config.DisabledRecipesConfig;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.database.dao.DiscoveredRecipeDao;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.parser.RecipeParser;
import pers.yufiria.craftorithm.recipe.register.RecipeRegister;
import pers.yufiria.craftorithm.recipe.sync.RecipeSyncManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ENABLE),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, priority = 2, isAsync = true),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE, priority = 2)
    }
)
public enum RecipeManager implements LifecycleTask {

    INSTANCE;
    public final File RECIPE_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
    public final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";
    private final Map<NamespacedKey, ParsedRecipe> craftorithmRecipes = new ConcurrentHashMap<>();
    private final Map<String, NamespacedKey> recipeFileNameToKeyMap = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, Recipe> disabledRecipes = new ConcurrentHashMap<>();
    private final Set<NamespacedKey> serverRecipeKeys = ConcurrentHashMap.newKeySet();
    private final Map<String, RecipeGroup> recipeGroupMap = new ConcurrentHashMap<>();
    private volatile CompletableFuture<Void> reloadCompletion = null;

    //以下视图缓存针对的底层集合均为final字段且只做clear/重填与单条增删, 引用从不替换, 视图恒有效, 无需失效
    private final Lazy<Map<NamespacedKey, ParsedRecipe>> lazyCraftorithmRecipesView = Lazy.of(() -> Collections.unmodifiableMap(craftorithmRecipes));
    private final Lazy<Set<NamespacedKey>> lazyServerRecipeKeysView = Lazy.of(() -> Collections.unmodifiableSet(serverRecipeKeys));
    private final Lazy<Set<NamespacedKey>> lazyDisableRecipeKeysView = Lazy.of(() -> Collections.unmodifiableSet(disabledRecipes.keySet()));

    //配方加载相关
    private void reloadRecipeManager(CompletableFuture<Void> completion) {
        resetRecipes();
        loadRecipesFromConfig(() -> {
            //无论中间哪一步抛异常都要走到收尾: 否则重载 future 永不完成, 只能等30秒超时兜底, 期间玩家配方不更新
            try {
                loadServerRecipeKeys();
                reloadDisabledRecipes();
            } catch (Throwable throwable) {
                CrypticLib.error("Unexpected error while preparing reloaded recipes", throwable);
            } finally {
                CrypticLibBukkit.scheduler().syncLater(() -> {
                    try {
                        //配方加载完毕后同步给玩家
                        RecipeSyncManager.INSTANCE.onRecipesChanged();
                        //从数据库恢复玩家已解锁配方（异步，避免阻塞主线程）
                        CrypticLibBukkit.scheduler().async(DiscoveredRecipeDao.INSTANCE::loadOnlinePlayersDiscoveredRecipes);
                    } catch (Throwable throwable) {
                        CrypticLib.error("Unexpected error while finishing recipe reload", throwable);
                    } finally {
                        //complete本轮启动时捕获的future而非读字段, 防止旧轮链路超时残留晚到时误触发下一轮已重建的future
                        completion.complete(null);
                    }
                }, 2L);
            }
        });
    }

    /**
     * 重置配方
     * 将会删除所有由本插件及使用本插件提供的API添加的配方
     * 同时还原被删除的其他配方
     */
    private void resetRecipes() {
        //删除所有由插件添加的配方
        craftorithmRecipes.forEach((recipeKey, parsed) -> {
            RecipeType recipeType = parsed.recipeType();
            recipeType.recipeRegister().unregisterRecipe(recipeKey, false);
        });
        craftorithmRecipes.clear();
        serverRecipeKeys.clear();
        recipeFileNameToKeyMap.clear();
        recipeGroupMap.clear();

        //重置所有配方的结果处理器
        ResultProcessorManager.INSTANCE.resetRecipeProcessors();

        //还原被禁用的配方里本次重载后不再被禁用的配方
        Set<String> disabledRecipeKeyStrSet = new HashSet<>(DisabledRecipesConfig.RECIPES.value());
        disabledRecipes.entrySet().removeIf(entry -> {
            NamespacedKey recipeKey = entry.getKey();
            Recipe recipe = entry.getValue();
            boolean shouldRestore = !disabledRecipeKeyStrSet.contains(recipeKey.toString());
            boolean result;
            try {
                if (shouldRestore) {
                    result = registerRecipe(recipe, false);
                } else {
                    result = false;
                }
            } catch (Throwable throwable) {
                CrypticLib.error(
                    "Failed to restore recipe <recipe_key>",
                    Map.of(
                        "<recipe_key>", recipeKey.asString()
                    ),
                    throwable
                );
                result = false;
            }
            return shouldRestore && result;
        });
    }

    private void loadRecipesFromConfig(Runnable callback) {
        CrypticLibBukkit.scheduler().async(() -> {
            if (!RECIPE_FILE_FOLDER.exists()) {
                boolean mkdirResult = RECIPE_FILE_FOLDER.mkdir();
                if (!mkdirResult) {
                    CrypticLib.error("Cannot create recipes folder: " + RECIPE_FILE_FOLDER.getPath() + ", skip loading recipes");
                    //提前返回也要执行callback, 且与正常结束一致调度到主线程运行
                    CrypticLibBukkit.scheduler().sync(callback);
                    return;
                }
            }
            long parseStart = System.currentTimeMillis();
            List<ParsedRecipe> parsedRecipes = IOHelper.allYamlFiles(RECIPE_FILE_FOLDER)
                .stream()
                .map(file -> {
                    BukkitConfigWrapper recipeConfig = new BukkitConfigWrapper(file);
                    String recipeName = deriveRecipeName(recipeConfig);
                    try {
                        return parseRecipeFromConfig(recipeName, recipeConfig);
                    } catch (Throwable throwable) {
                        LangUtils.info(Languages.RECIPE_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<recipe_name>", recipeName));
                        CrypticLib.error("Failed to parse recipe " + recipeName, throwable);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
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
        return recipeName.substring(0, lastDotIndex).toLowerCase(Locale.ROOT);
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
        RecipeType recipeType = RecipeTypeRegistry.INSTANCE.getByKey(typeId);
        if (recipeType == null) {
            throw new RecipeLoadException("Unknown recipe type of " + recipeFileName + "(" + recipeId + "): " + typeId);
        }
        RecipeParser<?> recipeParser = recipeType.recipeParser();
        Recipe recipe = recipeParser.parse(recipeId, recipeConfig);
        if (recipe == null) {
            CrypticLib.warn("Load recipe " + recipeFileName + "(" + recipeId + ") failed");
            return null;
        }
        NamespacedKey recipeKey = getRecipeKey(recipe);
        if (recipeKey == null) {
            CrypticLib.warn("Recipe " + recipeFileName + "(" + recipeId + ") has no valid NamespacedKey");
            return null;
        }
        File recipeFile = configWrapper.configFile();
        long createTime = recipeFile.exists() ? recipeFile.lastModified() : System.currentTimeMillis();
        return new ParsedRecipe(recipeFileName, recipeKey, recipe, recipeType, configWrapper, createTime);
    }

    public boolean registerParsedRecipe(ParsedRecipe parsed, boolean sync2Client) {
        Recipe recipe = parsed.recipe();
        RecipeType recipeType = parsed.recipeType();
        BukkitConfigWrapper configWrapper = parsed.configWrapper();
        YamlConfiguration recipeConfig = configWrapper.config();
        String recipeFileName = parsed.recipeFileName();
        NamespacedKey recipeKey = parsed.recipeKey();

        RecipeRegister recipeRegister = recipeType.recipeRegister();
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
        boolean result = recipeLoadFromConfigEvent.recipeRegister().registerRecipe(recipe, sync2Client);
        if (result) {
            craftorithmRecipes.put(recipeKey, parsed);
            serverRecipeKeys.add(recipeKey);
            recipeFileNameToKeyMap.put(recipeFileName, recipeKey);
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
            CrypticLib.warn("Register recipe " + recipeFileName + "(" + recipeKey + ") failed");
        }
        return result;
    }

    /**
     * 从配置文件里加载并注册一个配方
     *
     * @param recipeFileName      配方文件的名字,当配方文件里不存在recipe_id这个配置时,会尝试使用文件名字作为配方id
     * @param recipeConfigWrapper 配方的配置文件
     * @param sync2Client       是否在注册后同步给客户端
     * @return
     */
    public boolean loadRecipeFromConfig(String recipeFileName, BukkitConfigWrapper recipeConfigWrapper, boolean sync2Client) {
        ParsedRecipe parsed = parseRecipeFromConfig(recipeFileName, recipeConfigWrapper);
        if (parsed == null) {
            return false;
        }
        return registerParsedRecipe(parsed, sync2Client);
    }

    private void loadServerRecipeKeys() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        serverRecipeKeys.clear();
        while (recipeIterator.hasNext()) {
            //不知道为什么spigot 26.3酿造配方为什么没有实现toBukkitRecipe导致这里会报错
            try {
                Recipe recipe = recipeIterator.next();
                NamespacedKey recipeKey = getRecipeKey(recipe);
                if (recipeKey != null)
                    serverRecipeKeys.add(recipeKey);
            } catch (Throwable throwable) {
                CrypticLib.error("Error when load server recipe keys: <message>", Map.of("<message>", throwable.getMessage()));
            }
        }
    }

    private void reloadDisabledRecipes() {
        List<String> disabledRecipes = new ArrayList<>(DisabledRecipesConfig.RECIPES.value());
        if (PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value()) {
            //已收录的key用集合判定, 避免在不断增长的列表上做线性查找
            Set<String> knownRecipeKeys = new HashSet<>(disabledRecipes);
            for (NamespacedKey recipeKey : serverRecipeKeys) {
                if (!recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    continue;
                }
                String recipeKeyStr = recipeKey.toString();
                if (knownRecipeKeys.add(recipeKeyStr)) {
                    disabledRecipes.add(recipeKeyStr);
                }
            }
        }
        for (String recipeKeyStr : disabledRecipes) {
            NamespacedKey recipeKey = NamespacedKey.fromString(recipeKeyStr);
            if (recipeKey == null) {
                //非法键不能让整轮重载半途中断, 逐条告警后跳过
                CrypticLib.warn("Invalid recipe key in disabled recipes: " + recipeKeyStr + ", skipped");
                continue;
            }
            if (this.disabledRecipes.containsKey(recipeKey)) {
                //如果已经在这里面，就说明已经禁用了，不再重复禁用
                continue;
            }
            try {
                boolean result = disableRecipe(recipeKey, false, false);
                if (!result) {
                    CrypticLib.warn(
                        "Disable recipe <recipe_key> failed, please check is a valid recipe key!"
                        , Map.of("<recipe_key>", recipeKeyStr)
                    );
                }
            } catch (Throwable throwable) {
                CrypticLib.error("Failed to disable recipe " + recipeKeyStr, throwable);
            }
        }
    }

    //配方管理相关

    /**
     * 根据给定NamespacedKey获取配方实例,会从插件配方和服务器配方中寻找
     */
    public @Nullable Recipe getRecipe(NamespacedKey recipeKey) {
        ParsedRecipe parsed = craftorithmRecipes.get(recipeKey);
        if (parsed != null) {
            return parsed.recipe();
        }
        return Bukkit.getRecipe(recipeKey);
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
     *
     * @param recipeKey
     * @param save
     * @return
     */
    public boolean disableRecipe(NamespacedKey recipeKey, boolean save, boolean sync2Client) {
        if (save)
            addDisabledRecipes2Config(recipeKey);
        Recipe recipe = getRecipe(recipeKey);
        boolean result = unregisterRecipe(recipeKey, sync2Client);
        if (result) {
            disabledRecipes.put(recipeKey, recipe);
            serverRecipeKeys.remove(recipeKey);
            removeFromRecipeGroup(recipe);
        }
        return result;
    }

    /**
     * 把配方从所属配方组里摘掉, 组为空时一并移除。
     * 配方组只在重载时整体重建, 单条移除/禁用必须同步清理, 否则 RecipeApi 会返回已失效的配方
     */
    private void removeFromRecipeGroup(@Nullable Recipe recipe) {
        if (recipe == null) {
            return;
        }
        NamespacedKey recipeKey = getRecipeKey(recipe);
        if (recipeKey == null) {
            return;
        }
        recipeGroupMap.entrySet().removeIf(entry -> {
            RecipeGroup recipeGroup = entry.getValue();
            if (!recipeGroup.contains(recipeKey)) {
                return false;
            }
            recipeGroup.removeRecipe(recipe);
            return recipeGroup.isEmpty();
        });
    }

    /**
     * 重新启用一个已经被禁用的配方
     */
    public boolean restoreDisabledRecipe(NamespacedKey recipeKey) {
        if (!disabledRecipes.containsKey(recipeKey)) {
            return false;
        }
        Recipe recipe = disabledRecipes.get(recipeKey);
        boolean result = registerRecipe(recipe, true);
        if (!result) {
            CrypticLib.warn("Restore disabled recipe <recipe_key> failed!", Map.of("<recipe_key>", recipeKey.asString()));
            return false;
        }
        disabledRecipes.remove(recipeKey);
        serverRecipeKeys.add(recipeKey);
        CrypticLibBukkit.scheduler().async(() -> {
            List<String> disabledRecipes = new ArrayList<>(DisabledRecipesConfig.RECIPES.value());
            String keyStr = recipeKey.toString();
            disabledRecipes.remove(keyStr);
            DisabledRecipesConfig.RECIPES.setValue(disabledRecipes);
            DisabledRecipesConfig.RECIPES.saveConfig();
        });
        return true;
    }

    /**
     * 保存一个被禁用的配方
     *
     * @param recipeKey 被禁用的配方Key
     */
    private void addDisabledRecipes2Config(NamespacedKey recipeKey) {
        if (recipeKey.getNamespace().equals(NamespacedKey.MINECRAFT) && PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value())
            return;
        CrypticLibBukkit.scheduler().async(() -> {
            List<String> disabledRecipes = new ArrayList<>(DisabledRecipesConfig.RECIPES.value());
            String keyStr = recipeKey.toString();
            if (!disabledRecipes.contains(keyStr))
                disabledRecipes.add(keyStr);
            DisabledRecipesConfig.RECIPES.setValue(disabledRecipes);
            DisabledRecipesConfig.RECIPES.saveConfig();
        });
    }

    /**
     * 注册一个配方到服务器里
     * @param recipe 要注册的配方
     * @param sync2Client 是否同步到客户端
     * @return 是否注册成功
     */
    private boolean registerRecipe(Recipe recipe, boolean sync2Client) {
        RecipeType recipeType = RecipeTypeRegistry.INSTANCE.typeOf(recipe);
        return recipeType.recipeRegister().registerRecipe(recipe, sync2Client);
    }

    /**
     * 从服务器里卸载一个配方
     * @param recipeKey 要删除的配方key
     * @param sync2Client 是否同步到客户端
     * @return 是否卸载成功
     */
    private boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        Recipe recipe = getRecipe(recipeKey);
        RecipeType recipeType = RecipeTypeRegistry.INSTANCE.typeOf(recipe);
        return recipeType.recipeRegister().unregisterRecipe(recipeKey, sync2Client);
    }

    public boolean removeCraftorithmRecipe(String recipeId, boolean deleteFile, boolean sync2Client) {
        NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeId);
        boolean result = unregisterRecipe(recipeKey, sync2Client);
        if (result) {
            ParsedRecipe removed = craftorithmRecipes.remove(recipeKey);
            serverRecipeKeys.remove(recipeKey);
            if (removed != null) {
                recipeFileNameToKeyMap.remove(removed.recipeFileName());
                removeFromRecipeGroup(removed.recipe());
                if (deleteFile) {
                    BukkitConfigWrapper configWrapper = removed.configWrapper();
                    CrypticLibBukkit.scheduler().async(configWrapper::deleteConfigFile);
                }
            }
        }
        return result;
    }

    /**
     * 查看某名字的配方是否存在
     *
     * @param recipeName 需要查询的配方名字
     * @return 是否存在
     */
    public boolean containsRecipe(String recipeName) {
        NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
        return containsRecipe(recipeKey);
    }

    /**
     * 检查某配方是否存在
     *
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

    public @Nullable NamespacedKey getRecipeKeyByFileName(String recipeFileName) {
        return recipeFileNameToKeyMap.get(recipeFileName);
    }

    public @Nullable String getRecipeFileNameByKey(NamespacedKey recipeKey) {
        ParsedRecipe parsed = craftorithmRecipes.get(recipeKey);
        return parsed != null ? parsed.recipeFileName() : null;
    }

    public @Nullable Long getRecipeCreateTime(NamespacedKey recipeKey) {
        ParsedRecipe parsed = craftorithmRecipes.get(recipeKey);
        return parsed != null ? parsed.createTime() : null;
    }

    public List<Map.Entry<NamespacedKey, Recipe>> getRecipesByType(RecipeType type) {
        return craftorithmRecipes.entrySet().stream()
            .filter(entry -> type.isThisType(entry.getValue().recipe()))
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().recipe()))
            .toList();
    }

    public @Unmodifiable Map<NamespacedKey, ParsedRecipe> craftorithmRecipes() {
        return lazyCraftorithmRecipesView.get();
    }

    public @Nullable RecipeType getRecipeTypeByKey(String typeId) {
        return RecipeTypeRegistry.INSTANCE.getByKey(typeId);
    }

    public List<RecipeType> getRecipeTypes() {
        return RecipeTypeRegistry.INSTANCE.all();
    }

    public @Nullable BukkitConfigWrapper getRecipeConfigWrapper(NamespacedKey recipeKey) {
        ParsedRecipe parsed = craftorithmRecipes.get(recipeKey);
        return parsed != null ? parsed.configWrapper() : null;
    }

    /**
     * 获取配方相关的配置文件
     * 若不是由Craftorithm添加的配方,可能没有相关文件
     *
     * @param recipeKey 配方key
     */
    public @Nullable YamlConfiguration getRecipeConfig(NamespacedKey recipeKey) {
        ParsedRecipe parsed = craftorithmRecipes.get(recipeKey);
        if (parsed == null)
            return null;
        return parsed.configWrapper().config();
    }

    public List<String> getRecipeGroups() {
        return recipeGroupMap.keySet().stream().toList();
    }

    public @Unmodifiable Set<NamespacedKey> serverRecipeKeys() {
        return lazyServerRecipeKeysView.get();
    }

    public @Unmodifiable Set<NamespacedKey> disableRecipeKeys() {
        return lazyDisableRecipeKeysView.get();
    }

    public boolean supportBrewingRecipe() {
        return RecipeTypeRegistry.INSTANCE.supportBrewingRecipe();
    }

    public CompletableFuture<Void> getReloadCompletion() {
        return reloadCompletion;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        switch (lifeCycle) {
            case ENABLE -> {
                //注册各内置配方类型, 必须在读取任何配方配置之前完成
                RecipeTypeRegistry.INSTANCE.registerDefaults();
            }
            case RELOAD -> {
                CompletableFuture<Void> completion = new CompletableFuture<>();
                reloadCompletion = completion;
                CrypticLibBukkit.scheduler().sync(() -> reloadRecipeManager(completion));
            }
            case DISABLE -> {
                //顺序不能颠倒: 还原禁用配方时依赖类型表, 必须先重置配方再清空类型目录
                resetRecipes();
                RecipeTypeRegistry.INSTANCE.clear();
            }
        }
    }

    private class RecipeRegisterTask extends CrypticLibRunnable {

        List<ParsedRecipe> parsedRecipes;
        int useTick = 0;
        //配方加载完毕后执行的代码
        final Runnable callback;
        long useMilliseconds = 0;
        //收尾是否已执行: end() 既可能在 try 内调用, 也可能在 catch 里再次调用
        boolean ended = false;

        RecipeRegisterTask(File folder, Runnable doneActions, List<ParsedRecipe> parsedRecipes) {
            this.callback = doneActions;
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException(folder.getAbsolutePath() + " is not a directory");
            }
            this.parsedRecipes = parsedRecipes;
        }

        void start() {
            this.syncTimer(1L, 1L);
        }

        void end() {
            //回调只允许执行一次, 否则异常路径会让收尾逻辑(日志、更新配方、完成future)重复执行
            if (ended) {
                return;
            }
            ended = true;
            this.cancel();
            CrypticLib.info("Loaded " + craftorithmRecipes.size() + " recipes in " + useTick + " ticks(" + useMilliseconds + "ms)");
            if (callback != null) {
                try {
                    callback.run();
                } catch (Throwable throwable) {
                    CrypticLib.error("Unexpected error during recipe loading callback", throwable);
                }
            }
        }

        @Override
        public void run() {
            if (isCancelled()) {
                return;
            }
            try {
                //配置为0或负数时批次为空且剩余列表不变, 任务会每tick空转且配方永不加载完成, 兜底为1
                int maxRegRecipePerTick = Math.max(1, PluginConfigs.MAX_REG_RECIPE_PER_TICK.value());
                if (parsedRecipes.size() <= maxRegRecipePerTick) {
                    registerRecipes(parsedRecipes);
                    end();
                } else {
                    List<ParsedRecipe> batch = parsedRecipes.subList(0, maxRegRecipePerTick);
                    parsedRecipes = parsedRecipes.subList(maxRegRecipePerTick, parsedRecipes.size());
                    registerRecipes(batch);
                }
            } catch (Throwable t) {
                CrypticLib.error("Unexpected error during recipe loading, aborting...", t);
                end();
            }
        }

        void registerRecipes(List<ParsedRecipe> batch) {
            long startTime = System.currentTimeMillis();
            int recipeNum = 0;
            for (ParsedRecipe parsed : batch) {
                try {
                    boolean result = registerParsedRecipe(parsed, false);
                    if (result) {
                        recipeNum++;
                    }
                } catch (Throwable throwable) {
                    LangUtils.info(Languages.RECIPE_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<recipe_name>", parsed.recipeFileName()));
                    CrypticLib.error("Failed to register recipe " + parsed.recipeFileName(), throwable);
                }
            }
            long thisTickUseMs = System.currentTimeMillis() - startTime;
            CrypticLib.info("Registered " + recipeNum + " recipes in " + thisTickUseMs + "ms");
            useMilliseconds += thisTickUseMs;
            useTick++;
        }

    }
}
