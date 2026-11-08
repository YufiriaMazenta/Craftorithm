package pers.yufiria.craftorithm.recipe;

import crypticlib.MinecraftVersion;
import crypticlib.util.Lazy;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配方类型注册表
 */
public enum RecipeTypeRegistry {

    INSTANCE;

    private final Map<String, RecipeType> recipeTypes = new ConcurrentHashMap<>();
    //酿造配方的支持性只取决于服务器版本, 计算一次后永不失效
    private final Lazy<Boolean> lazySupportPotionMix = Lazy.of(() -> {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V26_3)) {
            return true;
        }
        try {
            Class.forName("io.papermc.paper.potion.PotionMix");
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    });

    public boolean register(RecipeType type) {
        return register(type, false);
    }

    public boolean register(RecipeType type, boolean force) {
        if (type == null) {
            return false;
        }
        if (recipeTypes.containsKey(type.typeKey())) {
            if (force) {
                recipeTypes.put(type.typeKey(), type);
                return true;
            } else {
                return false;
            }
        }
        recipeTypes.put(type.typeKey(), type);
        return true;
    }

    /**
     * 注册各内置配方类型, 需在读取任何配方配置之前调用
     */
    public void registerDefaults() {
        register(SimpleRecipeTypes.UNKNOWN);
        register(SimpleRecipeTypes.VANILLA_SHAPED);
        register(SimpleRecipeTypes.VANILLA_SHAPELESS);
        register(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE);
        register(SimpleRecipeTypes.VANILLA_SMELTING_BLAST);
        register(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER);
        register(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE);
        register(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM);
        register(SimpleRecipeTypes.VANILLA_SMITHING_TRIM);
        register(SimpleRecipeTypes.VANILLA_STONECUTTING);
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            register(SimpleRecipeTypes.ANVIL);
        }
        if (supportBrewingRecipe()) {
            register(SimpleRecipeTypes.VANILLA_BREWING);
        }
    }

    public @Nullable RecipeType getByKey(String typeId) {
        return recipeTypes.get(typeId);
    }

    /**
     * 判断配方属于哪种已注册的类型, 未匹配到任何类型时返回 UNKNOWN
     */
    public RecipeType typeOf(Recipe recipe) {
        for (RecipeType recipeType : recipeTypes.values()) {
            if (recipeType.isThisType(recipe)) {
                return recipeType;
            }
        }
        return SimpleRecipeTypes.UNKNOWN;
    }

    public List<RecipeType> all() {
        return new ArrayList<>(recipeTypes.values());
    }

    public boolean supportBrewingRecipe() {
        return lazySupportPotionMix.get();
    }

    public void clear() {
        recipeTypes.clear();
    }

}
