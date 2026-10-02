package pers.yufiria.craftorithm.trigger.event;

import crypticlib.script.ScriptValue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 通用事件触发器类型
 * 用于非配方相关的 Bukkit 事件
 */
public class GenericEventTriggerType implements TriggerType {

    private final String typeKey;
    private final Class<? extends Event> eventClass;
    private final Function<Event, TriggerContext> triggerContextCreator;

    public GenericEventTriggerType(
        @NotNull String typeKey,
        @NotNull Class<? extends Event> eventClass,
        @NotNull Function<Event, TriggerContext> triggerContextCreator
    ) {
        this.typeKey = typeKey;
        this.eventClass = eventClass;
        this.triggerContextCreator = triggerContextCreator;
    }

    public GenericEventTriggerType(
        @NotNull String typeKey,
        @NotNull Class<? extends Event> eventClass,
        @NotNull PlayerExtractor playerExtractor,
        @NotNull Map<String, EventVariableExtractor> variableExtractors
    ) {
        this(typeKey, eventClass, event -> {
            Player player = playerExtractor.extract(event);
            Map<String, ScriptValue> vars = new HashMap<>();
            for (Map.Entry<String, EventVariableExtractor> entry : variableExtractors.entrySet()) {
                ScriptValue val = entry.getValue().extract(event);
                if (val != null) {
                    vars.put(entry.getKey(), val);
                }
            }
            return new TriggerContext(player, vars);
        });
    }

    @Override
    public String typeKey() {
        return typeKey;
    }

    @Override
    public Class<? extends Event> eventClass() {
        return eventClass;
    }

    @Override
    public @Nullable TriggerContext extractContext(@NotNull Event event) {
        TriggerContext ctx = triggerContextCreator.apply(event);
        if (ctx != null) {
            //统一注入标准 event 变量
            ctx.putEvent(event);
        }
        return ctx;
    }

}
