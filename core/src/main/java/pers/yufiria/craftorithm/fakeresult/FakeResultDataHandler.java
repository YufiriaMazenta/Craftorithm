package pers.yufiria.craftorithm.fakeresult;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@LifecycleTaskSettings(
    rules = {@LifecycleRule(lifeCycle = Lifecycle.RELOAD)}
)
public enum FakeResultDataHandler implements Listener, LifecycleTask {

    INSTANCE;

    private final Map<NamespacedKey, ItemStack> fakeResultMap = new ConcurrentHashMap<>();
    private final String FAKE_RESULT_PREVIEW_CONFIG_KEY = "fake_result_preview";

    @EventHandler
    public void onRecipeLoadFromConfig(RecipeLoadFromConfigEvent event) {
        if (event.isCancelled()) {
            return;
        }
        YamlConfiguration recipeConfig = event.recipeConfig();
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
        fakeResultMap.put(event.recipeKey(), itemStack.get());
    }

    public Optional<ItemStack> getRecipeFakeResult(NamespacedKey recipeKey) {
        if (!fakeResultMap.containsKey(recipeKey)) {
            return Optional.empty();
        }
        return Optional.of(fakeResultMap.get(recipeKey).clone());
    }

    public boolean hasFakeResult(NamespacedKey recipeKey) {
        return fakeResultMap.containsKey(recipeKey);
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        this.fakeResultMap.clear();
    }
}
