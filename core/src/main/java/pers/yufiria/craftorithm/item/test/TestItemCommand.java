package pers.yufiria.craftorithm.item.test;

import crypticlib.Invoker;
import crypticlib.command.CommandTree;
import crypticlib.command.annotation.Command;
import crypticlib.perm.PermInfo;
import crypticlib.util.InventoryHelper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;

@Command
public class TestItemCommand extends CommandTree {
    public TestItemCommand() {
        super("testitem", new PermInfo("craftorithm.test"));
    }

    @Override
    public void execute(@NotNull Invoker invoker, @NotNull List<String> args) {
        if (args.isEmpty()) {
            invoker.sendMsg("Must input item id");
            return;
        }
        if (!invoker.isPlayer()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_PLAYER_ONLY);
            return;
        }
        ItemStack item = TestItemProvider.INSTANCE.matchItem(args.getFirst());
        invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer).ifPresent(
            player -> InventoryHelper.addItemOrDrop(player.getInventory(), item)
        );
    }
}
