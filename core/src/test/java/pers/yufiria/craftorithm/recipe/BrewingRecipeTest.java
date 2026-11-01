package pers.yufiria.craftorithm.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BrewingRecipeTest {
    @Test
    void constructorAndEveryResultReadOwnTheirItems() {
        ItemStack supplied = new RecipeTestItem(Material.POTION);
        BrewingRecipe recipe = new BrewingRecipe(new NamespacedKey("test", "brew"),
            mock(RecipeChoice.class), mock(RecipeChoice.class), supplied);
        supplied.setAmount(6);
        ItemStack first = recipe.getResult();
        ItemStack second = recipe.getResult();
        assertAll(
            () -> assertEquals(1, first.getAmount(), "constructor must snapshot the caller's template"),
            () -> assertNotSame(first, second, "every recipe result is independently mutable"),
            () -> assertNotSame(supplied, first)
        );
        first.setAmount(12);
        assertEquals(1, recipe.getResult().getAmount(), "processors must not mutate future batches");
    }
}
