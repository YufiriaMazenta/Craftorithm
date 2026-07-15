package pers.yufiria.craftorithm.nms;

import crypticlib.MinecraftVersion;
import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;

public enum CraftingMenuCurrentRecipeSetters {

    INSTANCE;

    private CraftingMenuCurrentRecipeSetter setter;

    public void load() {
        setter = null;
        if (MinecraftVersion.current() != MinecraftVersion.V1_20_1) {
            return;
        }
        try {
            Class<?> setterClass = Class.forName("pers.yufiria.craftorithm.nms.V1_20_1CraftingMenuCurrentRecipeSetter");
            setter = (CraftingMenuCurrentRecipeSetter) setterClass.getField("INSTANCE").get(null);
        } catch (ReflectiveOperationException | LinkageError throwable) {
            IOHelper.info("&cFailed to load crafting recipe NMS bridge for 1.20.1");
            throwable.printStackTrace();
        }
    }

    public boolean setCurrentRecipe(CraftingInventory craftingInventory, NamespacedKey recipeKey, Recipe recipe) {
        return setter != null && setter.setCurrentRecipe(craftingInventory, recipeKey, recipe);
    }

}
