package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventListener
public enum RecipeUnlockHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void unlockOnPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

}
