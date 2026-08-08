package pers.yufiria.craftorithm.recipe.fakeResult;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.listener.EventListener;
import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@LifecycleTaskSettings(
    rules = {@LifecycleRule(lifeCycle = Lifecycle.RELOAD)}
)
public enum FakeResultDataHandler implements Listener, LifecycleTask {

    INSTANCE;

    private final Map<NamespacedKey, NamespacedItemIdStack> FAKE_RESULT_MAP = new ConcurrentHashMap<>();
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
        FAKE_RESULT_MAP.put(event.recipeKey(), fakeResultId);
    }

    public @Nullable NamespacedItemIdStack getRecipeFakeResult(NamespacedKey recipeKey) {
        return FAKE_RESULT_MAP.get(recipeKey);
    }

    public boolean hasFakeResult(NamespacedKey recipeKey) {
        return FAKE_RESULT_MAP.containsKey(recipeKey);
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        this.FAKE_RESULT_MAP.clear();
    }
}
