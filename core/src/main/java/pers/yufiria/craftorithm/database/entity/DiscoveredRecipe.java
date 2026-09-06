package pers.yufiria.craftorithm.database.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "discovered_recipes")
public class DiscoveredRecipe {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "player_uuid", canBeNull = false, uniqueCombo = true)
    private UUID playerUuid;

    @DatabaseField(columnName = "recipe_key", canBeNull = false, uniqueCombo = true)
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
