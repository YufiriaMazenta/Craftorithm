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
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.permission.Permissions;
import pers.yufiria.craftorithm.recipe.ParsedRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.IngredientIdResolver;
import pers.yufiria.craftorithm.ui.ResultIdResolver;
import pers.yufiria.craftorithm.ui.SimpleIngredientIdResolver;
import pers.yufiria.craftorithm.ui.SimpleResultIdResolver;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.ui.recipebook.BackIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 配方编辑器基类
 * 继承StoredMenu，与RecipeCreator类似的模式，但用于编辑已有配方。
 * 子类在构造时调用fillRecipeData()将现有配方数据预填充到GUI槽位。
 */
public abstract class RecipeEditor extends StoredMenu implements BackableMenu {

    protected Menu parentMenu;
    protected final NamespacedKey recipeKey;
    protected final String recipeId;
    private final IngredientIdResolver ingredientIdResolver;
    private final ResultIdResolver resultIdResolver;
    /**
     * 标记该编辑页面是否初始化,用于首次打开时填充配方内容
     */
    private boolean initialized = false;

    public RecipeEditor(
        @NotNull Player player,
        @NotNull NamespacedKey recipeKey,
        @NotNull String recipeId
    ) {
        this(player, recipeKey, recipeId, SimpleIngredientIdResolver.INSTANCE, SimpleResultIdResolver.INSTANCE);
    }

    public RecipeEditor(
        @NotNull Player player,
        @NotNull NamespacedKey recipeKey,
        @NotNull String recipeId,
        @NotNull IngredientIdResolver ingredientIdResolver,
        @NotNull ResultIdResolver resultIdResolver
    ) {
        super(player);
        this.recipeKey = recipeKey;
        this.recipeId = recipeId;
        this.ingredientIdResolver = ingredientIdResolver;
        this.resultIdResolver = resultIdResolver;
    }

    @Override
    public String parsedMenuTitle() {
        String title = this.display.title();
        Player player = this.player().orElse(null);
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
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(config);
        return new BackIcon(iconDisplay);
    }

    /**
     * 创建删除按钮图标，从配置中读取外观
     * 子类调用此方法并传入自己的配置来创建删除按钮
     * 点击后删除当前编辑的配方及其配置文件
     * @param config 删除按钮的配置节
     */
    protected Icon createDeleteIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(config);
        return new TranslatableIcon(iconDisplay) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                if (!player.hasPermission(Permissions.RECIPE_REMOVE)) {
                    LangUtils.sendLang(player, Languages.COMMAND_NO_PERM);
                    return this;
                }
                if (RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeKey.getKey(), true, true)) {
                    LangUtils.sendLang(player, Languages.COMMAND_REMOVE_SUCCESS);
                } else {
                    LangUtils.sendLang(player, Languages.COMMAND_REMOVE_NOT_EXIST);
                }
                player.closeInventory();
                return this;
            }
        };
    }

    /**
     * 解析材料物品的ID字符串
     */
    protected final @Nullable String resolveIngredientId(@Nullable ItemStack item) {
        return ingredientIdResolver.resolve(item);
    }

    /**
     * 解析结果物品的ID
     */
    protected final @Nullable NamespacedItemIdStack resolveResultId(@NotNull ItemStack item) {
        return resultIdResolver.resolve(item);
    }

    /**
     * 保存配方配置到文件并重新加载
     * @param configWrapper 配方配置
     * @param callback 配方加载完成后的回调
     */
    protected void saveRecipeEdit(BukkitConfigWrapper configWrapper, Runnable callback) {
        CrypticLibBukkit.scheduler().async(() -> {
            configWrapper.saveConfig();
            configWrapper.reloadConfig();
            String recipeId = recipeKey.getKey();
            String recipeFileName = RecipeManager.INSTANCE.getRecipeFileNameByKey(recipeKey);
            RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeId, false, false);
            ParsedRecipe parsedRecipe = RecipeManager.INSTANCE.parseRecipeFromConfig(recipeFileName, configWrapper);
            if (parsedRecipe == null) {
                CrypticLib.error("Failed to load recipe " + recipeFileName);
                return;
            }
            CrypticLibBukkit.scheduler().syncLater(() -> {
                RecipeManager.INSTANCE.registerParsedRecipe(parsedRecipe, true);
                if (callback != null) {
                    callback.run();
                }
            }, 2L);
        });
    }

    /**
     * 保存编辑后的配方。
     * 统一处理「取出配方配置 → 写入字段 → 落盘重载并重新注册 → 关闭菜单 → 发送成功提示」。
     * @param player 操作玩家
     * @param writeFields 写入该配方类型字段的回调
     */
    protected void saveEdit(@NotNull Player player, @NotNull Consumer<BukkitConfigWrapper> writeFields) {
        BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
        if (configWrapper != null) {
            writeFields.accept(configWrapper);
            saveRecipeEdit(configWrapper, () -> {
                String fileName = RecipeManager.INSTANCE.getRecipeFileNameByKey(recipeKey);
                LangUtils.sendLang(player, Languages.COMMAND_EDIT_SUCCESS, Map.of(
                    "<recipe_id>", recipeId,
                    "<recipe_file_name>", fileName != null ? fileName : recipeId
                ));
            });
        }
        player.closeInventory();
    }

    /**
     * 获取配方的BukkitConfigWrapper
     */
    protected @Nullable BukkitConfigWrapper getRecipeConfigWrapper() {
        return RecipeManager.INSTANCE.getRecipeConfigWrapper(recipeKey);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        setReturnStoredItems(false);
    }

}
