package pers.yufiria.craftorithm.recipe.nms.input;

import org.bukkit.inventory.ItemStack;

public record BukkitSmithingInput(
    ItemStack template,
    ItemStack base,
    ItemStack addition
) {
}
