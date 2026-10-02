package pers.yufiria.craftorithm.trigger.extract;

import crypticlib.script.ScriptValue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.EventUtils;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 工作台合成触发器的上下文提取
 */
public class CraftingEventExtractor implements CraftEventExtractor {

    @Override
    public Class<? extends Event> eventClass() {
        return CraftItemEvent.class;
    }

    @Override
    public Class<? extends Event> prepareEventClass() {
        return PrepareItemCraftEvent.class;
    }

    @Override
    public @Nullable TriggerContext extract(Event event) {
        if (!(event instanceof CraftItemEvent craftItemEvent)) return null;
        if (!(craftItemEvent.getWhoClicked() instanceof Player player)) return null;
        TriggerContext ctx = build(player, craftItemEvent.getRecipe(), event, craftItemEvent.getInventory().getMatrix());
        ctx.setVariable("craft_num", ScriptValue.of(RecipeUtils.calculateVanillaCraftNum(craftItemEvent)));
        return ctx;
    }

    @Override
    public @Nullable TriggerContext extractPrepare(Event event) {
        if (!(event instanceof PrepareItemCraftEvent prepareItemCraftEvent)) return null;
        if (prepareItemCraftEvent.getRecipe() == null) return null;
        Optional<Player> viewer = EventUtils.getViewer(prepareItemCraftEvent);
        if (viewer.isEmpty()) {
            return null;
        }
        return build(
            viewer.get(),
            prepareItemCraftEvent.getRecipe(),
            event,
            prepareItemCraftEvent.getInventory().getMatrix()
        );
    }

    private static TriggerContext build(Player player, Recipe recipe, Event event, ItemStack[] matrix) {
        TriggerContext ctx = new TriggerContext(
            player,
            RecipeManager.INSTANCE.getRecipeKey(recipe),
            RecipeManager.INSTANCE.getRecipeType(recipe)
        );
        if (matrix.length > 0) {
            addIngredientsFromMatrix(ctx, matrix);
        }
        ctx.setVariable("is_crafter", ScriptValue.of(false));
        ctx.putEvent(event);
        return ctx;
    }

    private static void addIngredientsFromMatrix(TriggerContext ctx, ItemStack[] matrix) {
        int cols = (int) Math.sqrt(matrix.length);
        List<List<ItemStack>> grid = new ArrayList<>();
        for (int r = 0; r < cols; r++) {
            List<ItemStack> row = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                row.add(matrix[r * cols + c]);
            }
            grid.add(row);
        }
        CollectionsUtils.trimEmptyBorders(grid, item -> item == null || item.isEmpty());
        for (int r = 0; r < grid.size(); r++) {
            List<ItemStack> row = grid.get(r);
            for (int c = 0; c < row.size(); c++) {
                ctx.putItem("ingredient_" + r + "_" + c, row.get(c));
            }
        }
    }

}
