package pers.yufiria.craftorithm.recipe.ingredient;

import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemGroup;

public enum ItemIdRecipeChoiceParser implements RecipeChoiceParser {

    INSTANCE;

    @Override
    public RecipeChoice parse(String choiceStr) {
        RecipeChoice bukkitChoice = BukkitRecipeChoiceParser.INSTANCE.parse(choiceStr);
        return new ItemIdRecipeChoice(
            bukkitChoice,
            ItemGroup.fromIngredientId(choiceStr).isPresent() ? choiceStr : null
        );
    }

}
