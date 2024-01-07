package com.github.yufiriamazenta.craftorithm.recipe.loader;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.recipe.registry.ShapedRecipeRegistry;
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
import java.util.function.Function;

public class RecipeGroupLoader {

    public static final String TYPE_KEY = "type", RESULT_KEY = "result";
    public static final List<String> GLOBAL_KEYS = Arrays.asList(TYPE_KEY, RESULT_KEY);
    public final Map<RecipeType, BiFunction<String, ConfigurationSection, RecipeRegistry>> RECIPE_LOAD_MAP = new HashMap<>();
    protected ConfigWrapper configWrapper;
    protected RecipeType globalType;
    protected ItemStack globalResult;
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
        loadDefRecipeLoadMap();
    }

    public RecipeGroup load() {
        YamlConfiguration config = configWrapper.config();
        List<String> recipeKeys = new ArrayList<>(config.getKeys(false));
        recipeKeys.removeAll(GLOBAL_KEYS);
        for (String recipeKey : recipeKeys) {
            if (!config.isConfigurationSection(recipeKey))
                continue;
            ConfigurationSection recipeCfgSection = config.getConfigurationSection(recipeKey);
            RecipeType recipeType;
            String recipeTypeStr = recipeCfgSection.getString(TYPE_KEY);
            if (recipeTypeStr == null)
                recipeType = globalType;
            else
                recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase());
            //TODO 加载配方注册器
        }
        return null;
    }

    private void loadDefRecipeLoadMap() {
        RECIPE_LOAD_MAP.put(RecipeType.SHAPED, (subName, configSection) -> {
            ItemStack result;
            String resultStr = configSection.getString(RESULT_KEY);
            if (resultStr == null)
                result = globalResult;
            else 
                result = ItemManager.INSTANCE.matchItem(resultStr);
            List<String> shapeList = configSection.getStringList("source.shape");
            Map<Character, RecipeChoice> ingredientMap = new HashMap<>();
            ConfigurationSection ingredientCfgSection = configSection.getConfigurationSection("source.ingredient_map");
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
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), groupName + "." + subName);
            String[] shape = new String[shapeList.size()];
            return new ShapedRecipeRegistry(groupName, namespacedKey, result)
                .setShape(shapeList.toArray(shape))
                .setRecipeChoiceMap(ingredientMap);
        });
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
