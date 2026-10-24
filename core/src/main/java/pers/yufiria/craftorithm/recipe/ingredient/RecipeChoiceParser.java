package pers.yufiria.craftorithm.recipe.ingredient;

import org.bukkit.inventory.RecipeChoice;

public interface RecipeChoiceParser {

    RecipeChoice parse(String choiceStr);

}
