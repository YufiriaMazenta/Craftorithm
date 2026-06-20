package pers.yufiria.craftorithm.recipe.register;

import crypticlib.MinecraftVersion;
import crypticlib.compat.Compat;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.util.ServerUtils;

import java.util.Optional;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;

    private final Compat<NMSRecipeRegister> nmsRecipeRegisterCompat = new Compat<>(NMSRecipeRegister.class,
        (ver1, ver2) -> {
            MinecraftVersion current = MinecraftVersion.valueOf(ver1);
            MinecraftVersion minSupportVersion = MinecraftVersion.valueOf(ver2);
            if (current.after(minSupportVersion)) {
                return 1;
            } else if (current.equals(minSupportVersion)) {
                return 0;
            } else {
                return -1;
            }
        });

    @Override
    public boolean registerRecipe(Recipe recipe) {
        if (!PluginConfigs.USE_NMS_RECIPE_REGISTER.value()) {
            if (ServerUtils.after1_20Paper()) {
                //1.20.1以上paper端在添加配方时不对玩家进行更新,等加载完毕后统一更新
                return Bukkit.addRecipe(recipe, false);
            } else {
                return Bukkit.addRecipe(recipe);
            }
        } else {
            return findNMSRecipeRegisterIns().registerRecipe(recipe);
        }
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        if (!PluginConfigs.USE_NMS_RECIPE_REGISTER.value()) {
            if (ServerUtils.after1_20Paper()) {
                //1.20.1以上paper端在删除配方时不对玩家进行更新,等加载完毕后统一更新
                return Bukkit.removeRecipe(recipeKey, false);
            } else {
                return Bukkit.removeRecipe(recipeKey);
            }
        } else {
            return findNMSRecipeRegisterIns().unregisterRecipe(recipeKey);
        }
    }

    public Compat<NMSRecipeRegister> nmsRecipeRegisterCompat() {
        return nmsRecipeRegisterCompat;
    }

    public NMSRecipeRegister findNMSRecipeRegisterIns() {
        Optional<NMSRecipeRegister> implementation = nmsRecipeRegisterCompat.findImplementation(MinecraftVersion.current().name());
        if (implementation.isEmpty()) {
            IOHelper.info("&e[WARN] Failed to load minecraft recipe register");
        }
        return implementation.orElse(new NMSRecipeRegister() {
            @Override
            public boolean registerRecipe(Recipe recipe) {
                return false;
            }

            @Override
            public boolean unregisterRecipe(NamespacedKey recipeKey) {
                return false;
            }
        });
    }

    /**
     * 使用NMS方式注册配方的接口
     */
    public interface NMSRecipeRegister extends RecipeRegister { }

}
