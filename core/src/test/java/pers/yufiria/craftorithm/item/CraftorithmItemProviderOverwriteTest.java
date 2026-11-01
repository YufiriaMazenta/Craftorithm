package pers.yufiria.craftorithm.item;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import pers.yufiria.craftorithm.Craftorithm;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CraftorithmItemProviderOverwriteTest {

    @TempDir Path temporaryDirectory;
    private MockedStatic<Bukkit> bukkit;
    private MockedStatic<Craftorithm> plugin;
    private MockedStatic<CrypticLibBukkit> scheduler;
    private MockedStatic<MinecraftVersion> version;
    private final Map<ItemStack, Sample> samples = new IdentityHashMap<>();
    private final Map<String, CustomModelDataComponent> components = new HashMap<>();

    @BeforeEach
    void setUp() {
        plugin = mockStatic(Craftorithm.class);
        Craftorithm instance = mock(Craftorithm.class);
        when(instance.getDataFolder()).thenReturn(temporaryDirectory.toFile());
        plugin.when(Craftorithm::instance).thenReturn(instance);
        // No persistence task is run: this test exercises the actual in-memory save/lookup path only.
        scheduler = mockStatic(CrypticLibBukkit.class, RETURNS_DEEP_STUBS);
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getBukkitVersion).thenReturn("1.20.4-R0.1-SNAPSHOT");
        version = mockStatic(MinecraftVersion.class);
        version.when(MinecraftVersion::current).thenReturn(MinecraftVersion.V1_20);
        CraftorithmItemProvider.INSTANCE.onLifecycle(null, crypticlib.lifecycle.LifecyclePhase.DISABLE);
    }

    @AfterEach
    void tearDown() {
        try {
            CraftorithmItemProvider.INSTANCE.onLifecycle(null, crypticlib.lifecycle.LifecyclePhase.DISABLE);
        } finally {
            if (version != null) version.close();
            if (bukkit != null) bukkit.close();
            if (scheduler != null) scheduler.close();
            if (plugin != null) plugin.close();
        }
    }

    @Test
    void replacingMaterialRemovesOldIdentityWithoutReload() {
        ItemStack oldItem = item(new Sample(Material.STONE, "old", null, null, null), 1);
        ItemStack replacement = item(new Sample(Material.DIAMOND, "new", null, null, null), 1);
        NamespacedItemId id = save("token", oldItem);
        save("token", replacement);
        assertNull(CraftorithmItemProvider.INSTANCE.matchItemId(oldItem));
        assertEquals(id, CraftorithmItemProvider.INSTANCE.matchItemId(replacement));
        assertEquals(Material.DIAMOND, CraftorithmItemProvider.INSTANCE.matchItem("custom:token").getType());
    }

    @Test
    void replacingLegacyCustomModelDataRemovesOldBucketEntry() {
        ItemStack oldItem = item(new Sample(Material.PAPER, "token", 1, null, null), 1);
        ItemStack replacement = item(new Sample(Material.PAPER, "token", 2, null, null), 1);
        save("token", oldItem);
        NamespacedItemId id = save("token", replacement);
        assertNull(CraftorithmItemProvider.INSTANCE.matchItemId(oldItem));
        assertEquals(id, CraftorithmItemProvider.INSTANCE.matchItemId(replacement));
    }

    @Test
    void replacingItemModelRemovesOldBucketEntry() {
        version.when(MinecraftVersion::current).thenReturn(MinecraftVersion.V1_21_2);
        ItemStack oldItem = item(new Sample(Material.PAPER, "token", null, "old", null), 1);
        ItemStack replacement = item(new Sample(Material.PAPER, "token", null, "new", null), 1);
        save("token", oldItem);
        NamespacedItemId id = save("token", replacement);
        assertNull(CraftorithmItemProvider.INSTANCE.matchItemId(oldItem));
        assertEquals(id, CraftorithmItemProvider.INSTANCE.matchItemId(replacement));
    }

    @Test
    void replacingModernModelDataRemovesOldBucketEntry() {
        version.when(MinecraftVersion::current).thenReturn(MinecraftVersion.V1_21_4);
        ItemStack oldItem = item(new Sample(Material.PAPER, "token", null, null, "old"), 1);
        ItemStack replacement = item(new Sample(Material.PAPER, "token", null, null, "new"), 1);
        save("token", oldItem);
        NamespacedItemId id = save("token", replacement);
        assertNull(CraftorithmItemProvider.INSTANCE.matchItemId(oldItem));
        assertEquals(id, CraftorithmItemProvider.INSTANCE.matchItemId(replacement));
    }

    @Test
    void replacementWithinSameBucketStillUpdatesTheSample() {
        ItemStack oldItem = item(new Sample(Material.PAPER, "old lore", null, null, null), 1);
        ItemStack replacement = item(new Sample(Material.PAPER, "new lore", null, null, null), 1);
        save("token", oldItem);
        NamespacedItemId id = save("token", replacement);
        assertNull(CraftorithmItemProvider.INSTANCE.matchItemId(oldItem));
        assertEquals(id, CraftorithmItemProvider.INSTANCE.matchItemId(replacement));
    }

    @Test
    void replacingOneIdDoesNotDeleteOtherIdsInItsOldBucket() {
        ItemStack oldItem = item(new Sample(Material.PAPER, "old", null, null, null), 1);
        ItemStack otherItem = item(new Sample(Material.PAPER, "other", null, null, null), 1);
        NamespacedItemId otherId = save("other", otherItem);
        save("token", oldItem);
        save("token", item(new Sample(Material.DIAMOND, "new", null, null, null), 1));
        assertNull(CraftorithmItemProvider.INSTANCE.matchItemId(oldItem));
        assertEquals(otherId, CraftorithmItemProvider.INSTANCE.matchItemId(otherItem));
    }

    @Test
    void saveAndLookupStillUseIndependentSingleItemSnapshots() {
        ItemStack original = item(new Sample(Material.PAPER, "token", null, null, null), 32);
        save("token", original);
        ItemStack first = CraftorithmItemProvider.INSTANCE.matchItem("custom:token");
        ItemStack second = CraftorithmItemProvider.INSTANCE.matchItem("custom:token");
        assertNotSame(original, first);
        assertNotSame(first, second);
        assertEquals(32, original.getAmount());
        assertEquals(1, first.getAmount());
        first.setAmount(7);
        assertEquals(1, CraftorithmItemProvider.INSTANCE.matchItem("custom:token").getAmount());
    }

    @Test
    void namedIdsRetainPriorityOverAutomaticallyCreatedAliases() {
        ItemStack sample = item(new Sample(Material.PAPER, "same", null, null, null), 1);
        CraftorithmItemProvider.INSTANCE.saveItem("plugin_created", "token", sample);
        NamespacedItemId named = save("named", sample);
        assertEquals(named, CraftorithmItemProvider.INSTANCE.matchItemId(sample));
    }

    private NamespacedItemId save(String id, ItemStack sample) {
        return CraftorithmItemProvider.INSTANCE.saveItem("custom", id, sample);
    }

    @SuppressWarnings("removal")
    private ItemStack item(Sample sample, int amount) {
        ItemMeta meta = mock(ItemMeta.class);
        when(meta.hasCustomModelData()).thenReturn(sample.legacyModel() != null);
        if (sample.legacyModel() != null) when(meta.getCustomModelData()).thenReturn(sample.legacyModel());
        if (sample.itemModel() != null) when(meta.getItemModel()).thenReturn(new NamespacedKey("custom", sample.itemModel()));
        when(meta.hasCustomModelDataComponent()).thenReturn(sample.component() != null);
        if (sample.component() != null) {
            CustomModelDataComponent component = components.computeIfAbsent(sample.component(), key -> mock(CustomModelDataComponent.class));
            when(meta.getCustomModelDataComponent()).thenReturn(component);
        }
        ItemStack stack = mock(ItemStack.class);
        samples.put(stack, sample);
        AtomicInteger count = new AtomicInteger(amount);
        when(stack.getType()).thenReturn(sample.material());
        when(stack.getItemMeta()).thenReturn(meta);
        when(stack.hasItemMeta()).thenReturn(true);
        when(stack.getAmount()).thenAnswer(call -> count.get());
        doAnswer(call -> { count.set(call.getArgument(0)); return null; }).when(stack).setAmount(anyInt());
        when(stack.isSimilar(any())).thenAnswer(call -> sample.equals(samples.get(call.<ItemStack>getArgument(0))));
        when(stack.clone()).thenAnswer(call -> item(sample, count.get()));
        return stack;
    }

    private record Sample(Material material, String metadata, Integer legacyModel, String itemModel, String component) {}
}
