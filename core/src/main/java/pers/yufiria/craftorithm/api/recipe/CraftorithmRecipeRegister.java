package pers.yufiria.craftorithm.api.recipe;

import crypticlib.MinecraftVersion;
import crypticlib.compat.Compat;
import crypticlib.util.IOHelper;
import org.bukkit.inventory.Recipe;

import java.util.Optional;

public interface CraftorithmRecipeRegister {

    Compat<CraftorithmRecipeRegister> REGISTER_COMPAT = new Compat<>(
        CraftorithmRecipeRegister.class,
        (ver1, ver2) -> {
            MinecraftVersion ver1Obj = MinecraftVersion.valueOf(ver1.toUpperCase());
            MinecraftVersion ver2Obj = MinecraftVersion.valueOf(ver2.toUpperCase());
            if (ver1Obj.after(ver2Obj)) {
                return 1;
            } else if (ver1Obj.equals(ver2Obj)) {
                return 0;
            } else {
                return -1;
            }
        }
    );

    static CraftorithmRecipeRegister findImpl() {
        String currentVersionStr = MinecraftVersion.current().name();
        Optional<CraftorithmRecipeRegister> nmsRegisterOpt = REGISTER_COMPAT.findImplementation(currentVersionStr);
        if (nmsRegisterOpt.isEmpty()) {
            IOHelper.info("&eCan not find craftorithm recipe register impl for version " + MinecraftVersion.current().versionStr());
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
