package pers.yufiria.craftorithm.recipe.nms;

import java.util.Locale;
import crypticlib.CrypticLib;
import crypticlib.MinecraftVersion;
import crypticlib.compat.Compat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public interface NmsRecipeAccessor {

    Compat<NmsRecipeAccessor> COMPAT = new Compat<>(
        NmsRecipeAccessor.class,
        (ver1, ver2) -> {
            MinecraftVersion ver1Obj = MinecraftVersion.valueOf(ver1.toUpperCase(Locale.ROOT));
            MinecraftVersion ver2Obj = MinecraftVersion.valueOf(ver2.toUpperCase(Locale.ROOT));
            if (ver1Obj.after(ver2Obj)) {
                return 1;
            } else if (ver1Obj.equals(ver2Obj)) {
                return 0;
            } else {
                return -1;
            }
        }
    );

    AtomicReference<NmsRecipeAccessor> IMPL_CACHE = new AtomicReference<>();
    AtomicBoolean UNSUPPORTED_VERSION_WARNED = new AtomicBoolean(false);

    static NmsRecipeAccessor findImpl() {
        NmsRecipeAccessor cachedImpl = IMPL_CACHE.get();
        if (cachedImpl != null) {
            return cachedImpl;
        }
        String currentVersionStr = MinecraftVersion.current().name();
        Optional<NmsRecipeAccessor> implOpt = COMPAT.findImplementation(currentVersionStr);
        if (implOpt.isPresent()) {
            IMPL_CACHE.set(implOpt.get());
            return implOpt.get();
        }
        if (UNSUPPORTED_VERSION_WARNED.compareAndSet(false, true)) {
            CrypticLib.error("Can not find nms recipe accessor impl for version " + MinecraftVersion.current().versionStr() + ", custom recipes will not be registered!");
        }
        return (bukkitRecipe, send2Player) -> RegisterResult.UNSUPPORTED_VERSION;
    }

    /**
     * 往服务器里注册一个配方
     *
     * @param bukkitRecipe 要注册的bukkit配方
     * @param sync2Client 是否在注册后同步给客户端
     * @return
     */
    RegisterResult registerRecipe(Recipe bukkitRecipe, boolean sync2Client);

    /**
     * 从服务器里删除一个配方
     * 这个方法需要在1.21.3以上的服务端重写，以实现更好的性能表现
     *
     * @param recipeKey   要删除配方的key
     * @param sync2Client 是否在删除后同步给客户端
     */
    default boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        return Bukkit.removeRecipe(recipeKey);
    }

    /**
     * 静默重建客户端侧需要的配方数据(属性集/展示列表)
     * 1.20 ~ 1.21.1 的客户端直接接收全量配方列表, 没有派生数据, 默认留空
     */
    default void rebuildRecipes() {
    }

    /**
     * 构建当前最新的配方包, 需在 rebuildRecipes 之后调用
     */
    default void buildRecipePacket() {
    }

    /**
     * 向单个玩家下发配方数据
     *
     * @return false表示当前版本不支持逐玩家下发
     */
    default boolean syncRecipes(Player player) {
        return false;
    }

    /**
     * 立即向所有在线玩家下发配方数据
     */
    default void syncRecipes() {
        rebuildRecipes();
        buildRecipePacket();
        for (Player player : Bukkit.getOnlinePlayers()) {
            syncRecipes(player);
        }
    }

    enum RegisterResult {
        SUCCESS,
        UNSUPPORTED_VERSION,
        UNSUPPORTED_RECIPE_TYPE
    }

}
