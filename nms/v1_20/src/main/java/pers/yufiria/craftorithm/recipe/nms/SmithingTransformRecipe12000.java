package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftSmithingTransformRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class SmithingTransformRecipe12000 extends SmithingTransformRecipe {

    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final ItemStack result;
    private volatile Recipe cachedBukkitRecipe;

    SmithingTransformRecipe12000(
        MinecraftKey recipeKey,
        RecipeItemStack nmsTemplate,
        RecipeChoice template,
        RecipeItemStack nmsBase,
        RecipeChoice base,
        RecipeItemStack nmsAddition,
        RecipeChoice addition,
        ItemStack result
    ) {
        super(recipeKey, nmsTemplate, nmsBase, nmsAddition, result);
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.result = result;
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
    public Recipe toBukkitRecipe() {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(this.result);
        CraftSmithingTransformRecipe recipe = new CraftSmithingTransformRecipe(
            CraftNamespacedKey.fromMinecraft(this.e()),
            result,
            template, base, addition
        );
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static SmithingTransformRecipe fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTransformRecipe bukkitRecipe) {
        CraftSmithingTransformRecipe craftRecipe = CraftSmithingTransformRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        return new SmithingTransformRecipe12000(
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), true),
            bukkitRecipe.getTemplate(),
            craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), true),
            bukkitRecipe.getBase(),
            craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), true),
            bukkitRecipe.getAddition(),
            nmsResult
        );
    }

}
