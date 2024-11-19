package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import crypticlib.CrypticLibBukkit;
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
    private final Map<RecipeType, RecipeLoader<?>> recipeLoaderMap = new ConcurrentHashMap<>();

    private RecipeRegistry recipeRegistry;
    private final List<Recipe> removeRecipeRecycleBin;
    private final Map<NamespacedKey, Recipe> serverRecipesCache;
    public static final List<RecipeType> UNLOCKABLE_RECIPE_TYPE =
        List.of(RecipeType.VANILLA_SHAPED, RecipeType.SHAPELESS, RecipeType.COOKING, RecipeType.SMITHING, RecipeType.STONE_CUTTING, RecipeType.RANDOM_COOKING);
    private boolean supportPotionMix;

    RecipeManager() {
        removeRecipeRecycleBin = new CopyOnWriteArrayList<>();
        serverRecipesCache = new ConcurrentHashMap<>();
        recipeRegistry = SimpleRecipeRegistry.INSTANCE;
    }

    public void reloadRecipeManager() {
        resetRecipes();
        loadRecipesFromConfig();
        loadServerRecipeCache();
        reloadRemovedRecipes();
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
                recipeType = RecipeType.valueOf(typeStr.toUpperCase());
                RecipeLoader<?> recipeLoader = recipeLoaderMap.get(recipeType);
                if (recipeLoader == null) {
                    throw new RecipeLoadException("Can not load recipe type " + recipeType);
                }
                Recipe recipe = recipeLoader.loadRecipe(recipeName, recipeConfig);
                recipeRegistry.registerRecipe(recipe);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
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
        //删除Craftorithm注册的配方
        recipeRegistry.resetRecipes();

        //先将已经删除的配方还原
        for (Recipe recipe : removeRecipeRecycleBin) {
            Bukkit.addRecipe(recipe);
        }
        removeRecipeRecycleBin.clear();
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

    public RecipeRegistry recipeRegistry() {
        return recipeRegistry;
    }

    public RecipeManager setRecipeRegistry(RecipeRegistry recipeRegistry) {
        this.recipeRegistry = recipeRegistry;
        return this;
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        if (lifeCycle.equals(LifeCycle.ENABLE)) {
            //设置各类型配方的注册方法
            if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {

            }

            if (!CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
                supportPotionMix = true;

            }
        } else {
            reloadRecipeManager();
        }
    }

}
