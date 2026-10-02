package pers.yufiria.craftorithm.trigger;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.trigger.extract.AnvilEventExtractor;
import pers.yufiria.craftorithm.trigger.extract.CraftEventExtractor;
import pers.yufiria.craftorithm.trigger.extract.CraftingEventExtractor;
import pers.yufiria.craftorithm.trigger.extract.SmithingEventExtractor;

/**
 * 内置合成类触发器类型
 * 上下文提取逻辑委托给 {@link CraftEventExtractor} 实现
 */
public enum CraftTriggerTypes implements TriggerType {

    CRAFTING("crafting", new CraftingEventExtractor()),
    SMITHING("smithing", new SmithingEventExtractor()),
    ANVIL("anvil", new AnvilEventExtractor());

    private final String key;
    private final CraftEventExtractor extractor;

    CraftTriggerTypes(String key, CraftEventExtractor extractor) {
        this.key = key;
        this.extractor = extractor;
    }

    @Override
    public String typeKey() {
        return key;
    }

    @Override
    public Class<? extends Event> eventClass() {
        return extractor.eventClass();
    }

    @Override
    public @Nullable Class<? extends Event> prepareEventClass() {
        return extractor.prepareEventClass();
    }

    @Override
    public @Nullable TriggerContext extractContext(Event event) {
        return extractor.extract(event);
    }

    @Override
    public @Nullable TriggerContext extractPrepareContext(Event event) {
        return extractor.extractPrepare(event);
    }

}
