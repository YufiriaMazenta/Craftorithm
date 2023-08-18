package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.DefRecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum RecipeUnlockHandler implements Listener {

    INSTANCE;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        Map<NamespacedKey, Boolean> unlockMap = DefRecipeManager.getRecipeUnlockMap();
//        List<NamespacedKey> unlockKeyList = new ArrayList<>(unlockMap.keySet());
//        unlockKeyList.removeIf(key -> unlockMap.getOrDefault(key, false) && !player.hasDiscoveredRecipe(key));
//        player.discoverRecipes(unlockKeyList);
        //TODO
    }

}
