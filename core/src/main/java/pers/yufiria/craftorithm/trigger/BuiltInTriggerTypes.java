package pers.yufiria.craftorithm.trigger;

import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingInventory;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipeHandler;
import pers.yufiria.craftorithm.trigger.listener.AnvilTriggerListener;
import pers.yufiria.craftorithm.trigger.listener.CraftTriggerListener;
import pers.yufiria.craftorithm.trigger.listener.SmithingTriggerListener;

/**
 * 内置触发器类型
 */
public enum BuiltInTriggerTypes implements TriggerType {

    CRAFTING("crafting") {
        @Override
        public Class<? extends Event> eventClass() {
            return CraftItemEvent.class;
        }

        @Override
        public Listener listener() {
            return CraftTriggerListener.INSTANCE;
        }

        @Override
        public @Nullable TriggerContext extractContext(Event event) {
            CraftItemEvent e = (CraftItemEvent) event;
            if (!(e.getWhoClicked() instanceof Player player)) return null;
            ItemStack result = e.getCurrentItem();
            if (result == null) return null;
            Recipe recipe = e.getRecipe();
            NamespacedKey recipeKey = recipe != null ? RecipeManager.INSTANCE.getRecipeKey(recipe) : null;
            return TriggerContext.ofCraft(player, result, recipeKey, e.getInventory().getMatrix(), event);
        }

        @Override
        public Class<? extends Event> prepareEventClass() {
            return PrepareItemCraftEvent.class;
        }

        @Override
        public @Nullable TriggerContext extractPrepareContext(Event event) {
            PrepareItemCraftEvent e = (PrepareItemCraftEvent) event;
            if (e.getRecipe() == null) return null;
            if (!(e.getInventory().getHolder() instanceof Player player)) return null;
            ItemStack result = e.getRecipe().getResult();
            NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(e.getRecipe());
            return TriggerContext.ofCraft(player, result, recipeKey, e.getInventory().getMatrix(), event);
        }
    },

    SMITHING("smithing") {
        @Override
        public Class<? extends Event> eventClass() {
            return SmithItemEvent.class;
        }

        @Override
        public Listener listener() {
            return SmithingTriggerListener.INSTANCE;
        }

        @Override
        public @Nullable TriggerContext extractContext(Event event) {
            SmithItemEvent e = (SmithItemEvent) event;
            if (!(e.getWhoClicked() instanceof Player player)) return null;
            ItemStack result = e.getCurrentItem();
            if (result == null) return null;
            SmithingInventory inv = e.getInventory();
            ItemStack base, addition, template = null;
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20)) {
                base = inv.getItem(1);
                addition = inv.getItem(2);
                template = inv.getItem(0);
            } else {
                base = inv.getItem(0);
                addition = inv.getItem(1);
            }
            Recipe recipe = inv.getRecipe();
            NamespacedKey recipeKey = recipe != null ? RecipeManager.INSTANCE.getRecipeKey(recipe) : null;
            return TriggerContext.ofSmithing(player, result, base, addition, template, recipeKey, event);
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
            SmithingInventory inv = e.getInventory();
            ItemStack base, addition, template = null;
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20)) {
                base = inv.getItem(1);
                addition = inv.getItem(2);
                template = inv.getItem(0);
            } else {
                base = inv.getItem(0);
                addition = inv.getItem(1);
            }
            Recipe recipe = inv.getRecipe();
            NamespacedKey recipeKey = recipe != null ? RecipeManager.INSTANCE.getRecipeKey(recipe) : null;
            return TriggerContext.ofSmithing(player, e.getResult(), base, addition, template, recipeKey, event);
        }
    },

    ANVIL("anvil") {
        @Override
        public Class<? extends Event> eventClass() {
            return InventoryClickEvent.class;
        }

        @Override
        public Listener listener() {
            return AnvilTriggerListener.INSTANCE;
        }

        @Override
        public @Nullable TriggerContext extractContext(Event event) {
            InventoryClickEvent e = (InventoryClickEvent) event;
            if (!(e.getWhoClicked() instanceof Player player)) return null;
            if (!(e.getInventory() instanceof AnvilInventory anvilInv)) return null;
            if (e.getSlot() != 2) return null;

            ItemStack base = anvilInv.getItem(0);
            ItemStack addition = anvilInv.getItem(1);
            ItemStack result = anvilInv.getItem(2);
            if (base == null || addition == null || result == null) return null;

            AnvilRecipe customRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition);
            String baseIdStr = resolveItemId(base);
            String additionIdStr = resolveItemId(addition);
            String recipeKeyStr = customRecipe != null ? customRecipe.getKey().toString() : null;

            return TriggerContext.ofAnvil(player, result, baseIdStr, additionIdStr, recipeKeyStr, event);
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
            if (base == null || addition == null) return null;

            Player player = null;
            for (HumanEntity viewer : e.getViewers()) {
                if (viewer instanceof Player p) {
                    player = p;
                    break;
                }
            }
            if (player == null) return null;

            AnvilRecipe customRecipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(base, addition);
            String baseIdStr = resolveItemId(base);
            String additionIdStr = resolveItemId(addition);
            String recipeKeyStr = customRecipe != null ? customRecipe.getKey().toString() : null;
            ItemStack result = e.getResult();

            return TriggerContext.ofAnvil(player, result, baseIdStr, additionIdStr, recipeKeyStr, event);
        }
    };

    private final String key;

    BuiltInTriggerTypes(String key) {
        this.key = key;
    }

    @Override
    public String typeKey() {
        return key;
    }

    public static @Nullable BuiltInTriggerTypes fromKey(String key) {
        for (BuiltInTriggerTypes type : values()) {
            if (type.key.equalsIgnoreCase(key)) return type;
        }
        return null;
    }

    private static @Nullable String resolveItemId(ItemStack item) {
        if (item == null) return null;
        NamespacedItemIdStack id = ItemManager.INSTANCE.matchItemId(item, true);
        return id != null ? id.toString() : item.getType().getKey().toString();
    }

}
