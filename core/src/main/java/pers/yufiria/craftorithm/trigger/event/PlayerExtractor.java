package pers.yufiria.craftorithm.trigger.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 从 Bukkit 事件中提取玩家的策略接口
 */
@FunctionalInterface
public interface PlayerExtractor {

    @Nullable Player extract(@NotNull Event event);

    PlayerExtractor PLAYER = e -> {
        if (e instanceof PlayerEvent pe) return pe.getPlayer();
        return null;
    };

    PlayerExtractor WHO_CLICKED = e -> {
        if (e instanceof InventoryInteractEvent ie) {
            if (ie.getWhoClicked() instanceof Player p) return p;
        }
        return null;
    };

    PlayerExtractor ENTITY = e -> {
        if (e instanceof EntityEvent ee) {
            if (ee.getEntity() instanceof Player p) return p;
        }
        return null;
    };

    PlayerExtractor DAMAGER_OR_KILLER = e -> {
        if (e instanceof EntityDeathEvent ede) {
            return ede.getEntity().getKiller();
        }
        if (e instanceof EntityDamageByEntityEvent edbee) {
            if (edbee.getDamager() instanceof Player p) return p;
        }
        return null;
    };

    PlayerExtractor VIEWER = e -> {
        if (e instanceof InventoryEvent ie) {
            for (HumanEntity viewer : ie.getViewers()) {
                if (viewer instanceof Player p) return p;
            }
        }
        return null;
    };

}
