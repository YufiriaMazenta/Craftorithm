package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.item.ItemLoader;
import top.oasismc.oasisrecipe.recipe.RecipeManager;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        reloadPlugin();
        OasisRecipe.getPlugin().sendMsg(sender, "commands.reload");
        return true;
    }

    public static void reloadPlugin() {
        OasisRecipe.getPlugin().reloadConfig();
        ItemLoader.getItemFile().reloadConfig();
        ItemLoader.getResultFile().reloadConfig();
        RecipeManager.INSTANCE.getRecipeFile().reloadConfig();
        RemoveCommand.getRemovedRecipeConfig().reloadConfig();
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            RecipeManager.INSTANCE.reloadRecipes();
        } else {
            if (!ItemLoader.INSTANCE.isItemsAdderLoaded()) {
                return;
            }
            RecipeManager.INSTANCE.reloadRecipes();
        }
    }

}
