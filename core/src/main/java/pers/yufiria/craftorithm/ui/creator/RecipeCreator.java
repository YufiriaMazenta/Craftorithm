package pers.yufiria.craftorithm.ui.creator;

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
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class RecipeCreator extends StoredMenu {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9._-]+$");

    private @Nullable String recipeId;
    private @Nullable String recipeFileName;
    private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

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

    protected BukkitConfigWrapper createRecipeConfig(String recipeFileName) {
        BukkitConfigWrapper recipeConfigWrapper = new BukkitConfigWrapper(Craftorithm.instance(), "recipes/" + recipeFileName + ".yml");
        recipeConfigWrapper.saveDefaultConfigFile();
        return recipeConfigWrapper;
    }

    public RecipeCreator setRecipeFileName(@Nullable String recipeFileName) {
        this.recipeFileName = recipeFileName;
        return this;
    }

    /**
     * 解析配方文件名
     * 优先级：recipeFileName > recipeId > 自动生成
     * 如果recipeId已存在（通过命令或resolveRecipeId生成），则使用recipeId作为文件名
     * @param resultId
     * @return
     */
    public String resolveRecipeFileName(@NotNull NamespacedItemId resultId) {
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
        resolved = resolved + "_" + TIME_FORMAT.format(System.currentTimeMillis());
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
        if (!ID_PATTERN.matcher(itemIdStr).matches()) {
            return null;
        }
        String resolved = typeKey + "_" + itemIdStr;
        if (RecipeManager.INSTANCE.containsRecipe(resolved)) {
            resolved = resolved + "_" + TIME_FORMAT.format(System.currentTimeMillis());
        }
        this.recipeId = resolved;
        return resolved;
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
        return itemId != null ? itemId.toString() : null;
    }

}
