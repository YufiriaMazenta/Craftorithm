package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.CrypticLibBukkit;
import crypticlib.scheduler.BukkitScheduler;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.block.Furnace;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeTestItem;

import java.io.File;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SmeltingListenerTest {
    private final Queue<Runnable> tasks = new ArrayDeque<>();
    private MockedStatic<CrypticLibBukkit> cryptic;
    private MockedStatic<Bukkit> bukkit;
    private MockedStatic<Craftorithm> pluginStatic;
    private MockedStatic<ItemHelper> itemHelper;
    private Block block;
    private World world;
    private final ItemStack iron = new RecipeTestItem(Material.RAW_IRON);
    private final ItemStack gold = new RecipeTestItem(Material.RAW_GOLD);
    private ItemStack furnaceInput;
    private ItemStack[] slots;
    private int[] progress;

    @BeforeEach
    void setUp() throws Exception {
        clearCache();
        tasks.clear();
        itemHelper = mockStatic(ItemHelper.class);
        itemHelper.when(() -> ItemHelper.isAir(any())).thenAnswer(invocation -> {
            ItemStack item = invocation.getArgument(0);
            return item == null || item.getType() == Material.AIR || item.getAmount() <= 0;
        });
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getBukkitVersion).thenReturn("1.20.4-R0.1-SNAPSHOT");
        pluginStatic = mockStatic(Craftorithm.class);
        Craftorithm plugin = mock(Craftorithm.class);
        when(plugin.getDataFolder()).thenReturn(new File("build/test-only-smelting-plugin"));
        pluginStatic.when(Craftorithm::instance).thenReturn(plugin);
        BukkitScheduler scheduler = mock(BukkitScheduler.class, invocation -> {
            Object[] arguments = invocation.getArguments();
            if (arguments.length > 0 && arguments[0] instanceof Runnable runnable) tasks.add(runnable);
            return RETURNS_DEFAULTS.answer(invocation);
        });
        cryptic = mockStatic(CrypticLibBukkit.class);
        cryptic.when(CrypticLibBukkit::isPaper).thenReturn(false);
        cryptic.when(CrypticLibBukkit::scheduler).thenReturn(scheduler);
        world = mock(World.class);
        UUID id = UUID.randomUUID();
        when(world.getUID()).thenReturn(id);
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        block = mock(Block.class);
        when(block.getWorld()).thenReturn(world);
        when(block.getX()).thenReturn(18);
        when(block.getY()).thenReturn(64);
        when(block.getZ()).thenReturn(33);
        when(world.getBlockAt(18, 64, 33)).thenReturn(block);
        bukkit.when(() -> Bukkit.getWorld(id)).thenReturn(world);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (itemHelper != null) itemHelper.close();
        if (pluginStatic != null) pluginStatic.close();
        if (bukkit != null) bukkit.close();
        if (cryptic != null) cryptic.close();
        clearCache();
    }

    @Test
    void oldCleanupCannotDeleteSecondConsecutiveFurnaceInput() {
        furnace();
        CookingRecipe<?> first = mock(CookingRecipe.class);
        CookingRecipe<?> second = mock(CookingRecipe.class);
        startFurnace(first, iron);
        finishFurnace(iron);
        furnaceInput = gold;
        startFurnace(second, gold);
        runScheduledOnce(); // includes the first operation's old cookingTime + 20 cleanup on RED
        finishFurnace(gold);
        verify(first).getKey();
        verify(second).getKey();
    }

    @Test
    void twoPortionsOfTheSameFurnaceInputBothKeepTheirRecipe() {
        furnace();
        CookingRecipe<?> recipe = mock(CookingRecipe.class);
        startFurnace(recipe, iron);
        finishFurnace(iron);
        furnaceInput = iron.clone();
        startFurnace(recipe, furnaceInput);
        runScheduledOnce();
        finishFurnace(furnaceInput);
        verify(recipe, times(2)).getKey();
    }

    @Test
    void pausedFurnaceKeepsItsRecipeBeyondExpectedCookingTime() {
        furnace();
        CookingRecipe<?> recipe = mock(CookingRecipe.class);
        startFurnace(recipe, iron);
        for (int i = 0; i < 8; i++) runScheduledOnce();
        finishFurnace(iron);
        verify(recipe).getKey();
    }

    @Test
    void unloadedFurnaceResumesWithoutAnotherStartEvent() {
        furnace();
        CookingRecipe<?> recipe = mock(CookingRecipe.class);
        startFurnace(recipe, iron);
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(false);
        runScheduledOnce();
        verify(world, never()).getBlockAt(anyInt(), anyInt(), anyInt());
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        runScheduledOnce();
        finishFurnace(iron);
        verify(recipe).getKey();
    }

    @Test
    void campfireDifferentInputsFinishOutOfOrderWithoutOverwritingEachOther() {
        campfire();
        CampfireRecipe first = mock(CampfireRecipe.class);
        CampfireRecipe second = mock(CampfireRecipe.class);
        startCampfire(first, iron, 0);
        startCampfire(second, gold, 1);
        progress[0] = 100;
        finishCampfire(iron, 0);
        verify(first).getKey();
        verify(second, never()).getKey();
        progress[1] = 100;
        finishCampfire(gold, 1);
        verify(second).getKey();
    }

    @Test
    void campfireSameInputsConsumeOnlyTheCompletingSlot() {
        campfire();
        CampfireRecipe first = mock(CampfireRecipe.class);
        CampfireRecipe second = mock(CampfireRecipe.class);
        startCampfire(first, iron, 0);
        startCampfire(second, iron.clone(), 1);
        progress[1] = 100; // later insertion can finish first after a per-slot time change
        finishCampfire(iron, 1);
        verify(second).getKey();
        verify(first, never()).getKey();
        progress[0] = 100;
        finishCampfire(iron, 0);
        verify(first).getKey();
    }

    @Test
    void extinguishedCampfireRetainsLiveSlotsButRemovesWithdrawnInput() {
        campfire();
        CampfireRecipe first = mock(CampfireRecipe.class);
        CampfireRecipe second = mock(CampfireRecipe.class);
        startCampfire(first, iron, 0);
        startCampfire(second, gold, 1);
        slots[1] = null;
        for (int i = 0; i < 8; i++) runScheduledOnce();
        progress[0] = 100;
        finishCampfire(iron, 0);
        verify(first).getKey();
        finishCampfire(gold, 1);
        verify(second, never()).getKey();
    }

    @Test
    void cancelledCampfireCompletionKeepsRecipeForTheNextCompletionAttempt() {
        campfire();
        CampfireRecipe recipe = mock(CampfireRecipe.class);
        startCampfire(recipe, iron, 0);
        progress[0] = 100;
        BlockCookEvent cancelled = mock(BlockCookEvent.class);
        when(cancelled.getBlock()).thenReturn(block);
        when(cancelled.getSource()).thenReturn(iron);
        when(cancelled.isCancelled()).thenReturn(true);
        SmeltingListener.INSTANCE.processBlockCookResult(cancelled);
        // Spigot leaves the input in the slot after cancellation and does not fire another Start.
        finishCampfire(iron, 0);
        verify(recipe, times(2)).getKey();
    }

    @Test
    void removedFurnaceInputIsActuallyCleanedUp() {
        furnace();
        CookingRecipe<?> recipe = mock(CookingRecipe.class);
        startFurnace(recipe, iron);
        furnaceInput = null;
        runScheduledOnce();
        finishFurnace(iron);
        verify(recipe, never()).getKey();
        assertTrue(tasks.isEmpty(), "cleanup should stop once no valid input remains");
    }

    @Test
    void paperCompletionStillUsesItsOwnRecipeWithoutStartCache() {
        cryptic.when(CrypticLibBukkit::isPaper).thenReturn(true);
        CookingRecipe<?> recipe = mock(CookingRecipe.class);
        FurnaceSmeltEvent event = mock(FurnaceSmeltEvent.class);
        doReturn(recipe).when(event).getRecipe();
        SmeltingListener.INSTANCE.processSmeltResult(event);
        verify(recipe).getKey();
        assertTrue(tasks.isEmpty());
    }

    private void furnace() {
        Furnace state = mock(Furnace.class);
        FurnaceInventory inventory = mock(FurnaceInventory.class);
        when(block.getState()).thenReturn(state);
        when(state.getInventory()).thenReturn(inventory);
        furnaceInput = iron;
        when(inventory.getSmelting()).thenAnswer(invocation -> furnaceInput);
    }

    private void campfire() {
        Campfire state = mock(Campfire.class);
        slots = new ItemStack[4];
        progress = new int[4];
        when(block.getState()).thenReturn(state);
        when(state.getSize()).thenReturn(4);
        when(state.getItem(anyInt())).thenAnswer(invocation -> slots[invocation.getArgument(0)]);
        when(state.getCookTime(anyInt())).thenAnswer(invocation -> progress[invocation.getArgument(0)]);
        when(state.getCookTimeTotal(anyInt())).thenReturn(100);
    }

    private void startFurnace(CookingRecipe<?> recipe, ItemStack source) {
        FurnaceStartSmeltEvent event = mock(FurnaceStartSmeltEvent.class);
        when(event.getBlock()).thenReturn(block);
        doReturn(recipe).when(event).getRecipe();
        when(event.getSource()).thenReturn(source);
        SmeltingListener.INSTANCE.cacheFurnaceRecipe(event);
    }

    private void startCampfire(CampfireRecipe recipe, ItemStack source, int slot) {
        CampfireStartEvent event = mock(CampfireStartEvent.class);
        when(event.getBlock()).thenReturn(block);
        doReturn(recipe).when(event).getRecipe();
        when(event.getSource()).thenReturn(source);
        SmeltingListener.INSTANCE.cacheCampfireRecipe(event);
        slots[slot] = source; // Bukkit fires CampfireStartEvent before populating the first empty slot
    }

    private void finishFurnace(ItemStack source) {
        FurnaceSmeltEvent event = mock(FurnaceSmeltEvent.class);
        when(event.getBlock()).thenReturn(block);
        when(event.getSource()).thenReturn(source);
        SmeltingListener.INSTANCE.processSmeltResult(event);
    }

    private void finishCampfire(ItemStack source, int slot) {
        BlockCookEvent event = mock(BlockCookEvent.class);
        when(event.getBlock()).thenReturn(block);
        when(event.getSource()).thenReturn(source);
        SmeltingListener.INSTANCE.processBlockCookResult(event);
        slots[slot] = null;
    }

    private void runScheduledOnce() {
        List<Runnable> due = new ArrayList<>(tasks);
        tasks.clear();
        due.forEach(Runnable::run);
    }

    private void clearCache() throws Exception {
        for (Field field : SmeltingListener.class.getDeclaredFields()) {
            if (Map.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                ((Map<?, ?>) field.get(SmeltingListener.INSTANCE)).clear();
            } else if (field.getType() == boolean.class) {
                field.setAccessible(true);
                field.setBoolean(SmeltingListener.INSTANCE, false);
            }
        }
    }
}
