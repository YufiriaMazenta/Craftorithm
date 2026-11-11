package pers.yufiria.craftorithm.resultprocessor.behavior;

/**
 * 组件自身的注册器。
 * <p>
 * 在 {@link BehaviorRegistrar} 之上多一个值类型声明: 字段是组件自身的结构,
 * 只有组件能声明, 第三方拿到的始终是 {@link BehaviorRegistrar}。
 */
public interface ComponentRegistrar extends BehaviorRegistrar {

    /**
     * 声明该组件处理的值类型, 返回一个字段声明器
     *
     * @param type 字段值的类型
     */
    <T> FieldDeclaration<T> valueType(Class<T> type);

}
