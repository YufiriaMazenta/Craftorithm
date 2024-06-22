package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.exception.UnsupportedVersionException;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.RecipeUtil;
import crypticlib.CrypticLib;
import crypticlib.chat.MsgSender;
import crypticlib.config.ConfigWrapper;
import crypticlib.lang.entry.StringLangEntry;
import crypticlib.platform.IPlatform;
import crypticlib.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public enum RecipeManager {

    INSTANCE;
    public final File RECIPE_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
    private final ConfigWrapper removedRecipesConfigWrapper = new ConfigWrapper(Craftorithm.instance(), "removed_recipes.yml");
    public final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";
    private final Map<RecipeType, Map<String, RecipeGroup>> pluginRecipeMap;
    private final Map<RecipeType, Consumer<Recipe>> recipeRegisterMap;
    private final Map<RecipeType, Consumer<List<NamespacedKey>>> recipeRemoverMap;
    private final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private final List<Recipe> removeRecipeRecycleBin;
    private final Map<NamespacedKey, Recipe> serverRecipesCache;
    private final Map<NamespacedKey, PotionMixRecipe> potionMixRecipeMap;
    private final Map<NamespacedKey, AnvilRecipe> anvilRecipeMap;
    public static final List<RecipeType> UNLOCKABLE_RECIPE_TYPE =
        Collections.unmodifiableList(Arrays.asList(
            RecipeType.SHAPED,
            RecipeType.SHAPELESS,
            RecipeType.COOKING,
            RecipeType.SMITHING,
            RecipeType.STONE_CUTTING,
            RecipeType.RANDOM_COOKING
        ));
    private boolean supportPotionMix;

    RecipeManager() {
        pluginRecipeMap = new ConcurrentHashMap<>();
        recipeRegisterMap = new ConcurrentHashMap<>();
        recipeRemoverMap = new ConcurrentHashMap<>();
        recipeUnlockMap = new ConcurrentHashMap<>();
        potionMixRecipeMap = new ConcurrentHashMap<>();
        anvilRecipeMap = new ConcurrentHashMap<>();
        removeRecipeRecycleBin = new CopyOnWriteArrayList<>();
        serverRecipesCache = new ConcurrentHashMap<>();

        //设置各类型配方的注册方法
        recipeRegisterMap.put(RecipeType.SHAPED, RecipeUtil::regRecipe);
        recipeRemoverMap.put(RecipeType.SHAPED, this::removeRecipes);
        recipeRegisterMap.put(RecipeType.SHAPELESS, RecipeUtil::regRecipe);
        recipeRemoverMap.put(RecipeType.SHAPELESS, this::removeRecipes);
        recipeRegisterMap.put(RecipeType.COOKING, RecipeUtil::regRecipe);
        recipeRemoverMap.put(RecipeType.COOKING, this::removeRecipes);
        recipeRegisterMap.put(RecipeType.STONE_CUTTING, RecipeUtil::regRecipe);
        recipeRemoverMap.put(RecipeType.STONE_CUTTING, this::removeRecipes);
        recipeRegisterMap.put(RecipeType.SMITHING, RecipeUtil::regRecipe);
        recipeRemoverMap.put(RecipeType.SMITHING, this::removeRecipes);
        recipeRegisterMap.put(RecipeType.RANDOM_COOKING, RecipeUtil::regRecipe);
        recipeRemoverMap.put(RecipeType.RANDOM_COOKING, this::removeRecipes);

        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            recipeRegisterMap.put(RecipeType.ANVIL, recipe -> {
                anvilRecipeMap.put(getRecipeKey(recipe), (AnvilRecipe) recipe);
            });
            recipeRemoverMap.put(RecipeType.ANVIL, keys -> {
                for (NamespacedKey key : keys) {
                    anvilRecipeMap.remove(key);
                }
            });
        }

        if (!CrypticLib.platform().platform().equals(IPlatform.Platform.BUKKIT)) {
            supportPotionMix = true;
            recipeRegisterMap.put(RecipeType.POTION, recipe -> {
                Bukkit.getPotionBrewer().addPotionMix(((PotionMixRecipe) recipe).potionMix());
                potionMixRecipeMap.put(((PotionMixRecipe) recipe).key(), (PotionMixRecipe) recipe);
            });
            recipeRemoverMap.put(RecipeType.POTION, recipeList -> {
                for (NamespacedKey recipeKey : recipeList) {
                    Bukkit.getPotionBrewer().removePotionMix(recipeKey);
                }
            });
        }
    }

    public void reloadRecipeManager() {
        resetRecipes();
        loadRecipeGroups();
        loadRecipes();
        loadServerRecipeCache();
        reloadRemovedRecipes();
    }

    private void loadRecipes() {
        for (Map.Entry<RecipeType, Map<String, RecipeGroup>> pluginRecipeMapEntry : pluginRecipeMap.entrySet()) {
            Map<String, RecipeGroup> recipeGroupMap = pluginRecipeMapEntry.getValue();
            recipeGroupMap.forEach((recipeGroupName, recipeGroup) -> loadRecipeGroup(recipeGroup));
        }
    }

    public void loadRecipeGroup(RecipeGroup recipeGroup) {
        try {
            YamlConfiguration config = recipeGroup.recipeGroupConfig().config();
            if (!hasCraftorithmRecipe(recipeGroup.groupName())) {
                addRecipeGroup(recipeGroup);
            }
            for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(config, recipeGroup.groupName())) {
                recipeRegistry.register();
                if (UNLOCKABLE_RECIPE_TYPE.contains(recipeGroup.recipeType())) {
                    recipeUnlockMap.put(recipeRegistry.namespacedKey(), recipeGroup.unlock());
                }
            }
        } catch (Throwable throwable) {
            LangUtil.info(Languages.LOAD_RECIPE_LOAD_EXCEPTION, CollectionsUtil.newStringHashMap("<recipe_name>", recipeGroup.groupName()));
            throwable.printStackTrace();
        }
    }

    public void addRecipeGroup(RecipeGroup recipeGroup) {
        RecipeType recipeType = recipeGroup.recipeType();
        if (pluginRecipeMap.containsKey(recipeType)) {
            Map<String, RecipeGroup> recipeGroupMap = pluginRecipeMap.get(recipeType);
            recipeGroupMap.put(recipeGroup.groupName(), recipeGroup);
        } else {
            Map<String, RecipeGroup> recipeGroupMap = new ConcurrentHashMap<>();
            recipeGroupMap.put(recipeGroup.groupName(), recipeGroup);
            pluginRecipeMap.put(recipeType, recipeGroupMap);
        }
    }

    private void loadRecipeGroups() {
        pluginRecipeMap.clear();
        if (!RECIPE_FILE_FOLDER.exists()) {
            boolean mkdirResult = RECIPE_FILE_FOLDER.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = FileUtil.allYamlFiles(RECIPE_FILE_FOLDER);
        if (allFiles.isEmpty()) {
            saveDefConfigFile(allFiles);
        }
        for (File file : allFiles) {
            String recipeGroupName = file.getPath().substring(RECIPE_FILE_FOLDER.getPath().length() + 1);
            recipeGroupName = recipeGroupName.replace("\\", "/");
            int lastDotIndex = recipeGroupName.lastIndexOf(".");
            recipeGroupName = recipeGroupName.substring(0, lastDotIndex).toLowerCase();
            ConfigWrapper recipeGroupConfigWrapper = new ConfigWrapper(file);
            RecipeType recipeType = RecipeType.valueOf(recipeGroupConfigWrapper.config().getString("type").toUpperCase());
            RecipeGroup recipeGroup = new RecipeGroup(recipeGroupName, recipeType, recipeGroupConfigWrapper);
            addRecipeGroup(recipeGroup);
        }
    }

    public void regRecipe(String recipeGroupName, Recipe recipe, RecipeType recipeType) {
        if (!pluginRecipeMap.containsKey(recipeType))
            pluginRecipeMap.put(recipeType, new ConcurrentHashMap<>());
        Map<String, RecipeGroup> recipeGroupMap = pluginRecipeMap.get(recipeType);
        if (!recipeGroupMap.containsKey(recipeGroupName))
            throw new IllegalArgumentException("Can not find recipe group " + recipeGroupName + ", use addRecipeGroup() method to add recipe group.");
        RecipeGroup recipeGroup = recipeGroupMap.get(recipeGroupName);
        recipeGroup.addRecipeKey(getRecipeKey(recipe));
        recipeRegisterMap.getOrDefault(recipeType, recipe1 -> {
            throw new UnsupportedVersionException("Can not register " + recipeType.name().toLowerCase() + " recipe");
        }).accept(recipe);
    }

    public Map<RecipeType, Map<String, RecipeGroup>> recipeMap() {
        return pluginRecipeMap;
    }

    public Recipe getRecipe(NamespacedKey namespacedKey) {
        if (supportPotionMix) {
            if (potionMixRecipeMap.containsKey(namespacedKey))
                return potionMixRecipeMap.get(namespacedKey);
        }
        if (anvilRecipeMap.containsKey(namespacedKey))
            return anvilRecipeMap.get(namespacedKey);
        return Bukkit.getRecipe(namespacedKey);
    }

    public NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null)
            return null;
        if (recipe instanceof CustomRecipe) {
            return ((CustomRecipe) recipe).key();
        }
        if (recipe instanceof Keyed) {
            return ((Keyed) recipe).getKey();
        }
        else {
            MsgSender.info("&e[WARN] Can not get key of recipe " + recipe);
            return null;
        }
    }

    private void reloadRemovedRecipes() {
        removedRecipesConfigWrapper.reloadConfig();
        List<String> removedRecipes = removedRecipesConfigWrapper.config().getStringList("recipes");
        if (PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value()) {
            serverRecipesCache.forEach((key, recipe) -> {
                if (key.getNamespace().equals("minecraft")) {
                    if (removedRecipes.contains(key.toString()))
                        return;
                    removedRecipes.add(key.toString());
                }
            });
        }
        List<NamespacedKey> removedRecipeKeys = new ArrayList<>();
        for (String recipeKey : removedRecipes) {
            removedRecipeKeys.add(NamespacedKey.fromString(recipeKey));
        }
        disableOtherPluginsRecipe(removedRecipeKeys, false);
    }

    public boolean disableOtherPluginsRecipe(List<NamespacedKey> recipeKeys, boolean save) {
        if (save)
            addKeyToRemovedConfig(recipeKeys);
        addRecipeToRemovedRecipeRecycleBin(recipeKeys);
        return removeRecipes(recipeKeys) > 0;
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
                ConfigWrapper recipeConfig = recipeGroupMap.get(recipeGroupName).recipeGroupConfig();
                if (deleteFile) {
                    recipeConfig.configFile().delete();
                }
                recipeGroupMap.remove(recipeGroupName);
                return true;
            }
        }
        return false;
    }

    private void addKeyToRemovedConfig(List<NamespacedKey> keys) {
        List<String> removedList = removedRecipesConfigWrapper.config().getStringList("recipes");
        for (NamespacedKey key : keys) {
            if (key.getNamespace().equals(NamespacedKey.MINECRAFT) && PluginConfigs.REMOVE_ALL_VANILLA_RECIPE.value())
                continue;
            String keyStr = key.toString();
            if (!removedList.contains(keyStr))
                removedList.add(keyStr);
        }
        removedRecipesConfigWrapper.set("recipes", removedList);
        removedRecipesConfigWrapper.saveConfig();
    }

    private void addRecipeToRemovedRecipeRecycleBin(List<NamespacedKey> recipeKeys) {
        for (NamespacedKey recipeKey : recipeKeys) {
            Recipe recipe = getRecipe(recipeKey);
            if (recipe == null)
                continue;
            removeRecipeRecycleBin.add(recipe);
        }
    }


    /**
     * 删除配方的基础方法
     * @param recipeKeys 要删除的配方
     * @return 删除的配方数量
     */
    private int removeRecipes(List<NamespacedKey> recipeKeys) {
        if (recipeKeys == null || recipeKeys.isEmpty())
            return 0;
        //删除表里缓存的一些数据
        for (NamespacedKey recipeKey : recipeKeys) {
            recipeUnlockMap.remove(recipeKey);
        }

        //在服务器中缓存的数据
        int removedRecipeNum = 0;
        for (NamespacedKey recipeKey : recipeKeys) {
            if (Bukkit.removeRecipe(recipeKey))
                removedRecipeNum ++;
        }
        for (NamespacedKey recipeKey : recipeKeys) {
            serverRecipesCache.remove(recipeKey);
        }
        return removedRecipeNum;
    }

    public boolean hasCraftorithmRecipe(String recipeName) {
        return getRecipeGroups().contains(recipeName);
    }

    @Nullable
    public RecipeGroup getRecipeGroup(String groupName) {
        for (Map.Entry<RecipeType, Map<String, RecipeGroup>> recipeTypeMapEntry : pluginRecipeMap.entrySet()) {
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
        for (Map.Entry<RecipeType, Map<String, RecipeGroup>> recipeTypeMapEntry : pluginRecipeMap.entrySet()) {
            Map<String, RecipeGroup> recipeGroupMap = recipeTypeMapEntry.getValue();
            for (String recipeGroupName : recipeGroupMap.keySet()) {
                RecipeGroup recipeGroup = recipeGroupMap.get(recipeGroupName);
                if (recipeGroup.contains(recipeKey)) {
                    ConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
                    return configWrapper == null ? null : configWrapper.config();
                }
            }
        }
        return null;
    }

    public int getRecipeGroupSortId(String recipeGroupName) {
        for (Map.Entry<RecipeType, Map<String, RecipeGroup>> pluginRecipeMapEntry : pluginRecipeMap.entrySet()) {
            Map<String, RecipeGroup> recipeGroupMap = pluginRecipeMapEntry.getValue();
            if (recipeGroupMap.containsKey(recipeGroupName)) {
                return recipeGroupMap.get(recipeGroupName).sortId();
            }
        }
        return 0;
    }

    @Nullable
    public AnvilRecipe matchAnvilRecipe(ItemStack base, ItemStack addition) {
        for (Map.Entry<NamespacedKey, AnvilRecipe> anvilRecipeEntry : anvilRecipeMap.entrySet()) {
            AnvilRecipe anvilRecipe = anvilRecipeEntry.getValue();
            if (!anvilRecipe.base().isSimilar(base))
                continue;
            if (base.getAmount() < anvilRecipe.base().getAmount())
                continue;
            if (!anvilRecipe.addition().isSimilar(addition))
                continue;
            if (addition.getAmount() < anvilRecipe.addition().getAmount())
                continue;
            return anvilRecipe;
        }
        return null;
    }

    public RecipeType getRecipeType(Recipe recipe) {
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
        else if (recipe instanceof AnvilRecipe)
            return RecipeType.ANVIL;
        else
            return RecipeType.UNKNOWN;
    }

    public StringLangEntry getRecipeTypeName(RecipeType recipeType) {
        switch (recipeType) {
            case SHAPED:
                return Languages.RECIPE_TYPE_NAME_SHAPED;
            case SHAPELESS:
                return Languages.RECIPE_TYPE_NAME_SHAPELESS;
            case COOKING:
                return Languages.RECIPE_TYPE_NAME_COOKING;
            case SMITHING:
                return Languages.RECIPE_TYPE_NAME_SMITHING;
            case STONE_CUTTING:
                return Languages.RECIPE_TYPE_NAME_STONE_CUTTING;
            case POTION:
                return Languages.RECIPE_TYPE_NAME_POTION;
            case ANVIL:
                return Languages.RECIPE_TYPE_NAME_ANVIL;
            default:
                return null;
        }
    }

    public void resetRecipes() {
        //删除Craftorithm的配方
        pluginRecipeMap.forEach((type, recipeGroupMaps) -> {
            recipeGroupMaps.forEach((group, recipeKeys) -> {
                removeCraftorithmRecipe(group, false);
            });
        });

        potionMixRecipeMap.clear();
        anvilRecipeMap.clear();

        //先将已经删除的配方还原
        for (Recipe recipe : removeRecipeRecycleBin) {
            Bukkit.addRecipe(recipe);
        }
        removeRecipeRecycleBin.clear();
        pluginRecipeMap.clear();
        recipeUnlockMap.clear();
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

    public Map<NamespacedKey, PotionMixRecipe> potionMixRecipeMap() {
        return potionMixRecipeMap;
    }

    public List<String> getRecipeGroups() {
        //TODO 可能需要缓存一下
        List<String> recipes = new ArrayList<>();
        for (Map<String, RecipeGroup> value : pluginRecipeMap.values()) {
            recipes.addAll(value.keySet());
        }
        return recipes;
    }

    public Map<NamespacedKey, Boolean> recipeUnlockMap() {
        return recipeUnlockMap;
    }

    public Map<NamespacedKey, Recipe> serverRecipesCache() {
        return serverRecipesCache;
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
        Craftorithm.instance().saveResource("recipes/example_smithing.yml", false);
        Craftorithm.instance().saveResource("recipes/example_stone_cutting.yml", false);
        Craftorithm.instance().saveResource("recipes/example_cooking.yml", false);
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_cooking.yml"));
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_smithing.yml"));
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_stone_cutting.yml"));
        Craftorithm.instance().saveResource("recipes/example_random_cooking.yml", false);
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_random_cooking.yml"));
        if (supportPotionMix()) {
            Craftorithm.instance().saveResource("recipes/example_potion.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_potion.yml"));
        }
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            Craftorithm.instance().saveResource("recipes/example_anvil.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_anvil.yml"));
        }
    }

}
