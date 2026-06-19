package pers.yufiria.craftorithm.trigger;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/**
 * 触发器类型接口
 * 内置类型由 {@link BuiltInTriggerTypes} 枚举实现
 * 外部插件可实现此接口以注册自定义触发器类型
 */
public interface TriggerType {

    /**
     * 类型标识，对应YAML中的type字段
     */
    String typeKey();

    /**
     * 返回此类型使用的监听器实例
     */
    Listener listener();

    /**
     * 实际事件类（CraftItemEvent / SmithItemEvent / InventoryClickEvent 等）
     */
    Class<? extends Event> eventClass();

    /**
     * 从实际事件中提取触发上下文
     */
    @Nullable TriggerContext extractContext(Event event);

    /**
     * Prepare 事件类（PrepareItemCraftEvent / PrepareSmithingEvent / PrepareAnvilEvent 等）
     * 返回 null 表示此类型不支持 Prepare 阶段
     */
    @Nullable Class<? extends Event> prepareEventClass();

    /**
     * 从 Prepare 事件中提取触发上下文
     * 返回 null 表示此事件不应触发
     */
    @Nullable TriggerContext extractPrepareContext(Event event);

}
