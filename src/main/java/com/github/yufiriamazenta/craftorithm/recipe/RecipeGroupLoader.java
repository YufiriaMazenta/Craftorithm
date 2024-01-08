package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.registry.*;
import crypticlib.CrypticLib;
import crypticlib.config.ConfigWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

public class RecipeGroupLoader {

    public static final String TYPE_KEY = "type", RESULT_KEY = "result", SORT_ID = "sort_id";
    public static final List<String> GLOBAL_KEYS = Arrays.asList(TYPE_KEY, RESULT_KEY, SORT_ID);
    public final Map<RecipeType, BiFunction<String, ConfigurationSection, RecipeRegistry>> RECIPE_REGISTRY_LOAD_MAP = new HashMap<>();
    protected ConfigWrapper configWrapper;
    protected RecipeType globalType;
    protected ItemStack globalResult;
    protected int groupSortId;
    protected String groupName;

    public RecipeGroupLoader(String groupName, ConfigWrapper configWrapper) {
        this.configWrapper = configWrapper;
        this.groupName = groupName;
        String globalTypeStr = configWrapper.config().getString(TYPE_KEY);
        if (globalTypeStr != null)
            this.globalType = RecipeType.valueOf(globalTypeStr.toUpperCase());
        String globalResultStr = configWrapper.config().getString(RESULT_KEY);
        if (globalResultStr != null)
            this.globalResult = ItemManager.INSTANCE.matchItem(globalResultStr);
        groupSortId = configWrapper.config().getInt(SORT_ID, 0);
        loadDefRecipeLoadMap();
    }

    public RecipeGroup load() {
        YamlConfiguration config = configWrapper.config();
        List<String> recipeKeys = new ArrayList<>(config.getKeys(false));
        recipeKeys.removeAll(GLOBAL_KEYS);
        RecipeGroup recipeGroup = new RecipeGroup(groupName, configWrapper, groupSortId);
        for (String subName : recipeKeys) {
            if (!config.isConfigurationSection(subName))
                continue;
            ConfigurationSection recipeCfgSection = config.getConfigurationSection(subName);
            RecipeType recipeType;
            String recipeTypeStr = Objects.requireNonNull(recipeCfgSection).getString(TYPE_KEY);
            if (recipeTypeStr == null)
                recipeType = globalType;
            else
                recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase());
            //TODO 提醒使用者，此类型不可用
            RecipeRegistry recipeRegistry = RECIPE_REGISTRY_LOAD_MAP.getOrDefault(recipeType, (a, b) -> null).apply(subName, config);
            if (recipeRegistry != null)
                recipeGroup.addRecipeRegistry(subName, recipeRegistry);
        }
        return recipeGroup;
    }

    private void loadDefRecipeLoadMap() {
        RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.SHAPED, this::loadShapedRegistry);
        RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.SHAPELESS, this::loadShapelessRegistry);
        RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.ANVIL, this::loadAnvilRegistry);

        if (CrypticLib.minecraftVersion() >= 11400) {
            RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.COOKING, this::loadCookingRegistry);
            RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.SMITHING, this::loadSmithingRegistry);
            RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.STONE_CUTTING, this::loadStonecuttingRegistry);
        }

        if (CrypticLib.minecraftVersion() >= 11700) {
            RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.RANDOM_COOKING, this::loadRandomCookingRegistry);
        }

        if (RecipeManager.INSTANCE.supportPotionMix()) {
            RECIPE_REGISTRY_LOAD_MAP.put(RecipeType.POTION, this::loadPotionRegistry);
        }
    }

    protected RecipeRegistry loadShapedRegistry(String recipeName, ConfigurationSection configSection) {
        ItemStack result = matchResult(configSection);
        List<String> shapeList = configSection.getStringList("source.shape");
        Map<Character, RecipeChoice> ingredientMap = new HashMap<>();
        ConfigurationSection ingredientCfgSection = configSection.getConfigurationSection("source.ingredients");
        if (ingredientCfgSection == null)
            throw new IllegalArgumentException("Ingredient map cannot be null");
        for (String key : ingredientCfgSection.getKeys(false)) {
            String ingredientStr = ingredientCfgSection.getString(key);
            if (ingredientStr == null)
                ingredientStr = Material.AIR.getKey().toString();
            ingredientStr = ingredientStr.split(" ")[0];
            RecipeChoice recipeChoice = matchRecipeChoice(ingredientStr);
            ingredientMap.put(key.charAt(0), recipeChoice);
        }
        String[] shape = new String[shapeList.size()];
        return new ShapedRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
            .setShape(shapeList.toArray(shape))
            .setRecipeChoiceMap(ingredientMap)
            .setUnlock(matchUnlock(configSection));
    }

    protected RecipeRegistry loadShapelessRegistry(String recipeName, ConfigurationSection configSection) {
        ItemStack result = matchResult(configSection);
        List<String> ingredientList = configSection.getStringList("source.ingredients");
        List<RecipeChoice> recipeChoiceList = new ArrayList<>();
        for (String ingredient : ingredientList) {
            RecipeChoice recipeChoice = matchRecipeChoice(ingredient);
            recipeChoiceList.add(recipeChoice);
        }
        return new ShapelessRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
            .setChoiceList(recipeChoiceList)
            .setUnlock(matchUnlock(configSection));
    }

    protected RecipeRegistry loadCookingRegistry(String recipeName, ConfigurationSection configSection) {
        ItemStack result = matchResult(configSection);
        String choiceStr = Objects.requireNonNull(configSection.getString("source.ingredient"));
        RecipeChoice ingredient = matchRecipeChoice(choiceStr);
        String blockStr = configSection.getString("source.block", "furnace");
        float exp = (float) configSection.getDouble("source.exp", 0);
        int time = configSection.getInt("source.time", 100);
        return new CookingRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
            .setCookingBlock(blockStr)
            .setExp(exp)
            .setTime(time)
            .setIngredient(ingredient)
            .setUnlock(matchUnlock(configSection));
    }

    protected RecipeRegistry loadSmithingRegistry(String recipeName, ConfigurationSection configSection) {
        ItemStack result = matchResult(configSection);
        String baseStr = Objects.requireNonNull(configSection.getString("source.base"));
        RecipeChoice base = matchRecipeChoice(baseStr);
        String additionStr = Objects.requireNonNull(configSection.getString("source.addition"));
        RecipeChoice addition = matchRecipeChoice(additionStr);
        String typeStr = configSection.getString("source.type", "transform");
        XSmithingRecipeRegistry.SmithingType smithingType = XSmithingRecipeRegistry.SmithingType.valueOf(typeStr.toUpperCase());
        boolean copyNbt = configSection.getBoolean("source.copy_nbt", true);
        if (CrypticLib.minecraftVersion() >= 12000) {
            String templateStr = Objects.requireNonNull(configSection.getString("source.template"));
            RecipeChoice template = matchRecipeChoice(templateStr);
            return new XSmithingRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
                .setBase(base)
                .setAddition(addition)
                .setTemplate(template)
                .setSmithingType(smithingType)
                .setCopyNbt(copyNbt)
                .setUnlock(matchUnlock(configSection));
        } else {
            return new SmithingRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
                .setBase(base)
                .setAddition(addition)
                .setCopyNbt(copyNbt)
                .setUnlock(matchUnlock(configSection));
        }
    }

    protected RecipeRegistry loadStonecuttingRegistry(String recipeName, ConfigurationSection configSection) {
        List<ItemStack> results = new ArrayList<>();
        if (!configSection.contains("result")) {
            results.add(globalResult);
        } else if (configSection.isList("result")) {
            for (String resultStr : configSection.getStringList("result")) {
                results.add(ItemManager.INSTANCE.matchItem(resultStr));
            }
        } else {
            results.add(ItemManager.INSTANCE.matchItem(Objects.requireNonNull(configSection.getString("result"))));
        }
        List<RecipeChoice> ingredients = new ArrayList<>();
        if (configSection.isList("source.ingredients")) {
            for (String ingredientStr : configSection.getStringList("source.ingredients")) {
                ingredients.add(matchRecipeChoice(ingredientStr));
            }
        }
        return new StoneCuttingRecipeRegistry(groupName, generateRecipeKey(recipeName), results)
            .setIngredientList(ingredients)
            .setUnlock(matchUnlock(configSection));
    }

    protected RecipeRegistry loadRandomCookingRegistry(String recipeName, ConfigurationSection configSection) {
        List<String> resultStrList = configSection.getStringList("result");
        List<RandomCookingRecipeRegistry.RandomCookingResult> randomResults = getRandomResults(resultStrList);
        ItemStack result = randomResults.get(0).result();
        String choiceStr = Objects.requireNonNull(configSection.getString("source.ingredient"));
        RecipeChoice ingredient = matchRecipeChoice(choiceStr);
        String blockStr = configSection.getString("source.block", "furnace");
        float exp = (float) configSection.getDouble("source.exp", 0);
        int time = configSection.getInt("source.time", 100);
        return new RandomCookingRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
            .setResults(randomResults)
            .setExp(exp)
            .setTime(time)
            .setCookingBlock(blockStr)
            .setIngredient(ingredient)
            .setUnlock(matchUnlock(configSection));
    }

    protected RecipeRegistry loadPotionRegistry(String recipeName, ConfigurationSection configSection) {
        ItemStack result = matchResult(configSection);
        RecipeChoice input = matchRecipeChoice(Objects.requireNonNull(configSection.getString("source.input")));
        RecipeChoice ingredient = matchRecipeChoice(Objects.requireNonNull(configSection.getString("source.ingredient")));
        return new PotionMixRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
            .setInput(input)
            .setIngredient(ingredient);
    }

    protected RecipeRegistry loadAnvilRegistry(String recipeName, ConfigurationSection configSection) {
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return null;
        ItemStack result = matchResult(configSection);
        ItemStack base = ItemManager.INSTANCE.matchItem(Objects.requireNonNull(configSection.getString("source.base")));
        ItemStack addition = ItemManager.INSTANCE.matchItem(Objects.requireNonNull(configSection.getString("source.addition")));
        int costLevel = configSection.getInt("source.cost_level", 0);
        boolean copyNbt = configSection.getBoolean("source.copy_nbt", true);
        return new AnvilRecipeRegistry(groupName, generateRecipeKey(recipeName), result)
            .setBase(base)
            .setAddition(addition)
            .setCopyNbt(copyNbt)
            .setCostLevel(costLevel);
    }


    private List<RandomCookingRecipeRegistry.RandomCookingResult> getRandomResults(List<String> resultStr) {
        List<RandomCookingRecipeRegistry.RandomCookingResult> resultWeightList = new ArrayList<>();
        int sum = 0;
        for (String result : resultStr) {
            String item = result.substring(0, result.lastIndexOf(" "));
            int weight = Integer.parseInt(result.substring(result.lastIndexOf(" ") + 1));
            ItemStack itemStack = ItemManager.INSTANCE.matchItem(item);
            sum += weight;
            resultWeightList.add(new RandomCookingRecipeRegistry.RandomCookingResult(itemStack, sum));
        }
        return resultWeightList;
    }

    public NamespacedKey generateRecipeKey(String recipeName) {
        return new NamespacedKey(Craftorithm.instance(), groupName + "." + recipeName);
    }

    public boolean matchUnlock(ConfigurationSection configSection) {
        return configSection.contains("unlock") ? configSection.getBoolean("unlock") : PluginConfigs.DEFAULT_RECIPE_UNLOCK.value();
    }

    public ItemStack matchResult(ConfigurationSection configSection) {
        ItemStack result;
        String resultStr = configSection.getString(RESULT_KEY);
        if (resultStr == null)
            result = globalResult;
        else
            result = ItemManager.INSTANCE.matchItem(resultStr);
        return result;
    }

    public RecipeChoice matchRecipeChoice(String ingredientName) {
        if (!ingredientName.contains(":")) {
            Material material = Material.matchMaterial(ingredientName);
            if (material == null) {
                throw new IllegalArgumentException(ingredientName + " is a not exist item type");
            }
            return new RecipeChoice.MaterialChoice(material);
        }
        int index = ingredientName.indexOf(":");
        String namespace = ingredientName.substring(0, index);
        namespace = namespace.toLowerCase();
        switch (namespace) {
            case "minecraft":
                Material material = Material.matchMaterial(ingredientName);
                if (material == null) {
                    throw new IllegalArgumentException(ingredientName + " is a not exist item type");
                }
                return new RecipeChoice.MaterialChoice(material);
            case "tag":
                String tagStr = ingredientName.substring(4).toUpperCase(Locale.ROOT);
                Tag<Material> materialTag;
                try {
                    Field field = Tag.class.getField(tagStr);
                    materialTag = (Tag<Material>) field.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return new RecipeChoice.MaterialChoice(materialTag);
            default:
                ItemStack item = ItemManager.INSTANCE.matchItem(ingredientName);
                return new RecipeChoice.ExactChoice(item);
        }
    }

}
