package pers.yufiria.craftorithm.fakeresult;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipeHandler;
import pers.yufiria.craftorithm.util.EventUtils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@LifecycleTaskSettings(
    rules = {@LifecycleRule(lifeCycle = Lifecycle.RELOAD)}
)
public enum FakeResultDataHandler implements Listener, LifecycleTask {

    INSTANCE;

    private final Map<NamespacedKey, ItemStack> recipeFakeResultMap = new ConcurrentHashMap<>();
    private final Map<UUID, CacheRecipeData> playerPreparingRecipe = new ConcurrentHashMap<>();
    private boolean supportFakeResult = false;

    @EventHandler
    public void loadFakeResultConfig(RecipeLoadFromConfigEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!supportFakeResult) {
            return;
        }
        YamlConfiguration recipeConfig = event.recipeConfig();
        String FAKE_RESULT_PREVIEW_CONFIG_KEY = "fake_result_preview";
        if (!recipeConfig.contains(FAKE_RESULT_PREVIEW_CONFIG_KEY)) {
            return;
        }
        if (!recipeConfig.isString(FAKE_RESULT_PREVIEW_CONFIG_KEY)) {
            return;
        }
        String fakeResultIdStr = recipeConfig.getString(FAKE_RESULT_PREVIEW_CONFIG_KEY);
        NamespacedItemIdStack fakeResultId = NamespacedItemIdStack.fromString(fakeResultIdStr);
        if (fakeResultId == null) {
            CrypticLib.info("&eFailed to resolve fake result item from: " + fakeResultIdStr);
            return;
        }
        Optional<ItemStack> itemStack = ItemManager.INSTANCE.matchItem(fakeResultId);
        if (itemStack.isEmpty()) {
            throw new ItemNotFoundException("Can not find item: " + fakeResultId);
        }
        recipeFakeResultMap.put(event.recipeKey(), itemStack.get());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshCraftingRecipeCache(PrepareItemCraftEvent event) {
        if (!supportFakeResult) {
            return;
        }
        UUID playerId = EventUtils.getViewer(event).map(HumanEntity::getUniqueId).orElse(null);
        if (playerId == null) return;
        if (ItemHelper.isAir(event.getInventory().getResult())) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        playerPreparingRecipe.put(playerId, new CacheRecipeData(
            recipeKey,
            0
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshSmithingRecipeCache(PrepareSmithingEvent event) {
        if (!supportFakeResult) {
            return;
        }
        UUID playerId = EventUtils.getViewer(event).map(HumanEntity::getUniqueId).orElse(null);
        if (playerId == null) return;
        if (ItemHelper.isAir(event.getInventory().getResult())) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        playerPreparingRecipe.put(playerId, new CacheRecipeData(
            recipeKey,
            3
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshAnvilCache(PrepareAnvilEvent event) {
        if (!supportFakeResult) {
            return;
        }
        UUID playerId = EventUtils.getViewer(event).map(HumanEntity::getUniqueId).orElse(null);
        if (playerId == null) return;
        if (ItemHelper.isAir(event.getInventory().getResult())) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        AnvilInventory anvilInventory = event.getInventory();
        Recipe recipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(anvilInventory.getItem(0), anvilInventory.getItem(1));
        if (recipe == null) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            //当玩家预览配方为null时，去除缓存
            playerPreparingRecipe.remove(playerId);
            return;
        }
        playerPreparingRecipe.put(playerId, new CacheRecipeData(
            recipeKey,
            2
        ));
    }

    @EventHandler
    public void onPlayerCloseInvClearCache(InventoryCloseEvent event) {
        playerPreparingRecipe.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuitClearCache(PlayerQuitEvent event) {
        playerPreparingRecipe.remove(event.getPlayer().getUniqueId());
    }

    public Optional<ItemStack> getRecipeFakeResult(NamespacedKey recipeKey) {
        if (!recipeFakeResultMap.containsKey(recipeKey)) {
            return Optional.empty();
        }
        return Optional.of(recipeFakeResultMap.get(recipeKey).clone());
    }

    public boolean hasFakeResult(NamespacedKey recipeKey) {
        return recipeFakeResultMap.containsKey(recipeKey);
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        this.recipeFakeResultMap.clear();
    }

    public boolean supportFakeResult() {
        return supportFakeResult;
    }

    public void setSupportFakeResult(boolean supportFakeResult) {
        this.supportFakeResult = supportFakeResult;
    }

    public Optional<CacheRecipeData> getPlayerPreparingRecipe(UUID playerId) {
        return Optional.ofNullable(playerPreparingRecipe.get(playerId));
    }

    public record CacheRecipeData(
        @NotNull NamespacedKey recipeKey,
        int resultSlot
    ) {}

}
