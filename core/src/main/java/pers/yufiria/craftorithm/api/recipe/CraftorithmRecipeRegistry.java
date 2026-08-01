package pers.yufiria.craftorithm.api.recipe;

import crypticlib.MinecraftVersion;
import crypticlib.compat.Compat;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public interface CraftorithmRecipeRegistry {

    Compat<CraftorithmRecipeRegistry> REGISTRY_COMPAT = new Compat<>(
        CraftorithmRecipeRegistry.class,
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

    AtomicReference<CraftorithmRecipeRegistry> IMPL_CACHE = new AtomicReference<>();
    AtomicBoolean UNSUPPORTED_VERSION_WARNED = new AtomicBoolean(false);

    static CraftorithmRecipeRegistry findImpl() {
        CraftorithmRecipeRegistry cachedImpl = IMPL_CACHE.get();
        if (cachedImpl != null) {
            IOHelper.info(cachedImpl.getClass().getName());
            return cachedImpl;
        }
        String currentVersionStr = MinecraftVersion.current().name();
        Optional<CraftorithmRecipeRegistry> registryOpt = REGISTRY_COMPAT.findImplementation(currentVersionStr);
        if (registryOpt.isPresent()) {
            IMPL_CACHE.set(registryOpt.get());
            IOHelper.info(registryOpt.get().getClass().getName());
            return registryOpt.get();
        }
        if (UNSUPPORTED_VERSION_WARNED.compareAndSet(false, true)) {
            IOHelper.info("&cCan not find craftorithm recipe register impl for version " + MinecraftVersion.current().versionStr() + ", custom recipes will not be registered!");
        }
        return (bukkitRecipe, send2Player) -> RegisterResult.UNSUPPORTED_VERSION;
    }

    /**
     * 往服务器里注册一个配方
     * @param bukkitRecipe 要注册的bukkit配方
     * @param updateRecipes 是否在注册后向玩家广播配方更新，这个字段只在1.21.3以上有用，因为旧版本本来就不在此阶段更新
     * @return
     */
    RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes);

    /**
     * 从服务器里删除一个配方
     * 这个方法需要在1.21.3以上的服务端重写，以实现更好的性能表现
     *
     * @param recipeKey   要删除配方的key
     * @param updateRecipes 是否在删除后向玩家广播配方更新，这个字段只在1.21.3以上有用，因为旧版本本来就不在此阶段更新
     */
    default boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        return Bukkit.removeRecipe(recipeKey);
    }

    /**
     * 用于给玩家更新配方列表
     */
    default void updateRecipes() {
        Bukkit.updateRecipes();
    }

    enum RegisterResult {
        SUCCESS,
        UNSUPPORTED_VERSION,
        UNSUPPORTED_RECIPE_TYPE
    }

}
