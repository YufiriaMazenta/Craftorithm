package top.oasismc.oasisrecipe.recipe.object.vanilla;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import top.oasismc.oasisrecipe.item.ItemManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeBuilder {

    public static Recipe buildShapedRecipe(YamlConfiguration config, NamespacedKey key) {
        Map<Character, RecipeChoice> recipeChoiceMap;
        String[] shape = new String[3];
        String resultStr = config.getString("result", "");
        if (resultStr.length() < 1) {
            throw new IllegalArgumentException("Empty recipe result");
        }
        ItemStack result = parseRecipeItemStr(resultStr);
        List<String> shapeStrList = config.getStringList("shape");
        if (shapeStrList.size() > 3) {
            shapeStrList = shapeStrList.subList(0, 3);
        }

        shape = shapeStrList.toArray(shape);
        recipeChoiceMap = loadRecipeChoiceMap(config.getConfigurationSection("items"));
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
        shapedRecipe.shape(shape);
        for (Character ingredientKey : recipeChoiceMap.keySet()) {
            shapedRecipe.setIngredient(ingredientKey, recipeChoiceMap.get(ingredientKey));
        }
        return shapedRecipe;
    }

    private static Map<Character, RecipeChoice> loadRecipeChoiceMap(ConfigurationSection section) {
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
        return item;
    }

}
