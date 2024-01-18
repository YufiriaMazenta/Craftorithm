package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class RecipeCreator extends StoredMenu {

    protected RecipeType recipeType;
    protected String groupName;
    protected String recipeName;
    protected String title;

    public RecipeCreator(@NotNull Player player, @NotNull RecipeType recipeType, @NotNull String groupName, @NotNull String recipeName) {
        super(player);
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.title = Languages.MENU_RECIPE_CREATOR_TITLE.value(player)
            .replace("<recipe_type>", recipeType.typeName().value(player))
            .replace("<recipe_name>", groupName + "." + recipeName);
        this.groupName = groupName;
    }

    protected void toggleIconGlowing(int slot, InventoryClickEvent event) {
        ItemStack display = event.getCurrentItem();
        if (ItemUtil.isAir(display))
            return;
        ItemUtils.toggleItemGlowing(display);
        event.getClickedInventory().setItem(slot, display);
    }

    protected Icon getFrameIcon() {
        return new Icon(Material.BLACK_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_FRAME.value(player));
    }

    protected void sendSuccessMsg() {
        LangUtil.sendLang(
            player,
            Languages.COMMAND_CREATE_SUCCESS,
            CollectionsUtil.newStringHashMap(
                "<recipe_type>",
                recipeType.typeName().value(player),
                "<recipe_name>",
                groupName + "." + recipeName
            )
        );
    }

    protected Icon getResultFrameIcon() {
        return new Icon(Material.LIME_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_RESULT_FRAME.value(player));
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

    public RecipeGroup getRecipeGroup(String groupName) {
        if (RecipeManager.INSTANCE.hasRecipeGroup(groupName))
            return RecipeManager.INSTANCE.getRecipeGroup(groupName);

        return new RecipeGroup(groupName);
    }

}
