package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DisableRecipeCommand extends AbstractSubCommand {

    public static final DisableRecipeCommand INSTANCE = new DisableRecipeCommand();

    private DisableRecipeCommand() {
        super("disable", "craftorithm.command.disable");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return;
        }
        NamespacedKey removeRecipeKey = NamespacedKey.fromString(args.get(0));
        if (!RecipeManager.INSTANCE.serverRecipesCache().containsKey(removeRecipeKey)) {
            LangUtil.sendLang(sender, Languages.COMMAND_DISABLE_NOT_EXIST);
            return;
        }
        List<NamespacedKey> removeRecipeKeys = Collections.singletonList(removeRecipeKey);
        if (RecipeManager.INSTANCE.disableOtherPluginsRecipe(removeRecipeKeys, true)) {
            LangUtil.sendLang(sender, Languages.COMMAND_DISABLE_SUCCESS);
        }
        else
            LangUtil.sendLang(sender, Languages.COMMAND_DISABLE_FAILED);
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (NamespacedKey key : RecipeManager.INSTANCE.serverRecipesCache().keySet()) {
                String str = key.toString();
                if (str.contains(args.get(0)))
                    tabList.add(key.toString());
            }
            return tabList;
        }
        return List.of("");
    }
}
