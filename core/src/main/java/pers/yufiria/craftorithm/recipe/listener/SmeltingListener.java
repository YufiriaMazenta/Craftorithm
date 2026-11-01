package pers.yufiria.craftorithm.recipe.listener;

import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Campfire;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@EventListener
public enum SmeltingListener implements Listener {

    INSTANCE;

    /**
     * Spigot 完成事件没有配方，按容器的实际输入槽保存开始事件的配方（包括原版）。
     * 不按预计耗时过期：缺燃料、熄灭和区块卸载均可能令有效输入暂停任意长时间。
     * key 只保存坐标和世界 UUID，不因卸载而强引用 Block/World，也不强制加载区块。
     * 所有访问和失效清理均在服务端主线程进行。
     */
    private final Map<CookPosition, Map<Integer, CookInput>> blockCookRecipeMap = new HashMap<>();
    private boolean cleanupScheduled;

    @EventHandler
    public void cacheFurnaceRecipe(FurnaceStartSmeltEvent event) {
        if (CrypticLibBukkit.isPaper()) {
            //Paper及其下游可以在完成事件里直接取配方
            return;
        }
        putRecipeCache(event.getBlock(), 0, event.getSource(), event.getRecipe(), false);
    }

    @EventHandler
    public void processSmeltResult(FurnaceSmeltEvent event) {
        Recipe recipe;
        if (CrypticLibBukkit.isPaper()) {
            //Paper及其衍生端直接通过事件获取
            recipe = event.getRecipe();
        } else {
            recipe = findRecipe(event.getBlock(), event.getSource(), false);
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
        // CampfireStartEvent 没有槽号；事件发生在服务端向首个空槽放入物品之前。
        if (event.getBlock().getState() instanceof Campfire campfire) {
            for (int slot = 0; slot < campfire.getSize(); slot++) {
                if (isEmpty(campfire.getItem(slot))) {
                    putRecipeCache(event.getBlock(), slot, event.getSource(), event.getRecipe(), true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void processBlockCookResult(BlockCookEvent event) {
        Recipe recipe;
        if (CrypticLibBukkit.isPaper()) {
            //Paper及其衍生端直接通过事件获取
            recipe = event.getRecipe();
        } else {
            recipe = findRecipe(event.getBlock(), event.getSource(), true);
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

    private void putRecipeCache(Block block, int slot, ItemStack source, CookingRecipe<?> recipe, boolean campfire) {
        if (recipe == null || isEmpty(source)) {
            return;
        }
        blockCookRecipeMap.computeIfAbsent(CookPosition.of(block), ignored -> new HashMap<>())
            .put(slot, new CookInput(source.clone(), recipe, campfire));
        scheduleCleanup();
    }

    private Recipe findRecipe(Block block, ItemStack source, boolean campfireResult) {
        CookPosition position = CookPosition.of(block);
        Map<Integer, CookInput> inputs = blockCookRecipeMap.get(position);
        if (inputs == null) {
            return null;
        }
        BlockState state = block.getState();
        removeInvalidInputs(inputs, state);
        if (inputs.isEmpty()) {
            blockCookRecipeMap.remove(position);
            return null;
        }
        // 槽号顺序与服务端营火的 tick 顺序一致；同材料的多个槽不能只按材料匹配。
        for (int slot = 0; slot < 4; slot++) {
            CookInput input = inputs.get(slot);
            if (input == null || input.campfire() != campfireResult || !input.matches(source)) {
                continue;
            }
            if (campfireResult && (!(state instanceof Campfire campfire)
                || campfire.getCookTime(slot) < campfire.getCookTimeTotal(slot))) {
                continue;
            }
            // 完成事件还未消费实际输入，而且可能被取消。保留该槽直到输入真正离开，
            // 这样被取消后再次完成也会校验；绝不删除营火的其他槽或下一份输入。
            return input.recipe();
        }
        return null;
    }

    private void scheduleCleanup() {
        if (cleanupScheduled || blockCookRecipeMap.isEmpty()) {
            return;
        }
        cleanupScheduled = true;
        // 这是有效性检查的间隔，不是配方 TTL；停火/熄灭时仍存在的输入不会被删除。
        CrypticLibBukkit.scheduler().syncLater(() -> {
            cleanupScheduled = false;
            Iterator<Map.Entry<CookPosition, Map<Integer, CookInput>>> iterator = blockCookRecipeMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<CookPosition, Map<Integer, CookInput>> entry = iterator.next();
                CookPosition position = entry.getKey();
                World world = Bukkit.getWorld(position.worldId());
                if (world == null || !world.isChunkLoaded(position.x() >> 4, position.z() >> 4)) {
                    // 卸载会保存输入及进度，重新加载可能不会再次触发 Start，必须保留会话。
                    continue;
                }
                removeInvalidInputs(entry.getValue(), world.getBlockAt(position.x(), position.y(), position.z()).getState());
                if (entry.getValue().isEmpty()) {
                    iterator.remove();
                }
            }
            scheduleCleanup();
        }, 100);
    }

    private void removeInvalidInputs(Map<Integer, CookInput> inputs, BlockState state) {
        inputs.entrySet().removeIf(entry -> {
            CookInput input = entry.getValue();
            if (input.campfire()) {
                return !(state instanceof Campfire campfire) || entry.getKey() >= campfire.getSize()
                    || !input.matches(campfire.getItem(entry.getKey()));
            }
            return !(state instanceof Furnace furnace) || !input.matches(furnace.getInventory().getSmelting());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void invalidateBrokenBlock(BlockBreakEvent event) {
        blockCookRecipeMap.remove(CookPosition.of(event.getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void invalidateBlockExplosion(BlockExplodeEvent event) {
        event.blockList().forEach(block -> blockCookRecipeMap.remove(CookPosition.of(block)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void invalidateEntityExplosion(EntityExplodeEvent event) {
        event.blockList().forEach(block -> blockCookRecipeMap.remove(CookPosition.of(block)));
    }

    private static boolean isEmpty(ItemStack item) {
        return ItemHelper.isAir(item) || item.getAmount() <= 0;
    }

    private record CookPosition(UUID worldId, int x, int y, int z) {
        private static CookPosition of(Block block) {
            return new CookPosition(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
        }
    }

    private record CookInput(ItemStack source, Recipe recipe, boolean campfire) {
        private boolean matches(ItemStack input) {
            return !isEmpty(input) && source.isSimilar(input);
        }
    }

}
