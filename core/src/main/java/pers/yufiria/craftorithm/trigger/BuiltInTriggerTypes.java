package pers.yufiria.craftorithm.trigger;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
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
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipeHandler;
import pers.yufiria.craftorithm.trigger.listener.CraftTriggerHandler;
import pers.yufiria.craftorithm.trigger.listener.SmithingTriggerHandler;

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
            return CraftTriggerHandler.INSTANCE;
        }

        @Override
        public @Nullable TriggerContext extractContext(Event event) {
            CraftItemEvent e = (CraftItemEvent) event;
            if (!(e.getWhoClicked() instanceof Player player)) return null;
            Recipe recipe = e.getRecipe();
            NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
            return new TriggerContext(player, recipeKey, recipeType);
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
            NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(e.getRecipe());
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(e.getRecipe());
            return new TriggerContext(player, recipeKey, recipeType);
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
            SmithItemEvent e = (SmithItemEvent) event;
            if (!(e.getWhoClicked() instanceof Player player)) return null;
            Recipe recipe = e.getInventory().getRecipe();
            NamespacedKey recipeKey = recipe != null ? RecipeManager.INSTANCE.getRecipeKey(recipe) : null;
            RecipeType recipeType = recipe != null ? RecipeManager.INSTANCE.getRecipeType(recipe) : null;
            return new TriggerContext(player, recipeKey, recipeType);
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
            return new TriggerContext(player, recipeKey, recipeType);
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
            return new TriggerContext(player, recipeKey, recipeType);
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
            NamespacedKey recipeKey = customRecipe != null ? customRecipe.getKey() : null;
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType("anvil");
            return new TriggerContext(player, recipeKey, recipeType);
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

}
