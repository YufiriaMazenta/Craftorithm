package pers.yufiria.craftorithm.ui.editor;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.io.File;
import java.util.List;

/**
 * 配方编辑器基类
 * 继承StoredMenu，与RecipeCreator类似的模式，但用于编辑已有配方。
 * 子类在构造时调用fillRecipeData()将现有配方数据预填充到GUI槽位。
 */
public abstract class RecipeEditorMenu extends StoredMenu implements BackableMenu {

    protected Menu parentMenu;
    protected final NamespacedKey recipeKey;
    protected final String recipeId;
    /**
     * 标记该编辑页面是否初始化,用于首次打开时填充配方内容
     */
    private boolean initialized = false;

    public RecipeEditorMenu(
        @NotNull Player player,
        @NotNull NamespacedKey recipeKey,
        @NotNull String recipeId
    ) {
        super(player);
        this.recipeKey = recipeKey;
        this.recipeId = recipeId;
    }

    @Override
    public String parsedMenuTitle() {
        String title = this.display.title();
        Player player = this.player();
        title = LangManager.INSTANCE.replaceLang(title, player);
        title = title.replace("<recipe_key>", recipeKey.getKey());
        title = title.replace("<recipe_id>", recipeId);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }

    @Override
    public @Nullable Menu parentMenu() {
        return parentMenu;
    }

    @Override
    public void setParentMenu(@Nullable Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    @Override
    public void onDrawCompleted(Inventory inventory) {
        if (initialized) {
            return;
        }
        fillRecipeData(inventory);
        initialized = true;
    }

    protected abstract void fillRecipeData(Inventory inventory);

    /**
     * 获取返回按钮图标（红色玻璃板）
     */
    protected Icon getBackIcon() {
        IconDisplay iconDisplay = new IconDisplay(
            Material.RED_STAINED_GLASS_PANE,
            "§c← 返回",
            List.of("§7点击返回上一级菜单")
        );
        return new TranslatableIcon(iconDisplay) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                if (parentMenu != null) {
                    parentMenu.openMenu();
                }
                return this;
            }
        };
    }

    /**
     * 解析材料物品的ID字符串
     */
    protected @Nullable String resolveIngredientId(@Nullable ItemStack item) {
        if (ItemHelper.isAir(item)) {
            return null;
        }
        NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemIdOrCreate(item, true);
        return itemId != null ? itemId.toString() : null;
    }

    /**
     * 从物品ID字符串创建ItemStack用于GUI显示
     */
    protected @Nullable ItemStack itemFromId(@Nullable String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return null;
        }
        NamespacedItemIdStack stackedId = NamespacedItemIdStack.fromString(itemId);
        if (stackedId == null) {
            return null;
        }
        try {
            return ItemManager.INSTANCE.matchItem(stackedId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存配方配置到文件并重新加载
     */
    protected void saveRecipeConfig(BukkitConfigWrapper configWrapper) {
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
        String recipeId = recipeKey.getKey();
        RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeId, false, false);
        RecipeManager.INSTANCE.loadRecipeFromConfig(recipeId, configWrapper);
    }

    /**
     * 获取配方的BukkitConfigWrapper
     * 通过重新创建指向同一文件的wrapper来实现
     */
    protected @Nullable BukkitConfigWrapper getRecipeConfigWrapper() {
        return RecipeManager.INSTANCE.getRecipeConfigWrapper(recipeKey);
    }

}
