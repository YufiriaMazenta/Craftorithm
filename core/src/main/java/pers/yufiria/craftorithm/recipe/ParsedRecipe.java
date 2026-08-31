package pers.yufiria.craftorithm.recipe;

import crypticlib.config.BukkitConfigWrapper;
import org.bukkit.inventory.Recipe;

public record ParsedRecipe(
    String recipeName,
    String recipeId,
    Recipe recipe,
    RecipeType recipeType,
    BukkitConfigWrapper configWrapper,
    long createTime
) {}
