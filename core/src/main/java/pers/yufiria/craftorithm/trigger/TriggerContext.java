package pers.yufiria.craftorithm.trigger;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.ScriptValue;

/**
 * 触发器上下文
 * 包装事件信息，转换为 ScriptContext 供脚本引擎使用
 */
public class TriggerContext {

    private final @NotNull Player player;
    private final @Nullable ItemStack result;
    private final @Nullable NamespacedKey recipeKey;
    private final @Nullable String baseId;
    private final @Nullable String additionId;
    private final @Nullable String templateId;
    private final @Nullable ItemStack[] matrix;
    private final @Nullable Event bukkitEvent;

    private TriggerContext(
        @NotNull Player player,
        @Nullable ItemStack result,
        @Nullable NamespacedKey recipeKey,
        @Nullable String baseId,
        @Nullable String additionId,
        @Nullable String templateId,
        @Nullable ItemStack[] matrix,
        @Nullable Event bukkitEvent
    ) {
        this.player = player;
        this.result = result;
        this.recipeKey = recipeKey;
        this.baseId = baseId;
        this.additionId = additionId;
        this.templateId = templateId;
        this.matrix = matrix;
        this.bukkitEvent = bukkitEvent;
    }

    /**
     * 转换为脚本引擎的 ScriptContext
     * 将事件变量注入为脚本可访问的变量
     */
    public ScriptContext toScriptContext() {
        ScriptContext ctx = new ScriptContext(player, null, recipeKey);

        if (result != null) {
            NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemId(result, true);
            String resultIdStr = resultId != null ? resultId.toString() : result.getType().getKey().toString();
            ctx.setVariable("result", ScriptValue.of(resultIdStr));
        }
        if (baseId != null) {
            ctx.setVariable("base", ScriptValue.of(baseId));
        }
        if (additionId != null) {
            ctx.setVariable("addition", ScriptValue.of(additionId));
        }
        if (templateId != null) {
            ctx.setVariable("template", ScriptValue.of(templateId));
        }
        if (recipeKey != null) {
            ctx.setVariable("recipe", ScriptValue.of(recipeKey.toString()));
        }

        return ctx;
    }

    public @NotNull Player player() {
        return player;
    }

    public @Nullable ItemStack result() {
        return result;
    }

    public @Nullable NamespacedKey recipeKey() {
        return recipeKey;
    }

    public @Nullable String baseId() {
        return baseId;
    }

    public @Nullable String additionId() {
        return additionId;
    }

    public @Nullable String templateId() {
        return templateId;
    }

    /**
     * 获取原始的 Bukkit 事件对象
     */
    public @Nullable Event bukkitEvent() {
        return bukkitEvent;
    }

    // ---- 静态工厂方法 ----

    public static TriggerContext ofCraft(
        @NotNull Player player,
        @Nullable ItemStack result,
        @Nullable NamespacedKey recipeKey,
        @Nullable ItemStack[] matrix,
        @Nullable Event bukkitEvent
    ) {
        return new TriggerContext(player, result, recipeKey, null, null, null, matrix, bukkitEvent);
    }

    public static TriggerContext ofSmithing(
        @NotNull Player player,
        @Nullable ItemStack result,
        @Nullable ItemStack base,
        @Nullable ItemStack addition,
        @Nullable ItemStack template,
        @Nullable NamespacedKey recipeKey,
        @Nullable Event bukkitEvent
    ) {
        String baseStr = resolveItemId(base);
        String additionStr = resolveItemId(addition);
        String templateStr = resolveItemId(template);
        return new TriggerContext(player, result, recipeKey, baseStr, additionStr, templateStr, null, bukkitEvent);
    }

    public static TriggerContext ofAnvil(
        @NotNull Player player,
        @Nullable ItemStack result,
        @Nullable String baseId,
        @Nullable String additionId,
        @Nullable String recipeKeyStr,
        @Nullable Event bukkitEvent
    ) {
        NamespacedKey recipeKey = null;
        if (recipeKeyStr != null) {
            recipeKey = NamespacedKey.fromString(recipeKeyStr);
        }
        return new TriggerContext(player, result, recipeKey, baseId, additionId, null, null, bukkitEvent);
    }

    private static @Nullable String resolveItemId(@Nullable ItemStack item) {
        if (item == null) return null;
        NamespacedItemIdStack id = ItemManager.INSTANCE.matchItemId(item, true);
        return id != null ? id.toString() : item.getType().getKey().toString();
    }

}
