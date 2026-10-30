package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

import java.util.Locale;
import java.util.UUID;

/**
 * 结果处理器工厂的公共构建工具
 */
final class ProcessorUtils {

    /**
     * 组件未配置 data 时使用的空节点
     */
    static final ConfigurationSection EMPTY_DATA = new MemoryConfiguration();

    private ProcessorUtils() {
    }

    /**
     * 创建普通 processor，不要求 sourceItem 存在
     */
    static ResultProcessor processor(String name, TriConsumer<ItemStack, ItemStack, Player> action) {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return name;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                action.accept(sourceItem, resultItem, player);
            }
        };
    }

    /**
     * 创建需要 sourceItem 不为 null 的 processor。
     * 当 sourceItem 为 null 时（如工作台配方），直接跳过，不抛异常。
     */
    static ResultProcessor processorRequireSource(String name, TriConsumer<ItemStack, ItemStack, Player> action) {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return name;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                if (sourceItem == null) return;
                action.accept(sourceItem, resultItem, player);
            }
        };
    }

    /**
     * 配置有误时使用的空处理器，保证配方本身仍能正常加载
     */
    static ResultProcessor noop(String name) {
        return processor(name, (sourceItem, resultItem, player) -> {
        });
    }

    static ResultProcessor unsupported(String component, String type) {
        throw new UnsupportedOperationException(component + " does not support type: " + type);
    }

    /**
     * 解析颜色，支持 "#RRGGBB" 与 "r,g,b" 两种写法
     */
    static @Nullable Color parseColor(@Nullable String raw) {
        if (raw == null) {
            return null;
        }
        String value = raw.trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            if (value.startsWith("#")) {
                return Color.fromRGB(Integer.parseInt(value.substring(1), 16));
            }
            if (value.contains(",")) {
                String[] parts = value.split(",");
                if (parts.length != 3) {
                    return null;
                }
                int red = Integer.parseInt(parts[0].trim());
                int green = Integer.parseInt(parts[1].trim());
                int blue = Integer.parseInt(parts[2].trim());
                return Color.fromRGB(red, green, blue);
            }
            return Color.fromRGB(Integer.parseInt(value, 16));
        } catch (IllegalArgumentException exception) {
            //包含越界分量或非数字，视为无效配置
            return null;
        }
    }

    /**
     * 解析 NamespacedKey，未写命名空间时补 minecraft
     */
    static @Nullable NamespacedKey parseKey(@Nullable String raw) {
        if (raw == null) {
            return null;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.isEmpty()) {
            return null;
        }
        return NamespacedKey.fromString(value);
    }

    static @Nullable UUID parseUuid(@Nullable String raw) {
        if (raw == null) {
            return null;
        }
        String value = raw.trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    static <T extends Enum<T>> @Nullable T parseEnum(Class<T> type, @Nullable String raw) {
        if (raw == null) {
            return null;
        }
        String value = raw.trim().toUpperCase(Locale.ROOT);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * 从注册表按名字取条目，取不到返回 null
     */
    static <T extends Keyed> @Nullable T lookup(Class<T> type, @Nullable String raw) {
        NamespacedKey key = parseKey(raw);
        if (key == null) {
            return null;
        }
        Registry<T> registry = Bukkit.getRegistry(type);
        if (registry == null) {
            return null;
        }
        return registry.get(key);
    }

}
