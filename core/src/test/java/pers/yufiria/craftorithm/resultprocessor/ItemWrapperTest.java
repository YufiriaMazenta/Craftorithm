package pers.yufiria.craftorithm.resultprocessor;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 缓存物品的核心约定: meta 只取一次、只有改过才写回、写回由 apply 负责
 */
class ItemWrapperTest {

    @Test
    void metaIsFetchedOnlyOnce() {
        ItemMeta meta = mock(ItemMeta.class);
        ItemStack item = mock(ItemStack.class);
        when(item.getItemMeta()).thenReturn(meta);

        ItemWrapper cached = ItemWrapper.of(item);
        assertSame(meta, cached.meta());
        assertSame(meta, cached.meta());
        verify(item, times(1)).getItemMeta();
    }

    @Test
    void editIsWrittenBackOnlyOnApply() {
        ItemMeta meta = mock(ItemMeta.class);
        ItemStack item = mock(ItemStack.class);
        when(item.getItemMeta()).thenReturn(meta);

        ItemWrapper cached = ItemWrapper.of(item);
        cached.edit(resultMeta -> resultMeta.setDisplayName("demo"));
        verify(item, never()).setItemMeta(any());

        cached.apply();
        verify(item, times(1)).setItemMeta(meta);

        //没再改过, 重复 apply 不会重复写回
        cached.apply();
        verify(item, times(1)).setItemMeta(meta);
    }

    @Test
    void untouchedItemIsNotWrittenBack() {
        ItemStack item = mock(ItemStack.class);
        when(item.getItemMeta()).thenReturn(mock(ItemMeta.class));

        ItemWrapper cached = ItemWrapper.of(item);
        cached.meta();
        cached.apply();
        verify(item, never()).setItemMeta(any());
    }

    @Test
    void setMetaNullClearsOnApply() {
        ItemStack item = mock(ItemStack.class);
        ItemWrapper cached = ItemWrapper.of(item);
        cached.setMeta(null);

        cached.apply();
        verify(item).setItemMeta(null);
    }

    @Test
    void snapshotCarriesPendingEdit() {
        ItemMeta meta = mock(ItemMeta.class);
        ItemMeta cloned = mock(ItemMeta.class);
        when(meta.clone()).thenReturn(cloned);
        ItemStack item = mock(ItemStack.class);
        when(item.getItemMeta()).thenReturn(meta);
        ItemStack copy = mock(ItemStack.class);
        when(item.clone()).thenReturn(copy);

        ItemWrapper cached = ItemWrapper.of(item);
        cached.edit(resultMeta -> resultMeta.setDisplayName("demo"));

        assertSame(copy, cached.snapshot());
        verify(copy).setItemMeta(cloned);
        //快照只是副本, 不会把改动写回原物品
        verify(item, never()).setItemMeta(any());
    }

    @Test
    void readOnlyItemCachesMetaOnce() {
        ItemMeta meta = mock(ItemMeta.class);
        ItemStack item = mock(ItemStack.class);
        when(item.getItemMeta()).thenReturn(meta);

        ItemWrapper cached = ItemWrapper.readOnly(item);
        assertTrue(cached.isReadOnly());
        assertSame(meta, cached.meta());
        assertSame(meta, cached.meta());
        verify(item, times(1)).getItemMeta();
    }

    @Test
    void readOnlyItemRejectsWrites() {
        ItemStack item = mock(ItemStack.class);
        when(item.getItemMeta()).thenReturn(mock(ItemMeta.class));

        ItemWrapper cached = ItemWrapper.readOnly(item);
        assertThrows(UnsupportedOperationException.class, () -> cached.edit(resultMeta -> resultMeta.setDisplayName("demo")));
        assertThrows(UnsupportedOperationException.class, () -> cached.setMeta(null));
        assertThrows(UnsupportedOperationException.class, cached::apply);
        verify(item, never()).setItemMeta(any());
    }

}
