package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import crypticlib.platform.Platform;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
public enum SmeltResultRefreshHandler implements Listener {

    INSTANCE;

    private final Map<Block, Recipe> blockSmeltRecipeMap = new ConcurrentHashMap<>();

    @EventHandler
    public void putFurnaceSmeltRecipeCache(FurnaceStartSmeltEvent event) {
        if (!CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
            //因为Bukkit没有FurnaceSmeltEvent.getRecipe方法，如果是Paper及其下游就可以不用处理
            return;
        }
        CookingRecipe<?> recipe = event.getRecipe();
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }
        putRecipeCache(event.getBlock(), recipe);
    }

    @EventHandler
    public void refreshSmeltResult(FurnaceSmeltEvent event) {
        Recipe recipe;
        if (CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
            recipe = blockSmeltRecipeMap.get(event.getBlock());
            blockSmeltRecipeMap.remove(event.getBlock());
        } else {
            //Paper及其衍生端直接通过事件获取
            recipe = event.getRecipe();
        }
        if (recipe == null) {
            return;
        }
        ItemStack result = event.getResult();
        String id = ItemManager.INSTANCE.matchItemName(result, false);
        if (id == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id);
        result.setItemMeta(refreshItem.getItemMeta());
        event.setResult(result);
    }

    @EventHandler
    public void putBlockCookRecipeCache(CampfireStartEvent event) {
        if (!CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
            //因为Bukkit没有CampfireStartEvent.getRecipe方法，如果是Paper及其下游就可以不用处理
            return;
        }
        CampfireRecipe recipe = event.getRecipe();
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }
        putRecipeCache(event.getBlock(), recipe);
    }

    @EventHandler
    public void refreshBlockCookResult(BlockCookEvent event) {
        Recipe recipe;
        if (CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
            recipe = blockSmeltRecipeMap.get(event.getBlock());
            blockSmeltRecipeMap.remove(event.getBlock());
        } else {
            //Paper及其衍生端直接通过事件获取
            recipe = event.getRecipe();
        }
        if (recipe == null) {
            return;
        }
        ItemStack result = event.getResult();
        String id = ItemManager.INSTANCE.matchItemName(result, false);
        if (id == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id);
        result.setItemMeta(refreshItem.getItemMeta());
        event.setResult(result);
    }

    private void putRecipeCache(Block block, CookingRecipe<?> recipe) {
        blockSmeltRecipeMap.put(block, recipe);
        int cookingTime = recipe.getCookingTime();
        //防止玩家对大量烧炼方块进行烧炼打断操作导致出现大量无用缓存，在烧炼配方预计完成时间的一秒后清除缓存
        CrypticLibBukkit.scheduler().runTaskLaterAsync(Craftorithm.instance(), () -> {
            blockSmeltRecipeMap.remove(block);
        }, cookingTime + 20);
    }

}
