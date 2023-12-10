package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.config.yaml.YamlConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.FileUtil;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Supplier;

public abstract class RecipeCreator extends StoredMenu {

    private RecipeType recipeType;
    private String recipeName;
    private String title;

    public RecipeCreator(
        @NotNull Player player,
        @NotNull RecipeType recipeType,
        @NotNull String recipeName
    ) {
        super(player, () -> null);
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.title = Languages.MENU_RECIPE_CREATOR_TITLE.value()
            .replace("<recipe_type>", recipeType.name())
            .replace("<recipe_name>", recipeName);
    }

    protected void setIconGlowing(int slot, InventoryClickEvent event) {
        ItemStack display = event.getCurrentItem();
        if (ItemUtil.isAir(display))
            return;
        if (!display.containsEnchantment(Enchantment.MENDING)) {
            display.addUnsafeEnchantment(Enchantment.MENDING, 1);
            ItemMeta meta = display.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            display.setItemMeta(meta);
            event.getClickedInventory().setItem(slot, display);
        } else {
            display.removeEnchantment(Enchantment.MENDING);
            event.getClickedInventory().setItem(slot, display);
        }
    }

    protected Icon getFrameIcon() {
        return new Icon(Material.BLACK_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_FRAME.value());
    }

    protected Icon getUnlockIcon() {
        return new Icon(
            Material.KNOWLEDGE_BOOK,
            Languages.MENU_RECIPE_CREATOR_ICON_UNLOCK.value(),
            event -> setIconGlowing(event.getSlot(), event)
        );
    }

    protected void sendSuccessMsg(HumanEntity receiver, RecipeType recipeType, String recipeName) {
        LangUtil.sendLang(
            receiver,
            Languages.COMMAND_CREATE_SUCCESS.value(),
            CollectionsUtil.newStringHashMap("<recipe_type>", recipeType.name(), "<recipe_name>", recipeName)
        );
    }

    protected YamlConfigWrapper createRecipeConfig(String recipeName) {
        File recipeFile = new File(RecipeManager.INSTANCE.RECIPE_FILE_FOLDER, recipeName + ".yml");
        if (!recipeFile.exists()) {
            FileUtil.createNewFile(recipeFile);
        }
        return new YamlConfigWrapper(recipeFile);
    }

    protected Icon getResultFrameIcon() {
        return new Icon(Material.LIME_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_RESULT_FRAME.value());
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

}
