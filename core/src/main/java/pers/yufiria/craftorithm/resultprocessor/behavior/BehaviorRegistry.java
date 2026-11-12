package pers.yufiria.craftorithm.resultprocessor.behavior;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 行为注册表。
 * <p>
 * 行为按组件分组存放, 用来表达"这个行为只对 damage 生效"。
 * <p>
 * 组件自身声明的行为与外部注册的行为分开保存: 组件重新声明时只清空它自己那一份,
 * 外部行为保留, 因此注册顺序不影响结果。
 */
public final class BehaviorRegistry {

    private final Map<String, Map<String, ProcessorBehavior>> declared = new ConcurrentHashMap<>();
    private final Map<String, Map<String, ProcessorBehavior>> external = new ConcurrentHashMap<>();

    /**
     * 组件声明自身行为时使用的注册器
     */
    public BehaviorRegistrar registrar(String componentName) {
        return new Registrar(componentName);
    }

    /**
     * 第三方给已有组件追加行为时使用的注册器, 与组件自身声明同名时优先生效
     */
    public BehaviorRegistrar externalRegistrar(String componentName) {
        return new ExternalRegistrar(componentName);
    }

    public Optional<ProcessorBehavior> find(String componentName, @Nullable String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        String normalized = normalize(key);
        ProcessorBehavior behavior = external.getOrDefault(componentName, Map.of()).get(normalized);
        if (behavior == null) {
            behavior = declared.getOrDefault(componentName, Map.of()).get(normalized);
        }
        return Optional.ofNullable(behavior);
    }

    public Set<String> behaviorsOf(String componentName) {
        Set<String> keys = new LinkedHashSet<>(declared.getOrDefault(componentName, Map.of()).keySet());
        keys.addAll(external.getOrDefault(componentName, Map.of()).keySet());
        return Collections.unmodifiableSet(keys);
    }

    /**
     * 创建行为时的上下文
     */
    public BehaviorContext context(String componentName, String behaviorName) {
        return new Context(componentName, behaviorName);
    }

    /**
     * 注销外部注册的行为, 组件自身的声明不受影响
     */
    public boolean unregister(String componentName, String key) {
        Map<String, ProcessorBehavior> table = external.get(componentName);
        return table != null && table.remove(normalize(key)) != null;
    }

    /**
     * 组件重新声明前调用, 只丢弃它自己注册的行为
     */
    public void clearDeclared(String componentName) {
        declared.remove(componentName);
    }

    /**
     * 组件注销时调用, 连同外部注册的行为一并丢弃
     */
    public void clearAll(String componentName) {
        clearDeclared(componentName);
        external.remove(componentName);
    }

    private static String normalize(String key) {
        return key.trim();
    }

    private Map<String, ProcessorBehavior> declaredTable(String componentName) {
        return declared.computeIfAbsent(componentName, key -> new ConcurrentHashMap<>());
    }

    private Map<String, ProcessorBehavior> externalTable(String componentName) {
        return external.computeIfAbsent(componentName, key -> new ConcurrentHashMap<>());
    }

    private final class Registrar implements BehaviorRegistrar {

        private final String componentName;

        private Registrar(String componentName) {
            this.componentName = componentName;
        }

        @Override
        public BehaviorRegistrar behavior(String key, ProcessorBehavior behavior) {
            declaredTable(componentName).put(normalize(key), behavior);
            return this;
        }

    }

    private final class ExternalRegistrar implements BehaviorRegistrar {

        private final String componentName;

        private ExternalRegistrar(String componentName) {
            this.componentName = componentName;
        }

        @Override
        public BehaviorRegistrar behavior(String key, ProcessorBehavior behavior) {
            externalTable(componentName).put(normalize(key), behavior);
            return this;
        }

    }

    private record Context(String componentName, String behaviorName) implements BehaviorContext {
    }

}
