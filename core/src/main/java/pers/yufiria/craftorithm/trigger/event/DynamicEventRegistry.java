package pers.yufiria.craftorithm.trigger.event;

import crypticlib.CrypticLibBukkit;
import crypticlib.util.BukkitEventHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态事件注册器
 * 预定义所有玩家相关事件类型，通过 Bukkit API 动态注册监听器
 */
public enum DynamicEventRegistry {

    INSTANCE;

    private final Map<String, GenericEventTriggerType> eventTypes = new ConcurrentHashMap<>();
    private boolean initialized = false;

    public void init() {
        if (initialized) return;
        initialized = true;

        // PlayerEvent 子类
        register("player_join", PlayerJoinEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_quit", PlayerQuitEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_death", PlayerDeathEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "killer_name", event -> {
                    Player killer = ((PlayerDeathEvent) event).getEntity().getKiller();
                    return killer != null ? ScriptValue.of(killer.getName()) : null;
                }
            )
        );
        register("player_respawn", PlayerRespawnEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_interact", PlayerInteractEvent.class, PlayerExtractor.PLAYER,
            Map.of("action", event -> ScriptValue.of(((PlayerInteractEvent) event).getAction().name()))
        );
        register("player_interact_entity", PlayerInteractEntityEvent.class, PlayerExtractor.PLAYER,
            Map.of("entity_type", event -> ScriptValue.of(((PlayerInteractEntityEvent) event).getRightClicked().getType().name()))
        );
        register("player_interact_at_entity", PlayerInteractAtEntityEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_advancement", PlayerAdvancementDoneEvent.class, PlayerExtractor.PLAYER,
            Map.of("advancement", event -> ScriptValue.of(((PlayerAdvancementDoneEvent) event).getAdvancement().getKey().toString()))
        );
        register("player_level_change", PlayerLevelChangeEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "old_level", event -> ScriptValue.of(((PlayerLevelChangeEvent) event).getOldLevel()),
                "new_level", event -> ScriptValue.of(((PlayerLevelChangeEvent) event).getNewLevel())
            )
        );
        register("player_exp_change", PlayerExpChangeEvent.class, PlayerExtractor.PLAYER,
            Map.of("amount", event -> ScriptValue.of(((PlayerExpChangeEvent) event).getAmount()))
        );
        register("player_toggle_sneak", PlayerToggleSneakEvent.class, PlayerExtractor.PLAYER,
            Map.of("sneaking", event -> ScriptValue.of(((PlayerToggleSneakEvent) event).isSneaking()))
        );
        register("player_toggle_sprint", PlayerToggleSprintEvent.class, PlayerExtractor.PLAYER,
            Map.of("sprinting", event -> ScriptValue.of(((PlayerToggleSprintEvent) event).isSprinting()))
        );
        register("player_item_consume", PlayerItemConsumeEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "item", event -> resolveItemId(((PlayerItemConsumeEvent) event).getItem()),
                "amount", event -> resolveItemAmount(((PlayerItemConsumeEvent) event).getItem())
            )
        );
        register("player_item_held", PlayerItemHeldEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "previous_slot", event -> ScriptValue.of(((PlayerItemHeldEvent) event).getPreviousSlot()),
                "new_slot", event -> ScriptValue.of(((PlayerItemHeldEvent) event).getNewSlot())
            )
        );
        register("player_animation", PlayerAnimationEvent.class, PlayerExtractor.PLAYER,
            Map.of("animation", event -> ScriptValue.of(((PlayerAnimationEvent) event).getAnimationType().name()))
        );
        register("player_bed_enter", PlayerBedEnterEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_bed_leave", PlayerBedLeaveEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_bucket_fill", PlayerBucketFillEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_bucket_empty", PlayerBucketEmptyEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_changed_world", PlayerChangedWorldEvent.class, PlayerExtractor.PLAYER,
            Map.of("from", event -> ScriptValue.of(((PlayerChangedWorldEvent) event).getFrom().getName()))
        );
        register("player_portal", PlayerPortalEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_teleport", PlayerTeleportEvent.class, PlayerExtractor.PLAYER,
            Map.of("cause", event -> ScriptValue.of(((PlayerTeleportEvent) event).getCause().name()))
        );
        register("player_fish", PlayerFishEvent.class, PlayerExtractor.PLAYER,
            Map.of("state", event -> ScriptValue.of(((PlayerFishEvent) event).getState().name()))
        );
        register("player_shear_entity", PlayerShearEntityEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_unleash_entity", PlayerUnleashEntityEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_edit_book", PlayerEditBookEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_statistic", PlayerStatisticIncrementEvent.class, PlayerExtractor.PLAYER,
            Map.of("statistic", event -> ScriptValue.of(((PlayerStatisticIncrementEvent) event).getStatistic().name()))
        );
        register("player_swap_hand_items", PlayerSwapHandItemsEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_drop_item", PlayerDropItemEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "item", event -> resolveItemId(((PlayerDropItemEvent) event).getItemDrop().getItemStack()),
                "amount", event -> resolveItemAmount(((PlayerDropItemEvent) event).getItemDrop().getItemStack())
            )
        );
        register("player_pickup_item", PlayerPickupItemEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "item", event -> resolveItemId(((PlayerPickupItemEvent) event).getItem().getItemStack()),
                "amount", event -> resolveItemAmount(((PlayerPickupItemEvent) event).getItem().getItemStack())
            )
        );
        register("player_velocity", PlayerVelocityEvent.class, PlayerExtractor.PLAYER, Map.of());
        register("player_game_mode_change", PlayerGameModeChangeEvent.class, PlayerExtractor.PLAYER,
            Map.of("new_game_mode", event -> ScriptValue.of(((PlayerGameModeChangeEvent) event).getNewGameMode().name()))
        );
        register("player_resource_pack_status", PlayerResourcePackStatusEvent.class, PlayerExtractor.PLAYER,
            Map.of("status", event -> ScriptValue.of(((PlayerResourcePackStatusEvent) event).getStatus().name()))
        );
        register("player_item_damage", PlayerItemDamageEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "item", event -> resolveItemId(((PlayerItemDamageEvent) event).getItem()),
                "amount", event -> resolveItemAmount(((PlayerItemDamageEvent) event).getItem()),
                "damage", event -> ScriptValue.of(((PlayerItemDamageEvent) event).getDamage())
            )
        );
        register("player_item_mend", PlayerItemMendEvent.class, PlayerExtractor.PLAYER,
            Map.of(
                "item", event -> resolveItemId(((PlayerItemMendEvent) event).getItem()),
                "amount", event -> resolveItemAmount(((PlayerItemMendEvent) event).getItem()),
                "repair_amount", event -> ScriptValue.of(((PlayerItemMendEvent) event).getRepairAmount())
            )
        );
        register("player_recipe_discover", PlayerRecipeDiscoverEvent.class, PlayerExtractor.PLAYER,
            Map.of("recipe", event -> ScriptValue.of(((PlayerRecipeDiscoverEvent) event).getRecipe().toString()))
        );
        register("player_take_campfire", PlayerTakeLecternBookEvent.class, PlayerExtractor.PLAYER, Map.of());

        // EntityEvent 子类（涉及玩家）
        register("damage_entity", EntityDamageByEntityEvent.class, PlayerExtractor.DAMAGER_OR_KILLER,
            Map.of(
                "damage", event -> ScriptValue.of(((EntityDamageByEntityEvent) event).getDamage()),
                "entity_type", event -> {
                    EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                    return ScriptValue.of(damageEvent.getEntity().getType().name());
                },
                "entity_name", event -> {
                    EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                    return ScriptValue.of(damageEvent.getEntity().getName());
                }
            )
        );
        register("kill_entity", EntityDeathEvent.class, PlayerExtractor.DAMAGER_OR_KILLER,
            Map.of(
                "entity_type", event -> {
                    EntityDeathEvent deathEvent = (EntityDeathEvent) event;
                    return ScriptValue.of(deathEvent.getEntity().getType().name());
                },
                "entity_name", event -> {
                    EntityDeathEvent deathEvent = (EntityDeathEvent) event;
                    return ScriptValue.of(deathEvent.getEntity().getName());
                }
            )
        );
        register("entity_shoot_bow", EntityShootBowEvent.class, PlayerExtractor.ENTITY,
            Map.of("force", event -> ScriptValue.of(((EntityShootBowEvent) event).getForce()))
        );
        register("entity_breed", EntityBreedEvent.class, PlayerExtractor.ENTITY, Map.of());
        register("entity_tame", EntityTameEvent.class, PlayerExtractor.ENTITY, Map.of());
        register("entity_potion_effect", EntityPotionEffectEvent.class, PlayerExtractor.ENTITY, Map.of());

        // InventoryEvent 子类
        register("inventory_click", InventoryClickEvent.class, PlayerExtractor.WHO_CLICKED,
            Map.of("slot", event -> ScriptValue.of(((InventoryClickEvent) event).getSlot()))
        );
        register("inventory_close", InventoryCloseEvent.class, (event) -> {
            if (!(event instanceof InventoryCloseEvent closeEvent)) {
                return null;
            }
            return (Player) closeEvent.getPlayer();
        }, Map.of());
        register("inventory_open", InventoryOpenEvent.class, (event) -> {
            if (!(event instanceof InventoryOpenEvent openEvent)) {
                return null;
            }
            return (Player) openEvent.getPlayer();
        }, Map.of());

        // BlockEvent 子类（涉及玩家）
        register("block_break", BlockBreakEvent.class, PlayerExtractor.PLAYER,
            Map.of("block_type", event -> ScriptValue.of(((BlockBreakEvent) event).getBlock().getType().name()))
        );
        register("block_place", BlockPlaceEvent.class, PlayerExtractor.PLAYER,
            Map.of("block_type", event -> ScriptValue.of(((BlockPlaceEvent) event).getBlock().getType().name()))
        );

        // 其他
        registerAsync("async_player_chat", AsyncPlayerChatEvent.class, PlayerExtractor.PLAYER,
            Map.of("message", event -> {
                String message = ((AsyncPlayerChatEvent) event).getMessage();
                return ScriptValue.of(ChatColor.stripColor(message));
            })
        );
        register("player_command_preprocess", PlayerCommandPreprocessEvent.class, PlayerExtractor.PLAYER,
            Map.of("message", event -> {
                String message = ((PlayerCommandPreprocessEvent) event).getMessage();
                return ScriptValue.of(ChatColor.stripColor(message));
            })
        );
        register("player_move", PlayerMoveEvent.class,
            event -> {
                if (!(event instanceof PlayerMoveEvent moveEvent)) return null;
                if (!BukkitEventHelper.isMove(moveEvent)) return null;
                return moveEvent.getPlayer();
            },
            Map.of());
    }

    private void register(
        String typeKey,
        Class<? extends Event> eventClass,
        PlayerExtractor extractor,
        Map<String, EventVariableExtractor> varExtractors
    ) {
        GenericEventTriggerType type = new GenericEventTriggerType(typeKey, eventClass, extractor, varExtractors);
        eventTypes.put(typeKey, type);
        registerEventListener(type, false);
    }

    private void registerAsync(
        String typeKey,
        Class<? extends Event> eventClass,
        PlayerExtractor extractor,
        Map<String, EventVariableExtractor> varExtractors
    ) {
        GenericEventTriggerType type = new GenericEventTriggerType(typeKey, eventClass, extractor, varExtractors);
        eventTypes.put(typeKey, type);
        registerEventListener(type, true);
    }

    private void registerEventListener(GenericEventTriggerType type, boolean async) {
        EventExecutor executor = (listener, event) -> {
            if (!type.eventClass().isInstance(event)) return;
            TriggerContext ctx = type.extractContext(event);
            if (ctx == null) return;
            if (async) {
                CrypticLibBukkit.scheduler().sync(() -> TriggerManager.INSTANCE.fire(type.typeKey(), ctx));
            } else {
                TriggerManager.INSTANCE.fire(type.typeKey(), ctx);
            }
        };
        Bukkit.getPluginManager().registerEvent(
            type.eventClass(),
            new Listener() {},
            EventPriority.MONITOR,
            executor,
            Craftorithm.instance(),
            false
        );
    }

    public @Nullable GenericEventTriggerType getEventType(String typeKey) {
        return eventTypes.get(typeKey);
    }

    private static ScriptValue resolveItemId(ItemStack item) {
        if (item == null) return ScriptValue.nil();
        NamespacedItemIdStack id = ItemManager.INSTANCE.matchItemId(item, false);
        if (id != null) return ScriptValue.of(id.itemId().toString());
        return ScriptValue.of(item.getType().getKey().toString());
    }

    private static ScriptValue resolveItemAmount(ItemStack item) {
        if (item == null) return ScriptValue.of(0);
        return ScriptValue.of(item.getAmount());
    }

}
