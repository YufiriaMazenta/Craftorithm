package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtils;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.IOHelper;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class RecipeCreator extends StoredMenu {

    private RecipeType recipeType;
    private String recipeName;
    private String title;

    public RecipeCreator(
        @NotNull Player player,
        @NotNull RecipeType recipeType,
        @NotNull String recipeName
    ) {
        super(player);
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.title = Languages.MENU_RECIPE_CREATOR_TITLE.value(player)
            .replace("<recipe_type>", RecipeManager.INSTANCE.getRecipeTypeName(recipeType).value(player))
            .replace("<recipe_name>", recipeName);
    }

    protected void toggleIconGlowing(int slot, InventoryClickEvent event) {
        ItemStack display = event.getCurrentItem();
        if (ItemHelper.isAir(display))
            return;
        ItemUtils.toggleItemGlowing(display);
        event.getClickedInventory().setItem(slot, display);
    }

    protected Icon getFrameIcon() {
        return new Icon(new IconDisplay(Material.BLACK_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_FRAME.value(player)));
    }

    protected void sendSuccessMsg(HumanEntity receiver, String recipeName) {
        LangUtils.sendLang(
            receiver,
            Languages.COMMAND_CREATE_SUCCESS,
            CollectionsUtils.newStringHashMap(
                "<recipe_type>",
                RecipeManager.INSTANCE.getRecipeTypeName(recipeType).value((Player) receiver),
                "<recipe_name>",
                recipeName
            )
        );
    }

    protected BukkitConfigWrapper createRecipeConfig(String recipeName) {
        File recipeFile = new File(RecipeManager.INSTANCE.RECIPE_FILE_FOLDER, recipeName + ".yml");
        if (!recipeFile.exists()) {
            IOHelper.createNewFile(recipeFile);
        }
        BukkitConfigWrapper configWrapper = new BukkitConfigWrapper(recipeFile);
        configWrapper.reloadConfig();
        return configWrapper;
    }

    protected Icon getResultFrameIcon() {
        return new Icon(new IconDisplay(Material.LIME_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_RESULT_FRAME.value(player)));
    }

    public RecipeType recipeType() {
        return recipeType;
    }

    public RecipeCreator setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
        return this;
    }

    public String recipeName() {
        return recipeName;
    }

    public RecipeCreator setRecipeName(String recipeName) {
        this.recipeName = recipeName;
        return this;
    }

    public String title() {
        return title;
    }

    public RecipeCreator setTitle(String title) {
        this.title = title;
        return this;
    }

    public void regRecipeGroup(BukkitConfigWrapper recipeConfig) {
        RecipeGroup recipeGroup = new RecipeGroup(recipeName, recipeType(), recipeConfig);
        RecipeManager.INSTANCE.addRecipeGroup(recipeGroup);
        RecipeManager.INSTANCE.loadRecipeGroup(recipeGroup);
        if (CrypticLibBukkit.platform().isPaper()) {
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_1)) {
                Bukkit.updateRecipes();
            }
        }
    }

}
