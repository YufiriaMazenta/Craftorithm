package top.oasismc.oasisrecipe.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.item.ItemManager;

import java.util.*;

public class RecipeBuilder {

    public static Recipe buildShapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), key);

        List<String> shapeStrList = config.getStringList("shape");
        if (shapeStrList.size() > 3) {
            shapeStrList = shapeStrList.subList(0, 3);
        }
        String[] shape = new String[shapeStrList.size()];
        shape = shapeStrList.toArray(shape);
        return buildShapedRecipe(namespacedKey, result, shape, recipeChoiceMap);
    }

    public static Recipe[] buildMultipleShapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        List<?> shapeList = config.getList("shape", new ArrayList<>());
        ShapedRecipe[] shapedRecipes = new ShapedRecipe[shapeList.size()];
        for (int i = 0; i < shapeList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), fullKey);
            List<String> shapeStrList = (List<String>) shapeList.get(i);
            if (shapeStrList.size() > 3) {
                shapeStrList = shapeStrList.subList(0, 3);
            }
            String[] shape = new String[shapeStrList.size()];
            shape = shapeStrList.toArray(shape);
            shapedRecipes[i] = buildShapedRecipe(namespacedKey, result, shape, recipeChoiceMap);
            shapedRecipes[i].setGroup(key);
        }
        return shapedRecipes;
    }

    public static Recipe buildShapelessRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        ItemStack result = getResultItem(config);
        List<String> itemStrList = config.getStringList("source");
        List<RecipeChoice> recipeChoiceList = new ArrayList<>();
        for (String itemStr : itemStrList) {
            recipeChoiceList.add(getRecipeChoice(itemStr));
        }
        return buildShapelessRecipe(namespacedKey, result, recipeChoiceList);
    }

    public static Recipe[] buildMultipleShapelessRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<?> itemsList = config.getList("source", new ArrayList<>());
        ShapelessRecipe[] shapelessRecipes = new ShapelessRecipe[itemsList.size()];

        for (int i = 0; i < itemsList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), fullKey);
            List<String> itemStrList = (List<String>) itemsList.get(i);
            List<RecipeChoice> choiceList = new ArrayList<>();
            for (String itemStr : itemStrList) {
                choiceList.add(getRecipeChoice(itemStr));
            }
            shapelessRecipes[i] = buildShapelessRecipe(namespacedKey, result, choiceList);
            shapelessRecipes[i].setGroup(key);
        }
        return shapelessRecipes;
    }

    public static Recipe buildCookingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        ItemStack result = getResultItem(config);
        String choiceStr = config.getString("source.item", "");
        String cookingBlock = config.getString("source.block", "furnace");
        RecipeChoice source = getRecipeChoice(choiceStr);
        int exp = config.getInt("exp", 0);
        int time = config.getInt("time", 200);
        return buildCookingRecipe(namespacedKey, result, source, exp, time, cookingBlock);
    }

    public static Recipe[] buildMultipleCookingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        int globalExp = config.getInt("exp", 0);
        int globalTime = config.getInt("time", 0);

        List<Map<?, ?>> sourceList = config.getMapList("source");
        CookingRecipe<?>[] cookingRecipes = new CookingRecipe[sourceList.size()];
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), fullKey);
            RecipeChoice source = getRecipeChoice((String) map.get("item"));
            String cookingBlock = (String) map.get("block");
            int exp = map.containsKey("exp") ? (Integer) map.get("exp") : globalExp;
            int time = map.containsKey("time") ? (Integer) map.get("time") : globalTime;
            cookingRecipes[i] = buildCookingRecipe(namespacedKey, result, source, exp, time, cookingBlock);
            cookingRecipes[i].setGroup(key);
        }
        return cookingRecipes;
    }

    public static Recipe buildSmithingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        ItemStack result = getResultItem(config);
        RecipeChoice base = getRecipeChoice(config.getString("source.base", ""));
        RecipeChoice addition = getRecipeChoice(config.getString("source.addition", ""));
        return new SmithingRecipe(namespacedKey, result, base, addition);
    }

    public static Recipe[] buildMultipleSmithingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        SmithingRecipe[] smithingRecipes = new SmithingRecipe[sourceList.size()];
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), fullKey);
            RecipeChoice base = getRecipeChoice((String) map.get("base"));
            RecipeChoice addition = getRecipeChoice((String) map.get("addition"));
            smithingRecipes[i] = buildSmithingRecipe(namespacedKey, result, base, addition);
        }
        return smithingRecipes;
    }

    public static Recipe buildStoneCuttingRecipe(YamlConfiguration config, String key) {
        RecipeChoice choice = getRecipeChoice(config.getString("source", ""));
        ItemStack result;
        if (config.isList("result")) {
            List<String> resultList = config.getStringList("result");
            result = parseRecipeItemStr(resultList.get(0));
            for (int i = 1; i < resultList.size(); i++) {
                ItemStack result1 = parseRecipeItemStr(resultList.get(i));
                String fullKey = key + "." + i;
                NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), fullKey);
                RecipeManager.regRecipe(namespacedKey, buildStoneCuttingRecipe(namespacedKey, result1, choice), config);
            }
        } else {
            result = getResultItem(config);
        }
        NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        return buildStoneCuttingRecipe(namespacedKey, result, choice);
    }

    public static Recipe[] buildMultipleStoneCuttingRecipe(YamlConfiguration config, String key) {
        boolean resultIsList = config.isList("result");
        if (resultIsList) {
            List<String> resultList = config.getStringList("result");
            List<String> sourceList = config.getStringList("source");
            StonecuttingRecipe[] stonecuttingRecipes = new StonecuttingRecipe[resultList.size() * sourceList.size()];
            for (int i = 0; i < resultList.size(); i++) {
                ItemStack result = parseRecipeItemStr(resultList.get(i));
                for (int j = 0; j < sourceList.size(); j++) {
                    String fullKey = String.format(key + ".%d.%d", i, j);
                    NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), fullKey);
                    RecipeChoice source = getRecipeChoice(sourceList.get(j));
                    int index = i * sourceList.size() + j;
                    stonecuttingRecipes[index] = buildStoneCuttingRecipe(namespacedKey, result, source);
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
                NamespacedKey namespacedKey = new NamespacedKey(OasisRecipe.getInstance(), fullKey);
                RecipeChoice source = getRecipeChoice(sourceList.get(i));
                stonecuttingRecipes[i] = buildStoneCuttingRecipe(namespacedKey, result, source);
                stonecuttingRecipes[i].setGroup(key);
            }
            return stonecuttingRecipes;
        }
    }

    private static ShapedRecipe buildShapedRecipe(NamespacedKey key, ItemStack result, String[] shape, Map<Character, RecipeChoice> recipeChoiceMap) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
        shapedRecipe.shape(shape);
        Set<Character> shapeStrChars = new HashSet<>();
        for (String s : shape) {
            for (char c : s.toCharArray()) {
                shapeStrChars.add(c);
            }
        }
        Set<Character> keySet = new HashSet<>(recipeChoiceMap.keySet());
        keySet.removeIf((character -> !shapeStrChars.contains(character)));
        for (Character ingredientKey : keySet) {
            shapedRecipe.setIngredient(ingredientKey, recipeChoiceMap.get(ingredientKey));
        }
        return shapedRecipe;
    }

    private static ShapelessRecipe buildShapelessRecipe(NamespacedKey key, ItemStack result, List<RecipeChoice> choiceList) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result);
        for (RecipeChoice choice : choiceList) {
            shapelessRecipe.addIngredient(choice);
        }
        return shapelessRecipe;
    }

    private static CookingRecipe<?> buildCookingRecipe(NamespacedKey key, ItemStack result, RecipeChoice choice, int exp, int time, String block) {
        CookingRecipe<?> cookingRecipe;
        switch (block) {
            case "furnace":
            default:
                cookingRecipe = new FurnaceRecipe(key, result, choice, exp, time);
                break;
            case "smoker":
                cookingRecipe = new SmokingRecipe(key, result, choice, exp, time);
                break;
            case "blast":
                cookingRecipe = new BlastingRecipe(key, result, choice, exp, time);
                break;
            case "campfire":
                cookingRecipe = new CampfireRecipe(key, result, choice, exp, time);
                break;
        }
        return cookingRecipe;
    }

    private static SmithingRecipe buildSmithingRecipe(NamespacedKey key, ItemStack result, RecipeChoice base, RecipeChoice addition) {
        return new SmithingRecipe(key, result, base, addition);
    }

    private static StonecuttingRecipe buildStoneCuttingRecipe(NamespacedKey key, ItemStack result, RecipeChoice source) {
        return new StonecuttingRecipe(key, result, source);
    }

    private static Map<Character, RecipeChoice> getShapedRecipeChoiceMap(ConfigurationSection section) {
        Map<Character, RecipeChoice> recipeChoiceMap = new HashMap<>();
        if (section == null)
            return recipeChoiceMap;
        for (String key : section.getKeys(false)) {
            char keyWord = key.toCharArray()[0];
            String itemStr = section.getString(key, "");
            if (itemStr.length() < 1) {
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

        if (resultStr.length() < 1) {
            throw new IllegalArgumentException("Empty recipe result");
        }
        return parseRecipeItemStr(resultStr);
    }

    public static RecipeChoice getRecipeChoice(String itemStr) {
        if (itemStr.startsWith("items:")) {
            ItemStack item = parseRecipeItemStr(itemStr);
            return new RecipeChoice.ExactChoice(item);
        } else {
            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                throw new IllegalArgumentException(itemStr + " is a not exist item type");
            }
            return new RecipeChoice.MaterialChoice(material);
        }
    }

    public static ItemStack parseRecipeItemStr(String itemStr) {
        ItemStack item;
        int lastSpaceIndex = itemStr.lastIndexOf(" ");
        int amountScale = 1;
        if (lastSpaceIndex > 0) {
            amountScale = Integer.parseInt(itemStr.substring(lastSpaceIndex + 1));
            itemStr = itemStr.substring(0, lastSpaceIndex);
        }
        itemStr = itemStr.replace(" ", "");
        if (itemStr.startsWith("items:")) {
            itemStr = itemStr.substring("items:".length());
            item = ItemManager.getOasisRecipeItem(itemStr);
        } else {
            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                throw new IllegalArgumentException(itemStr + " is a not exist item type");
            }
            item = new ItemStack(material);
        }
        item.setAmount(item.getAmount() * amountScale);
        return item;
    }

}
