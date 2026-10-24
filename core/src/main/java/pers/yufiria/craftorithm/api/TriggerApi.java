package pers.yufiria.craftorithm.api;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;
import pers.yufiria.craftorithm.trigger.TriggerType;

public enum TriggerApi {

    INSTANCE;

    /**
     * 获取触发器管理器
     *
     * @return 触发器管理器实例
     */
    public @NotNull TriggerManager getTriggerManager() {
        return TriggerManager.INSTANCE;
    }

    /**
     * 触发指定类型的触发器
     *
     * @param triggerType 触发器类型
     * @param context     触发上下文
     * @return 是否有触发器被执行
     */
    public boolean fireTrigger(@NotNull TriggerType triggerType, @NotNull TriggerContext context) {
        return TriggerManager.INSTANCE.fire(triggerType, context);
    }

}
