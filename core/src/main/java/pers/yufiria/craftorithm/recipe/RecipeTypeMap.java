package pers.yufiria.craftorithm.recipe;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 专门用于存储RecipeType相关的map, 使用{@link RecipeType#typeId()}作为识别方式, 比HashMap性能更好且线程安全
 */
public class RecipeTypeMap<K extends RecipeType, V> implements Map<K, V> {

    private static final int MAX_ID = 256;
    private static final Object NULL_VALUE = new Object();

    // 存储键对象 (类型为 K)
    private final Object[] keys = new Object[MAX_ID + 1];
    // 存储值对象 (实际值或 NULL_VALUE)
    private final Object[] table = new Object[MAX_ID + 1];
    private final ReentrantLock[] locks = new ReentrantLock[MAX_ID + 1];
    private final AtomicInteger size = new AtomicInteger(0);

    public RecipeTypeMap() {
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    private void validateId(int typeId) {
        if (typeId < 0 || typeId > MAX_ID) {
            throw new IllegalArgumentException("typeId must in [0, " + MAX_ID + "]");
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
            return keys[typeId] != null;
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        int typeId = key.typeId();
        validateId(typeId);
        ReentrantLock lock = locks[typeId];
        lock.lock();
        try {
            Object oldValue = table[typeId];
            Object newValue = (value == null) ? NULL_VALUE : value;
            if (keys[typeId] == null) {          // 原本不存在该键
                size.incrementAndGet();
            }
            keys[typeId] = key;                  // 存储原始键对象
            table[typeId] = newValue;
            return (oldValue == NULL_VALUE) ? null : (V) oldValue;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        if (!(key instanceof RecipeType)) return null;
        int typeId = ((RecipeType) key).typeId();
        if (typeId < 0 || typeId > MAX_ID) return null;
        ReentrantLock lock = locks[typeId];
        lock.lock();
        try {
            Object oldValue = table[typeId];
            if (keys[typeId] != null) {
                keys[typeId] = null;
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
                if (keys[i] != null) {
                    keys[i] = null;
                    table[i] = null;
                    size.decrementAndGet();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    // ==================== 视图集合 ====================

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

    // ==================== 迭代器实现 ====================

    /** 键迭代器：基于 keys 数组，返回原始键对象 */
    private class KeyIterator implements Iterator<K> {
        private int cursor = 0;      // 下一个要检查的位置
        private int nextIndex = -1;  // 找到的下一个有效索引

        @Override
        public boolean hasNext() {
            if (nextIndex != -1) return true;
            for (int i = cursor; i <= MAX_ID; i++) {
                ReentrantLock lock = locks[i];
                lock.lock();
                try {
                    if (keys[i] != null) {
                        nextIndex = i;
                        cursor = i + 1;
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K next() {
            if (!hasNext()) throw new NoSuchElementException();
            K result = (K) keys[nextIndex];
            nextIndex = -1;
            return result;
        }

        @Override
        public void remove() {
            if (nextIndex == -1) throw new IllegalStateException();
            RecipeTypeMap.this.remove(keys[nextIndex]);
            nextIndex = -1;
        }
    }

    /** 值迭代器：基于 table 数组，正确处理 NULL_VALUE */
    private class ValuesIterator implements Iterator<V> {
        private int cursor = 0;
        private int nextIndex = -1;

        @Override
        public boolean hasNext() {
            if (nextIndex != -1) return true;
            for (int i = cursor; i <= MAX_ID; i++) {
                ReentrantLock lock = locks[i];
                lock.lock();
                try {
                    if (table[i] != null) {
                        nextIndex = i;
                        cursor = i + 1;
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V next() {
            if (!hasNext()) throw new NoSuchElementException();
            Object value = table[nextIndex];
            V result = (value == NULL_VALUE) ? null : (V) value;
            nextIndex = -1;
            return result;
        }

        @Override
        public void remove() {
            if (nextIndex == -1) throw new IllegalStateException();
            RecipeTypeMap.this.remove(keys[nextIndex]); // 通过键删除
            nextIndex = -1;
        }
    }

    /** 条目迭代器：基于 keys 和 table，返回原始键和对应值 */
    private class EntryIterator implements Iterator<Entry<K, V>> {
        private int cursor = 0;
        private int nextIndex = -1;

        @Override
        public boolean hasNext() {
            if (nextIndex != -1) return true;
            for (int i = cursor; i <= MAX_ID; i++) {
                ReentrantLock lock = locks[i];
                lock.lock();
                try {
                    if (keys[i] != null) {   // 键存在即视为有效条目
                        nextIndex = i;
                        cursor = i + 1;
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Entry<K, V> next() {
            if (!hasNext()) throw new NoSuchElementException();
            K key = (K) keys[nextIndex];
            Object value = table[nextIndex];
            V actualValue = (value == NULL_VALUE) ? null : (V) value;
            Entry<K, V> entry = new AbstractMap.SimpleImmutableEntry<>(key, actualValue);
            nextIndex = -1;
            return entry;
        }

        @Override
        public void remove() {
            if (nextIndex == -1) throw new IllegalStateException();
            RecipeTypeMap.this.remove(keys[nextIndex]);
            nextIndex = -1;
        }
    }
}