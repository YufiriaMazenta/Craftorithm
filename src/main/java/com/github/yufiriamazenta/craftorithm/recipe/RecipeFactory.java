package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.builder.custom.PotionMixBuilder;
import com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla.*;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import crypticlib.CrypticLib;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

public class RecipeFactory {

    private static final Map<RecipeType, BiFunction<YamlConfiguration, String, Recipe[]>> recipeBuilderMap;
    private static final Map<RecipeType, BiFunction<YamlConfiguration, String, Recipe[]>> multipleRecipeBuilderMap;

    static {
        recipeBuilderMap = new HashMap<>();
        recipeBuilderMap.put(RecipeType.SHAPED, RecipeFactory::shapedRecipe);
        recipeBuilderMap.put(RecipeType.SHAPELESS, RecipeFactory::shapelessRecipe);
        recipeBuilderMap.put(RecipeType.COOKING, RecipeFactory::cookingRecipe);
        recipeBuilderMap.put(RecipeType.STONE_CUTTING, RecipeFactory::stoneCuttingRecipe);
        recipeBuilderMap.put(RecipeType.RANDOM_COOKING, RecipeFactory::cookingRecipe);
        recipeBuilderMap.put(RecipeType.SMITHING, RecipeFactory::smithingRecipe);
        recipeBuilderMap.put(RecipeType.POTION, RecipeFactory::potionMixRecipe);

        multipleRecipeBuilderMap = new HashMap<>();
        multipleRecipeBuilderMap.put(RecipeType.SHAPED, RecipeFactory::multipleShapedRecipe);
        multipleRecipeBuilderMap.put(RecipeType.SHAPELESS, RecipeFactory::multipleShapelessRecipe);
        multipleRecipeBuilderMap.put(RecipeType.COOKING, RecipeFactory::multipleCookingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.STONE_CUTTING, RecipeFactory::multipleStoneCuttingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.RANDOM_COOKING, RecipeFactory::multipleCookingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.SMITHING, RecipeFactory::multipleSmithingRecipe);
        multipleRecipeBuilderMap.put(RecipeType.POTION, RecipeFactory::multiplePotionMixRecipe);
    }

    public static Recipe[] newRecipe(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeTypeStr = config.getString("type", "shaped");
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
        return recipeBuilderMap.get(recipeType).apply(config, key);
    }

    public static Recipe[] newMultipleRecipe(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeTypeStr = config.getString("type", "shaped");
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
        return multipleRecipeBuilderMap.get(recipeType).apply(config, key);
    }

    public static Recipe[] shapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), key);

        List<String> shapeStrList = config.getStringList("shape");
        if (shapeStrList.size() > 3) {
            shapeStrList = shapeStrList.subList(0, 3);
        }
        String[] shape = new String[shapeStrList.size()];
        shape = shapeStrList.toArray(shape);
        Recipe recipe = ShapedRecipeBuilder.builder().setKey(namespacedKey).setResult(result).shape(shape).recipeChoiceMap(recipeChoiceMap).build();
        return new Recipe[]{recipe};
    }

    public static Recipe[] multipleShapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        List<?> shapeList = config.getList("shape", new ArrayList<>());
        ShapedRecipe[] shapedRecipes = new ShapedRecipe[shapeList.size()];
        for (int i = 0; i < shapeList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
            List<String> shapeStrList = (List<String>) shapeList.get(i);
            if (shapeStrList.size() > 3) {
                shapeStrList = shapeStrList.subList(0, 3);
            }
            String[] shape = new String[shapeStrList.size()];
            shape = shapeStrList.toArray(shape);
            shapedRecipes[i] = ShapedRecipeBuilder.builder().setKey(namespacedKey).setResult(result).shape(shape).recipeChoiceMap(recipeChoiceMap).build();
            shapedRecipes[i].setGroup(key);
        }
        return shapedRecipes;
    }

    public static Recipe[] shapelessRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), key);
        ItemStack result = getResultItem(config);
        List<String> itemStrList = config.getStringList("source");
        List<RecipeChoice> recipeChoiceList = new ArrayList<>();
        for (String itemStr : itemStrList) {
            recipeChoiceList.add(getRecipeChoice(itemStr));
        }
        Recipe recipe = ShapelessRecipeBuilder.builder().setKey(namespacedKey).setResult(result).choiceList(recipeChoiceList).build();
        return new Recipe[]{recipe};
    }

    public static Recipe[] multipleShapelessRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<?> itemsList = config.getList("source", new ArrayList<>());
        ShapelessRecipe[] shapelessRecipes = new ShapelessRecipe[itemsList.size()];

        for (int i = 0; i < itemsList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
            List<String> itemStrList = (List<String>) itemsList.get(i);
            List<RecipeChoice> choiceList = new ArrayList<>();
            for (String itemStr : itemStrList) {
                choiceList.add(getRecipeChoice(itemStr));
            }
            shapelessRecipes[i] = ShapelessRecipeBuilder.builder().setKey(namespacedKey).setResult(result).choiceList(choiceList).build();
            shapelessRecipes[i].setGroup(key);
        }
        return shapelessRecipes;
    }

    public static Recipe[] cookingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), key);
        ItemStack result = getResultItem(config);
        String choiceStr = config.getString("source.item", "");
        String cookingBlock = config.getString("source.block", "furnace");
        RecipeChoice source = getRecipeChoice(choiceStr);
        float exp = (float) config.getDouble("exp", 0);
        int time = config.getInt("time", 200);
        Recipe recipe = CookingRecipeBuilder.builder().setKey(namespacedKey).setResult(result).block(cookingBlock).source(source).exp(exp).time(time).build();
        return new Recipe[]{recipe};
    }

    public static Recipe[] multipleCookingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        float globalExp = (float) config.getDouble("exp", 0);
        int globalTime = config.getInt("time", 200);

        List<Map<?, ?>> sourceList = config.getMapList("source");
        CookingRecipe<?>[] cookingRecipes = new CookingRecipe[sourceList.size()];
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
            RecipeChoice source = getRecipeChoice((String) map.get("item"));
            String cookingBlock = (String) map.get("block");
            float exp = map.containsKey("exp") ? Float.parseFloat(String.valueOf(map.get("exp"))) : globalExp;
            int time = map.containsKey("time") ? (Integer) map.get("time") : globalTime;
            cookingRecipes[i] = CookingRecipeBuilder.builder().setKey(namespacedKey).setResult(result).block(cookingBlock).source(source).exp(exp).time(time).build();
            cookingRecipes[i].setGroup(key);
        }
        return cookingRecipes;
    }

    public static Recipe[] smithingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), key);
        ItemStack result = getResultItem(config);
        RecipeChoice base = getRecipeChoice(config.getString("source.base", ""));
        RecipeChoice addition = getRecipeChoice(config.getString("source.addition", ""));
        Recipe recipe;
        if (CrypticLib.minecraftVersion() >= 12000) {
            RecipeChoice template = getRecipeChoice(config.getString("source.template", ""));
            XSmithingRecipeBuilder.SmithingType type = XSmithingRecipeBuilder.SmithingType.valueOf(config.getString("source.type", "default").toUpperCase());
            recipe = XSmithingRecipeBuilder.builder(type).setKey(namespacedKey).setResult(result).base(base).addition(addition).template(template).build();
        } else {
            recipe = SmithingRecipeBuilder.builder().setKey(namespacedKey).setResult(result).base(base).addition(addition).build();
        }
        return new Recipe[]{recipe};
    }

    public static Recipe[] multipleSmithingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        SmithingRecipe[] smithingRecipes = new SmithingRecipe[sourceList.size()];
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
            RecipeChoice base = getRecipeChoice((String) map.get("base"));
            RecipeChoice addition = getRecipeChoice((String) map.get("addition"));
            String typeStr = (String) map.get("type");
            if (typeStr == null) {
                typeStr = "DEFAULT";
            }
            if (CrypticLib.minecraftVersion() >= 12000) {
                RecipeChoice template = getRecipeChoice((String) map.get("template"));
                XSmithingRecipeBuilder.SmithingType type = XSmithingRecipeBuilder.SmithingType.valueOf(typeStr.toUpperCase());
                smithingRecipes[i] = XSmithingRecipeBuilder.builder(type).setKey(namespacedKey).setResult(result).base(base).addition(addition).template(template).build();
            } else {
                smithingRecipes[i] = SmithingRecipeBuilder.builder().setKey(namespacedKey).setResult(result).base(base).addition(addition).build();
            }
        }
        return smithingRecipes;
    }

    public static Recipe[] stoneCuttingRecipe(YamlConfiguration config, String key) {
        RecipeChoice choice = getRecipeChoice(config.getString("source", ""));
        ItemStack result;
        if (config.isList("result")) {
            List<Recipe> recipes = new ArrayList<>();
            List<String> resultList = config.getStringList("result");
            for (int i = 0; i < resultList.size(); i++) {
                ItemStack result1 = ItemManager.matchItem(resultList.get(i));
                String fullKey = key + "." + i;
                NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
                recipes.add(StoneCuttingRecipeBuilder.builder().setKey(namespacedKey).setResult(result1).source(choice).build());
            }
            return recipes.toArray(new Recipe[0]);
        } else {
            result = getResultItem(config);
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), key);
            Recipe recipe = StoneCuttingRecipeBuilder.builder().setKey(namespacedKey).setResult(result).source(choice).build();
            return new Recipe[]{recipe};
        }
    }

    public static Recipe[] multipleStoneCuttingRecipe(YamlConfiguration config, String key) {
        boolean resultIsList = config.isList("result");
        if (resultIsList) {
            List<String> resultList = config.getStringList("result");
            List<String> sourceList = config.getStringList("source");
            StonecuttingRecipe[] stonecuttingRecipes = new StonecuttingRecipe[resultList.size() * sourceList.size()];
            for (int i = 0; i < resultList.size(); i++) {
                ItemStack result = ItemManager.matchItem(resultList.get(i));
                for (int j = 0; j < sourceList.size(); j++) {
                    String fullKey = String.format(key + ".%d.%d", i, j);
                    NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
                    RecipeChoice source = getRecipeChoice(sourceList.get(j));
                    int index = i * sourceList.size() + j;
                    stonecuttingRecipes[index] = StoneCuttingRecipeBuilder.builder().setKey(namespacedKey).setResult(result).source(source).build();
                    stonecuttingRecipes[index].setGroup(key);
                }
            }
            return stonecuttingRecipes;
        } else {
            ItemStack result = getResultItem(config);
            List<String> sourceList = config.getStringList("source");
            StonecuttingRecipe[] stonecuttingRecipes = new StonecuttingRecipe[sourceList.size()];
            for (int i = 0; i < sourceList.size(); i++) {
                String fullKey = key + "." + i;
                NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
                RecipeChoice source = getRecipeChoice(sourceList.get(i));
                stonecuttingRecipes[i] = StoneCuttingRecipeBuilder.builder().setKey(namespacedKey).setResult(result).source(source).build();
                stonecuttingRecipes[i].setGroup(key);
            }
            return stonecuttingRecipes;
        }
    }

    public static Recipe[] potionMixRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), key);
        ItemStack result = getResultItem(config);
        RecipeChoice input = getRecipeChoice(config.getString("source.input", ""));
        RecipeChoice ingredient = getRecipeChoice(config.getString("source.ingredient", ""));
        Recipe recipe = PotionMixBuilder.builder().setKey(namespacedKey).setResult(result).setInput(input).setIngredient(ingredient).build();
        return new Recipe[]{recipe};
    }

    public static Recipe[] multiplePotionMixRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        PotionMixRecipe [] potionMixRecipes = new PotionMixRecipe[sourceList.size()];
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.getInstance(), fullKey);
            RecipeChoice input = getRecipeChoice((String) map.get("input"));
            RecipeChoice ingredient = getRecipeChoice((String) map.get("ingredient"));
            potionMixRecipes[i] = PotionMixBuilder.builder().setKey(namespacedKey).setResult(result).setInput(input).setIngredient(ingredient).build();
        }
        return potionMixRecipes;
    }

    private static Map<Character, RecipeChoice> getShapedRecipeChoiceMap(ConfigurationSection section) {
        Map<Character, RecipeChoice> recipeChoiceMap = new HashMap<>();
        if (section == null)
            return recipeChoiceMap;
        for (String key : section.getKeys(false)) {
            char keyWord = key.toCharArray()[0];
            String itemStr = section.getString(key, "");
            if (itemStr.isEmpty()) {
                throw new IllegalArgumentException("Empty recipe ingredient: " + key);
            }
            recipeChoiceMap.put(keyWord, getRecipeChoice(itemStr));
        }
        return recipeChoiceMap;
    }

    private static ItemStack getResultItem(YamlConfiguration config) {
        String resultStr;
        if (config.getString("type", "shaped").equals("random_cooking")) {
            String tmpStr = config.getStringList("result").get(0);
            tmpStr = tmpStr.substring(0, tmpStr.lastIndexOf(" "));
            resultStr = tmpStr;
        }
        else {
            resultStr = config.getString("result", "");
        }

        if (resultStr.isEmpty()) {
            throw new IllegalArgumentException("Empty recipe result");
        }
        return ItemManager.matchItem(resultStr);
    }

    public static RecipeChoice getRecipeChoice(String itemStr) {
        if (itemStr.contains(":")) {
            if (itemStr.startsWith("tag:")) {
                String tagStr = itemStr.substring(4).toUpperCase(Locale.ROOT);
                Tag<Material> materialTag;
                try {
                    Field field = Tag.class.getField(tagStr);
                    materialTag = (Tag<Material>) field.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return new RecipeChoice.MaterialChoice(materialTag);
            }
            ItemStack item = ItemManager.matchItem(itemStr);
            return new RecipeChoice.ExactChoice(item);
        } else {
            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                throw new IllegalArgumentException(itemStr + " is a not exist item type");
            }
            return new RecipeChoice.MaterialChoice(material);
        }
    }

}
