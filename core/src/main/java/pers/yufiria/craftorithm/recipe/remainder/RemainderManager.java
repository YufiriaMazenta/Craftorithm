package pers.yufiria.craftorithm.recipe.remainder;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.listener.EventListener;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.util.BukkitConfigHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum RemainderManager implements Listener, LifecycleTask {

    INSTANCE;

    public static final String REMAINDERS_CONFIG_KEY = "remainders";

    /**
     * 支持remainders的配方类型
     * <p>
     * 原版只有合成配方在取出产物时会逐槽应用合成剩余物, 其余配方类型的输入消耗是写死的, 因此不在此列
     */
    private static final List<RecipeType> SUPPORTED_RECIPE_TYPES = List.of(
        SimpleRecipeTypes.VANILLA_SHAPED,
        SimpleRecipeTypes.VANILLA_SHAPELESS
    );

    private final Map<NamespacedKey, List<RemainderRule>> recipeRemainders = new ConcurrentHashMap<>();

    /**
     * 判断某个配方类型是否支持remainders
     */
    public static boolean supportsRemainders(@NotNull RecipeType recipeType) {
        return SUPPORTED_RECIPE_TYPES.contains(recipeType);
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        recipeRemainders.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRecipeLoad(RecipeLoadFromConfigEvent event) {
        if (event.isCancelled()) {
            return;
        }
        recipeRemainders.remove(event.recipeKey());

        YamlConfiguration recipeConfig = event.recipeConfig();
        if (!recipeConfig.contains(REMAINDERS_CONFIG_KEY)) {
            return;
        }

        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(event.recipe());
        if (!supportsRemainders(recipeType)) {
            CrypticLib.warn(
                "Recipe " + event.recipeKey() + " (" + recipeType.typeKey() + ") has a '" + REMAINDERS_CONFIG_KEY + "' section, " +
                    "but this recipe type does not support remainders, the section will be ignored. " +
                    "Supported types: " + String.join(", ", SUPPORTED_RECIPE_TYPES.stream().map(RecipeType::typeKey).toList())
            );
            return;
        }

        if (!recipeConfig.isList(REMAINDERS_CONFIG_KEY)) {
            CrypticLib.warn(REMAINDERS_CONFIG_KEY + " must be a list in recipe: " + event.recipeKey());
            return;
        }

        List<RemainderRule> rules = new ArrayList<>();
        for (Map<?, ?> entry : recipeConfig.getMapList(REMAINDERS_CONFIG_KEY)) {
            ConfigurationSection section = BukkitConfigHelper.map2ConfigSection(entry);
            RemainderRule rule = RemainderRule.fromConfig(section, event.recipeKey());
            if (rule != null) {
                rules.add(rule);
            }
        }
        if (!rules.isEmpty()) {
            recipeRemainders.put(event.recipeKey(), List.copyOf(rules));
        }
    }

    public @NotNull List<RemainderRule> getRules(@NotNull NamespacedKey recipeKey) {
        return recipeRemainders.getOrDefault(recipeKey, List.of());
    }

}
