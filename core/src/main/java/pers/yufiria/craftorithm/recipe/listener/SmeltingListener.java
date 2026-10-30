package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessors;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
public enum SmeltingListener implements Listener {

    INSTANCE;

    /**
     * Spigot 的 FurnaceSmeltEvent / BlockCookEvent 拿不到配方，只能在开始烧炼时记一份，
     * 因此这里缓存全部配方（不限命名空间），否则原版配方在完成阶段无法校验。
     */
    private final Map<Block, Recipe> blockCookRecipeMap = new ConcurrentHashMap<>();

    @EventHandler
    public void cacheFurnaceRecipe(FurnaceStartSmeltEvent event) {
        if (CrypticLibBukkit.isPaper()) {
            //Paper及其下游可以在完成事件里直接取配方
            return;
        }
        putRecipeCache(event.getBlock(), event.getRecipe());
    }

    @EventHandler
    public void processSmeltResult(FurnaceSmeltEvent event) {
        Recipe recipe;
        if (CrypticLibBukkit.isPaper()) {
            //Paper及其衍生端直接通过事件获取
            recipe = event.getRecipe();
        } else {
            recipe = blockCookRecipeMap.remove(event.getBlock());
        }
        if (recipe == null) {
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            return;
        }
        // 拦截材料用于烧炼配方，这里主要是拦截其他插件和原版的配方
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{event.getSource()}, recipeKey)) {
            event.setCancelled(true);
            return;
        }
        ItemStack result = event.getResult();
        // 重新从物品源获取物品, 刷新结果的组件
        ItemManager.INSTANCE.refreshItem(result, null);
        // 运行结果处理器（烧炼配方的source是输入物品）
        Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
        processors.ifPresent(p -> p.processItem(event.getSource(), result, null));
        event.setResult(result);
    }

    @EventHandler
    public void cacheCampfireRecipe(CampfireStartEvent event) {
        if (CrypticLibBukkit.isPaper()) {
            //Paper及其下游可以在完成事件里直接取配方
            return;
        }
        putRecipeCache(event.getBlock(), event.getRecipe());
    }

    @EventHandler
    public void processBlockCookResult(BlockCookEvent event) {
        Recipe recipe;
        if (CrypticLibBukkit.isPaper()) {
            //Paper及其衍生端直接通过事件获取
            recipe = event.getRecipe();
        } else {
            recipe = blockCookRecipeMap.remove(event.getBlock());
        }
        if (recipe == null) {
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            return;
        }
        // 拦截指定材料用于营火配方，这里主要是拦截其他插件和原版的配方
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{event.getSource()}, recipeKey)) {
            event.setCancelled(true);
            return;
        }
        ItemStack result = event.getResult();
        // 重新从物品源获取物品, 刷新结果的组件
        ItemManager.INSTANCE.refreshItem(result, null);
        // 运行结果处理器（营火配方没有sourceItem）
        Optional<ResultProcessors> processors = ResultProcessorManager.INSTANCE.getRecipeProcessors(recipeKey);
        processors.ifPresent(p -> p.processItem(null, result, null));
        event.setResult(result);
    }

    private void putRecipeCache(Block block, CookingRecipe<?> recipe) {
        if (recipe == null) {
            return;
        }
        blockCookRecipeMap.put(block, recipe);
        int cookingTime = recipe.getCookingTime();
        //防止玩家对大量烧炼方块进行烧炼打断操作导致出现大量无用缓存，在烧炼配方预计完成时间的一秒后清除缓存
        CrypticLibBukkit.scheduler().asyncLater(() -> {
            blockCookRecipeMap.remove(block);
        }, cookingTime + 20);
    }

}
