package pers.yufiria.craftorithm.api;

import crypticlib.CrypticLib;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;

import java.util.Set;

/**
 * 结果处理器的扩展入口。
 * <p>
 * 两种扩展都是"拿到注册器, 再用 {@code behavior(key, 实现)} 声明行为", 写法没有区别:
 * <ul>
 *     <li>{@link #registerComponent(ComponentProcessorFactory)} 新增整个组件,
 *     组件的注册器额外提供 {@code field(...)}</li>
 *     <li>{@link #component(String)} 给已有组件追加行为</li>
 * </ul>
 * 注册时机: 配方加载时就会实例化处理器, 因此扩展内容要在配方加载前注册完成,
 * 建议在 {@code onLoad()} 里注册并用 {@code softdepend} 声明依赖。
 */
public enum ResultProcessorApi {

    INSTANCE;

    public void registerComponent(ComponentProcessorFactory factory) {
        ResultProcessorManager.INSTANCE.registerFactory(factory);
    }

    public void unregisterComponent(String componentName) {
        ResultProcessorManager.INSTANCE.unregisterFactory(componentName);
    }

    public boolean isComponentRegistered(String componentName) {
        return ResultProcessorManager.INSTANCE.isComponentRegistered(componentName);
    }

    /**
     * 取得已有组件的注册器, 用于追加行为; 与组件自身声明同名时优先生效
     * <pre>{@code
     * CraftorithmApi.INSTANCE.resultProcessor()
     *     .component("lore")
     *     .behavior("swap", (context, data) -> ...);
     * }</pre>
     */
    public @NotNull BehaviorRegistrar component(String componentName) {
        if (componentName == null || componentName.isBlank()) {
            throw new IllegalArgumentException("Component name cannot be empty");
        }
        String name = componentName.trim();
        if (!ResultProcessorManager.INSTANCE.isComponentRegistered(name)) {
            CrypticLib.warn("Component '" + name + "' is not registered yet, behaviors registered on it take effect once the component exists");
        }
        return ResultProcessorManager.INSTANCE.externalRegistrar(name);
    }

    public boolean unregisterBehavior(String componentName, String key) {
        return ResultProcessorManager.INSTANCE.unregisterBehavior(componentName, key);
    }

    public Set<String> behaviorsOf(String componentName) {
        return ResultProcessorManager.INSTANCE.behaviorsOf(componentName);
    }

}
