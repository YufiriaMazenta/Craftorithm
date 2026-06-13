package pers.yufiria.craftorithm.recipe;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 专门用于存储RecipeType相关的map, 使用{@link RecipeType#typeId()}作为识别方式, 比HashMap性能更好且线程安全
 */
public class RecipeTypeMap<K extends RecipeType, V> implements Map<K, V> {

    private static final int MAX_ID = 256;
    private static final Object NULL_VALUE = new Object(); // 特殊标记，用于表示用户存储的 null

    private final Object[] table = new Object[MAX_ID + 1]; // 存储实际值或 NULL_VALUE
    private final ReentrantLock[] locks = new ReentrantLock[MAX_ID + 1];
    private final AtomicInteger size = new AtomicInteger(0);

    public RecipeTypeMap() {
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    // 校验 typeId 范围
    private void validateId(int typeId) {
        if (typeId < 0 || typeId > MAX_ID) {
            throw new IllegalArgumentException("typeId must in [0, " + MAX_ID + "]");
        }
    }

    // 核心方法实现
    @Override
    public int size() {
        return size.get();
    }

    @Override
    public boolean isEmpty() {
        return size.get() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof RecipeType)) return false;
        int typeId = ((RecipeType) key).typeId();
        if (typeId < 0 || typeId > MAX_ID) return false;

        ReentrantLock lock = locks[typeId];
        lock.lock();
        try {
            return table[typeId] != null; // 只要不为 null，键即存在
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        Object target = (value == null) ? NULL_VALUE : value;
        for (int i = 0; i <= MAX_ID; i++) {
            ReentrantLock lock = locks[i];
            lock.lock();
            try {
                if (Objects.equals(table[i], target)) {
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (!(key instanceof RecipeType)) return null;
        int typeId = ((RecipeType) key).typeId();
        if (typeId < 0 || typeId > MAX_ID) return null;

        ReentrantLock lock = locks[typeId];
        lock.lock();
        try {
            Object value = table[typeId];
            return (value == NULL_VALUE) ? null : (V) value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        int typeId = key.typeId();
        validateId(typeId);

        ReentrantLock lock = locks[typeId];
        lock.lock();
        try {
            Object oldValue = table[typeId];
            Object newValue = (value == null) ? NULL_VALUE : value;

            // 如果oldValue为null,说明原本没有值,更新 size
            if (oldValue == null) {
                size.incrementAndGet(); // 新增键
            }
            // 其他情况 size都 不变（如覆盖旧值）

            table[typeId] = newValue;
            return (oldValue == NULL_VALUE) ? null : (V) oldValue;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof RecipeType)) return null;
        int typeId = ((RecipeType) key).typeId();
        if (typeId < 0 || typeId > MAX_ID) return null;

        ReentrantLock lock = locks[typeId];
        lock.lock();
        try {
            Object oldValue = table[typeId];
            if (oldValue != null) {
                table[typeId] = null;
                size.decrementAndGet();
            }
            return (oldValue == NULL_VALUE) ? null : (V) oldValue;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        for (int i = 0; i <= MAX_ID; i++) {
            ReentrantLock lock = locks[i];
            lock.lock();
            try {
                if (table[i] != null) {
                    table[i] = null;
                    size.decrementAndGet();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    // 视图集合实现
    @Override
    public Set<K> keySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<K> iterator() {
                return new KeyIterator();
            }

            @Override
            public int size() {
                return RecipeTypeMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                return containsKey(o);
            }

            @Override
            public boolean remove(Object o) {
                return RecipeTypeMap.this.remove(o) != null;
            }

            @Override
            public void clear() {
                RecipeTypeMap.this.clear();
            }
        };
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<>() {
            @Override
            public Iterator<V> iterator() {
                return new ValuesIterator();
            }

            @Override
            public int size() {
                return RecipeTypeMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                return containsValue(o);
            }

            @Override
            public void clear() {
                RecipeTypeMap.this.clear();
            }
        };
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return RecipeTypeMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Entry)) return false;
                Entry<?, ?> e = (Entry<?, ?>) o;
                Object value = get(e.getKey());
                return Objects.equals(value, e.getValue());
            }

            @Override
            public boolean remove(Object o) {
                if (o instanceof Entry) {
                    Entry<?, ?> e = (Entry<?, ?>) o;
                    Object value = get(e.getKey());
                    if (Objects.equals(value, e.getValue())) {
                        RecipeTypeMap.this.remove(e.getKey());
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void clear() {
                RecipeTypeMap.this.clear();
            }
        };
    }

    // 迭代器基类
    private abstract class BaseIterator<T> implements Iterator<T> {
        protected int currentIndex = 0;
        protected T nextElement = null;

        @Override
        public boolean hasNext() {
            while (currentIndex <= MAX_ID) {
                ReentrantLock lock = locks[currentIndex];
                lock.lock();
                try {
                    if (table[currentIndex] != null) {
                        nextElement = prepareNext();
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
                currentIndex++;
            }
            return false;
        }

        protected abstract T prepareNext();

        @Override
        public T next() {
            if (nextElement == null && !hasNext()) {
                throw new NoSuchElementException();
            }
            T element = nextElement;
            nextElement = null;
            currentIndex++;
            return element;
        }
    }

    // 键迭代器
    private class KeyIterator extends BaseIterator<K> {
        @Override
        @SuppressWarnings("unchecked")
        protected K prepareNext() {
            return (K) new RecipeType() {
                @Override
                public @NotNull String typeKey() {
                    return "dynamic_key_" + currentIndex;
                }

                @Override
                public @Range(from = 0, to = 256) int typeId() {
                    return currentIndex;
                }

                @Override
                public @NotNull RecipeLoader<?> recipeLoader() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public @NotNull RecipeRegister recipeRegister() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean isThisType(Recipe recipe) {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    // 值迭代器
    private class ValuesIterator extends BaseIterator<V> {
        @Override
        @SuppressWarnings("unchecked")
        protected V prepareNext() {
            Object value = table[currentIndex];
            return (value == NULL_VALUE) ? null : (V) value;
        }
    }

    // 条目迭代器
    private class EntryIterator extends BaseIterator<Entry<K, V>> {
        @Override
        @SuppressWarnings("unchecked")
        protected Entry<K, V> prepareNext() {
            K key = new KeyIterator().prepareNext();
            Object value = table[currentIndex];
            V actualValue = (value == NULL_VALUE) ? null : (V) value;
            return new AbstractMap.SimpleEntry<>(key, actualValue);
        }
    }

}