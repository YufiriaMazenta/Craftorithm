package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.CrypticLib;
import crypticlib.listener.BukkitListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@BukkitListener
public enum RecipeUnlockHandler implements Listener {

    INSTANCE;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<NamespacedKey, Boolean> unlockMap = RecipeManager.INSTANCE.recipeUnlockMap();
        List<NamespacedKey> unlockKeyList = new ArrayList<>();
        List<NamespacedKey> notUnlockKeyList = new ArrayList<>();
        unlockMap.forEach(
            (recipeKey, unlock) -> {
                if (unlock)
                    unlockKeyList.add(recipeKey);
                else
                    notUnlockKeyList.add(recipeKey);
            }
        );
        player.discoverRecipes(unlockKeyList);
        player.undiscoverRecipes(notUnlockKeyList);
    }



}
