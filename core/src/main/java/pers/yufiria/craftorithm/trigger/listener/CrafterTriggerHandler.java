package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.listener.EventListener;
import crypticlib.script.ScriptValue;
import crypticlib.script.object.ReflectPropertyResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.CraftingRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.trigger.CraftTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;

import java.util.UUID;

@EventListener
public enum CrafterTriggerHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraft(CrafterCraftEvent event) {
        CraftingRecipe recipe = event.getRecipe();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(event.getRecipe());
        if (!TriggerManager.INSTANCE.hasTrigger(
            CraftTriggerTypes.CRAFTING,
            recipeKey
        )) {
            //如果没有配方对应的触发器，直接返回
            return;
        }
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        TriggerContext ctx = new TriggerContext((UUID) null, recipeKey, recipeType);
        ctx.setVariable("is_crafter", ScriptValue.of(true));
        ctx.setVariable("event", ScriptValue.of(
            event,
            ReflectPropertyResolver.INSTANCE
        ));
        int deniedTriggerNum = TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.CRAFTING, ctx);
        if (deniedTriggerNum > 0) {
            event.setCancelled(true);
            return;
        }
        ctx.setVariable("craft_num", ScriptValue.of(1));
        TriggerManager.INSTANCE.fire(CraftTriggerTypes.CRAFTING, ctx);
    }

}
