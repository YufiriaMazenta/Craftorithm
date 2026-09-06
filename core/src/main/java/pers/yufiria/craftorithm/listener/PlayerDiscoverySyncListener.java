package pers.yufiria.craftorithm.listener;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.database.dao.DiscoveredRecipeDao;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@EventListener
public enum PlayerDiscoverySyncListener implements Listener {

    INSTANCE;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!PluginConfigs.RECIPE_DISCOVERY_SYNC_ENABLE.value()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int delayTicks = PluginConfigs.RECIPE_DISCOVERY_SYNC_JOIN_SYNC_DELAY_TICKS.value();

        CrypticLibBukkit.scheduler().syncLater(() -> {
            CrypticLibBukkit.scheduler().runOnEntity(player, () -> {
                try {
                    Set<String> localRecipes = player.getDiscoveredRecipes().stream()
                        .map(NamespacedKey::toString)
                        .collect(Collectors.toSet());

                    CrypticLibBukkit.scheduler().async(() -> {
                        try {
                            Set<String> dbRecipes = DiscoveredRecipeDao.INSTANCE.getDiscoveredRecipes(uuid);

                            Set<String> serverRecipeKeys = RecipeManager.INSTANCE.serverRecipeKeys().stream()
                                .map(Objects::toString)
                                .collect(Collectors.toSet());

                            dbRecipes.retainAll(serverRecipeKeys);

                            Set<String> merged = new HashSet<>(dbRecipes);
                            merged.addAll(localRecipes);
                            merged.retainAll(serverRecipeKeys);

                            DiscoveredRecipeDao.INSTANCE.saveDiscoveredRecipes(uuid, merged);

                            Set<String> toDiscover = new HashSet<>(merged);
                            toDiscover.removeAll(localRecipes);

                            if (!toDiscover.isEmpty()) {
                                CrypticLibBukkit.scheduler().runOnEntity(player, () -> {
                                    player.discoverRecipes(toDiscover.stream()
                                        .map(NamespacedKey::fromString)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList()));
                                }, () -> {
                                });
                            }
                        } catch (Exception e) {
                            CrypticLib.info("&cFailed to sync discovered recipes on join for " + player.getName() + ": " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    CrypticLib.info("&cFailed to read local recipes on join for " + player.getName() + ": " + e.getMessage());
                }
            }, () -> {
            });
        }, delayTicks);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!PluginConfigs.RECIPE_DISCOVERY_SYNC_ENABLE.value()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Set<String> localRecipes = player.getDiscoveredRecipes().stream()
            .map(NamespacedKey::toString)
            .collect(Collectors.toSet());

        Set<String> serverRecipeKeys = RecipeManager.INSTANCE.serverRecipeKeys().stream()
            .map(Objects::toString)
            .collect(Collectors.toSet());

        localRecipes.retainAll(serverRecipeKeys);

        CrypticLibBukkit.scheduler().async(() -> {
            try {
                DiscoveredRecipeDao.INSTANCE.saveDiscoveredRecipes(uuid, localRecipes);
            } catch (Exception e) {
                CrypticLib.info("&cFailed to sync discovered recipes on quit for " + player.getName() + ": " + e.getMessage());
            }
        });
    }

}
