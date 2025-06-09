package pers.yufiria.craftorithm.command.menu;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import crypticlib.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.ui.custom.CustomMenuManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class OpenMenuCommand extends CommandNode {

    public static final OpenMenuCommand INSTANCE = new OpenMenuCommand();

    private OpenMenuCommand() {
        super(
            CommandInfo
                .builder("openmenu")
                .permission(new PermInfo("craftorithm.command.openmenu"))
                .usage("&r/craftorithm openmenu <menu_name> [player_name]")
                .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, @NotNull List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        String menuName = args.get(0);
        Optional<Function<Player, Menu>> menuOpenerOpt = CustomMenuManager.INSTANCE.getMenuOpenerOpt(menuName);
        if (menuOpenerOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_OPENMENU_UNKNOWN_MENU, Map.of("<menu_name>", menuName));
        }
        Function<Player, Menu> menuOpener = menuOpenerOpt.get();
        Player target;
        if (args.size() > 1) {
            String targetName = args.get(1);
            Player player = Bukkit.getPlayer(targetName);
            if (player == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_UNKNOWN_PLAYER, Map.of("<player_name>", targetName));
                return;
            }
            target = player;
        } else {
            if (!CommandUtils.checkInvokerIsPlayer(invoker)) {
                return;
            }
            target = (Player) invoker.asPlayer().getPlatformPlayer();
        }
        menuOpener.apply(target);
    }

    @Override
    public @Nullable List<String> tab(@NotNull CommandInvoker invoker, @NotNull List<String> args) {
        return CustomMenuManager.INSTANCE.menuOpeners().keySet().stream().toList();
    }
}
