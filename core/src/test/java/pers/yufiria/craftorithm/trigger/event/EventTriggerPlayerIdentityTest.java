package pers.yufiria.craftorithm.trigger.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;
import pers.yufiria.craftorithm.trigger.TriggerType;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class EventTriggerPlayerIdentityTest {

    private static final UUID VICTIM_ID = UUID.fromString("00000000-0000-0000-0000-000000000111");
    private static final UUID KILLER_ID = UUID.fromString("00000000-0000-0000-0000-000000000222");
    private MockedStatic<Craftorithm> craftorithm;
    private MockedStatic<Bukkit> bukkit;
    private Map<String, TriggerType> previousTypes;
    private Set<String> previousListenerKeys;

    @BeforeEach
    void registerRealTypesWithoutStartingServer() throws ReflectiveOperationException {
        // TriggerManager's enum constructor calls Craftorithm.instance().getDataFolder().
        // Install the static mock before the FIRST access to TriggerManager.INSTANCE.
        craftorithm = mockStatic(Craftorithm.class);
        Craftorithm plugin = mock(Craftorithm.class);
        when(plugin.getDataFolder()).thenReturn(new File("build/test-fixtures/event-trigger-identity"));
        craftorithm.when(Craftorithm::instance).thenReturn(plugin);
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getPluginManager).thenReturn(mock(PluginManager.class));

        previousTypes = new HashMap<>(TriggerManager.INSTANCE.triggerTypes());
        previousListenerKeys = new HashSet<>(registeredListenerKeys());
        EventTriggerTypes.INSTANCE.reset();
        EventTriggerTypes.INSTANCE.init();
    }

    @AfterEach
    void restoreRegistrations() throws ReflectiveOperationException {
        if (previousTypes != null) {
            Map<String, TriggerType> types = mutableTypes();
            types.clear();
            types.putAll(previousTypes);
        }
        if (previousListenerKeys != null) {
            Set<String> keys = registeredListenerKeys();
            keys.clear();
            keys.addAll(previousListenerKeys);
        }
        if (bukkit != null) bukkit.close();
        if (craftorithm != null) craftorithm.close();
    }

    @Test
    void playerDeathUsesVictimWhileKeepingKillerName() {
        Player victim = player(VICTIM_ID);
        Player killer = player(KILLER_ID);
        when(killer.getName()).thenReturn("Attacker");
        when(victim.getKiller()).thenReturn(killer);
        PlayerDeathEvent event = mock(PlayerDeathEvent.class);
        when(event.getEntity()).thenReturn(victim);

        TriggerContext context = registeredType("player_death", PlayerDeathEvent.class).extractContext(event);

        assertNotNull(context);
        assertEquals(VICTIM_ID, context.playerUniqueId());
        assertEquals("Attacker", context.variables().get("killer_name").asString());
        assertTrue(context.variables().get("event").isObject());
    }

    @Test
    void environmentalPlayerDeathStillUsesVictimAndHasNilKillerName() {
        Player victim = player(VICTIM_ID);
        PlayerDeathEvent event = mock(PlayerDeathEvent.class);
        when(event.getEntity()).thenReturn(victim);

        TriggerContext context = registeredType("player_death", PlayerDeathEvent.class).extractContext(event);

        assertNotNull(context);
        assertEquals(VICTIM_ID, context.playerUniqueId());
        assertTrue(context.variables().get("killer_name").isNull());
    }

    @Test
    void blockBreakUsesBreakingPlayerAndPreservesBlockType() {
        Player player = player(VICTIM_ID);
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.STONE);
        BlockBreakEvent event = new BlockBreakEvent(block, player);

        TriggerContext context = registeredType("block_break", BlockBreakEvent.class).extractContext(event);

        assertNotNull(context);
        assertEquals(VICTIM_ID, context.playerUniqueId());
        assertEquals("STONE", context.variables().get("block_type").asString());
    }

    @Test
    void blockPlaceUsesPlacingPlayerAndPreservesBlockType() {
        Player player = player(VICTIM_ID);
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.DIRT);
        BlockPlaceEvent event = mock(BlockPlaceEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.getBlock()).thenReturn(block);

        TriggerContext context = registeredType("block_place", BlockPlaceEvent.class).extractContext(event);

        assertNotNull(context);
        assertEquals(VICTIM_ID, context.playerUniqueId());
        assertEquals("DIRT", context.variables().get("block_type").asString());
    }

    @Test
    void sharedDamagerOrKillerExtractorStillReturnsKillerForEntityDeath() {
        Player killer = player(KILLER_ID);
        LivingEntity entity = mock(LivingEntity.class);
        when(entity.getKiller()).thenReturn(killer);
        EntityDeathEvent event = mock(EntityDeathEvent.class);
        when(event.getEntity()).thenReturn(entity);

        assertSame(killer, PlayerExtractor.DAMAGER_OR_KILLER.extract(event));
    }

    private static TriggerType registeredType(String key, Class<?> eventClass) {
        TriggerType type = TriggerManager.INSTANCE.getTriggerType(key);
        assertNotNull(type, key + " must be registered by the real EventTriggerTypes.init()");
        assertEquals(eventClass, type.eventClass());
        return type;
    }

    private static Player player(UUID id) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        return player;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, TriggerType> mutableTypes() throws ReflectiveOperationException {
        Field field = TriggerManager.class.getDeclaredField("triggerTypes");
        field.setAccessible(true);
        return (Map<String, TriggerType>) field.get(TriggerManager.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    private static Set<String> registeredListenerKeys() throws ReflectiveOperationException {
        Field field = EventTriggerTypes.class.getDeclaredField("registeredTypeKeys");
        field.setAccessible(true);
        return (Set<String>) field.get(EventTriggerTypes.INSTANCE);
    }
}
