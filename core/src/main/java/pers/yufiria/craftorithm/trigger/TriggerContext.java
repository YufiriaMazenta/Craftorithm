package pers.yufiria.craftorithm.trigger;

import crypticlib.CommonPlayer;
import crypticlib.Invoker;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.object.ReflectPropertyResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.script.RootScriptContext;
import pers.yufiria.craftorithm.util.ItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 触发器上下文
 * 包装事件信息，转换为 ScriptContext 供脚本引擎使用
 */
public class TriggerContext {

    private final @NotNull UUID invokerId;
    private final @Nullable NamespacedKey recipeKey;
    private final @Nullable RecipeType recipeType;
    private final Map<String, ScriptValue> variables;

    public TriggerContext(
        @Nullable Player player,
        @Nullable NamespacedKey recipeKey,
        @Nullable RecipeType recipeType,
        @Nullable Map<String, ScriptValue> variables
    ) {
        this.invokerId = player != null ? player.getUniqueId() : Invoker.CONSOLE_UUID;
        this.recipeKey = recipeKey;
        this.recipeType = recipeType;
        this.variables = variables != null ? new HashMap<>(variables) : new HashMap<>();
    }

    public TriggerContext(@Nullable Player player, @Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType) {
        this(player, recipeKey, recipeType, null);
    }

    /**
     * 控制台触发的上下文（无玩家场景，如发射器合成）
     */
    public TriggerContext(@Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType) {
        this(null, recipeKey, recipeType, null);
    }

    public TriggerContext(@Nullable Player player, @NotNull Map<String, ScriptValue> variables) {
        this(player, null, null, variables);
    }

    public TriggerContext(@Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType, @NotNull Map<String, ScriptValue> variables) {
        this(null, recipeKey, recipeType, variables);
    }

    public void setVariable(@NotNull String name, @NotNull ScriptValue value) {
        variables.put(name, value);
    }

    /**
     * 注入标准 event 变量（脚本可通过反射访问事件对象）
     */
    public void putEvent(@NotNull Event event) {
        variables.put("event", ScriptValue.of(event, ReflectPropertyResolver.INSTANCE));
    }

    /**
     * 注入物品变量：name = 物品id，name_amount = 数量
     * 空物品不注入
     */
    public void putItem(@NotNull String name, @Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return;
        variables.put(name, ItemUtils.resolveItemId(item));
        variables.put(name + "_amount", ItemUtils.resolveItemAmount(item));
    }

    /**
     * 转换为脚本引擎的 ScriptContext
     * 将事件变量注入为脚本可访问的变量
     */
    public ScriptContext toScriptContext() {
        Invoker invoker;
        if (!Invoker.CONSOLE_UUID.equals(invokerId)) {
            invoker = CommonPlayer.fromUuid(invokerId).orElse(null);
            if (invoker == null) {
                invoker = Craftorithm.instance().getConsoleInvoker();
            }
        } else {
            invoker = Craftorithm.instance().getConsoleInvoker();
        }
        ScriptContext ctx = new ScriptContext(invoker, RootScriptContext.INSTANCE);

        if (recipeKey != null) {
            ctx.setVariable("recipe", ScriptValue.of(recipeKey.toString()));
        }
        if (recipeType != null) {
            ctx.setVariable("recipe_type", ScriptValue.of(recipeType.typeKey()));
        }
        for (Map.Entry<String, ScriptValue> entry : variables.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        return ctx;
    }

    /**
     * 触发者UUID，无玩家场景返回 {@link Invoker#CONSOLE_UUID}
     */
    public @NotNull UUID playerUniqueId() {
        return invokerId;
    }

    public @Nullable NamespacedKey recipeKey() {
        return recipeKey;
    }

    public @Nullable RecipeType recipeType() {
        return recipeType;
    }

    public @NotNull Map<String, ScriptValue> variables() {
        return variables;
    }

}
