package pers.yufiria.craftorithm.trigger;

import crypticlib.script.ScriptValue;
import pers.yufiria.craftorithm.util.EventUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.handler.AnvilRecipeHandler;
import pers.yufiria.craftorithm.trigger.listener.CraftingTriggerHandler;
import pers.yufiria.craftorithm.trigger.listener.SmithingTriggerHandler;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.ItemUtils;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 内置触发器类型
 */
public enum CraftTriggerTypes implements TriggerType {

    CRAFTING("crafting") {
        @Override
        public Class<? extends Event> eventClass() {
            return CraftItemEvent.class;
        }

        @Override
        public Listener listener() {
            return CraftingTriggerHandler.INSTANCE;
        }

        @Override
        public @Nullable TriggerContext extractContext(Event event) {
            if (event instanceof CraftItemEvent craftItemEvent) {
                if (!(craftItemEvent.getWhoClicked() instanceof Player player)) return null;
                Recipe recipe = craftItemEvent.getRecipe();
                NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
                RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
                TriggerContext ctx = new TriggerContext(player.getUniqueId(), recipeKey, recipeType);
                @Nullable ItemStack[] matrix = craftItemEvent.getInventory().getMatrix();
                if (matrix != null) {
                    addIngredientsFromMatrix(ctx, matrix);
                }
                ItemStack result = craftItemEvent.getInventory().getResult();
                ctx.setVariable("craft_num", ScriptValue.of(RecipeUtils.calculateVanillaCraftNum(craftItemEvent.getClick(), matrix, result, player)));
                return ctx;
            }
            return null;
        }

        @Override
        public Class<? extends Event> prepareEventClass() {
            return PrepareItemCraftEvent.class;
        }

        @Override
        public @Nullable TriggerContext extractPrepareContext(Event event) {
            if (event instanceof PrepareItemCraftEvent prepareItemCraftEvent) {
                if (prepareItemCraftEvent.getRecipe() == null) return null;
                Optional<Player> viewer = EventUtils.getViewer(prepareItemCraftEvent);
                if (viewer.isEmpty()) {
                    return null;
                }
                Player player = viewer.get();
                NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(prepareItemCraftEvent.getRecipe());
                RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(prepareItemCraftEvent.getRecipe());
                TriggerContext ctx = new TriggerContext(player.getUniqueId(), recipeKey, recipeType);
                @Nullable ItemStack[] matrix = prepareItemCraftEvent.getInventory().getMatrix();
                if (matrix != null) {
                    addIngredientsFromMatrix(ctx, matrix);
                }
                return ctx;
            }
            return null;
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
                    ItemStack item = row.get(c);
                    if (item == null || item.isEmpty()) continue;
                    String key = "ingredient_" + r + "_" + c;
                    ctx.setVariable(key, ItemUtils.resolveItemId(item));
                    ctx.setVariable(key + "_amount", ItemUtils.resolveItemAmount(item));
                }
            }
        }
    },

    SMITHING("smithing") {
        @Override
        public Class<? extends Event> eventClass() {
            return SmithItemEvent.class;
        }

        @Override
        public Listener listener() {
            return SmithingTriggerHandler.INSTANCE;
        }

        @Override
        public @Nullable TriggerContext extractContext(Event event) {
            SmithItemEvent smithItemEvent = (SmithItemEvent) event;
            if (!(smithItemEvent.getWhoClicked() instanceof Player player)) return null;
            Recipe recipe = smithItemEvent.getInventory().getRecipe();
            NamespacedKey recipeKey = recipe != null ? RecipeManager.INSTANCE.getRecipeKey(recipe) : null;
            RecipeType recipeType = recipe != null ? RecipeManager.INSTANCE.getRecipeType(recipe) : null;
            TriggerContext ctx = new TriggerContext(player, recipeKey, recipeType);
            ItemStack templateItem = smithItemEvent.getInventory().getItem(0);
            ItemStack baseItem = smithItemEvent.getInventory().getItem(1);
            ItemStack additionItem = smithItemEvent.getInventory().getItem(2);
            addSlotVariable(ctx, "template", templateItem);
            addSlotVariable(ctx, "base", baseItem);
            addSlotVariable(ctx, "addition", additionItem);
            ItemStack result = smithItemEvent.getInventory().getResult();
            ItemStack[] matrix = {
                templateItem,
                baseItem,
                additionItem
            };
            ctx.setVariable("craft_num", ScriptValue.of(RecipeUtils.calculateVanillaCraftNum(smithItemEvent.getClick(), matrix, result, player)));
            return ctx;
        }

        @Override
        public Class<? extends Event> prepareEventClass() {
            return PrepareSmithingEvent.class;
        }

        @Override
        public @Nullable TriggerContext extractPrepareContext(Event event) {
            PrepareSmithingEvent e = (PrepareSmithingEvent) event;
            if (e.getResult() == null) return null;
            if (!(e.getInventory().getHolder() instanceof Player player)) return null;
            Recipe recipe = e.getInventory().getRecipe();
            NamespacedKey recipeKey = recipe != null ? RecipeManager.INSTANCE.getRecipeKey(recipe) : null;
            RecipeType recipeType = recipe != null ? RecipeManager.INSTANCE.getRecipeType(recipe) : null;
            TriggerContext ctx = new TriggerContext(player, recipeKey, recipeType);
            addSlotVariable(ctx, "template", e.getInventory().getItem(0));
            addSlotVariable(ctx, "base", e.getInventory().getItem(1));
            addSlotVariable(ctx, "addition", e.getInventory().getItem(2));
            return ctx;
        }
    },

    ANVIL("anvil") {
        @Override
        public Class<? extends Event> eventClass() {
            return InventoryClickEvent.class;
        }

        @Override
        public Listener listener() {
            return AnvilRecipeHandler.INSTANCE;
        }

        @Override
        public @Nullable TriggerContext extractContext(Event event) {
            InventoryClickEvent e = (InventoryClickEvent) event;
            if (!(e.getWhoClicked() instanceof Player player)) return null;
            if (!(e.getInventory() instanceof AnvilInventory anvilInv)) return null;
            if (e.getSlot() != 2) return null;

            ItemStack base = anvilInv.getItem(0);
            ItemStack addition = anvilInv.getItem(1);
            if (base == null || addition == null) return null;

            AnvilRecipe customRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition);
            NamespacedKey recipeKey = customRecipe != null ? customRecipe.getKey() : null;
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType("anvil");
            TriggerContext ctx = new TriggerContext(player, recipeKey, recipeType);
            addSlotVariable(ctx, "base", base);
            addSlotVariable(ctx, "addition", addition);
            return ctx;
        }

        @Override
        public Class<? extends Event> prepareEventClass() {
            return PrepareAnvilEvent.class;
        }

        @Override
        public @Nullable TriggerContext extractPrepareContext(Event event) {
            PrepareAnvilEvent e = (PrepareAnvilEvent) event;
            ItemStack base = e.getInventory().getItem(0);
            ItemStack addition = e.getInventory().getItem(1);
            if (base == null || addition == null) {
                return null;
            }

            return EventUtils.getViewer(e).map(player -> {
                AnvilRecipe customRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition);
                NamespacedKey recipeKey = customRecipe != null ? customRecipe.getKey() : null;
                RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType("anvil");
                TriggerContext ctx = new TriggerContext(player, recipeKey, recipeType);
                addSlotVariable(ctx, "base", base);
                addSlotVariable(ctx, "addition", addition);
                return ctx;
            }).orElse(null);
        }
    };

    private final String key;

    CraftTriggerTypes(String key) {
        this.key = key;
    }

    @Override
    public String typeKey() {
        return key;
    }

    public static @Nullable CraftTriggerTypes fromKey(String key) {
        for (CraftTriggerTypes type : values()) {
            if (type.key.equalsIgnoreCase(key)) return type;
        }
        return null;
    }

    private static void addSlotVariable(TriggerContext ctx, String slotName, ItemStack item) {
        if (item == null || item.isEmpty()) return;
        ctx.setVariable(slotName, ItemUtils.resolveItemId(item));
        ctx.setVariable(slotName + "_amount", ItemUtils.resolveItemAmount(item));
    }

}
