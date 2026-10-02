package pers.yufiria.craftorithm.trigger.extract;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.trigger.TriggerContext;

/**
 * 合成类触发器类型（crafting/smithing/anvil）的事件上下文提取策略
 * 由 {@link pers.yufiria.craftorithm.trigger.CraftTriggerTypes} 持有并委托
 */
public interface CraftEventExtractor {

    /**
     * 实际事件类
     */
    Class<? extends Event> eventClass();

    /**
     * Prepare 事件类
     * 返回 null 表示此类型不支持 Prepare 阶段
     */
    default @Nullable Class<? extends Event> prepareEventClass() {
        return null;
    }

    /**
     * 从实际事件中提取触发上下文
     * 返回 null 表示此事件不应触发
     */
    @Nullable TriggerContext extract(Event event);

    /**
     * 从 Prepare 事件中提取触发上下文
     * 返回 null 表示此事件不应触发
     */
    default @Nullable TriggerContext extractPrepare(Event event) {
        return null;
    }

}
