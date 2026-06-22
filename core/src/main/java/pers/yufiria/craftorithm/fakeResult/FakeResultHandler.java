package pers.yufiria.craftorithm.fakeResult;

import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.listener.EventListener;
import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@LifeCycleTaskSettings(
    rules = {@TaskRule(lifeCycle = LifeCycle.RELOAD)}
)
public enum FakeResultHandler implements Listener, BukkitLifeCycleTask {

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
            IOHelper.info("&eFailed to resolve fake result item from: " + fakeResultIdStr);
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
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        this.FAKE_RESULT_MAP.clear();
    }
}
