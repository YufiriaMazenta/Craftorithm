package pers.yufiria.craftorithm.resultprocessor.behavior;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 行为注册表。
 * <p>
 * 行为按组件分组, 而不是一张全局扁平的 {@code 行为名 -> 实现} 表,
 * 这样可以明确"这个行为只对 damage 生效"。
 * <p>
 * 组件自身声明的行为和外部注册的行为分开存放:
 * 组件重新声明时只清空自己那一份, 外部行为保留, 因此注册顺序不影响结果。
 */
public final class BehaviorRegistry {

    private final Map<String, Map<String, ProcessorBehavior>> declared = new ConcurrentHashMap<>();
    private final Map<String, Map<String, ProcessorBehavior>> external = new ConcurrentHashMap<>();
    private final Map<String, FieldBinding<?>> primaryFields = new ConcurrentHashMap<>();

    /**
     * 组件声明自身行为时使用的注册器
     */
    public ComponentRegistrar registrar(String componentName) {
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
     * 创建行为时的上下文, 标量组件会带上主字段
     */
    public BehaviorContext context(String componentName, String behaviorName) {
        return new Context(componentName, behaviorName, primaryFields.get(componentName));
    }

    /**
     * 注销外部注册的行为, 组件自身的声明不受影响
     */
    public boolean unregister(String componentName, String key) {
        Map<String, ProcessorBehavior> table = external.get(componentName);
        return table != null && table.remove(normalize(key)) != null;
    }

    /**
     * 组件重新声明前调用, 只丢弃自身旧声明
     */
    public void clearDeclared(String componentName) {
        declared.remove(componentName);
        primaryFields.remove(componentName);
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

    private final class Registrar implements ComponentRegistrar {

        private final String componentName;

        private Registrar(String componentName) {
            this.componentName = componentName;
        }

        @Override
        public <T> FieldDeclaration<T> valueType(Class<T> type) {
            return new FieldBuilder<>(componentName, type);
        }

        @Override
        public BehaviorRegistrar behavior(String key, ProcessorBehavior behavior) {
            declaredTable(componentName).put(normalize(key), behavior);
            return this;
        }

    }

    /**
     * 字段的链式声明器。
     * <p>
     * 字段对象在第一次声明行为时才构建: 只声明了 getter/setter 却没有行为的字段不参与注册,
     * 也不会成为组件的主字段。
     */
    private final class FieldBuilder<T> implements FieldDeclaration<T> {

        private final String componentName;
        private final Class<T> valueType;
        private @Nullable Function<ItemMeta, T> getter;
        private @Nullable BiConsumer<ItemMeta, T> setter;
        private Function<ConfigurationSection, ValueProvider<T>> valueReader = data -> null;
        private @Nullable Predicate<ItemMeta> acceptance;
        private @Nullable FieldBinding<T> field;

        private FieldBuilder(String componentName, Class<T> valueType) {
            this.componentName = componentName;
            this.valueType = valueType;
        }

        @Override
        public FieldDeclaration<T> getter(Function<ItemMeta, T> getter) {
            this.getter = getter;
            return this;
        }

        @Override
        public FieldDeclaration<T> setter(BiConsumer<ItemMeta, T> setter) {
            this.setter = setter;
            return this;
        }

        @Override
        public FieldDeclaration<T> valueReader(Function<ConfigurationSection, ValueProvider<T>> valueReader) {
            this.valueReader = valueReader;
            return this;
        }

        @Override
        public FieldDeclaration<T> accepts(Predicate<ItemMeta> acceptance) {
            this.acceptance = acceptance;
            return this;
        }

        @Override
        public FieldDeclaration<T> behavior(String key, FieldBehavior<T> behavior) {
            FieldBinding<T> binding = binding();
            declaredTable(componentName).put(normalize(key), (context, data) -> behavior.create(context, binding, data));
            return this;
        }

        private FieldBinding<T> binding() {
            if (field == null) {
                if (getter == null || setter == null) {
                    throw new IllegalStateException(
                        componentName + ": field of " + valueType.getSimpleName()
                            + " must declare getter and setter before its behaviors"
                    );
                }
                field = new SimpleFieldBinding<>(valueType, getter, setter, valueReader, acceptance);
                //首个字段作为组件的主字段暴露给第三方行为
                primaryFields.putIfAbsent(componentName, field);
            }
            return field;
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

    private static final class Context implements BehaviorContext {

        private final String componentName;
        private final String behaviorName;
        private final @Nullable FieldBinding<?> field;

        private Context(String componentName, String behaviorName, @Nullable FieldBinding<?> field) {
            this.componentName = componentName;
            this.behaviorName = behaviorName;
            this.field = field;
        }

        @Override
        public String componentName() {
            return componentName;
        }

        @Override
        public String behaviorName() {
            return behaviorName;
        }

        @Override
        public Optional<FieldBinding<?>> field() {
            return Optional.ofNullable(field);
        }

    }

}
