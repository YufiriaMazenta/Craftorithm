package pers.yufiria.craftorithm.api.recipe;

import crypticlib.MinecraftVersion;
import crypticlib.compat.Compat;
import crypticlib.util.IOHelper;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.Optional;

public interface NmsRecipeRegister {

    Compat<NmsRecipeRegister> NMS_REGISTER_COMPAT = new Compat<>(
        NmsRecipeRegister.class,
        (ver1, compareVersion) -> {
            MinecraftVersion compareVersionObj = MinecraftVersion.valueOf(compareVersion);
            MinecraftVersion current = MinecraftVersion.current();
            if (current.after(compareVersionObj)) {
                return 1;
            } else if (current.equals(compareVersionObj)) {
                return 0;
            } else {
                return -1;
            }
        }
    );

    static NmsRecipeRegister findImpl() {
        String currentVersionStr = MinecraftVersion.current().versionStr();
        Optional<NmsRecipeRegister> nmsRegisterOpt = NMS_REGISTER_COMPAT.findImplementation(currentVersionStr);
        if (nmsRegisterOpt.isEmpty()) {
            IOHelper.info("&eCan not find nms recipe register for version " + currentVersionStr);
        }
        return nmsRegisterOpt.orElseGet(() -> bukkitRecipe -> RegisterResult.UNSUPPORTED_VERSION);
    }

    RegisterResult registerRecipe(Recipe bukkitRecipe);

    enum RegisterResult {
        SUCCESS,
        UNSUPPORTED_VERSION,
        UNSUPPORTED_RECIPE_TYPE
    }

}
