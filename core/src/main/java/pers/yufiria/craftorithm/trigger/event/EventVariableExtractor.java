package pers.yufiria.craftorithm.trigger.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.script.ScriptValue;

/**
 * 从 Bukkit 事件中提取脚本变量的策略接口
 */
@FunctionalInterface
public interface EventVariableExtractor {

    @Nullable ScriptValue extract(@NotNull Event event);

}
