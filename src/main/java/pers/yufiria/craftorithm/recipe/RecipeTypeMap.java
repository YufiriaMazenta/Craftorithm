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
    private final Object[] table = new Object[MAX_ID + 1]; // 存储值的数组（0 ~ 256）
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
            throw new IllegalArgumentException("typeId 必须为 [0, " + MAX_ID + "] 的整数");
        }
    }

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
            return table[typeId] != null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i <= MAX_ID; i++) {
            ReentrantLock lock = locks[i];
            lock.lock();
            try {
                if (Objects.equals(table[i], value)) {
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
            return (V) table[typeId];
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
            V oldValue = (V) table[typeId];
            table[typeId] = value;
            if (oldValue == null && value != null) {
                size.incrementAndGet();
            } else if (oldValue != null && value == null) {
                size.decrementAndGet();
            }
            return oldValue;
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
            V oldValue = (V) table[typeId];
            if (oldValue != null) {
                table[typeId] = null;
                size.decrementAndGet();
            }
            return oldValue;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> other) {
        other.forEach(this::put);
    }

    @Override
    public void clear() {
        for (int i = 0; i <= MAX_ID; i++) {
            ReentrantLock lock = locks[i];
            lock.lock();
            try {
                table[i] = null;
            } finally {
                lock.unlock();
            }
        }
        size.set(0);
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
                return Objects.equals(get(e.getKey()), e.getValue());
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

    // 迭代器实现
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

    private class KeyIterator extends BaseIterator<K> {

        @Override
        @SuppressWarnings("unchecked")
        protected K prepareNext() {
            // 根据 typeId 反推原始键（需要外部保证键的唯一性）
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

    private class ValuesIterator extends BaseIterator<V> {

        @Override
        @SuppressWarnings("unchecked")
        protected V prepareNext() {
            return (V) table[currentIndex];
        }

    }

    private class EntryIterator extends BaseIterator<Entry<K, V>> {

        @Override
        @SuppressWarnings("unchecked")
        protected Entry<K, V> prepareNext() {
            K key = new KeyIterator().prepareNext();
            V value = (V) table[currentIndex];
            return new AbstractMap.SimpleEntry<>(key, value);
        }

    }

}