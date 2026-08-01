package pers.yufiria.craftorithm.recipe.resultProcessor;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public interface ComponentProcessorFactory {

    String componentName();

    /**
     * 根据策略和数据创建处理器
     *
     * @param strategy 处理策略
     * @param data     配置中的 data 节点，可以为null
     * @return 处理器实例
     */
    ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data);

}
