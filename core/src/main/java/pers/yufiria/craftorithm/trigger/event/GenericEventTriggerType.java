package pers.yufiria.craftorithm.trigger.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerType;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用事件触发器类型
 * 用于非配方相关的 Bukkit 事件
 */
public class GenericEventTriggerType implements TriggerType {

    private final String typeKey;
    private final Class<? extends Event> eventClass;
    private final PlayerExtractor playerExtractor;
    private final Map<String, EventVariableExtractor> variableExtractors;

    public GenericEventTriggerType(
        @NotNull String typeKey,
        @NotNull Class<? extends Event> eventClass,
        @NotNull PlayerExtractor playerExtractor,
        @NotNull Map<String, EventVariableExtractor> variableExtractors
    ) {
        this.typeKey = typeKey;
        this.eventClass = eventClass;
        this.playerExtractor = playerExtractor;
        this.variableExtractors = variableExtractors;
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
    public Listener listener() {
        return null;
    }

    @Override
    public @Nullable TriggerContext extractContext(@NotNull Event event) {
        Player player = playerExtractor.extract(event);
        if (player == null) return null;
        Map<String, ScriptValue> vars = new HashMap<>();
        for (Map.Entry<String, EventVariableExtractor> entry : variableExtractors.entrySet()) {
            ScriptValue val = entry.getValue().extract(event);
            if (val != null) {
                vars.put(entry.getKey(), val);
            }
        }
        return new TriggerContext(player, vars);
    }

    public @NotNull PlayerExtractor playerExtractor() {
        return playerExtractor;
    }

    public @NotNull Map<String, EventVariableExtractor> variableExtractors() {
        return variableExtractors;
    }

}
