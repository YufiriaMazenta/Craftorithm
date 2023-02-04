package top.oasismc.oasisrecipe.recipe.handler;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.OasisRecipeAPI;
import top.oasismc.oasisrecipe.cmd.subcmd.RemoveCommand;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;
import top.oasismc.oasisrecipe.util.MsgUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum OldRecipeManager {

    INSTANCE;

    private final YamlFileWrapper recipeFile;
    private final List<String> keyList;

    OldRecipeManager() {
        recipeFile = new YamlFileWrapper("recipe.yml");
        keyList = new ArrayList<>();
    }

    public void loadRecipesFromConfig() {
        YamlConfiguration config = (YamlConfiguration) recipeFile.getConfig();
        for (String recipeName : config.getKeys(false)) {
            if (config.getBoolean(recipeName + ".multiple", false))
                addMultipleRecipe(recipeName, config);
            else
                addRecipe(recipeName, config);
        }
    }

    public void addRecipe(String recipeName, YamlConfiguration config) {
        try {
            String key = recipeName.toLowerCase();
            List<String> choiceStrList = config.getStringList(recipeName + ".items");
            RecipeChoice[] choices = getChoices(recipeName, config, choiceStrList);

            String resultStr = config.getString(recipeName + ".result", "Null");
            if (config.getString(recipeName + ".type", "shaped").startsWith("random_")) {
                resultStr = config.getStringList(recipeName + ".result").get(0);
                resultStr = resultStr.substring(0, resultStr.indexOf(" "));
            }
            ItemStack result = null;

            switch (config.getString(recipeName + ".type", "shaped")) {
                case "shaped":
                    addShapedRecipe(key, result, choices);
                    break;
                case "shapeless":
                    addShapelessRecipe(key, result, choices);
                    break;
                case "furnace":
                case "smoking":
                case "campfire":
                case "blasting":
                    int exp = config.getInt(recipeName + ".exp", 0);
                    int cookTime = config.getInt(recipeName + ".time", 10) * 20;
                    addCookingRecipe(key, result, choices[0], exp, cookTime, config.getString(recipeName + ".type", "furnace"));
                    break;
                case "random_furnace":
                case "random_smoking":
                case "random_blasting":
                    if (OasisRecipe.getInstance().getVanillaVersion() < 18)
                        break;
                    exp = config.getInt(recipeName + ".exp", 0);
                    cookTime = config.getInt(recipeName + ".time", 10) * 20;
                    addCookingRecipe(key, result, choices[0], exp, cookTime, config.getString(recipeName + ".type", "furnace"));
                    break;
                case "smithing":
                    if (OasisRecipe.getInstance().getVanillaVersion() < 14)
                        break;
                    addSmithingRecipe(key, result, choices[0], choices[1]);
                    break;
                case "stoneCutting":
                    if (OasisRecipe.getInstance().getVanillaVersion() < 14)
                        break;
                    addStoneCuttingRecipe(key, result, choices[0]);
                    break;
            }
            keyList.add(config.getString(recipeName + ".key"));
        } catch (Exception e) {
            MsgUtil.info("&cSome errors occurred while loading the " + recipeName + " recipe, please check your config file");
            e.printStackTrace();
        }
    }

    public void addMultipleRecipe(String recipeName, YamlConfiguration config) {
        try {
            String key = recipeName.toLowerCase();
            ConfigurationSection section = config.getConfigurationSection(recipeName + ".items");
            Validate.notNull(section, "The recipe must set the crafting item");
            Map<String, RecipeChoice[]> choicesMap = new ConcurrentHashMap<>();
            for (String sectionKey : section.getKeys(false)) {
                List<String> choiceStrList = section.getStringList(sectionKey);
                RecipeChoice[] choices = getChoices(recipeName, config, choiceStrList);
                choicesMap.put(sectionKey, choices);
            }

            String resultStr = config.getString(recipeName + ".result", "Null");
            if (config.getString(recipeName + ".type", "shaped").startsWith("random_")) {
                resultStr = config.getStringList(recipeName + ".result").get(0);
                resultStr = resultStr.substring(0, resultStr.indexOf(" "));
            }
            ItemStack result = null;

            switch (config.getString(recipeName + ".type", "shaped")) {
                case "shaped":
                    for (String s : choicesMap.keySet()) {
                        s = s.toLowerCase();
                        addShapedRecipe(key + "." + s, result, choicesMap.get(s));
                    }
                    break;
                case "shapeless":
                    for (String s : choicesMap.keySet()) {
                        s = s.toLowerCase();
                        addShapelessRecipe(key + "." + s, result, choicesMap.get(s));
                    }
                    break;
                case "furnace":
                case "smoking":
                case "campfire":
                case "blasting":
                    int exp = config.getInt(recipeName + ".exp", 0);
                    int cookTime = config.getInt(recipeName + ".time", 10) * 20;
                    for (String s : choicesMap.keySet()) {
                        s = s.toLowerCase();
                        addCookingRecipe(key + "." + s, result, choicesMap.get(s)[0], exp, cookTime, config.getString(recipeName + ".type", "furnace"));
                    }
                    break;
                case "random_furnace":
                case "random_smoking":
                case "random_blasting":
                    if (OasisRecipe.getInstance().getVanillaVersion() < 18)
                        break;
                    exp = config.getInt(recipeName + ".exp", 0);
                    cookTime = config.getInt(recipeName + ".time", 10) * 20;
                    for (String s : choicesMap.keySet()) {
                        s = s.toLowerCase();
                        addCookingRecipe(key + "." + s, result, choicesMap.get(s)[0], exp, cookTime, config.getString(recipeName + ".type", "furnace"));
                    }
                    break;
                case "smithing":
                    if (OasisRecipe.getInstance().getVanillaVersion() < 14)
                        break;
                    for (String s : choicesMap.keySet()) {
                        s = s.toLowerCase();
                        addSmithingRecipe(key + "." + s, result, choicesMap.get(s)[0], choicesMap.get(s)[1]);
                    }
                    break;
                case "stoneCutting":
                    if (OasisRecipe.getInstance().getVanillaVersion() < 14)
                        break;
                    for (String s : choicesMap.keySet()) {
                        s = s.toLowerCase();
                        addStoneCuttingRecipe(key + "." + s, result, choicesMap.get(s)[0]);
                    }
                    break;
            }
            keyList.add(config.getString(recipeName + ".key"));
        } catch (Exception e) {
            MsgUtil.info("&cSome errors occurred while loading the " + recipeName + " recipe, please check your config file");
            e.printStackTrace();
        }
    }

    private RecipeChoice[] getChoices(String recipeName, YamlConfiguration config, List<String> choiceStrList) {
        RecipeChoice[] choices = new RecipeChoice[choiceStrList.size()];
        switch (config.getString(recipeName + ".type", "shaped")) {
            case "shaped":
                choices = getShapedRecipeItems(choiceStrList);
                break;
            case "shapeless":
                choices = getShapelessRecipeItems(choiceStrList);
                break;
            case "furnace":
            case "smoking":
            case "campfire":
            case "blasting":
            case "random_furnace":
            case "random_smoking":
            case "random_blasting":
//            case "stoneCutting":
//                choices[0] = getChoiceFromStr(choiceStrList.get(0));
//                break;
//            case "smithing":
//                choices[0] = getChoiceFromStr(choiceStrList.get(0));
//                choices[1] = getChoiceFromStr(choiceStrList.get(1));
//                break;
        }
        return choices;
    }

    public RecipeChoice[] getShapedRecipeItems(List<String> items) {
        RecipeChoice[] choices = new RecipeChoice[9];
//        for(int i = 0; i < 3; i++) {
//            int space1 = items.get(i).indexOf(" ");
//            String item1 = items.get(i).substring(0, space1);
//            int space2 = items.get(i).indexOf(" ", space1 + 1);
//            String item2 = items.get(i).substring(space1 + 1, space2);
//            String item3 = items.get(i).substring(space2 + 1);
//            choices[i * 3] = getChoiceFromStr(item1);
//            choices[i * 3 + 1] = getChoiceFromStr(item2);
//            choices[i * 3 + 2] = getChoiceFromStr(item3);
//        }
        return choices;
    }

    public RecipeChoice[] getShapelessRecipeItems(List<String> items) {
        RecipeChoice[] choices = new RecipeChoice[items.size()];
        for (int i = 0; i < items.size(); i++) {
//            choices[i] = getChoiceFromStr(items.get(i));
        }
        return choices;
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public void addStoneCuttingRecipe(String key, ItemStack result, RecipeChoice item) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        StonecuttingRecipe recipe = new StonecuttingRecipe(recipeKey, result, item);
        Bukkit.getServer().addRecipe(recipe);
    }

    public void addSmithingRecipe(String key, ItemStack result, RecipeChoice base, RecipeChoice addition) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        SmithingRecipe recipe = new SmithingRecipe(recipeKey, result, base, addition);
        Bukkit.getServer().addRecipe(recipe);
    }

    //待优化
    public void addShapedRecipe(String key, ItemStack result, RecipeChoice[] itemList) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        StringBuffer[] shapeArr = {new StringBuffer("abc"), new StringBuffer("def"), new StringBuffer("ghi")};
        int tmpNum = 0;
        for (int i = 0; i < 3; i++) {
            for (int i1 = 0; i1 < 3; i1++) {
                if (itemList[tmpNum].getItemStack().getType().equals(Material.AIR)) {
                    shapeArr[i].setCharAt(i1, ' ');
                }
                tmpNum ++;
            }
        }
        recipe.shape(shapeArr[0].toString(), shapeArr[1].toString(), shapeArr[2].toString());
        String tempStr = "abcdefghi";
        int i = 0;
        for (int j = 0; j < 9; j ++) {
            if (!itemList[j].getItemStack().getType().equals(Material.AIR))
                recipe.setIngredient(tempStr.charAt(i), itemList[j]);
            i++;
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    public void addShapelessRecipe(String key, ItemStack result, RecipeChoice[] itemList) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        ShapelessRecipe recipe = new ShapelessRecipe(recipeKey, result);
        for (RecipeChoice choice : itemList) {
            recipe.addIngredient(choice);
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    public void addCookingRecipe(String key, ItemStack result, RecipeChoice item, int exp, int cookingTime, String type) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getInstance(), key);
        Recipe recipe;
        switch (type) {
            case "furnace":
            case "random_furnace":
            default:
                recipe = new FurnaceRecipe(recipeKey, result, item, exp, cookingTime);
                break;
            case "smoking":
            case "random_smoking":
                recipe = new SmokingRecipe(recipeKey, result, item, exp, cookingTime);
                break;
            case "blasting":
            case "random_blasting":
                recipe = new BlastingRecipe(recipeKey, result, item, exp, cookingTime);
                break;
            case "campfire":
                recipe = new CampfireRecipe(recipeKey, result, item, exp, cookingTime);
                break;
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    public String getRecipeName(Recipe recipe) {
        Set<String> recipes = getRecipeFile().getConfig().getKeys(false);
        NamespacedKey namespacedKey = null;
        if (recipe instanceof ShapedRecipe) {
            namespacedKey = ((ShapedRecipe) recipe).getKey();
        } else if (recipe instanceof ShapelessRecipe){
            namespacedKey = ((ShapelessRecipe) recipe).getKey();
        } else if (recipe instanceof SmithingRecipe) {
            namespacedKey = ((SmithingRecipe) recipe).getKey();
        } else if (recipe instanceof CookingRecipe) {
            namespacedKey = ((CookingRecipe<?>) recipe).getKey();
        }
        if (namespacedKey == null) {
            return null;
        }
        for (String key : recipes) {
            if (namespacedKey.getKey().equals(key)) {
                return key;
            }
        }
        return null;
    }

    public YamlFileWrapper getRecipeFile() {
        return recipeFile;
    }

    public void reloadRecipes() {
        Bukkit.resetRecipes();
        getKeyList().clear();
        loadRecipesFromConfig();
        loadRecipeFromOtherPlugins();
        removeRecipes();
        ((RemoveCommand) RemoveCommand.INSTANCE).reloadRecipeMap();
    }

    private void removeRecipes() {
        List<String> removedRecipes = RemoveCommand.getRemovedRecipeConfig().getConfig().getStringList("recipes");
        if (OasisRecipe.getInstance().getConfig().getBoolean("remove_all_vanilla_recipe", false)) {
            for (NamespacedKey key : ((RemoveCommand) RemoveCommand.INSTANCE).getRecipeMap().keySet()) {
                if (key.getNamespace().equals("minecraft")) {
                    if (removedRecipes.contains(key.toString()))
                        continue;
                    removedRecipes.add(key.toString());
                }
            }
        }
        ((RemoveCommand) RemoveCommand.INSTANCE).removeRecipes(removedRecipes);

    }

    private void loadRecipeFromOtherPlugins() {
        Map<Plugin, List<Recipe>> pluginRecipeMap = OasisRecipeAPI.INSTANCE.getPluginRecipeMap();
        for (Plugin plugin : pluginRecipeMap.keySet()) {
            for (Recipe recipe : pluginRecipeMap.get(plugin)) {
                OasisRecipe.getInstance().getServer().addRecipe(recipe);
            }
        }
    }

}
