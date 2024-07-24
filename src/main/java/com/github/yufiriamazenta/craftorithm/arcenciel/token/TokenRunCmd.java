package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenRunCmd extends AbstractArcencielToken<Boolean> {

    public static final TokenRunCmd INSTANCE = new TokenRunCmd();

    protected TokenRunCmd() {
        super("command");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        String command = BukkitTextProcessor.placeholder(player, CollectionsUtil.list2ArcencielBlock(args));
        return new ReturnObj<>(Bukkit.dispatchCommand(player, command));
    }
}
