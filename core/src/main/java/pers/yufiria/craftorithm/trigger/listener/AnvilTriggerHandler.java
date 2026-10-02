package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.script.ScriptValue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.trigger.CraftTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;
import pers.yufiria.craftorithm.trigger.extract.AnvilEventExtractor;

/**
 * 铁砧触发器处理器
 * 事件监听由 {@link pers.yufiria.craftorithm.recipe.anvil.AnvilRecipeHandler} 保留，本类只负责触发器逻辑
 */
public enum AnvilTriggerHandler {

    INSTANCE;

    /**
     * 构建触发上下文
     * 该配方无触发器时返回 null
     */
    public @Nullable TriggerContext context(Event event, Player player, ItemStack base, ItemStack addition, AnvilRecipe recipe) {
        if (!TriggerManager.INSTANCE.hasTrigger(CraftTriggerTypes.ANVIL, recipe.getKey())) return null;
        return AnvilEventExtractor.build(player, event, base, addition, recipe);
    }

    /**
     * 条件预检，true = 触发器拒绝本次合成
     */
    public boolean deny(TriggerContext ctx) {
        return TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.ANVIL, ctx) > 0;
    }

    /**
     * Prepare 阶段检查，true = 触发器拒绝（不产出结果）
     */
    public boolean denyPrepare(Event event, Player player, ItemStack base, ItemStack addition, AnvilRecipe recipe) {
        if (!TriggerManager.INSTANCE.hasTrigger(CraftTriggerTypes.ANVIL, recipe.getKey())) {
            //没有配方对应的触发器，直接放行
            return false;
        }
        TriggerContext ctx = AnvilEventExtractor.build(player, event, base, addition, recipe);
        return TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.ANVIL, ctx) > 0;
    }

    /**
     * 合成成功后执行 actions
     */
    public void fire(TriggerContext ctx, int craftNum) {
        ctx.setVariable("craft_num", ScriptValue.of(craftNum));
        TriggerManager.INSTANCE.fire(CraftTriggerTypes.ANVIL, ctx);
    }

}
