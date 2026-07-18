package pers.yufiria.craftorithm.recipe.choice;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.inventory.RecipeChoice;

public enum BrewingRecipeChoiceParser implements RecipeChoiceParser {

    INSTANCE;

    @Override
    public RecipeChoice parse(String choiceStr) {
        RecipeChoice itemIdChoice = ItemIdRecipeChoiceParser.INSTANCE.parse(choiceStr);
        return PotionMix.createPredicateChoice(
            itemIdChoice
        );
    }

}
