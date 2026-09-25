package pers.yufiria.craftorithm.command.item;

import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;
import crypticlib.util.InventoryHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemGroup;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.IngredientUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;
import java.util.Optional;

public final class GetItemGroupCommand extends CommandNode {

    public static final GetItemGroupCommand INSTANCE = new GetItemGroupCommand();

    private GetItemGroupCommand() {
        super(CommandInfo.builder("getitemgroup").build());
    }

    @Subcommand
    final CommandNode tag = new CommandNode(
        CommandInfo
            .builder("tag")
            .usage("&r/craftorithm item getitemgroup tag <tag_key>")
            .build()
    ) {

        @Override
        public void execute(@NotNull Invoker invoker, List<String> args) {
            if (args.isEmpty()) {
                sendDescriptions(invoker);
                return;
            }
            if (!CommandUtils.checkInvokerIsPlayer(invoker))
                return;
            String tagId = args.getFirst();
            Optional<ItemStack> itemOpt = ItemGroup.of(ItemGroup.GROUP_TYPE_TAG, tagId)
                .flatMap(ItemGroup::toPlaceholderItem);
            if (itemOpt.isEmpty()) {
                LangUtils.sendLang(
                    invoker,
                    Languages.COMMAND_ITEM_GET_ITEM_GROUP_TAG_UNKNOWN_TAG,
                    CollectionsUtils.newStringHashMap("<tag_id>", tagId)
                );
                return;
            }
            giveItemToPlayer(invoker, itemOpt.get());
        }

        @Override
        public List<String> tabComplete(@NotNull Invoker invoker, List<String> args) {
            return IngredientUtils.allTagKeys().stream().map(NamespacedKey::asString).toList();
        }
    };

    @Subcommand
    final CommandNode itemPack = new CommandNode(
        CommandInfo
            .builder("itempack")
            .usage("&r/craftorithm item getitemgroup itempack <item_pack_id>")
            .build()
    ) {

        @Override
        public void execute(@NotNull Invoker invoker, List<String> args) {
            if (args.isEmpty()) {
                sendDescriptions(invoker);
                return;
            }
            if (!CommandUtils.checkInvokerIsPlayer(invoker))
                return;
            String itemPackId = args.getFirst();
            Optional<ItemStack> itemOpt = ItemGroup.of(ItemGroup.GROUP_TYPE_ITEM_PACK, itemPackId)
                .flatMap(ItemGroup::toPlaceholderItem);
            if (itemOpt.isEmpty()) {
                LangUtils.sendLang(
                    invoker,
                    Languages.COMMAND_ITEM_GET_ITEM_GROUP_ITEM_PACK_UNKNOWN_ITEM_PACK,
                    CollectionsUtils.newStringHashMap("<item_pack_id>", itemPackId)
                );
                return;
            }
            giveItemToPlayer(invoker, itemOpt.get());
        }

        @Override
        public List<String> tabComplete(@NotNull Invoker invoker, List<String> args) {
            return List.copyOf(ItemManager.INSTANCE.itemPackIds());
        }
    };

    private static void giveItemToPlayer(Invoker invoker, ItemStack itemStack) {
        Optional<Player> playerOpt = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer);
        if (playerOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_PLAYER_ONLY);
            return;
        }
        InventoryHelper.addItemOrDrop(playerOpt.get().getInventory(), itemStack);
    }

}