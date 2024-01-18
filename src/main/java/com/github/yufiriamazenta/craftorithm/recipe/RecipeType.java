package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import crypticlib.chat.entry.StringLangConfigEntry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RecipeType {

    private static final Map<String, RecipeType> BY_NAME = new ConcurrentHashMap<>();

    public static RecipeType SHAPED = new RecipeType("shaped", Languages.RECIPE_TYPE_NAME_SHAPED);
    public static RecipeType SHAPELESS = new RecipeType("shapeless", Languages.RECIPE_TYPE_NAME_SHAPELESS);
    public static RecipeType COOKING = new RecipeType("cooking", Languages.RECIPE_TYPE_NAME_COOKING);
    public static RecipeType SMITHING = new RecipeType("smithing", Languages.RECIPE_TYPE_NAME_SMITHING);
    public static RecipeType STONE_CUTTING = new RecipeType("stone_cutting", Languages.RECIPE_TYPE_NAME_STONE_CUTTING);
    public static RecipeType RANDOM_COOKING = new RecipeType("random_cooking", Languages.RECIPE_TYPE_NAME_COOKING);
    public static RecipeType UNKNOWN = new RecipeType("unknown", null);
    public static RecipeType POTION = new RecipeType("potion", Languages.RECIPE_TYPE_NAME_POTION);
    public static RecipeType ANVIL = new RecipeType("anvil", Languages.RECIPE_TYPE_NAME_ANVIL);

    private final String typeId;
    private StringLangConfigEntry typeName;

    private RecipeType(String typeId, StringLangConfigEntry typeName) {
        this.typeId = typeId.toUpperCase();
        this.typeName = typeName;
        BY_NAME.put(this.typeId, this);
    }

    public String typeId() {
        return typeId;
    }

    public StringLangConfigEntry typeName() {
        return typeName;
    }

    public RecipeType setTypeName(StringLangConfigEntry typeName) {
        this.typeName = typeName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeType that = (RecipeType) o;

        return typeId.equals(that.typeId);
    }

    @Override
    public int hashCode() {
        return typeId.hashCode();
    }

    public static RecipeType getByName(String typeName) {
        return BY_NAME.get(typeName.toUpperCase());
    }

    public static RecipeType[] types() {
        return BY_NAME.values().toArray(new RecipeType[0]);
    }

}
