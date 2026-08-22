package pers.yufiria.craftorithm.resultprocessor;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public interface ComponentProcessorFactory {

    String componentName();

    /**
     * 根据类型和数据创建处理器
     *
     * @param type 处理器类型
     * @param data 配置中的 data 节点，可以为null
     * @return 处理器实例
     */
    ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data);

}
