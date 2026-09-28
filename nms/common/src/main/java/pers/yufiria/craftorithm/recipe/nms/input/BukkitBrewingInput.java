package pers.yufiria.craftorithm.recipe.nms.input;

import org.bukkit.inventory.ItemStack;

public record BukkitBrewingInput(
    ItemStack input,
    ItemStack reagent
) {
}
