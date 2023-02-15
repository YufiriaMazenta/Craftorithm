package top.oasismc.oasisrecipe.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import top.oasismc.oasisrecipe.menu.MenuHolder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum MenuHandler implements Listener {

    INSTANCE;

    private final Map<UUID, Inventory> playerInvConversationMap;

    MenuHandler() {
        playerInvConversationMap = new ConcurrentHashMap<>();
    }

    public Inventory getPlayerInvConversation(UUID uuid) {
        return playerInvConversationMap.get(uuid);
    }

    public Inventory getPlayerInvConversation(Player player) {
        return getPlayerInvConversation(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerOpenInventory(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof MenuHolder))
            return;
        playerInvConversationMap.put(event.getPlayer().getUniqueId(), inventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        playerInvConversationMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerInvConversationMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder))
            return;
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTopInventory().getHolder() instanceof MenuHolder)
            event.setCancelled(true);
        if (!playerInvConversationMap.containsKey(player.getUniqueId()))
            return;
        MenuHolder holder = (MenuHolder) event.getClickedInventory().getHolder();
    }

}
