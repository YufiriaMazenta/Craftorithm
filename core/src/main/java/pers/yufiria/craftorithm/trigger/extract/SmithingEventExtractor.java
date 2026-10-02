package pers.yufiria.craftorithm.trigger.extract;

import crypticlib.script.ScriptValue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingInventory;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.util.RecipeUtils;

/**
 * 锻造台触发器的上下文提取
 */
public class SmithingEventExtractor implements CraftEventExtractor {

    @Override
    public Class<? extends Event> eventClass() {
        return SmithItemEvent.class;
    }

    @Override
    public Class<? extends Event> prepareEventClass() {
        return PrepareSmithingEvent.class;
    }

    @Override
    public @Nullable TriggerContext extract(Event event) {
        if (!(event instanceof SmithItemEvent smithItemEvent)) return null;
        if (!(smithItemEvent.getWhoClicked() instanceof Player player)) return null;
        TriggerContext ctx = build(player, event, smithItemEvent.getInventory());
        ctx.setVariable("craft_num", ScriptValue.of(RecipeUtils.calculateVanillaCraftNum(smithItemEvent)));
        return ctx;
    }

    @Override
    public @Nullable TriggerContext extractPrepare(Event event) {
        if (!(event instanceof PrepareSmithingEvent prepareSmithingEvent)) return null;
        if (prepareSmithingEvent.getResult() == null) return null;
        if (!(prepareSmithingEvent.getInventory().getHolder() instanceof Player player)) return null;
        return build(player, event, prepareSmithingEvent.getInventory());
    }

    private static TriggerContext build(Player player, Event event, SmithingInventory inventory) {
        Recipe recipe = inventory.getRecipe();
        TriggerContext ctx = new TriggerContext(
            player,
            recipe != null ? RecipeManager.INSTANCE.getRecipeKey(recipe) : null,
            recipe != null ? RecipeManager.INSTANCE.getRecipeType(recipe) : null
        );
        ctx.putItem("template", inventory.getItem(0));
        ctx.putItem("base", inventory.getItem(1));
        ctx.putItem("addition", inventory.getItem(2));
        ctx.putEvent(event);
        return ctx;
    }

}
