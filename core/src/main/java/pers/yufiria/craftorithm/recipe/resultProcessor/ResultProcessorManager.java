package pers.yufiria.craftorithm.recipe.resultProcessor;

import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.impl.AttributesProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.impl.CustomPersistentDataProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.impl.EnchantmentsProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.impl.ItemFlagProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.impl.LoreProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.impl.SimpleComponentProcessorFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum ResultProcessorManager {

    INSTANCE;

    private final Map<String, ComponentProcessorFactory> factoryMap = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, ResultProcessors> recipeProcessors = new ConcurrentHashMap<>();

    ResultProcessorManager() {
        for (SimpleComponentProcessorFactory factory : SimpleComponentProcessorFactory.values()) {
            if (factory.supportedByCurrentVersion()) {
                registerFactory(factory);
            }
        }
        registerFactory(new CustomPersistentDataProcessorFactory());
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

    /**
     * 解析新格式 result_processors
     */
    public void addRecipeProcessors(NamespacedKey recipeKey, ConfigurationSection processorsSection) {
        List<ResultProcessor> processors = new ArrayList<>();
        for (String componentName : processorsSection.getKeys(false)) {
            ConfigurationSection entry = processorsSection.getConfigurationSection(componentName);
            if (entry == null) {
                IOHelper.info("&eInvalid result_processor entry: " + componentName);
                continue;
            }
            String strategyStr = entry.getString("type");
            if (strategyStr == null) {
                IOHelper.info("&eMissing 'type' for result_processor: " + componentName);
                continue;
            }
            ProcessingStrategy strategy;
            try {
                strategy = ProcessingStrategy.fromString(strategyStr);
            } catch (IllegalArgumentException e) {
                IOHelper.info("&eUnknown processing strategy '" + strategyStr + "' for result_processor: " + componentName);
                continue;
            }
            ConfigurationSection data = entry.getConfigurationSection("data");
            ComponentProcessorFactory factory = factoryMap.get(componentName);
            if (factory == null) {
                IOHelper.info("&eUnknown component: " + componentName);
                continue;
            }
            processors.add(factory.createProcessor(strategy, data));
        }
        recipeProcessors.put(recipeKey, new ResultProcessors(processors));
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
                IOHelper.info("&eUnknown rule: " + ruleName);
                continue;
            }
            ConfigurationSection data = parseLegacyArg(arg);
            processors.add(factory.createProcessor(ProcessingStrategy.COPY_FROM_SOURCE, data));
        }
        recipeProcessors.put(recipeKey, new ResultProcessors(processors));
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
        org.bukkit.configuration.MemoryConfiguration memConfig = new org.bukkit.configuration.MemoryConfiguration();
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

}
