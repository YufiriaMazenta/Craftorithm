package pers.yufiria.craftorithm.command.menu;

import crypticlib.Invoker;
import crypticlib.command.CommandContext;
import pers.yufiria.craftorithm.command.AbstractSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import crypticlib.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.permission.Permissions;
import pers.yufiria.craftorithm.ui.custom.CustomMenuManager;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class OpenMenuCommand extends AbstractSubcommand {

    public static final OpenMenuCommand INSTANCE = new OpenMenuCommand();

    private OpenMenuCommand() {
        super(
            CommandInfo
                .builder("openmenu")
                .permission(new PermInfo(Permissions.COMMAND_OPEN_MENU))
                .usage("&r/craftorithm openmenu <menu_name> [player_name]")
                .build()
        );
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        Invoker invoker = context.invoker();
        List<String> args = context.args();
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        String menuName = args.getFirst();
        Optional<Function<Player, Menu>> menuOpenerOpt = CustomMenuManager.INSTANCE.getMenuCreatorOpt(menuName);
        if (menuOpenerOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_OPENMENU_UNKNOWN_MENU, Map.of("<menu_name>", menuName));
            return;
        }
        Function<Player, Menu> menuOpener = menuOpenerOpt.get();
        Player target;
        if (args.size() > 1) {
            String targetName = args.get(1);
            target = Bukkit.getPlayer(targetName);
            if (target == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_UNKNOWN_PLAYER, Map.of("<player_name>", targetName));
                return;
            }
        } else {
            target = PlayerUtils.resolveInvokerPlayer(invoker);
            if (target == null) {
                return;
            }
        }
        menuOpener.apply(target);
    }


    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandContext context) {
        return CustomMenuManager.INSTANCE.menuCreators().keySet().stream().toList();
    }
}
