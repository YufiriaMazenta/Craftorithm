package pers.yufiria.craftorithm.database.entity;

import crypticlib.database.annotation.Field;
import crypticlib.database.annotation.Table;

import java.util.UUID;

@Table(name = "discovered_recipes")
public class DiscoveredRecipe {

    @Field(id = true, generated = true)
    private long id;

    @Field(name = "player_uuid", nullable = false)
    private UUID playerUuid;

    @Field(name = "recipe_key", nullable = false)
    private String recipeKey;

    public DiscoveredRecipe() {
    }

    public DiscoveredRecipe(UUID playerUuid, String recipeKey) {
        this.playerUuid = playerUuid;
        this.recipeKey = recipeKey;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getRecipeKey() {
        return recipeKey;
    }

}
