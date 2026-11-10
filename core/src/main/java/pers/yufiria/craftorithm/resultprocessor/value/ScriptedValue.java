package pers.yufiria.craftorithm.resultprocessor.value;

import crypticlib.BukkitPlayer;
import crypticlib.CrypticLib;
import crypticlib.Invoker;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.compile.CompiledScript;
import crypticlib.script.object.ReflectPropertyResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.script.RootScriptContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 脚本取值: 加载期编译一次, 每次合成执行一次
 * <p>
 * 用一个标记代替 "只报一次" 的临时状态: 重载后处理器重建, 该标记随之复位
 */
public final class ScriptedValue<T> implements ValueProvider<T> {

    private final CompiledScript script;
    private final ScriptConverter<T> converter;
    private final String description;
    private final AtomicBoolean warned = new AtomicBoolean();

    ScriptedValue(CompiledScript script, ScriptConverter<T> converter, String description) {
        this.script = script;
        this.converter = converter;
        this.description = description;
    }

    @Override
    public @Nullable T resolve(@Nullable Player player, @Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
        try {
            return apply(createContext(player, sourceItem, resultItem));
        } catch (Throwable throwable) {
            if (warned.compareAndSet(false, true)) {
                CrypticLib.warn("Failed to evaluate script value '" + description + "': " + throwable.getMessage());
            }
            return null;
        }
    }

    /**
     * 供单测直接喂上下文, 避开 Bukkit 玩家对象
     */
    @Nullable
    T apply(@NotNull ScriptContext context) {
        return converter.convert(script.execute(context));
    }

    private static ScriptContext createContext(@Nullable Player player, @Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
        Invoker invoker = player != null
            ? BukkitPlayer.byPlayer(player)
            : Craftorithm.instance().getConsoleInvoker();
        ScriptContext context = new ScriptContext(invoker, RootScriptContext.INSTANCE);
        if (player != null) {
            context.setVariable("player", ScriptValue.of(player, ReflectPropertyResolver.INSTANCE));
        }
        if (sourceItem != null) {
            context.setVariable("source_item", ScriptValue.of(sourceItem, ReflectPropertyResolver.INSTANCE));
        }
        context.setVariable("result_item", ScriptValue.of(resultItem, ReflectPropertyResolver.INSTANCE));
        return context;
    }

}
