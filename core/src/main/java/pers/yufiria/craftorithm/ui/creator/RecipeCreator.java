package pers.yufiria.craftorithm.ui.creator;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.Key;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.StoredMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.ItemGroup;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.ParsedRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.util.LangUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class RecipeCreator extends StoredMenu {

    private @Nullable String recipeId;
    private @Nullable String recipeFileName;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public RecipeCreator(
        @NotNull Player player,
        @Nullable String recipeId,
        @Nullable String recipeFileName
    ) {
        super(player);
        this.recipeId = recipeId;
        this.recipeFileName = recipeFileName;
    }

    protected abstract RecipeType recipeType();

    protected abstract Icon getFrameIcon();

    protected abstract Icon getResultFrameIcon();

    protected void createRecipeConfig(String recipeFileName, Consumer<BukkitConfigWrapper> callback) {
        CrypticLibBukkit.scheduler().async(() -> {
            BukkitConfigWrapper recipeConfigWrapper = new BukkitConfigWrapper(Craftorithm.instance(), "recipes/" + recipeFileName + ".yml");
            recipeConfigWrapper.saveDefaultConfigFile();
            callback.accept(recipeConfigWrapper);
        });
    }

    /**
     * 保存并注册配方。
     * 统一处理「解析配方id与文件名 → 关闭菜单 → 写入配置 → 落盘并重载 → 解析配方 →
     * 回到主线程注册 → 发送结果提示」这一整套流程。
     * @param player 操作玩家
     * @param resultId 结果物品id
     * @param writeFields 写入该配方类型特有字段的回调; type / result / recipe_id 由本方法统一写入
     */
    protected void saveAndRegister(
        @NotNull Player player,
        @NotNull NamespacedItemIdStack resultId,
        @NotNull Consumer<BukkitConfigWrapper> writeFields
    ) {
        String typeKey = recipeType().typeKey();
        String recipeId = resolveRecipeId(typeKey, resultId.itemId());
        String recipeFileName = resolveRecipeFileName(resultId.itemId());
        player.closeInventory();

        createRecipeConfig(recipeFileName, recipeConfig -> {
            recipeConfig.set("type", typeKey);
            recipeConfig.set("result", resultId.toString());
            writeFields.accept(recipeConfig);
            if (recipeId != null) {
                recipeConfig.set("recipe_id", recipeId);
            }
            recipeConfig.saveConfig();
            recipeConfig.reloadConfig();

            ParsedRecipe parsedRecipe = RecipeManager.INSTANCE.parseRecipeFromConfig(recipeFileName, recipeConfig);
            if (parsedRecipe == null) {
                CrypticLib.error("Failed to load recipe " + recipeFileName);
                return;
            }

            CrypticLibBukkit.scheduler().sync(() -> {
                boolean loadResult = RecipeManager.INSTANCE.registerParsedRecipe(parsedRecipe, true);
                if (loadResult) {
                    LangUtils.sendLang(player, Languages.COMMAND_CREATE_SUCCESS, Map.of(
                        "<recipe_type>", recipeType().getLocalizedName(player),
                        "<recipe_file_name>", recipeFileName,
                        "<recipe_id>", recipeId != null ? recipeId : recipeFileName
                    ));
                } else {
                    LangUtils.sendLang(player, Languages.RECIPE_LOAD_EXCEPTION, Map.of("<recipe_name>", recipeFileName));
                }
            });
        });
    }

    /**
     * 解析配方文件名
     * 优先级：recipeFileName > recipeId > 自动生成
     * 如果recipeId已存在（通过命令或resolveRecipeId生成），则使用recipeId作为文件名
     * @param resultId
     * @return
     */
    protected String resolveRecipeFileName(@NotNull NamespacedItemId resultId) {
        if (this.recipeFileName != null) {
            return this.recipeFileName;
        }
        if (this.recipeId != null) {
            this.recipeFileName = this.recipeId;
            return this.recipeFileName;
        }
        Objects.requireNonNull(resultId, "Recipe result is null!");
        String resolved = resultId.namespace() + "_" + resultId.itemId()
            .replace(':', '_')
            .replace('-', '_');
        if (!RecipeManager.INSTANCE.containsRecipe(resolved)) {
            return resolved;
        }
        resolved = resolved + "_" + timeFormat.format(System.currentTimeMillis());
        this.recipeFileName = resolved;
        return recipeFileName;
    }

    /**
     * 解析配方id
     * 如果命令中已指定recipeId,直接返回
     * 否则根据 typeKey + resultItemId 自动生成
     * 如果result物品id不符合命名规则,返回null(使用文件名作为配方id)
     * @param typeKey 配方类型的typeKey
     * @param resultId 结果物品id
     * @return 配方id, 或null
     */
    public @Nullable String resolveRecipeId(@NotNull String typeKey, @NotNull NamespacedItemId resultId) {
        if (this.recipeId != null) {
            return this.recipeId;
        }
        String itemIdStr = resultId.itemId()
            .replace(':', '_')
            .replace('-', '_');
        if (!Key.isValidKey(itemIdStr)) {
            return null;
        }
        String resolved = typeKey + "_" + itemIdStr;
        if (RecipeManager.INSTANCE.containsRecipe(resolved)) {
            resolved = resolved + "_" + timeFormat.format(System.currentTimeMillis());
        }
        this.recipeId = resolved;
        return resolved;
    }

    //实现标题的翻译功能
    @Override
    public String parsedMenuTitle() {
        String title = this.display.title();
        Player player = this.player().orElse(null);
        title = LangManager.INSTANCE.replaceLang(title, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }

    /**
     * 解析材料物品的ID字符串。
     * 若为物品组占位符物品, 解析为tag:xxx或item_pack:xxx
     */
    protected String resolveIngredientId(ItemStack item) {
        Optional<ItemGroup> itemGroupOpt = ItemGroup.fromItemStack(item);
        if (itemGroupOpt.isPresent()) {
            return itemGroupOpt.get().toIngredientId();
        }
        NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemIdOrCreate(item, true);
        return itemId != null ? itemId.toString() : null;
    }

}
