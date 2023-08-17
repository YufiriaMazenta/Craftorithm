package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubCommand;
import crypticlib.config.impl.YamlConfigWrapper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveRecipeCommand extends AbstractSubCommand {

    private static final YamlConfigWrapper removedRecipeConfig = new YamlConfigWrapper(Craftorithm.getInstance(), "removed_recipes.yml");
    private final Map<NamespacedKey, Recipe> recipeMap;

    public static final ISubCommand INSTANCE = new RemoveRecipeCommand();

    private RemoveRecipeCommand() {
        super("remove", "craftorithm.command.remove");
        recipeMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        if (removeRecipe(args.get(0), true)) {
            LangUtil.sendMsg(sender, "command.remove.success");
        }
        else
            LangUtil.sendMsg(sender, "command.remove.not_exist");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (NamespacedKey key : recipeMap.keySet()) {
                String str = key.toString();
                if (str.startsWith(args.get(0)))
                    tabList.add(key.toString());
            }
            filterTabList(tabList, args.get(0));
            return tabList;
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

    public static YamlConfigWrapper getRemovedRecipeConfig() {
        return removedRecipeConfig;
    }

    public void removeRecipes(List<String> keyStrList, boolean save2File) {
        List<NamespacedKey> keyList = new ArrayList<>();
        for (String str : keyStrList) {
            NamespacedKey key = NamespacedKey.fromString(str);
            if (key != null)
                keyList.add(key);
        }
        if (keyList.size() < 1)
            return;
        if (Craftorithm.getInstance().getVanillaVersion() >= 15) {
            for (NamespacedKey key : keyList) {
                Bukkit.removeRecipe(key);
                if (save2File) {
                    addKey2RemovedConfig(key.toString());
                }
            }
        } else {
            Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                Recipe recipe1 = recipeIterator.next();
                NamespacedKey key1 = RecipeManager.getRecipeKey(recipe1);
                if (key1 == null)
                    continue;
                if (keyList.contains(key1)) {
                    recipeIterator.remove();
                    if (save2File) {
                        addKey2RemovedConfig(key1.toString());
                    }
                    keyList.remove(key1);
                    if (keyList.size() <= 0)
                        break;
                }
            }
        }
        reloadRecipeMap();
    }

    public boolean removeRecipe(String keyStr, boolean save2File) {
        NamespacedKey key = NamespacedKey.fromString(keyStr);
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        if (key == null)
            return false;
        if (Craftorithm.getInstance().getVanillaVersion() >= 15) {
            if (Bukkit.removeRecipe(key) && save2File)
                addKey2RemovedConfig(key.toString());
            reloadRecipeMap();
        } else {
            while (recipeIterator.hasNext()) {
                Recipe recipe1 = recipeIterator.next();
                NamespacedKey key1 = RecipeManager.getRecipeKey(recipe1);
                if (key.equals(key1)) {
                    recipeIterator.remove();
                    if (save2File) {
                        addKey2RemovedConfig(key.toString());
                    }
                    reloadRecipeMap();
                    return true;
                }
            }
        }
        return false;
    }

    public void addKey2RemovedConfig(String key) {
        List<String> removedList = removedRecipeConfig.getConfig().getStringList("recipes");
        if (!removedList.contains(key))
            removedList.add(key);
        removedRecipeConfig.getConfig().set("recipes", removedList);
        removedRecipeConfig.saveConfig();
    }

}
