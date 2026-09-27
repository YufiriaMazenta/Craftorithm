package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftSmithingTrimRecipe;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSmithingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Optional;

public class SmithingTrimRecipe12003 extends SmithingTrimRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTrimRecipe12003(
        NamespacedKey recipeKey,
        RecipeItemStack nmsTemplate,
        RecipeChoice template,
        RecipeItemStack nmsBase,
        RecipeChoice base,
        RecipeItemStack nmsAddition,
        RecipeChoice addition
    ) {
        super(nmsTemplate, nmsBase, nmsAddition);
        this.recipeKey = recipeKey;
        this.template = template;
        this.addition = addition;
        this.base = base;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public boolean a(ItemStack itemstack) {
        return template.test(CraftItemStack.asCraftMirror(itemstack));
    }

    @Override
    public boolean b(ItemStack itemstack) {
        return base.test(CraftItemStack.asCraftMirror(itemstack));
    }

    @Override
    public boolean c(ItemStack itemstack) {
        return addition.test(CraftItemStack.asCraftMirror(itemstack));
    }

    @Override
    public Optional<RecipeChoice> templateChoice() {
        return Optional.ofNullable(template);
    }

    @Override
    public Optional<RecipeChoice> baseChoice() {
        return Optional.ofNullable(base);
    }

    @Override
    public Optional<RecipeChoice> additionChoice() {
        return Optional.ofNullable(addition);
    }

    @Override
    public boolean a(IInventory smithingInput, World world) {
        return matchesSmithing(
            CraftItemStack.asCraftMirror(smithingInput.a(0)),
            CraftItemStack.asCraftMirror(smithingInput.a(1)),
            CraftItemStack.asCraftMirror(smithingInput.a(2)),
            world.getWorld()
        );
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey recipeKey) {
        return cachedBukkitRecipe.get(() -> {
            Recipe recipe = new CraftSmithingTrimRecipe(
                recipeKey,
                template, base, addition
            );
            return recipe;
        });
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new SmithingTrimRecipe12003(
                recipeKey,
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), true),
                bukkitRecipe.getTemplate(),
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), true),
                bukkitRecipe.getBase(),
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), true),
                bukkitRecipe.getAddition()
            )
        );
    }

}
