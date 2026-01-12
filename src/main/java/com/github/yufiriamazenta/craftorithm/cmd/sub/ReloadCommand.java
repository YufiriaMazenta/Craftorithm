package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class ReloadCommand extends BukkitSubcommand {

    public static final ReloadCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super(CommandInfo.builder("reload").permission(new PermInfo("craftorithm.command.reload")).build());
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        try {
            Craftorithm.instance().reloadPlugin();
            LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_EXCEPTION);
        }
    }

    @Subcommand
    BukkitSubcommand recipes = new BukkitSubcommand("recipes") {

        private final Cache<UUID, Object> CONFIRM_CACHE = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS).build();
        private final UUID SERVER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

        @Override
        public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            UUID senderId;
            if (sender instanceof Player player) {
                senderId = player.getUniqueId();
            } else {
                senderId = SERVER_UUID;
            }
            if (CONFIRM_CACHE.getIfPresent(senderId) == null) {
                LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_RECIPES_CONFIRM);
                CONFIRM_CACHE.put(senderId, new Object());
                return;
            }
            try {
                RecipeManager.INSTANCE.reloadRecipeManager();
                LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_RECIPES);
            } catch (Exception e) {
                e.printStackTrace();
                LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_EXCEPTION);
            }

        }
    };

}
