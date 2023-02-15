package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import top.oasismc.oasisrecipe.api.cmd.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;
import top.oasismc.oasisrecipe.recipe.RecipeManager;
import top.oasismc.oasisrecipe.util.LangUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveCommand extends AbstractSubCommand {

    private static final YamlFileWrapper removedRecipeConfig = new YamlFileWrapper("removed_recipes.yml");
    private final Map<NamespacedKey, Recipe> recipeMap;

    public static final ISubCommand INSTANCE = new RemoveCommand();

    private RemoveCommand() {
        super("remove");
        recipeMap = new ConcurrentHashMap<>();
        reloadRecipeMap();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        if (removeRecipe(args.get(0))) {
            LangUtil.sendMsg(sender, "command.remove.success");
        }
        else
            LangUtil.sendMsg(sender, "command.remove.not_exist");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> arrayList = new ArrayList<>();
            for (NamespacedKey key : recipeMap.keySet()) {
                String str = key.toString();
                if (str.startsWith(args.get(0)))
                    arrayList.add(key.toString());
            }
            return arrayList;
        }
        return super.onTabComplete(sender, args);
    }

    public void reloadRecipeMap() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        recipeMap.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = RecipeManager.getRecipeKey(recipe);
            recipeMap.put(key, recipe);
        }
    }

    public Map<NamespacedKey, Recipe> getRecipeMap() {
        return Collections.unmodifiableMap(recipeMap);
    }

    public static YamlFileWrapper getRemovedRecipeConfig() {
        return removedRecipeConfig;
    }

    public void removeRecipes(List<String> keyStrList) {
        List<NamespacedKey> keyList = new ArrayList<>();
        for (String str : keyStrList) {
            NamespacedKey key = NamespacedKey.fromString(str);
            if (key != null)
                keyList.add(key);
        }
        if (keyList.size() < 1)
            return;
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe1 = recipeIterator.next();
            NamespacedKey key1 = RecipeManager.getRecipeKey(recipe1);
            if (key1 == null)
                continue;
            if (keyList.contains(key1)) {
                recipeIterator.remove();
                List<String> removedList = removedRecipeConfig.getConfig().getStringList("recipes");
                if (!removedList.contains(key1.toString())) {
                    removedList.add(key1.toString());
                    removedRecipeConfig.getConfig().set("recipes", removedList);
                }
                keyList.remove(key1);
                if (keyList.size() <= 0)
                    break;
            }
        }
        reloadRecipeMap();
        removedRecipeConfig.saveConfig();
    }

    public boolean removeRecipe(String keyStr) {
        NamespacedKey key = NamespacedKey.fromString(keyStr);
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        if (key == null)
            return false;
        while (recipeIterator.hasNext()) {
            Recipe recipe1 = recipeIterator.next();
            NamespacedKey key1 = RecipeManager.getRecipeKey(recipe1);
            if (key.equals(key1)) {
                recipeIterator.remove();
                List<String> removedList = removedRecipeConfig.getConfig().getStringList("recipes");
                if (!removedList.contains(keyStr))
                    removedList.add(keyStr);
                removedRecipeConfig.getConfig().set("recipes", removedList);
                removedRecipeConfig.saveConfig();
                reloadRecipeMap();
                return true;
            }
        }
        return false;
    }

}
