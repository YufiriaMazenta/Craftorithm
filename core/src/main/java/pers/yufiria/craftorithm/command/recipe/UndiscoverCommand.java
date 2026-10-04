package pers.yufiria.craftorithm.command.recipe;

import crypticlib.Invoker;
import crypticlib.command.CommandContext;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.permission.Permissions;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.List;
import java.util.Map;

public class UndiscoverCommand extends CommandNode {

    public static final UndiscoverCommand INSTANCE = new UndiscoverCommand();

    private UndiscoverCommand() {
        super(CommandInfo
            .builder("undiscover")
            .permission(new PermInfo(Permissions.RECIPE_UNDISCOVER))
            .usage("&r/craftorithm undiscover <target> <recipe_key_pattern>")
            .build()
        );
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        Invoker invoker = context.invoker();
        List<String> args = context.args();
        if (args.size() < 2) {
            sendDescriptions(invoker);
            return;
        }
        CommandSender sender = CommandUtils.invoker2Sender(invoker);
        String targetName = args.get(0);
        String patternStr = args.get(1);

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            LangUtils.sendLang(sender, Languages.COMMAND_UNKNOWN_PLAYER, Map.of("<player_name>", targetName));
            return;
        }

        RecipeUtils.undiscoverRecipe(target, patternStr, count -> {
            if (count > 0) {
                LangUtils.sendLang(sender, Languages.COMMAND_UNDISCOVER_SUCCESS, Map.of(
                    "<count>", String.valueOf(count),
                    "<player_name>", target.getName()
                ));
            } else {
                LangUtils.sendLang(sender, Languages.COMMAND_UNDISCOVER_NO_MATCH);
            }
        });
    }

    @Override
    public void onNoPerm(@NotNull CommandContext context) {
        LangUtils.sendLang(context.invoker(), Languages.COMMAND_NO_PERM);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        List<String> args = context.args();
        if (args.size() <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if (args.size() == 2) {
            return RecipeManager.INSTANCE.serverRecipeKeys().stream().map(NamespacedKey::toString).toList();
        }
        return null;
    }
}
