package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftSmithingTransformRecipe;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSmithingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Optional;

public class SmithingTransformRecipe12002 extends SmithingTransformRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTransformRecipe12002(
        NamespacedKey recipeKey,
        RecipeItemStack nmsTemplate,
        RecipeChoice template,
        RecipeItemStack nmsBase,
        RecipeChoice base,
        RecipeItemStack nmsAddition,
        RecipeChoice addition,
        ItemStack result
    ) {
        super(nmsTemplate, nmsBase, nmsAddition, result);
        this.recipeKey = recipeKey;
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.result = result;
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
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(this.result);
            Recipe recipe = new CraftSmithingTransformRecipe(
                recipeKey,
                result,
                template, base, addition
            );
            return recipe;
        });
    }

    public static RecipeHolder<SmithingTransformRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTransformRecipe bukkitRecipe) {
        CraftSmithingTransformRecipe craftRecipe = CraftSmithingTransformRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new SmithingTransformRecipe12002(
                recipeKey,
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), true),
                bukkitRecipe.getTemplate(),
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), true),
                bukkitRecipe.getBase(),
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), true),
                bukkitRecipe.getAddition(),
                nmsResult
            )
        );
    }

}
