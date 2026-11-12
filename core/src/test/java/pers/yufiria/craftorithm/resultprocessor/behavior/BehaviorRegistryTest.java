package pers.yufiria.craftorithm.resultprocessor.behavior;

import crypticlib.CrypticLib;
import crypticlib.log.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 验证扩展点本身: 组件声明内置行为、第三方追加行为,
 * 全部不依赖 Craftorithm 的内部枚举或管理器单例。
 */
class BehaviorRegistryTest {

    @BeforeAll
    static void installLogger() {
        CrypticLib.setLogger(mock(Logger.class));
    }

    private final AtomicReference<Integer> stored = new AtomicReference<>();

    private BiConsumer<ItemMeta, Integer> setter() {
        return (meta, value) -> stored.set(value);
    }

    private BehaviorRegistry registryWithDemo() {
        BehaviorRegistry registry = new BehaviorRegistry();
        registry.registrar("demo")
            .behavior(Behaviors.COPY_FROM_SOURCE, ScalarBehaviors.copyFromSource(
                meta -> stored.get(), setter()))
            .behavior(Behaviors.ADD, ScalarBehaviors.add(
                setter(), data -> ValueProviders.integer(data, "value", 0, "demo/value")))
            .behavior(Behaviors.REMOVE, ScalarBehaviors.remove(setter()));
        return registry;
    }

    /**
     * 物品带一份可读写的 meta, 否则缓存拿到 null 就什么都不写
     */
    private ItemStack resultItem() {
        ItemStack item = mock(ItemStack.class);
        when(item.getItemMeta()).thenReturn(mock(ItemMeta.class));
        return item;
    }

    @Test
    void componentDeclaresOnlyItsOwnActions() {
        BehaviorRegistry registry = registryWithDemo();
        assertEquals(
            Set.of(Behaviors.COPY_FROM_SOURCE, Behaviors.ADD, Behaviors.REMOVE),
            registry.behaviorsOf("demo")
        );
        assertTrue(registry.find("demo", Behaviors.SET).isEmpty());
        assertTrue(registry.find("unknown", Behaviors.ADD).isEmpty());
        assertTrue(registry.find("demo", "  ").isEmpty());
    }

    @Test
    void addWritesConfiguredValue() {
        BehaviorRegistry registry = registryWithDemo();
        ConfigurationSection data = new MemoryConfiguration();
        data.set("value", 7);

        ResultProcessor processor = registry.find("demo", Behaviors.ADD).orElseThrow()
            .create(registry.context("demo", Behaviors.ADD), data);
        assertNotNull(processor);
        assertEquals("demo/add", processor.processorName());

        ItemStack result = resultItem();
        ItemWrapper cached = ItemWrapper.of(result);
        processor.processItem(null, cached, null);
        cached.apply();

        assertEquals(7, stored.get().intValue());
        verify(result).setItemMeta(any(ItemMeta.class));
    }

    /**
     * 处理期间只改缓存, 由调用方在链尾统一写回
     */
    @Test
    void valueIsWrittenOnlyOnApply() {
        BehaviorRegistry registry = registryWithDemo();
        ConfigurationSection data = new MemoryConfiguration();
        data.set("value", 7);
        ResultProcessor processor = registry.find("demo", Behaviors.ADD).orElseThrow()
            .create(registry.context("demo", Behaviors.ADD), data);

        ItemStack result = resultItem();
        ItemWrapper cached = ItemWrapper.of(result);
        processor.processItem(null, cached, null);
        assertEquals(7, stored.get().intValue());
        verify(result, never()).setItemMeta(any());

        cached.apply();
        verify(result).setItemMeta(any(ItemMeta.class));
    }

    @Test
    void copyFromSourceSkipsWhenSourceIsAbsent() {
        BehaviorRegistry registry = registryWithDemo();
        ResultProcessor processor = registry.find("demo", Behaviors.COPY_FROM_SOURCE).orElseThrow()
            .create(registry.context("demo", Behaviors.COPY_FROM_SOURCE), null);

        stored.set(123);
        processor.processItem(null, ItemWrapper.of(resultItem()), null);
        assertEquals(123, stored.get().intValue());

        stored.set(42);
        processor.processItem(ItemWrapper.readOnly(resultItem()), ItemWrapper.of(resultItem()), null);
        assertEquals(42, stored.get().intValue());
    }

    @Test
    void removeClearsField() {
        BehaviorRegistry registry = registryWithDemo();
        ResultProcessor processor = registry.find("demo", Behaviors.REMOVE).orElseThrow()
            .create(registry.context("demo", Behaviors.REMOVE), null);

        stored.set(9);
        processor.processItem(null, ItemWrapper.of(resultItem()), null);
        assertNull(stored.get());
    }

    @Test
    void thirdPartyBehaviorCanBeRegisteredOnExistingComponent() {
        BehaviorRegistry registry = registryWithDemo();
        registry.externalRegistrar("demo").behavior("double", (context, data) ->
            context.processor((sourceItem, resultItem, player) ->
                stored.set(stored.get() == null ? 1 : stored.get() * 2)));

        ResultProcessor processor = registry.find("demo", "double").orElseThrow()
            .create(registry.context("demo", "double"), null);
        stored.set(5);
        processor.processItem(null, ItemWrapper.of(resultItem()), null);

        assertEquals(10, stored.get().intValue());
        assertTrue(registry.unregister("demo", "double"));
        assertTrue(registry.find("demo", "double").isEmpty());
        assertEquals(3, registry.behaviorsOf("demo").size());
    }

    @Test
    void behaviorKeyIsTrimmed() {
        BehaviorRegistry registry = registryWithDemo();
        assertTrue(registry.find("demo", " add ").isPresent());
    }

    @Test
    void addRequiredSkipsWhenValueIsMissing() {
        BehaviorRegistry registry = new BehaviorRegistry();
        registry.registrar("demo")
            .behavior(Behaviors.ADD, ScalarBehaviors.addRequired(
                setter(),
                data -> ValueProviders.integer(data, "value", 0, "demo/value"),
                (value, player) -> value));

        ResultProcessor processor = registry.find("demo", Behaviors.ADD).orElseThrow()
            .create(registry.context("demo", Behaviors.ADD), new MemoryConfiguration());
        assertNotNull(processor);

        stored.set(5);
        processor.processItem(null, ItemWrapper.of(resultItem()), null);
        assertEquals(5, stored.get().intValue());
    }

    @Test
    void addRequiredWritesWhenValueIsPresent() {
        BehaviorRegistry registry = new BehaviorRegistry();
        registry.registrar("demo")
            .behavior(Behaviors.ADD, ScalarBehaviors.addRequired(
                setter(),
                data -> ValueProviders.integer(data, "value", 0, "demo/value"),
                (value, player) -> value));

        ConfigurationSection data = new MemoryConfiguration();
        data.set("value", 8);
        ResultProcessor processor = registry.find("demo", Behaviors.ADD).orElseThrow()
            .create(registry.context("demo", Behaviors.ADD), data);

        stored.set(5);
        processor.processItem(null, ItemWrapper.of(resultItem()), null);
        assertEquals(8, stored.get().intValue());
    }

    @Test
    void externalBehaviorSurvivesComponentRedeclaration() {
        BehaviorRegistry registry = registryWithDemo();
        registry.externalRegistrar("demo").behavior("double", (context, data) ->
            context.processor((sourceItem, resultItem, player) ->
                stored.set(stored.get() == null ? 1 : stored.get() * 2)));

        //组件重新注册: 只丢弃自身声明
        registry.clearDeclared("demo");
        registry.registrar("demo")
            .behavior(Behaviors.ADD, ScalarBehaviors.add(
                setter(), data -> ValueProviders.integer(data, "value", 0, "demo/value")));

        ResultProcessor processor = registry.find("demo", "double").orElseThrow()
            .create(registry.context("demo", "double"), null);
        stored.set(5);
        processor.processItem(null, ItemWrapper.of(resultItem()), null);
        assertEquals(10, stored.get().intValue());
    }

    @Test
    void externalBehaviorOverridesDeclaredOneUntilUnregistered() {
        BehaviorRegistry registry = registryWithDemo();
        registry.externalRegistrar("demo").behavior(Behaviors.ADD, (context, data) ->
            context.processor((sourceItem, resultItem, player) -> stored.set(99)));

        ConfigurationSection data = new MemoryConfiguration();
        data.set("value", 7);

        ResultProcessor external = registry.find("demo", Behaviors.ADD).orElseThrow()
            .create(registry.context("demo", Behaviors.ADD), data);
        stored.set(1);
        external.processItem(null, ItemWrapper.of(resultItem()), null);
        assertEquals(99, stored.get().intValue());

        assertTrue(registry.unregister("demo", Behaviors.ADD));

        ResultProcessor declared = registry.find("demo", Behaviors.ADD).orElseThrow()
            .create(registry.context("demo", Behaviors.ADD), data);
        declared.processItem(null, ItemWrapper.of(resultItem()), null);
        assertEquals(7, stored.get().intValue());
    }

    @Test
    void unregisterNeverRemovesDeclaredBehaviors() {
        BehaviorRegistry registry = registryWithDemo();
        assertFalse(registry.unregister("demo", Behaviors.ADD));
        assertTrue(registry.find("demo", Behaviors.ADD).isPresent());
    }

}
