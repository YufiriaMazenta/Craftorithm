package top.oasismc.oasisrecipe.recipe.object.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import top.oasismc.oasisrecipe.OasisRecipe;

import java.util.Map;

public class VanillaRecipe {

    private NamespacedKey key;
    private String[] shape;
    private Map<Character, RecipeChoice> ingredientMap;
    private ItemStack result;
    private final RecipeType type;

    public VanillaRecipe(String key, String[] shape, Map<Character, RecipeChoice> ingredientMap, ItemStack result, RecipeType type) {
        this.key = new NamespacedKey(OasisRecipe.getInstance(), key);
        this.shape = shape;
        this.ingredientMap = ingredientMap;
        this.result = result;
        this.type = type;
    }

    public VanillaRecipe(String key, String[] shape, Map<Character, RecipeChoice> ingredientMap, ItemStack result) {
        this(key, shape, ingredientMap, result, RecipeType.SHAPED);
    }

    public boolean regRecipe() {
        Recipe recipe = null;
        switch (type) {
            case SHAPED:
            default:
                recipe = createShapedRecipe();
                break;
            case SHAPELESS:
                //TODO
                break;
            case FURNACE:
                //TODO
                break;
            case SMOKING:
                //TODO
                break;
            case CAMPFIRE:
                //TODO
                break;
            case BLASTING:
                //TODO
                break;
            case SMITHING:
                //TODO
                break;
            case STONECUTTING:
                //TODO
                break;
        }
        return Bukkit.getServer().addRecipe(recipe);
    }

    private ShapedRecipe createShapedRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length(); j++) {
                recipe.setIngredient(shape[i].charAt(j), ingredientMap.get(shape[i].charAt(j)));
            }
        }
        return recipe;
    }

    private ShapelessRecipe createShapelessRecipe() {
        return null;
    }

    enum RecipeType {

        SHAPED("shaped"),
        SHAPELESS("shapeless"),
        FURNACE("furnace"),
        SMOKING("smoking"),
        CAMPFIRE("campfire"),
        BLASTING("blasting"),
        SMITHING("smithing"),
        STONECUTTING("stonecutting");

        private String name;

        RecipeType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
