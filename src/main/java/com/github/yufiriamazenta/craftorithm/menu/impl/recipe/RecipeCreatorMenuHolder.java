package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.ItemDisplayIcon;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import crypticlib.CrypticLib;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.FileUtil;
import crypticlib.util.ItemUtil;
import crypticlib.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

//TODO 重构配方创建模块
@Deprecated
public class RecipeCreatorMenuHolder extends BukkitMenuHandler {

    private RecipeType recipeType;
    private String recipeName;

    public RecipeCreatorMenuHolder(RecipeType recipeType, String recipeName) {
        super();
        this.recipeType = recipeType;
        this.recipeName = recipeName;
        setMenuIcons();
    }

    private void setMenuIcons() {
        switch (recipeType) {
            case SHAPED:
            case SHAPELESS:
                setCraftMenuIcons();
                break;
            case COOKING:
                setCookingMenuIcons();
                break;
            case SMITHING:
                setSmithingMenuIcons();
                break;
            case STONE_CUTTING:
                setStoneCuttingMenuIcons();
                break;
            case POTION:
                setPotionMixMenuIcons();
            default:
                break;
        }
    }

    private void setPotionMixMenuIcons() {
        int[] frameSlots = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 20, 21, 23, 24, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 41, 42, 43, 44
        };
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.frame"));
        for (int frameSlot : frameSlots) {
            menuIconMap().put(frameSlot, frameIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.BREWING_STAND, LangUtil.langMsg("menu.recipe_creator.icon.confirm"),
            event -> {
                event.setCancelled(true);
                ItemStack result = event.getClickedInventory().getItem(25);
                ItemStack input = event.getClickedInventory().getItem(19);
                ItemStack ingredient = event.getClickedInventory().getItem(22);
                if (result == null || result.getType().equals(Material.AIR)) {
                    LangUtil.sendLang(event.getWhoClicked(), "command.create.null_result");
                    return;
                }
                String resultName = getItemName(result, false);
                String inputName = getItemName(input, true);
                String ingredientName = getItemName(ingredient, true);
                File recipeFile = new File(RecipeManager.recipeFileFolder(), recipeName + ".yml");
                if (!recipeFile.exists()) {
                    FileUtil.createNewFile(recipeFile);
                }
                YamlConfigWrapper recipeConfig = new YamlConfigWrapper(recipeFile);
                recipeConfig.config().set("type", "potion");
                recipeConfig.config().set("source.input", inputName);
                recipeConfig.config().set("source.ingredient", ingredientName);
                recipeConfig.config().set("result", resultName);
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();
                Recipe[] recipes = RecipeFactory.newRecipe(recipeConfig.config(), recipeName);
                RecipeManager.regPotionMix(recipeName, Arrays.asList(recipes), recipeConfig);
                RecipeManager.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                event.getWhoClicked().closeInventory();
                sendSuccessMsgAndReloadMap(event.getWhoClicked());
            });
        menuIconMap().put(40, confirmIcon);
    }

    private void setStoneCuttingMenuIcons() {
        int[] frameSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17,
                18, 19, 20, 21, 23, 24, 25, 26,
                27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.frame"));
        for (int frameSlot : frameSlots) {
            menuIconMap().put(frameSlot, frameIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.STONECUTTER, LangUtil.langMsg("menu.recipe_creator.icon.confirm"),
                event -> {
                    event.setCancelled(true);
                    List<String> sources = new ArrayList<>();
                    List<String> results = new ArrayList<>();
                    for (int i = 10; i < 17; i++) {
                        ItemStack item = event.getClickedInventory().getItem(i);
                        if (item == null || item.getType().equals(Material.AIR))
                            continue;
                        String itemName = getItemName(item, true);
                        sources.add(itemName);
                    }
                    for (int i = 28; i < 35; i++) {
                        ItemStack item = event.getClickedInventory().getItem(i);
                        if (item == null || item.getType().equals(Material.AIR))
                            continue;
                        String itemName = getItemName(item, false);
                        results.add(itemName);
                    }
                    File recipeFile = new File(RecipeManager.recipeFileFolder(), recipeName + ".yml");
                    if (!recipeFile.exists()) {
                        FileUtil.createNewFile(recipeFile);
                    }
                    YamlConfigWrapper recipeConfig = new YamlConfigWrapper(recipeFile);
                    recipeConfig.config().set("multiple", true);
                    recipeConfig.config().set("result", results);
                    recipeConfig.config().set("source", sources);
                    recipeConfig.config().set("type", "stone_cutting");
                    recipeConfig.saveConfig();
                    recipeConfig.reloadConfig();
                    Recipe[] recipes = RecipeFactory.newMultipleRecipe(recipeConfig.config(), recipeName);
                    RecipeManager.regRecipes(recipeName, Arrays.asList(recipes), recipeConfig);
                    RecipeManager.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                    event.getWhoClicked().closeInventory();
                    sendSuccessMsgAndReloadMap(event.getWhoClicked());
                });
        menuIconMap().put(22, confirmIcon);
    }

    private void setSmithingMenuIcons() {
    }

    private void setCookingMenuIcons() {

    }

    private void setCraftMenuIcons() {

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        String title = TextUtil.color(LangUtil.langMsg("menu.recipe_creator.title"));
        title = title.replace("<recipe_type>", recipeType.name().toLowerCase(Locale.ROOT));
        title = title.replace("<recipe_name>", recipeName);
        Inventory inventory = Bukkit.createInventory(this, 45, title);
        for (Integer slot : menuIconMap().keySet()) {
            inventory.setItem(slot, menuIconMap().get(slot).display());
        }
        return inventory;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    private String getItemName(ItemStack item, boolean ignoreAmount) {
        if (ItemUtil.isAir(item)) {
            return null;
        }
        String itemName = checkIsOtherPluginName(item);
        if (itemName != null)
            return itemName;
        if (item.hasItemMeta()) {
            itemName = ItemManager.getItemName(item, ignoreAmount, true, "gui_items", UUID.randomUUID().toString());
            itemName = "items:" + itemName;
        } else {
            itemName = item.getType().name();
        }
        return itemName;
    }

    private String checkIsOtherPluginName(ItemStack item) {
        //识别是否是ItemsAdder的物品
        String itemsAdderName = PluginHookUtil.getItemsAdderName(item);
        if (itemsAdderName != null)
            return "items_adder:" + itemsAdderName;

        //识别是否是Oraxen的物品
        String oraxenName = PluginHookUtil.getOraxenName(item);
        if (oraxenName != null) {
            return "oraxen:" + oraxenName;
        }

        //识别是否是MythicMobs的物品
        String mythicName = PluginHookUtil.getMythicMobsName(item);
        if (mythicName != null)
            return "mythic_mobs:" + mythicName;
        return null;
    }

    private void sendSuccessMsgAndReloadMap(HumanEntity entity) {
        Map<String, String> replaceMap = CollectionsUtil.newStringHashMap("<recipe_type>", recipeType.name().toLowerCase(),
                "<recipe_name>", recipeName);
        LangUtil.sendLang(entity, "command.create.success", replaceMap);
        RecipeManager.reloadServerRecipeCache();
    }

}
