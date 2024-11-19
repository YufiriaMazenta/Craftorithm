package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.exception.UnsupportedVersionException;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.recipe.registry.impl.SmithingRecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lang.entry.StringLangEntry;
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
import java.util.function.Consumer;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE, priority = 2),
        @TaskRule(lifeCycle = LifeCycle.RELOAD, priority = 2)
    }
)
public enum RecipeManager implements BukkitLifeCycleTask {

    INSTANCE;
    public final File RECIPE_FILE_FOLDER = new File(Craftorithm.instance().getDataFolder().getPath(), "recipes");
    private final BukkitConfigWrapper removedRecipesConfigWrapper = new BukkitConfigWrapper(Craftorithm.instance(), "removed_recipes.yml");
    public final String PLUGIN_RECIPE_NAMESPACE = "craftorithm";
    private final Map<NamespacedKey, RecipeRegistry> recipeRegistryMap = new ConcurrentHashMap<>();
    private final Map<RecipeType, Map<String, RecipeGroup>> pluginRecipeMap;
    private final Map<RecipeType, Consumer<Recipe>> recipeRegisterMap;
    private final Map<RecipeType, Consumer<List<NamespacedKey>>> recipeRemoverMap;
    private final Map<NamespacedKey, Boolean> recipeUnlockMap;
    private final List<Recipe> removeRecipeRecycleBin;
    private final Map<NamespacedKey, Recipe> serverRecipesCache;
    private final Map<NamespacedKey, PotionMixRecipe> potionMixRecipeMap;
    private final Map<NamespacedKey, AnvilRecipe> anvilRecipeMap;
    public static final List<RecipeType> UNLOCKABLE_RECIPE_TYPE =
        List.of(RecipeType.VANILLA_SHAPED, RecipeType.SHAPELESS, RecipeType.COOKING, RecipeType.SMITHING, RecipeType.STONE_CUTTING, RecipeType.RANDOM_COOKING);
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
        if (CrypticLibBukkit.platform().isPaper()) {
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_1)) {
                Bukkit.updateRecipes();
            }
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
                recipeRegistryMap.put(recipeRegistry.namespacedKey(), recipeRegistry);
                if (UNLOCKABLE_RECIPE_TYPE.contains(recipeGroup.recipeType())) {
                    recipeUnlockMap.put(recipeRegistry.namespacedKey(), recipeGroup.unlock());
                }
            }
        } catch (Throwable throwable) {
            LangUtils.info(Languages.LOAD_RECIPE_LOAD_EXCEPTION, CollectionsUtils.newStringHashMap("<recipe_name>", recipeGroup.groupName()));
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
        List<File> allFiles = FileHelper.allYamlFiles(RECIPE_FILE_FOLDER);
        if (allFiles.isEmpty()) {
            saveDefConfigFile(allFiles);
        }
        for (File file : allFiles) {
            try {
                String recipeGroupName = file.getPath().substring(RECIPE_FILE_FOLDER.getPath().length() + 1);
                recipeGroupName = recipeGroupName.replace("\\", "/");
                int lastDotIndex = recipeGroupName.lastIndexOf(".");
                recipeGroupName = recipeGroupName.substring(0, lastDotIndex).toLowerCase();
                BukkitConfigWrapper recipeGroupConfigWrapper = new BukkitConfigWrapper(file);
                String typeStr = recipeGroupConfigWrapper.config().getString("type");
                RecipeType recipeType = RecipeType.valueOf(typeStr.toUpperCase());
                RecipeGroup recipeGroup = new RecipeGroup(recipeGroupName, recipeType, recipeGroupConfigWrapper);
                addRecipeGroup(recipeGroup);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
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

    public @Nullable NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe instanceof CustomRecipe customRecipe) {
            return customRecipe.key();
        } else if (recipe instanceof Keyed keyed) {
            return keyed.getKey();
        } else {
//            MsgSender.info("&e[WARN] Can not get key of recipe " + recipe);
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
                    BukkitConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
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
        String baseId = ItemManager.INSTANCE.matchItemName(base, true);
        baseId = baseId != null ? baseId : base.getType().getKey().toString();
        String additionId = ItemManager.INSTANCE.matchItemName(addition, true);
        additionId = additionId != null ? additionId : addition.getType().getKey().toString();

        BukkitMsgSender.INSTANCE.debug("base: " + baseId + ", addition: " + additionId);
        for (Map.Entry<NamespacedKey, AnvilRecipe> anvilRecipeEntry : anvilRecipeMap.entrySet()) {
            AnvilRecipe anvilRecipe = anvilRecipeEntry.getValue();
            String recipeBaseId = ItemManager.INSTANCE.matchItemName(anvilRecipe.base(), true);
            recipeBaseId = recipeBaseId != null ? recipeBaseId : anvilRecipe.base().getType().getKey().toString();
            String recipeAdditionId = ItemManager.INSTANCE.matchItemName(anvilRecipe.addition(), true);
            recipeAdditionId = recipeAdditionId != null ? recipeAdditionId : anvilRecipe.addition().getType().getKey().toString();
            BukkitMsgSender.INSTANCE.debug("recipe base: " + recipeBaseId + ", recipe addition: " + recipeAdditionId);
            if (!Objects.equals(baseId, recipeBaseId))
                continue;
            BukkitMsgSender.INSTANCE.debug("matched base id");
            if (base.getAmount() < anvilRecipe.base().getAmount())
                continue;
            BukkitMsgSender.INSTANCE.debug("matched base amount");
            if (!Objects.equals(additionId, recipeAdditionId))
                continue;
            BukkitMsgSender.INSTANCE.debug("matched addition id");
            if (addition.getAmount() < anvilRecipe.addition().getAmount())
                continue;
            BukkitMsgSender.INSTANCE.debug("matched addition amount");
            return anvilRecipe;
        }
        return null;
    }

    public RecipeType getRecipeType(Recipe recipe) {
        return switch (recipe) {
            case ShapedRecipe shapedRecipe -> RecipeType.VANILLA_SHAPED;
            case ShapelessRecipe shapelessRecipe -> RecipeType.SHAPELESS;
            case CookingRecipe<?> cookingRecipe -> RecipeType.COOKING;
            case SmithingRecipe smithingRecipe -> RecipeType.SMITHING;
            case PotionMixRecipe potionMixRecipe -> RecipeType.POTION;
            case StonecuttingRecipe stonecuttingRecipe -> RecipeType.STONE_CUTTING;
            case AnvilRecipe anvilRecipe -> RecipeType.ANVIL;
            case null, default -> RecipeType.UNKNOWN;
        };
    }

    public StringLangEntry getRecipeTypeName(RecipeType recipeType) {
        return switch (recipeType) {
            case VANILLA_SHAPED -> Languages.RECIPE_TYPE_NAME_SHAPED;
            case SHAPELESS -> Languages.RECIPE_TYPE_NAME_SHAPELESS;
            case COOKING -> Languages.RECIPE_TYPE_NAME_COOKING;
            case SMITHING -> Languages.RECIPE_TYPE_NAME_SMITHING;
            case STONE_CUTTING -> Languages.RECIPE_TYPE_NAME_STONE_CUTTING;
            case POTION -> Languages.RECIPE_TYPE_NAME_POTION;
            case ANVIL -> Languages.RECIPE_TYPE_NAME_ANVIL;
            default -> null;
        };
    }

    public boolean getSmithingCopyEnchantment(Recipe recipe) {
        if (!(recipe instanceof SmithingRecipe))
            return false;
        NamespacedKey namespacedKey = getRecipeKey(recipe);
        if (namespacedKey == null)
            return false;
        RecipeRegistry recipeRegistry = recipeRegistryMap.get(namespacedKey);
        if (!(recipeRegistry instanceof SmithingRecipeRegistry smithingRecipeRegistry)) {
            return false;
        }
        return smithingRecipeRegistry.copyEnchantments();
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
        Craftorithm.instance().saveResource("recipes/legacy/example_shaped.yml", false);
        Craftorithm.instance().saveResource("recipes/legacy/example_shapeless.yml", false);
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_shaped.yml"));
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_shapeless.yml"));
        Craftorithm.instance().saveResource("recipes/legacy/example_smithing.yml", false);
        Craftorithm.instance().saveResource("recipes/legacy/example_stone_cutting.yml", false);
        Craftorithm.instance().saveResource("recipes/legacy/example_cooking.yml", false);
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_cooking.yml"));
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_smithing.yml"));
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_stone_cutting.yml"));
        Craftorithm.instance().saveResource("recipes/legacy/example_random_cooking.yml", false);
        allFiles.add(new File(RECIPE_FILE_FOLDER, "example_random_cooking.yml"));
        if (supportPotionMix()) {
            Craftorithm.instance().saveResource("recipes/legacy/example_potion.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_potion.yml"));
        }
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            Craftorithm.instance().saveResource("recipes/legacy/example_anvil.yml", false);
            allFiles.add(new File(RECIPE_FILE_FOLDER, "example_anvil.yml"));
        }
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        if (lifeCycle.equals(LifeCycle.ENABLE)) {
            //设置各类型配方的注册方法
            recipeRegisterMap.put(RecipeType.VANILLA_SHAPED, Bukkit::addRecipe);
            recipeRemoverMap.put(RecipeType.VANILLA_SHAPED, this::removeRecipes);
            recipeRegisterMap.put(RecipeType.SHAPELESS, Bukkit::addRecipe);
            recipeRemoverMap.put(RecipeType.SHAPELESS, this::removeRecipes);
            recipeRegisterMap.put(RecipeType.COOKING, Bukkit::addRecipe);
            recipeRemoverMap.put(RecipeType.COOKING, this::removeRecipes);
            recipeRegisterMap.put(RecipeType.STONE_CUTTING, Bukkit::addRecipe);
            recipeRemoverMap.put(RecipeType.STONE_CUTTING, this::removeRecipes);
            recipeRegisterMap.put(RecipeType.SMITHING, Bukkit::addRecipe);
            recipeRemoverMap.put(RecipeType.SMITHING, this::removeRecipes);
            recipeRegisterMap.put(RecipeType.RANDOM_COOKING, Bukkit::addRecipe);
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

            if (!CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
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
        } else {
            reloadRecipeManager();
        }
    }

}
