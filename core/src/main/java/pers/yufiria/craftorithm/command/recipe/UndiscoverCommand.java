package pers.yufiria.craftorithm.command.recipe;

import crypticlib.CrypticLibBukkit;
import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UndiscoverCommand extends CommandNode {

    public static final UndiscoverCommand INSTANCE = new UndiscoverCommand();

    private UndiscoverCommand() {
        super(CommandInfo
            .builder("undiscover")
            .permission(new PermInfo("craftorithm.command.undiscover"))
            .usage("&r/craftorithm undiscover <target> <recipe_key_pattern>")
            .build()
        );
    }

    @Override
    public void execute(@NotNull Invoker invoker, @NotNull List<String> args) {
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

        Pattern pattern;
        try {
            pattern = Pattern.compile(patternStr);
        } catch (PatternSyntaxException e) {
            pattern = null;
        }

        final Pattern finalPattern = pattern;
        List<NamespacedKey> undiscoverRecipes = RecipeManager.INSTANCE.serverRecipeKeys().stream().filter(key -> {
            if (finalPattern != null) {
                return finalPattern.matcher(key.toString()).matches();
            } else {
                return key.toString().equals(patternStr);
            }
        }).toList();

        CrypticLibBukkit.scheduler().runOnEntity(target, () -> {
            int count = target.undiscoverRecipes(undiscoverRecipes);
            if (count > 0) {
                LangUtils.sendLang(sender, Languages.COMMAND_UNDISCOVER_SUCCESS, Map.of(
                    "<count>", String.valueOf(count),
                    "<player_name>", target.getName()
                ));
            } else {
                LangUtils.sendLang(sender, Languages.COMMAND_UNDISCOVER_NO_MATCH);
            }
        }, () -> {});

    }

    @Override
    public void onNoPerm(@NotNull Invoker invoker, @NotNull List<String> args) {
        LangUtils.sendLang(invoker, Languages.COMMAND_NO_PERM);
    }

    @Override
    public List<String> tabComplete(@NotNull Invoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if (args.size() == 2) {
            return RecipeManager.INSTANCE.serverRecipeKeys().stream().map(NamespacedKey::toString).toList();
        }
        return null;
    }
}
