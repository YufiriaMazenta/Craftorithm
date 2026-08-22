package pers.yufiria.craftorithm.resultprocessor;

import crypticlib.CrypticLib;
import crypticlib.listener.EventListener;
import crypticlib.util.BukkitConfigHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.resultprocessor.impl.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
public enum ResultProcessorManager implements Listener {

    INSTANCE;

    private final Map<String, ComponentProcessorFactory> factoryMap = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, ResultProcessors> recipeProcessors = new ConcurrentHashMap<>();
    private final Set<RecipeType> SUPPORT_LEGACY_RECIPE_TYPES = Set.of(
        SimpleRecipeTypes.ANVIL,
        SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM,
        SimpleRecipeTypes.VANILLA_SMITHING_TRIM
    );

    ResultProcessorManager() {
        for (SimpleComponentProcessorFactory factory : SimpleComponentProcessorFactory.values()) {
            if (factory.supportedByCurrentVersion()) {
                registerFactory(factory);
            }
        }
        registerFactory(CustomPersistentDataProcessorFactory.INSTANCE);
        registerFactory(EnchantmentsProcessorFactory.INSTANCE);
        registerFactory(AttributesProcessorFactory.INSTANCE);
        registerFactory(ItemFlagProcessorFactory.INSTANCE);
        registerFactory(LoreProcessorFactory.INSTANCE);
    }

    public void registerFactory(ComponentProcessorFactory factory) {
        factoryMap.put(factory.componentName(), factory);
    }

    public void unregisterFactory(String componentName) {
        factoryMap.remove(componentName);
    }

    @EventHandler
    public void loadFromRecipeConfigWhenLoad(RecipeLoadFromConfigEvent event) {
        NamespacedKey recipeKey = event.recipeKey();
        YamlConfiguration recipeConfig = event.recipeConfig();
        if (recipeConfig.isConfigurationSection("result_processors")) {
            ConfigurationSection section = recipeConfig.getConfigurationSection("result_processors");
            addRecipeProcessors(recipeKey, section);
        } else if (recipeConfig.isList("copy_components_rules")) {
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(event.recipe());
            if (SUPPORT_LEGACY_RECIPE_TYPES.contains(recipeType)) {
                //只允许原本支持的配方加载copy_components_rules
                addRecipeProcessorsLegacy(recipeKey, recipeConfig.getStringList("copy_components_rules"));
            }
        }
    }

    /**
     * 支持单个配置或列表配置:
     *   enchantments: { type: add, data: ... }          # 单个
     *   enchantments:                                    # 列表
     *     - type: copy_from_source
     *     - type: remove
     *       data: { value: ["minecraft:sharpness"] }
     */
    public void addRecipeProcessors(NamespacedKey recipeKey, ConfigurationSection processorsSection) {
        List<ResultProcessor> processors = new ArrayList<>();
        for (String componentName : processorsSection.getKeys(false)) {
            ComponentProcessorFactory factory = factoryMap.get(componentName);
            if (factory == null) {
                CrypticLib.info("&eUnknown component: " + componentName);
                continue;
            }
            if (processorsSection.isList(componentName)) {
                // 列表格式: 同一组件多个处理动作
                List<?> rawList = processorsSection.getList(componentName);
                if (rawList == null) continue;
                for (Object item : rawList) {
                    ConfigurationSection entry = toConfigSection(item);
                    if (entry != null) {
                        parseAndAddProcessor(processors, factory, componentName, entry);
                    }
                }
            } else {
                // 单个配置格式
                ConfigurationSection entry = processorsSection.getConfigurationSection(componentName);
                if (entry == null) {
                    CrypticLib.info("&eInvalid result_processor entry: " + componentName);
                    continue;
                }
                parseAndAddProcessor(processors, factory, componentName, entry);
            }
        }
        recipeProcessors.put(recipeKey, new ResultProcessors(processors));
    }

    private void parseAndAddProcessor(List<ResultProcessor> processors, ComponentProcessorFactory factory, String componentName, ConfigurationSection entry) {
        String type = entry.getString("type");
        if (type == null) {
            CrypticLib.info("&eMissing 'type' for result_processor: " + componentName);
            return;
        }
        ConfigurationSection data = entry.getConfigurationSection("data");
        try {
            processors.add(factory.createProcessor(type, data));
        } catch (UnsupportedOperationException e) {
            CrypticLib.info("&e" + e.getMessage());
        }
    }

    private static ConfigurationSection toConfigSection(Object obj) {
        if (obj instanceof ConfigurationSection section) {
            return section;
        }
        if (obj instanceof Map<?, ?> map) {
            return BukkitConfigHelper.map2ConfigSection(map);
        }
        return null;
    }

    /**
     * 旧格式兼容: copy_components_rules 列表自动转为 copy_from_source
     */
    public void addRecipeProcessorsLegacy(NamespacedKey recipeKey, List<String> ruleStrList) {
        List<ResultProcessor> processors = new ArrayList<>();
        for (String ruleStr : ruleStrList) {
            String ruleName, arg;
            int spaceIndex = ruleStr.indexOf(' ');
            if (spaceIndex == -1) {
                ruleName = ruleStr;
                arg = null;
            } else {
                ruleName = ruleStr.substring(0, spaceIndex);
                arg = ruleStr.substring(spaceIndex + 1);
            }
            ComponentProcessorFactory factory = factoryMap.get(ruleName);
            if (factory == null) {
                CrypticLib.info("&eUnknown rule: " + ruleName);
                continue;
            }
            ConfigurationSection data = parseLegacyArg(arg);
            try {
                processors.add(factory.createProcessor("copy_from_source", data));
            } catch (UnsupportedOperationException e) {
                CrypticLib.info("&e" + e.getMessage());
            }
        }
        recipeProcessors.put(recipeKey, new ResultProcessors(processors));
    }

    /**
     * 解析旧格式的参数字符串，转为 MemorySection
     * 例如: "key=ns:key type=STRING"
     */
    @Nullable
    private static ConfigurationSection parseLegacyArg(@Nullable String arg) {
        if (arg == null || arg.isEmpty()) {
            return null;
        }
        // 用 MemorySection 模拟一个 ConfigurationSection
        MemoryConfiguration memConfig = new MemoryConfiguration();
        String[] parts = arg.split(" ");
        for (String part : parts) {
            int eqIndex = part.indexOf('=');
            if (eqIndex > 0) {
                String key = part.substring(0, eqIndex);
                String value = part.substring(eqIndex + 1);
                memConfig.set(key, value);
            }
        }
        return memConfig.getKeys(false).isEmpty() ? null : memConfig;
    }

    public boolean removeRecipeProcessors(NamespacedKey recipeKey) {
        return recipeProcessors.remove(recipeKey) != null;
    }

    public Optional<ResultProcessors> getRecipeProcessors(NamespacedKey recipeKey) {
        return Optional.ofNullable(recipeProcessors.get(recipeKey));
    }

    public void resetRecipeProcessors() {
        recipeProcessors.clear();
    }

}
