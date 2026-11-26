package pers.yufiria.craftorithm.recipe.worldcondition;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pers.yufiria.craftorithm.api.event.RecipeRegisterEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配方的世界条件
 * <p>
 * 配方注册时把 {@code world_conditions} 解析成 {@link WorldRule}，运行期由各配方类型的匹配逻辑与监听器查询。
 * 查询只做一次表查找和若干字段比较，可以放在配方匹配的热路径上。
 */
@EventListener
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum WorldConditionHandler implements Listener, LifecycleTask {

    INSTANCE;

    public static final String WORLD_CONDITIONS_KEY = "world_conditions";
    /**
     * 旧版本限制世界用的配置项，启动时会被 {@link pers.yufiria.craftorithm.migrator.EnableWorldsMigrator} 转换掉，
     * 运行期再出现即视为配置错误
     */
    public static final String LEGACY_ENABLE_WORLDS_KEY = "enable_worlds";

    private final Map<NamespacedKey, WorldRule> recipeWorldRules = new ConcurrentHashMap<>();

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        recipeWorldRules.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRecipeLoad(RecipeRegisterEvent event) {
        if (event.isCancelled()) {
            return;
        }
        YamlConfiguration recipeConfig = event.recipeConfig();
        if (!recipeConfig.contains(WORLD_CONDITIONS_KEY) && !recipeConfig.contains(LEGACY_ENABLE_WORLDS_KEY)) {
            return;
        }

        WorldRule rule;
        try {
            rule = WorldConditionParser.parse(recipeConfig, event.recipeKey());
        } catch (Throwable throwable) {
            //配置写错时宁可不放行: 否则条件静默失效, 配方会在所有世界都能合成
            CrypticLib.error(
                "Failed to parse world conditions of recipe " + event.recipeKey()
                    + ", the recipe will be unavailable in all worlds",
                throwable
            );
            rule = WorldRule.DenyAll.INSTANCE;
        }
        recipeWorldRules.put(event.recipeKey(), rule);
    }

    public boolean canRecipeUse(NamespacedKey recipeKey, World world) {
        //如果没有相关设置，默认即为允许所有世界合成
        WorldRule rule = recipeWorldRules.get(recipeKey);
        return rule == null || rule.test(world);
    }

}
