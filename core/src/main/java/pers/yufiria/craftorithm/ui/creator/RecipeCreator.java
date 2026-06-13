package pers.yufiria.craftorithm.ui.creator;

import crypticlib.config.BukkitConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.util.ItemUtils;

public abstract class RecipeCreator extends StoredMenu {

    protected String recipeName;

    public RecipeCreator(
        @NotNull Player player,
        @NotNull String recipeName
    ) {
        super(player);
        this.recipeName = recipeName;
    }

    protected void toggleIconGlowing(int slot, InventoryClickEvent event) {
        ItemStack display = event.getCurrentItem();
        if (ItemHelper.isAir(display))
            return;
        ItemUtils.toggleItemGlowing(display);
        event.getClickedInventory().setItem(slot, display);
    }

    protected abstract Icon getFrameIcon();

    protected abstract Icon getResultFrameIcon();

    protected BukkitConfigWrapper createRecipeConfig() {
        BukkitConfigWrapper recipeConfigWrapper = new BukkitConfigWrapper(Craftorithm.instance(), "recipes/" + recipeName + ".yml");
        recipeConfigWrapper.saveDefaultConfigFile();
        return recipeConfigWrapper;
    }

    public String recipeName() {
        return recipeName;
    }

    public RecipeCreator setRecipeName(String recipeName) {
        this.recipeName = recipeName;
        return this;
    }

}
