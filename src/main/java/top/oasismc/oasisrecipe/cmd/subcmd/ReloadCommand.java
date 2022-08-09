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
        regSubCommand(new AbstractSubCommand("config", null) {
            @Override
            public boolean onCommand(CommandSender sender, List<String> args) {
                reloadConfigs();
                OasisRecipe.getInstance().sendMsg(sender, "commands.reload_config");
                return true;
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            reloadPlugin();
            OasisRecipe.getInstance().sendMsg(sender, "commands.reload");
            return true;
        }
        return super.onCommand(sender, args);
    }

    public static void reloadPlugin() {
        reloadConfigs();
        reloadRecipes();
    }

    public static void reloadConfigs() {
        OasisRecipe.getInstance().reloadConfig();
        ItemLoader.getItemFile().reloadConfig();
        ItemLoader.getResultFile().reloadConfig();
        RecipeManager.INSTANCE.getRecipeFile().reloadConfig();
        RemoveCommand.getRemovedRecipeConfig().reloadConfig();
    }

    public static void reloadRecipes() {
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
