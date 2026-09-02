package pers.yufiria.craftorithm.recipe;

import crypticlib.config.BukkitConfigWrapper;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public record ParsedRecipe(
    String recipeFileName,
    NamespacedKey recipeKey,
    Recipe recipe,
    RecipeType recipeType,
    BukkitConfigWrapper configWrapper,
    long createTime
) {}
