package pers.yufiria.craftorithm.recipe.register;

import crypticlib.MinecraftVersion;
import crypticlib.compat.Compat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.nms.NmsRecipeRegister;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.util.ServerUtils;

import java.util.Optional;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;

    private final Compat<NmsRecipeRegister> NMS_REGISTER_COMPAT = new Compat<>(
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

    @Override
    public boolean registerRecipe(Recipe recipe) {
        if (PluginConfigs.USE_NMS_RECIPE_REGISTER.value()) {
            String currentVersionStr = MinecraftVersion.current().versionStr();
            Optional<NmsRecipeRegister> nmsRegisterOpt = NMS_REGISTER_COMPAT.findImplementation(currentVersionStr);
            if (nmsRegisterOpt.isEmpty()) {
                throw new RecipeLoadException("Can not find nms recipe register for version: " + currentVersionStr);
            }
            return nmsRegisterOpt.get().registerRecipe(recipe);
        }

        if (ServerUtils.after1_20Paper()) {
            //1.20.1以上paper端在添加配方时不对玩家进行更新,等加载完毕后统一更新
            return Bukkit.addRecipe(recipe, false);
        } else {
            return Bukkit.addRecipe(recipe);
        }
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        if (ServerUtils.after1_20Paper()) {
            //1.20.1以上paper端在删除配方时不对玩家进行更新,等加载完毕后统一更新
            return Bukkit.removeRecipe(recipeKey, false);
        } else {
            return Bukkit.removeRecipe(recipeKey);
        }
    }

    public Compat<NmsRecipeRegister> nmsRegisterCompat() {
        return NMS_REGISTER_COMPAT;
    }

}
