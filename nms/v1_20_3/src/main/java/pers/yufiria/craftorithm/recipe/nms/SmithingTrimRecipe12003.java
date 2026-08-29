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
import pers.yufiria.craftorithm.util.IngredientUtils;

public class SmithingTrimRecipe12003 extends SmithingTrimRecipe {

    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private volatile Recipe cachedBukkitRecipe;

    SmithingTrimRecipe12003(
        RecipeItemStack nmsTemplate,
        RecipeChoice template,
        RecipeItemStack nmsBase,
        RecipeChoice base,
        RecipeItemStack nmsAddition,
        RecipeChoice addition
    ) {
        super(nmsTemplate, nmsBase, nmsAddition);
        this.template = template;
        this.addition = addition;
        this.base = base;
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
    public boolean a(IInventory smithingInput, World world) {
        return template.test(CraftItemStack.asCraftMirror(smithingInput.a(0)))
            && base.test(CraftItemStack.asCraftMirror(smithingInput.a(1)))
            && addition.test(CraftItemStack.asCraftMirror(smithingInput.a(2)));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey recipeKey) {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        Recipe recipe = new CraftSmithingTrimRecipe(
            recipeKey,
            template, base, addition
        );
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new SmithingTrimRecipe12003(
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
