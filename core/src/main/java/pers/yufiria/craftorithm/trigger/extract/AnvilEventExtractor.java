package pers.yufiria.craftorithm.trigger.extract;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipeHandler;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.util.EventUtils;

/**
 * 铁砧触发器的上下文提取
 */
public class AnvilEventExtractor implements CraftEventExtractor {

    @Override
    public Class<? extends Event> eventClass() {
        return InventoryClickEvent.class;
    }

    @Override
    public Class<? extends Event> prepareEventClass() {
        return PrepareAnvilEvent.class;
    }

    @Override
    public @Nullable TriggerContext extract(Event event) {
        if (!(event instanceof InventoryClickEvent e)) return null;
        if (!(e.getWhoClicked() instanceof Player player)) return null;
        if (!(e.getInventory() instanceof AnvilInventory anvilInv)) return null;
        if (e.getSlot() != 2) return null;

        ItemStack base = anvilInv.getItem(0);
        ItemStack addition = anvilInv.getItem(1);
        if (base == null || addition == null) return null;

        AnvilRecipe customRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition, player.getWorld());
        return build(player, event, base, addition, customRecipe);
    }

    @Override
    public @Nullable TriggerContext extractPrepare(Event event) {
        if (!(event instanceof PrepareAnvilEvent e)) return null;
        ItemStack base = e.getInventory().getItem(0);
        ItemStack addition = e.getInventory().getItem(1);
        if (base == null || addition == null) {
            return null;
        }

        return EventUtils.getViewer(e).map(player -> {
            AnvilRecipe customRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition, player.getWorld());
            return build(player, event, base, addition, customRecipe);
        }).orElse(null);
    }

    /**
     * 构建铁砧触发上下文
     * 供事件提取与 {@link pers.yufiria.craftorithm.trigger.listener.AnvilTriggerHandler} 复用
     */
    public static TriggerContext build(
        Player player,
        Event event,
        ItemStack base,
        ItemStack addition,
        @Nullable AnvilRecipe recipe
    ) {
        TriggerContext ctx = new TriggerContext(
            player,
            recipe != null ? recipe.getKey() : null,
            RecipeManager.INSTANCE.getRecipeTypeByKey("anvil")
        );
        ctx.putItem("base", base);
        ctx.putItem("addition", addition);
        ctx.putEvent(event);
        return ctx;
    }

}
