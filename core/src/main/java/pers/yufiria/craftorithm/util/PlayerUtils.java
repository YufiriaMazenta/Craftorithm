package pers.yufiria.craftorithm.util;

import crypticlib.Invoker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerUtils {

    public static boolean isCreativeOrSpectator(Player player) {
        GameMode gameMode = player.getGameMode();
        return gameMode.equals(GameMode.CREATIVE) || gameMode.equals(GameMode.SPECTATOR);
    }

    public static boolean isSurvivalOrAdventure(Player player) {
        GameMode gameMode = player.getGameMode();
        return gameMode.equals(GameMode.SURVIVAL) || gameMode.equals(GameMode.ADVENTURE);
    }

    public static Optional<Player> getPlayerOpt(UUID playerId) {
        if (playerId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Bukkit.getPlayer(playerId));
    }

    public static Optional<Player> getPlayerOpt(String playerName) {
        if (playerName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Bukkit.getPlayer(playerName));
    }

    /**
     * 从Invoker中提取Player，如果Invoker不是玩家则返回null
     */
    public static @Nullable Player getPlayerFromInvoker(Invoker invoker) {
        if (invoker.isPlayer()) {
            return invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer).orElse(null);
        }
        return null;
    }

    /**
     * 从Invoker中提取Player的UUID，如果Invoker不是玩家则返回null
     */
    public static @Nullable UUID getPlayerIdFromInvoker(Invoker invoker) {
        if (invoker.isPlayer()) {
            return invoker.asPlayer().uniqueId();
        }
        return null;
    }

    /**
     * 取命令执行者对应的在线玩家。
     * 执行者不是玩家或玩家已不在线时会向执行者发送提示。
     * @param invoker 命令执行者
     * @return 执行者对应的玩家, 取不到返回null
     */
    public static @Nullable Player resolveInvokerPlayer(Invoker invoker) {
        if (!CommandUtils.checkInvokerIsPlayer(invoker)) {
            return null;
        }
        Optional<Player> playerOpt = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer);
        if (playerOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_PLAYER_ONLY);
            return null;
        }
        return playerOpt.get();
    }

    /**
     * 获取当前所有在线玩家的名字
     */
    public static List<String> onlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

}
