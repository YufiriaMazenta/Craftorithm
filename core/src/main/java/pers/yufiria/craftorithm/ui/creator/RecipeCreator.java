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

import java.text.SimpleDateFormat;
import java.util.Objects;

public abstract class RecipeCreator extends StoredMenu {

    private @Nullable String recipeName;
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public RecipeCreator(
        @NotNull Player player,
        @Nullable String recipeName
    ) {
        super(player);
        this.recipeName = recipeName;
    }

    protected abstract Icon getFrameIcon();

    protected abstract Icon getResultFrameIcon();

    protected BukkitConfigWrapper createRecipeConfig(String recipeName) {
        BukkitConfigWrapper recipeConfigWrapper = new BukkitConfigWrapper(Craftorithm.instance(), "recipes/" + recipeName + ".yml");
        recipeConfigWrapper.saveDefaultConfigFile();
        return recipeConfigWrapper;
    }

    public RecipeCreator setRecipeName(@Nullable String recipeName) {
        this.recipeName = recipeName;
        return this;
    }

    /**
     * 解析配方名字,如果打开页面时已经输入,将返回recipeName
     * 如果未输入,则生成以配方名字为基础生成的配方id
     * @param resultId
     * @return
     */
    public String resolveRecipeName(@NotNull NamespacedItemId resultId) {
        if (this.recipeName != null) {
            return this.recipeName;
        }
        Objects.requireNonNull(resultId, "Recipe result is null!");
        String resolveRecipeName = resultId.namespace() + "_" + resultId.itemId()
            .replace(':', '_')
            .replace('-', '_');
        if (!RecipeManager.INSTANCE.containsRecipe(resolveRecipeName)) {
            return resolveRecipeName;
        }
        resolveRecipeName = resolveRecipeName + "_" + FORMAT.format(System.currentTimeMillis());
        this.recipeName = resolveRecipeName;
        return recipeName;
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
