package pers.yufiria.craftorithm.hook.script;

import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.ScriptFunction;
import crypticlib.script.func.ScriptFunctionRegistry;
import crypticlib.script.func.ScriptModule;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.objenesis.ObjenesisStd;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.PlayerPointsHook;
import pers.yufiria.craftorithm.hook.VaultHook;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ScriptEconomyTransactionResultTest {

    private static final UUID PLAYER_ID = UUID.fromString("00000000-0000-0000-0000-000000000123");
    private static final BigDecimal AMOUNT = new BigDecimal("12.5");
    private static final String PLUGIN = "Craftorithm";
    private static final String CURRENCY = "gems";
    private static final String WORLD = "test-world";

    private final ScriptFunctionRegistry registry = ScriptFunctionRegistry.INSTANCE;
    private ScriptContext context;
    private Player player;
    private MockedStatic<PlayerUtils> players;
    private MockedStatic<Bukkit> bukkit;
    private MockedStatic<Craftorithm> craftorithm;
    private Object previousEconomy;
    private Object previousPlayerPoints;
    private Map<String, ScriptFunction> previousFunctions;

    @BeforeEach
    void isolateExternalServices() throws ReflectiveOperationException {
        context = mock(ScriptContext.class);
        player = mock(Player.class);
        World world = mock(World.class);
        when(world.getName()).thenReturn(WORLD);
        when(player.getWorld()).thenReturn(world);
        players = mockStatic(PlayerUtils.class);
        players.when(() -> PlayerUtils.getPlayerIdFromInvoker(null)).thenReturn(PLAYER_ID);
        players.when(() -> PlayerUtils.getPlayerOpt(PLAYER_ID)).thenReturn(Optional.of(player));
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(() -> Bukkit.getPlayer(PLAYER_ID)).thenReturn(player);
        craftorithm = mockStatic(Craftorithm.class);
        Craftorithm plugin = mock(Craftorithm.class);
        when(plugin.pluginName()).thenReturn(PLUGIN);
        craftorithm.when(Craftorithm::instance).thenReturn(plugin);
        previousEconomy = field(VaultHook.class, "economy").get(VaultHook.INSTANCE);
        previousPlayerPoints = field(PlayerPointsHook.class, "playerPoints").get(PlayerPointsHook.INSTANCE);
        previousFunctions = new HashMap<>(registry.allFunctions());
    }

    @AfterEach
    void restoreSingletons() throws ReflectiveOperationException {
        if (previousFunctions != null) {
            field(VaultHook.class, "economy").set(VaultHook.INSTANCE, previousEconomy);
            field(PlayerPointsHook.class, "playerPoints").set(PlayerPointsHook.INSTANCE, previousPlayerPoints);
            registry.unregisterModule("vault");
            registry.unregisterModule("vault_unlocked");
            registry.unregisterModule("playerpoints");
            previousFunctions.forEach((name, function) -> {
                if (name.startsWith("vault.") || name.startsWith("vault_unlocked.") || name.startsWith("playerpoints.")) {
                    int separator = name.lastIndexOf('.');
                    registry.register(name.substring(0, separator), name.substring(separator + 1), function);
                }
            });
        }
        if (craftorithm != null) craftorithm.close();
        if (bukkit != null) bukkit.close();
        if (players != null) players.close();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void vaultWithdrawalReturnsBackendResult(boolean success) throws ReflectiveOperationException {
        Economy economy = useVault();
        when(economy.withdrawPlayer(player, 12.5)).thenReturn(vaultResponse(success));

        assertEquals(success, invoke(VaultModule.INSTANCE, "take_money", ScriptValue.of(12.5)));
        verify(economy).withdrawPlayer(player, 12.5);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void vaultDepositReturnsBackendResult(boolean success) throws ReflectiveOperationException {
        Economy economy = useVault();
        when(economy.depositPlayer(player, 12.5)).thenReturn(vaultResponse(success));

        assertEquals(success, invoke(VaultModule.INSTANCE, "give_money", ScriptValue.of(12.5)));
        verify(economy).depositPlayer(player, 12.5);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unlockedDefaultWithdrawalReturnsBackendResult(boolean success) throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = useUnlocked();
        when(economy.withdraw(PLUGIN, PLAYER_ID, AMOUNT)).thenReturn(unlockedResponse(success));

        assertEquals(success, invoke(VaultUnlockedModule.INSTANCE, "take_money", ScriptValue.of(AMOUNT)));
        verify(economy).withdraw(PLUGIN, PLAYER_ID, AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unlockedDefaultDepositReturnsBackendResult(boolean success) throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = useUnlocked();
        when(economy.deposit(PLUGIN, PLAYER_ID, AMOUNT)).thenReturn(unlockedResponse(success));

        assertEquals(success, invoke(VaultUnlockedModule.INSTANCE, "give_money", ScriptValue.of(AMOUNT)));
        verify(economy).deposit(PLUGIN, PLAYER_ID, AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unlockedCurrencyWithdrawalPreservesWorldAndReturnsBackendResult(boolean success) throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = useUnlocked();
        when(economy.withdraw(PLUGIN, PLAYER_ID, WORLD, CURRENCY, AMOUNT)).thenReturn(unlockedResponse(success));

        assertEquals(success, invoke(VaultUnlockedModule.INSTANCE, "take_money", ScriptValue.of(AMOUNT), ScriptValue.of(CURRENCY)));
        verify(economy).withdraw(PLUGIN, PLAYER_ID, WORLD, CURRENCY, AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unlockedCurrencyDepositPreservesWorldAndReturnsBackendResult(boolean success) throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = useUnlocked();
        when(economy.deposit(PLUGIN, PLAYER_ID, WORLD, CURRENCY, AMOUNT)).thenReturn(unlockedResponse(success));

        assertEquals(success, invoke(VaultUnlockedModule.INSTANCE, "give_money", ScriptValue.of(AMOUNT), ScriptValue.of(CURRENCY)));
        verify(economy).deposit(PLUGIN, PLAYER_ID, WORLD, CURRENCY, AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void pointsTakeReturnsBackendResultAndPreservesIntegerConversion(boolean success) throws ReflectiveOperationException {
        PlayerPointsAPI api = usePoints();
        when(api.take(PLAYER_ID, 12)).thenReturn(success);

        assertEquals(success, invoke(PlayerPointsModule.INSTANCE, "take_points", ScriptValue.of(12.5)));
        verify(api).take(PLAYER_ID, 12);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void pointsGiveReturnsBackendResultAndPreservesIntegerConversion(boolean success) throws ReflectiveOperationException {
        PlayerPointsAPI api = usePoints();
        when(api.give(PLAYER_ID, 12)).thenReturn(success);

        assertEquals(success, invoke(PlayerPointsModule.INSTANCE, "give_points", ScriptValue.of(12.5)));
        verify(api).give(PLAYER_ID, 12);
    }

    @Test
    void missingArgumentsStillFailWithoutCallingBackend() throws ReflectiveOperationException {
        Economy economy = useVault();
        assertFalse(invoke(VaultModule.INSTANCE, "take_money"));
        assertFalse(invoke(VaultModule.INSTANCE, "give_money"));
        verifyNoInteractions(economy);

        net.milkbowl.vault2.economy.Economy unlocked = useUnlocked();
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "take_money"));
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "give_money"));
        verifyNoInteractions(unlocked);

        PlayerPointsAPI api = usePoints();
        assertFalse(invoke(PlayerPointsModule.INSTANCE, "take_points"));
        assertFalse(invoke(PlayerPointsModule.INSTANCE, "give_points"));
        verifyNoInteractions(api);
    }

    @Test
    void legacyNonPositiveAmountsRemainSuccessfulNoOps() throws ReflectiveOperationException {
        Economy economy = useVault();
        assertTrue(invoke(VaultModule.INSTANCE, "take_money", ScriptValue.of(0)));
        assertTrue(invoke(VaultModule.INSTANCE, "give_money", ScriptValue.of(-1)));
        verifyNoInteractions(economy);

        PlayerPointsAPI api = usePoints();
        assertTrue(invoke(PlayerPointsModule.INSTANCE, "take_points", ScriptValue.of(0)));
        assertTrue(invoke(PlayerPointsModule.INSTANCE, "give_points", ScriptValue.of(-1)));
        verifyNoInteractions(api);
    }

    @Test
    void unlockedNegativeAmountsStillFailWithoutCallingBackend() throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = useUnlocked();
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "take_money", ScriptValue.of(-1)));
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "give_money", ScriptValue.of(-1), ScriptValue.of(CURRENCY)));
        verifyNoInteractions(economy);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unlockedZeroAmountStillUsesBackendResult(boolean success) throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = useUnlocked();
        when(economy.withdraw(PLUGIN, PLAYER_ID, BigDecimal.ZERO)).thenReturn(unlockedResponse(success));
        when(economy.deposit(PLUGIN, PLAYER_ID, WORLD, CURRENCY, BigDecimal.ZERO)).thenReturn(unlockedResponse(success));

        assertEquals(success, invoke(VaultUnlockedModule.INSTANCE, "take_money", ScriptValue.of(BigDecimal.ZERO)));
        assertEquals(success, invoke(VaultUnlockedModule.INSTANCE, "give_money", ScriptValue.of(BigDecimal.ZERO), ScriptValue.of(CURRENCY)));
        verify(economy).withdraw(PLUGIN, PLAYER_ID, BigDecimal.ZERO);
        verify(economy).deposit(PLUGIN, PLAYER_ID, WORLD, CURRENCY, BigDecimal.ZERO);
    }

    @Test
    void missingPlayerStillFailsWithoutCallingBackend() throws ReflectiveOperationException {
        players.when(() -> PlayerUtils.getPlayerIdFromInvoker(null)).thenReturn(null);
        Economy economy = useVault();
        assertFalse(invoke(VaultModule.INSTANCE, "take_money", ScriptValue.of(1)));
        assertFalse(invoke(VaultModule.INSTANCE, "give_money", ScriptValue.of(1)));
        verifyNoInteractions(economy);

        net.milkbowl.vault2.economy.Economy unlocked = useUnlocked();
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "take_money", ScriptValue.of(1)));
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "give_money", ScriptValue.of(1)));
        verifyNoInteractions(unlocked);

        PlayerPointsAPI api = usePoints();
        assertFalse(invoke(PlayerPointsModule.INSTANCE, "take_points", ScriptValue.of(1)));
        assertFalse(invoke(PlayerPointsModule.INSTANCE, "give_points", ScriptValue.of(1)));
        verifyNoInteractions(api);
    }

    @Test
    void unlockedCurrencyStillRequiresOnlinePlayer() throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = useUnlocked();
        bukkit.when(() -> Bukkit.getPlayer(PLAYER_ID)).thenReturn(null);
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "take_money", ScriptValue.of(1), ScriptValue.of(CURRENCY)));
        assertFalse(invoke(VaultUnlockedModule.INSTANCE, "give_money", ScriptValue.of(1), ScriptValue.of(CURRENCY)));
        verifyNoInteractions(economy);
    }

    private boolean invoke(ScriptModule module, String name, ScriptValue... args) {
        module.register(registry);
        ScriptFunction function = registry.getFunction(module.moduleName() + "." + name);
        assertNotNull(function);
        return function.execute(context, null, args).asBoolean();
    }

    private Economy useVault() throws ReflectiveOperationException {
        Economy economy = mock(Economy.class);
        field(VaultHook.class, "economy").set(VaultHook.INSTANCE, economy);
        return economy;
    }

    private net.milkbowl.vault2.economy.Economy useUnlocked() throws ReflectiveOperationException {
        net.milkbowl.vault2.economy.Economy economy = mock(net.milkbowl.vault2.economy.Economy.class);
        field(VaultHook.class, "economy").set(VaultHook.INSTANCE, economy);
        return economy;
    }

    private PlayerPointsAPI usePoints() throws ReflectiveOperationException {
        PlayerPointsAPI api = mock(PlayerPointsAPI.class);
        // Do not start a JavaPlugin or inline-mock PlayerPoints: its optional Treasury
        // integration is absent in this unit-test runtime. Only its API accessor is needed.
        PlayerPointsFixture plugin = new ObjenesisStd().newInstance(PlayerPointsFixture.class);
        plugin.api = api;
        field(PlayerPointsHook.class, "playerPoints").set(PlayerPointsHook.INSTANCE, plugin);
        return api;
    }

    private static final class PlayerPointsFixture extends PlayerPoints {
        private PlayerPointsAPI api;

        @Override
        public PlayerPointsAPI getAPI() {
            return api;
        }
    }

    private static EconomyResponse vaultResponse(boolean success) {
        return new EconomyResponse(12.5, 100, success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, success ? null : "declined");
    }

    private static net.milkbowl.vault2.economy.EconomyResponse unlockedResponse(boolean success) {
        return new net.milkbowl.vault2.economy.EconomyResponse(AMOUNT, BigDecimal.TEN, success ? net.milkbowl.vault2.economy.EconomyResponse.ResponseType.SUCCESS : net.milkbowl.vault2.economy.EconomyResponse.ResponseType.FAILURE, success ? null : "declined");
    }

    private static Field field(Class<?> owner, String name) throws NoSuchFieldException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
