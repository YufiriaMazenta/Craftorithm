package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.MinecraftVersion;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.BrewingRecipe;

import java.io.File;
import pers.yufiria.craftorithm.recipe.RecipeTestItem;
import pers.yufiria.craftorithm.recipe.register.BrewingRecipeRegister;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BrewingListenerTest {
    @Test
    void threeSlotsAndTwoBatchesNeverShareTemplateOrProcessorMutations() throws Exception {
        withRecipe((recipe, template, inventory, event) -> {
            List<ItemStack> previousBatch = null;
            for (int batch = 0; batch < 2; batch++) {
                List<ItemStack> results = new ArrayList<>(List.of(template, template, template));
                when(event.getResults()).thenReturn(results);
                BrewingListener.INSTANCE.rebuildBrewResult(event);
                BrewingListener.INSTANCE.processResult(event);
                for (ItemStack result : results) {
                    assertEquals(2, result.getAmount(), "one processor application per slot, independent of earlier slots/batches");
                    assertNotSame(template, result);
                    if (previousBatch != null) for (ItemStack previous : previousBatch) assertNotSame(previous, result);
                }
                assertNotSame(results.get(0), results.get(1));
                assertNotSame(results.get(1), results.get(2));
                assertEquals(1, template.getAmount(), "caller template remains untouched");
                assertEquals(1, recipe.getResult().getAmount(), "registered recipe template remains untouched");
                previousBatch = results;
            }
        });
    }

    @Test
    void processingClonesEvenAlreadyAliasedEventResults() throws Exception {
        withRecipe((recipe, template, inventory, event) -> {
            ItemStack sharedExternalResult = new RecipeTestItem(Material.POTION);
            List<ItemStack> results = new ArrayList<>(List.of(sharedExternalResult, sharedExternalResult, sharedExternalResult));
            when(event.getResults()).thenReturn(results);
            BrewingListener.INSTANCE.processResult(event); // no rebuild: also protects vanilla/third-party event result lists
            for (ItemStack result : results) assertEquals(2, result.getAmount());
            assertEquals(1, sharedExternalResult.getAmount());
            assertNotSame(results.get(0), results.get(1));
        });
    }

    private void withRecipe(BrewTest test) throws Exception {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getBukkitVersion).thenReturn("1.20.4-R0.1-SNAPSHOT");
            try (MockedStatic<Craftorithm> pluginStatic = mockStatic(Craftorithm.class);
                 MockedStatic<MinecraftVersion> version = mockStatic(MinecraftVersion.class);
                 MockedStatic<ItemHelper> itemHelper = mockStatic(ItemHelper.class)) {
                Craftorithm plugin = mock(Craftorithm.class);
                when(plugin.getDataFolder()).thenReturn(new File("build/test-only-brewing-plugin"));
                pluginStatic.when(Craftorithm::instance).thenReturn(plugin);
                version.when(MinecraftVersion::current).thenReturn(MinecraftVersion.V26_1);
                itemHelper.when(() -> ItemHelper.isAir(any())).thenAnswer(invocation -> invocation.getArgument(0) == null);
                RecipeChoice choice = mock(RecipeChoice.class);
                when(choice.test(any(ItemStack.class))).thenReturn(true);
                NamespacedKey key = new NamespacedKey("test", "independent_brew");
                ItemStack template = new RecipeTestItem(Material.POTION);
                BrewingRecipe recipe = new BrewingRecipe(key, choice, choice, template);
                Map<NamespacedKey, BrewingRecipe> recipes = map(BrewingRecipeRegister.INSTANCE, "brewingRecipeMap");
                Map<NamespacedKey, ResultProcessors> processors = map(ResultProcessorManager.INSTANCE, "recipeProcessors");
                Map<NamespacedKey, BrewingRecipe> oldRecipes = Map.copyOf(recipes);
                Map<NamespacedKey, ResultProcessors> oldProcessors = Map.copyOf(processors);
                recipes.clear();
                processors.clear();
                recipes.put(key, recipe);
                ResultProcessor increment = mock(ResultProcessor.class);
                doAnswer(invocation -> {
                    ItemStack result = invocation.getArgument(1);
                    result.setAmount(result.getAmount() + 1);
                    return null;
                }).when(increment).processItem(any(), any(), isNull());
                processors.put(key, new ResultProcessors(List.of(increment)));
                try {
                    BrewerInventory inventory = mock(BrewerInventory.class);
                    when(inventory.getIngredient()).thenReturn(new RecipeTestItem(Material.NETHER_WART));
                    for (int slot = 0; slot < 3; slot++) when(inventory.getItem(slot)).thenReturn(new RecipeTestItem(Material.GLASS_BOTTLE));
                    BrewEvent event = mock(BrewEvent.class);
                    when(event.getContents()).thenReturn(inventory);
                    when(event.getBlock()).thenReturn(mock(Block.class));
                    test.run(recipe, template, inventory, event);
                } finally {
                    recipes.clear();
                    recipes.putAll(oldRecipes);
                    processors.clear();
                    processors.putAll(oldProcessors);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <K, V> Map<K, V> map(Object owner, String name) throws Exception {
        Field field = owner.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return (Map<K, V>) field.get(owner);
    }

    private interface BrewTest {
        void run(BrewingRecipe recipe, ItemStack template, BrewerInventory inventory, BrewEvent event) throws Exception;
    }
}
