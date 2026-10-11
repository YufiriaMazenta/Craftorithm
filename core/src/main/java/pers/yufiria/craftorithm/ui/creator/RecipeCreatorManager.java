package pers.yufiria.craftorithm.ui.creator;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.ui.menu.Menu;
import crypticlib.util.TriFunction;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.SmeltingMenuType;
import pers.yufiria.craftorithm.ui.creator.anvil.AnvilCreator;
import pers.yufiria.craftorithm.ui.creator.vanillabrewing.VanillaBrewingCreator;
import pers.yufiria.craftorithm.ui.creator.vanillacrafting.VanillaShapedCreator;
import pers.yufiria.craftorithm.ui.creator.vanillacrafting.VanillaShapelessCreator;
import pers.yufiria.craftorithm.ui.creator.vanillasmelting.VanillaSmeltingCreator;
import pers.yufiria.craftorithm.ui.creator.vanillasmithing.VanillaSmithingTransformCreator;
import pers.yufiria.craftorithm.ui.creator.vanillastonecutting.VanillaStonecuttingCreator;

import java.util.Optional;
import java.util.Set;

/**
 * 配方创建器管理器。
 * <p>
 * 负责“配方类型 -> 创建界面”的路由注册与查询，路由函数直接使用 {@link TriFunction}，
 * 形态与 {@code RecipeEditorManager}、{@code RecipeDisplayManager} 保持一致。
 * 原先这部分路由表定义在 {@code CreateCommand} 内部，现迁移到创建界面所在的包内，
 * 命令类只负责解析参数并委托本管理器。
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum RecipeCreatorManager implements LifecycleTask {

    INSTANCE;

    private final RecipeTypeMap<RecipeType, TriFunction<Player, String, String, Menu>> creatorFactoryMap = new RecipeTypeMap<>();

    public void registerCreator(RecipeType recipeType, TriFunction<Player, String, String, Menu> creatorFactory) {
        creatorFactoryMap.put(recipeType, creatorFactory);
    }

    public void removeCreator(RecipeType recipeType) {
        creatorFactoryMap.remove(recipeType);
    }

    public Optional<TriFunction<Player, String, String, Menu>> getCreatorFactory(RecipeType recipeType) {
        return Optional.ofNullable(creatorFactoryMap.get(recipeType));
    }

    /**
     * 已注册创建器的配方类型集合，用于命令补全
     * @return 配方类型集合
     */
    public Set<RecipeType> recipeTypes() {
        return creatorFactoryMap.keySet();
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        registerDefaultCreators();
    }

    private void registerDefaultCreators() {
        registerCreator(SimpleRecipeTypes.VANILLA_SHAPED, (player, recipeId, recipeFileName) -> {
            VanillaShapedCreator creator = new VanillaShapedCreator(player, recipeId, recipeFileName);
            creator.openMenu();
            return creator;
        });
        registerCreator(SimpleRecipeTypes.VANILLA_SHAPELESS, (player, recipeId, recipeFileName) -> {
            VanillaShapelessCreator creator = new VanillaShapelessCreator(player, recipeId, recipeFileName);
            creator.openMenu();
            return creator;
        });
        // 熔炉配方 (furnace, blast, smoker, campfire)
        for (SmeltingMenuType smeltingType : SmeltingMenuType.values()) {
            registerCreator(smeltingType.recipeType(), (player, recipeId, recipeFileName) -> {
                VanillaSmeltingCreator creator = new VanillaSmeltingCreator(player, recipeId, recipeFileName, smeltingType);
                creator.openMenu();
                return creator;
            });
        }
        // 锻造台配方
        registerCreator(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM, (player, recipeId, recipeFileName) -> {
            VanillaSmithingTransformCreator creator = new VanillaSmithingTransformCreator(player, recipeId, recipeFileName);
            creator.openMenu();
            return creator;
        });
        // 切石机配方
        registerCreator(SimpleRecipeTypes.VANILLA_STONECUTTING, (player, recipeId, recipeFileName) -> {
            VanillaStonecuttingCreator creator = new VanillaStonecuttingCreator(player, recipeId, recipeFileName);
            creator.openMenu();
            return creator;
        });
        // 酿造台配方
        registerCreator(SimpleRecipeTypes.VANILLA_BREWING, (player, recipeId, recipeFileName) -> {
            VanillaBrewingCreator creator = new VanillaBrewingCreator(player, recipeId, recipeFileName);
            creator.openMenu();
            return creator;
        });
        // 铁砧配方
        registerCreator(SimpleRecipeTypes.ANVIL, (player, recipeId, recipeFileName) -> {
            AnvilCreator creator = new AnvilCreator(player, recipeId, recipeFileName);
            creator.openMenu();
            return creator;
        });
    }

}
