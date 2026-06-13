package pers.yufiria.craftorithm.ui.creator;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
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

    //实现标题的翻译功能
    @Override
    public String parsedMenuTitle() {
        String title = this.display.title();
        Player player = this.player();
        title = LangManager.INSTANCE.replaceLang(title, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }

    /**
     * 解析材料物品的ID字符串。
     */
    protected String resolveIngredientId(ItemStack item) {
        NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemIdOrCreate(item, true);
        return itemId != null ? itemId.itemId().toString() : null;
    }
}
