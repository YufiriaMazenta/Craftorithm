package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.config.ConfigFile;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveCommand extends AbstractSubCommand {

    private static final ConfigFile removedRecipeConfig = new ConfigFile("removed_recipes.yml");
    private final Map<NamespacedKey, Recipe> recipeMap;

    public static final ISubCommand INSTANCE = new RemoveCommand();

    private RemoveCommand() {
        super("remove", null);
        recipeMap = new ConcurrentHashMap<>();
        reloadRecipeSet();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (removeRecipe(args.get(0)))
            OasisRecipe.getPlugin().sendMsg(sender, "commands.removed");
        else
            OasisRecipe.getPlugin().sendMsg(sender, "commands.notExist");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> arrayList = new ArrayList<>();
            for (NamespacedKey key : recipeMap.keySet()) {
                arrayList.add(key.toString());
            }
            arrayList.removeIf(str -> str.startsWith(args.get(0)));
            return arrayList;
        }
        return super.onTabComplete(sender, args);
    }

    public void reloadRecipeSet() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            try {
                Class<?> recipeClass = Class.forName(recipe.getClass().toString());
                Method getKeyMethod = recipeClass.getMethod("getKey");
                NamespacedKey key = (NamespacedKey) getKeyMethod.invoke(recipe);
                recipeMap.put(key, recipe);
            } catch (Exception ignored) {
            }
        }
    }

    public Map<NamespacedKey, Recipe> getRecipeTypeMap() {
        return Collections.unmodifiableMap(recipeMap);
    }

    public static ConfigFile getRemovedRecipeConfig() {
        return removedRecipeConfig;
    }

    public boolean removeRecipe(String keyStr) {
        NamespacedKey key = NamespacedKey.fromString(keyStr);
        Recipe recipe = recipeMap.get(key);
        if (recipe == null) {
            OasisRecipe.getPlugin().sendMsg(Bukkit.getConsoleSender(), "commands.notExist");
            return false;
        }
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe1 = recipeIterator.next();
            if (recipe.equals(recipe1)) {
                recipeIterator.remove();
                List<String> removedList = removedRecipeConfig.getConfig().getStringList("recipes");
                removedList.add(keyStr);
                removedRecipeConfig.getConfig().set(keyStr, removedList);
                removedRecipeConfig.saveConfig();
                return true;
            }
        }
        return false;
    }

}
