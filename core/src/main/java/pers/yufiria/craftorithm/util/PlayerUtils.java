package pers.yufiria.craftorithm.util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

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

}
