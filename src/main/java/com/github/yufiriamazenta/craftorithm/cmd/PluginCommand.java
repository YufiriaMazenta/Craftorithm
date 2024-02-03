package com.github.yufiriamazenta.craftorithm.cmd;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.cmd.sub.item.ItemCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.recipe.CreateRecipeCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.recipe.DisableRecipeCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.recipe.RecipeListCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.recipe.RemoveRecipeCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.CommandTreeInfo;
import crypticlib.command.CommandTreeNode;
import crypticlib.command.CommandTreeRoot;
import crypticlib.command.annotation.CommandNode;
import crypticlib.command.annotation.CommandTree;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

@CommandTree
public class PluginCommand extends CommandTreeRoot {

    PluginCommand() {
        super(new CommandTreeInfo("craftorithm", new PermInfo("craftorithm.command"), new String[]{"craft", "cra"}));
        setExecutor((sender, args) -> {
            LangUtil.sendLang(sender, Languages.COMMAND_UNDEFINED_SUBCMD);
            return true;
        });
    }

    @CommandNode
    private CommandTreeNode reload = new CommandTreeNode(
        new CommandTreeNode.NodeInfo("reload", new PermInfo("craftorithm.command.reload")),
        (sender, args) -> {
            try {
                Craftorithm.instance().reloadConfig();
                ItemUtils.reloadCannotCraftLore();
                ArcencielDispatcher.INSTANCE.functionFile().reloadConfig();
                CraftorithmItemProvider.INSTANCE.reloadItemProvider();
                ItemManager.INSTANCE.reloadCustomCookingFuel();
                RecipeManager.INSTANCE.reloadRecipeManager();
                LangUtil.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                LangUtil.sendLang(sender, Languages.COMMAND_RELOAD_EXCEPTION);
            }
            return true;
        }
    );

    @CommandNode
    private CommandTreeNode version = new CommandTreeNode(
        new CommandTreeNode.NodeInfo("version",new PermInfo("craftorithm.command.version")),
        (sender, args) -> {
            LangUtil.sendLang(sender, Languages.COMMAND_VERSION);
            return true;
        }
    );

    @CommandNode
    private CommandTreeNode run = new CommandTreeNode(
        new CommandTreeNode.NodeInfo("run", new PermInfo("craftorithm.command.run")),
        (sender, args) -> {
            if (!checkSenderIsPlayer(sender))
                return true;
            if (args.isEmpty()) {
                sendNotEnoughCmdParamMsg(sender, 1);
                return true;
            }
            long startTime = System.currentTimeMillis();
            StringJoiner arcencielBlock = new StringJoiner(" ");
            for (String arg : args) {
                arcencielBlock.add(arg);
            }
            ArcencielDispatcher.INSTANCE.dispatchArcencielBlock((Player) sender, arcencielBlock.toString());
            long execTime = System.currentTimeMillis() - startTime;
            LangUtil.sendLang(sender, Languages.COMMAND_RUN_ARCENCIEL_SUCCESS, CollectionsUtil.newStringHashMap("<time>", String.valueOf(execTime)));
            return true;
        }
    );

    @CommandNode
    private CommandTreeNode item = ItemCommand.INSTANCE;
    @CommandNode
    private CommandTreeNode disable = DisableRecipeCommand.INSTANCE;
    @CommandNode
    private CommandTreeNode removeRecipe = RemoveRecipeCommand.INSTANCE;
    @CommandNode
    private CommandTreeNode createRecipe = CreateRecipeCommand.INSTANCE;
    @CommandNode
    private CommandTreeNode recipeList = RecipeListCommand.INSTANCE;

    public boolean checkSenderIsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            LangUtil.sendLang(sender, Languages.COMMAND_PLAYER_ONLY);
            return false;
        }
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, int paramNum) {
        sendNotEnoughCmdParamMsg(sender, String.valueOf(paramNum));
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, String paramStr) {
        LangUtil.sendLang(sender, Languages.COMMAND_NOT_ENOUGH_PARAM, CollectionsUtil.newStringHashMap("<number>", paramStr));
    }

}
