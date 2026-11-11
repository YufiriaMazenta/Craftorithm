package pers.yufiria.craftorithm.resultprocessor.impl;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistry;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 重构前后容易走样的组件行为, 逐条钉住。
 */
class ComponentBehaviorRegressionTest {

    @Test
    void copyAllClonesSourceMetaInsteadOfSharingIt() {
        ItemMeta sourceMeta = mock(ItemMeta.class);
        ItemMeta cloned = mock(ItemMeta.class);
        when(sourceMeta.clone()).thenReturn(cloned);
        ItemStack sourceItem = mock(ItemStack.class);
        when(sourceItem.getItemMeta()).thenReturn(sourceMeta);
        ItemStack resultItem = mock(ItemStack.class);

        ResultProcessor processor = processorOf(
            BaseComponentProcessors.ALL, Behaviors.COPY_FROM_SOURCE
        );
        ItemWrapper resultWrapper = ItemWrapper.of(resultItem);
        processor.processItem(ItemWrapper.readOnly(sourceItem), resultWrapper, null);
        resultWrapper.apply();

        //共用同一份 meta 会让后续组件改到源物品的缓存
        assertNotSame(sourceMeta, resultWrapper.meta());
        verify(resultItem).setItemMeta(cloned);
    }

    @Test
    void removeMaxDamageClearsTheCustomLimit() {
        Damageable resultMeta = mock(Damageable.class);
        ItemStack resultItem = mock(ItemStack.class);
        when(resultItem.getItemMeta()).thenReturn(resultMeta);

        ResultProcessor processor = processorOf(
            ComponentProcessors12005.MAX_DAMAGE, Behaviors.REMOVE
        );
        ItemWrapper resultWrapper = ItemWrapper.of(resultItem);
        processor.processItem(null, resultWrapper, null);
        resultWrapper.apply();

        verify(resultMeta).setMaxDamage(null);
    }

    private static ResultProcessor processorOf(
        pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory factory,
        String behavior
    ) {
        String componentName = factory.componentName();
        BehaviorRegistry registry = new BehaviorRegistry();
        factory.declareBehaviors(registry.registrar(componentName));
        return registry.find(componentName, behavior).orElseThrow()
            .create(registry.context(componentName, behavior), null);
    }

}
