package pers.yufiria.craftorithm.resultprocessor.behavior;

/**
 * 行为注册器。
 * <p>
 * 组件声明自身行为和第三方给已有组件追加行为都是同一个入口: 拿到注册器, 再用
 * {@link #behavior(String, ProcessorBehavior)} 声明。二者的区别只在于注册器从哪来:
 * <ul>
 *     <li>组件自身: {@code declareBehaviors(ComponentRegistrar)}, 还可以用
 *     {@link ComponentRegistrar#valueType(Class)} 声明字段</li>
 *     <li>第三方: {@code CraftorithmApi.INSTANCE.resultProcessor().component(组件名)}</li>
 * </ul>
 * 同名行为以外部注册的为准, 与注册顺序无关。
 */
public interface BehaviorRegistrar {

    /**
     * 注册一个行为
     *
     * @param key      配置里的 {@code type}, 使用 {@link Behaviors} 里的常量或自定义名字
     * @param behavior 行为实现
     */
    BehaviorRegistrar behavior(String key, ProcessorBehavior behavior);

}
