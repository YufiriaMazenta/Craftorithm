package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DisableRecipeCommand extends BukkitSubcommand {

    public static final DisableRecipeCommand INSTANCE = new DisableRecipeCommand();

    private DisableRecipeCommand() {
        super(CommandInfo
            .builder("disable")
            .permission(new PermInfo("craftorithm.command.disable"))
            .usage("&r/craftorithm disable <recipe_id>")
            .build()
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        NamespacedKey removeRecipeKey = NamespacedKey.fromString(args.get(0));
        if (!RecipeManager.INSTANCE.serverRecipesCache().containsKey(removeRecipeKey)) {
            LangUtils.sendLang(sender, Languages.COMMAND_DISABLE_NOT_EXIST);
            return;
        }
        List<NamespacedKey> removeRecipeKeys = Collections.singletonList(removeRecipeKey);
        if (RecipeManager.INSTANCE.disableOtherPluginsRecipe(removeRecipeKeys, true)) {
            LangUtils.sendLang(sender, Languages.COMMAND_DISABLE_SUCCESS);
        }
        else
            LangUtils.sendLang(sender, Languages.COMMAND_DISABLE_FAILED);
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
