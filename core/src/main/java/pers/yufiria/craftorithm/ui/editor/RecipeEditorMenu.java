package pers.yufiria.craftorithm.ui.editor;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

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
     * 创建返回按钮图标，从配置中读取外观
     * 子类调用此方法并传入自己的配置来创建返回按钮
     * @param config 返回按钮的配置节
     */
    protected Icon createBackIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(config);
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
     * @param configWrapper 配方配置
     * @param callback 配方加载完成后的回调
     */
    protected void saveRecipeEdit(BukkitConfigWrapper configWrapper, Runnable callback) {
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
        String recipeId = recipeKey.getKey();
        RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeId, false, false);
        CrypticLibBukkit.scheduler().syncLater(() -> {
            RecipeManager.INSTANCE.loadRecipeFromConfig(recipeId, configWrapper, true);
            if (callback != null) {
                callback.run();
            }
        }, 2L);
    }

    /**
     * 获取配方的BukkitConfigWrapper
     * 通过重新创建指向同一文件的wrapper来实现
     */
    protected @Nullable BukkitConfigWrapper getRecipeConfigWrapper() {
        return RecipeManager.INSTANCE.getRecipeConfigWrapper(recipeKey);
    }

}
