package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.ItemDisplayIcon;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
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
            default:
                break;
        }
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
            getMenuIconMap().put(frameSlot, frameIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.STONECUTTER, LangUtil.langMsg("menu.recipe_creator.icon.frame"),
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
                    File recipeFile = new File(RecipeManager.getRecipeFileFolder(), recipeName + ".yml");
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
                    RecipeManager.getRecipeConfigWrapperMap().put(recipeName, recipeConfig);
                    event.getWhoClicked().closeInventory();
                    sendSuccessMsgAndReloadMap(event.getWhoClicked());
                });
        getMenuIconMap().put(22, confirmIcon);
    }

    private void setSmithingMenuIcons() {
        int[] frameSlots = {
                0, 1 ,2, 3, 4, 5, 6, 7, 8,
                9, 13, 17,
                18, 26,
                27, 31, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.frame"));
        for (int slot : frameSlots) {
            getMenuIconMap().put(slot, frameIcon);
        }
        int[] resultFrameSlots = {
                14, 15, 16,
                23, 25,
                32, 33, 34
        };
        ItemDisplayIcon resultFrameIcon = ItemDisplayIcon.icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.result_frame"));
        for (int slot : resultFrameSlots) {
            getMenuIconMap().put(slot, resultFrameIcon);
        }
        int[] smithingFrameSlots;
        if (CrypticLib.minecraftVersion() < 12000) {
            smithingFrameSlots = new int[]{
                    10, 11, 12,
                    20,
                    28, 29, 30
            };
        } else {
            smithingFrameSlots = new int[]{
                    10, 11, 12,
                    28, 29, 30
            };
        }
        ItemDisplayIcon smithingFrameIcon = ItemDisplayIcon.icon(Material.CYAN_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.smithing_frame"));
        for (int slot : smithingFrameSlots) {
            getMenuIconMap().put(slot, smithingFrameIcon);
        }
        ItemDisplayIcon confirmFrameIcon = ItemDisplayIcon.icon(Material.SMITHING_TABLE, LangUtil.langMsg("menu.recipe_creator.icon.confirm"), event -> {
            event.setCancelled(true);
            ItemStack result = event.getClickedInventory().getItem(24);
            if (result == null || result.getType().equals(Material.AIR)) {
                LangUtil.sendLang(event.getWhoClicked(), "command.create.null_result");
                return;
            }
            String resultName = getItemName(result, false);
            ItemStack base, addition, template;
            String baseName, additionName, templateName = null;
            if (CrypticLib.minecraftVersion() < 12000) {
                base = event.getClickedInventory().getItem(19);
                addition = event.getClickedInventory().getItem(21);
            } else {
                template = event.getClickedInventory().getItem(19);
                base = event.getClickedInventory().getItem(20);
                addition = event.getClickedInventory().getItem(21);
                templateName = getItemName(template, true);
            }
            baseName = getItemName(base, true);
            additionName = getItemName(addition, true);
            File recipeFile = new File(RecipeManager.getRecipeFileFolder(), recipeName + ".yml");
            if (!recipeFile.exists()) {
                FileUtil.createNewFile(recipeFile);
            }
            YamlConfigWrapper recipeConfig = new YamlConfigWrapper(recipeFile);
            recipeConfig.config().set("result", resultName);
            recipeConfig.config().set("source.base", baseName);
            recipeConfig.config().set("source.addition", additionName);
            recipeConfig.config().set("type", "smithing");
            if (CrypticLib.minecraftVersion() >= 12000) {
                recipeConfig.config().set("source.type", "transform");
                recipeConfig.config().set("source.template", templateName);
            }
            recipeConfig.saveConfig();
            recipeConfig.reloadConfig();
            Recipe[] recipes = RecipeFactory.newRecipe(recipeConfig.config(), recipeName);
            RecipeManager.regRecipes(recipeName, Arrays.asList(recipes), recipeConfig);
            RecipeManager.getRecipeConfigWrapperMap().put(recipeName, recipeConfig);
            event.getWhoClicked().closeInventory();
            sendSuccessMsgAndReloadMap(event.getWhoClicked());
        });
        getMenuIconMap().put(22, confirmFrameIcon);
    }

    private void setCookingMenuIcons() {
        int[] frameSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 13, 17,
                18, 26,
                27, 31, 35,
                36, 37, 40, 43, 44
        };
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.frame"));
        for (int slot : frameSlots) {
            getMenuIconMap().put(slot, frameIcon);
        }
        int[] resultFrameSlots = {
                14, 15, 16,
                23, 25,
                32, 33, 34
        };
        ItemDisplayIcon resultFrameIcon = ItemDisplayIcon.icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.result_frame"));
        for (int slot : resultFrameSlots) {
            getMenuIconMap().put(slot, resultFrameIcon);
        }
        int[] cookingFrameSlots = {
                10, 11, 12,
                19, 21,
                28, 29, 30
        };
        ItemDisplayIcon cookingFrameIcon = ItemDisplayIcon.icon(Material.CYAN_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.cooking_frame"));
        for (int slot : cookingFrameSlots) {
            getMenuIconMap().put(slot, cookingFrameIcon);
        }
        if (CrypticLib.minecraftVersion() >= 11400) {
            ItemDisplayIcon furnaceIcon = ItemDisplayIcon.icon(Material.FURNACE, LangUtil.langMsg("menu.recipe_creator.icon.furnace_toggle"), event -> setIconGlowing(38, event));
            getMenuIconMap().put(38, furnaceIcon);
            ItemDisplayIcon blastingIcon = ItemDisplayIcon.icon(Material.BLAST_FURNACE, LangUtil.langMsg("menu.recipe_creator.icon.blasting_toggle"), event -> setIconGlowing(39, event));
            getMenuIconMap().put(39, blastingIcon);
            ItemDisplayIcon smokingIcon = ItemDisplayIcon.icon(Material.SMOKER, LangUtil.langMsg("menu.recipe_creator.icon.smoking_toggle"), event -> setIconGlowing(41, event));
            getMenuIconMap().put(41, smokingIcon);
            ItemDisplayIcon campfireIcon = ItemDisplayIcon.icon(Material.CAMPFIRE, LangUtil.langMsg("menu.recipe_creator.icon.campfire_toggle"), event -> setIconGlowing(42, event));
            getMenuIconMap().put(42, campfireIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.FURNACE, LangUtil.langMsg("menu.recipe_creator.icon.confirm"), event -> {
            event.setCancelled(true);
            ItemStack source = event.getClickedInventory().getItem(20);
            String sourceName = getItemName(source, true);
            ItemStack result = event.getClickedInventory().getItem(24);
            if (result == null || result.getType().equals(Material.AIR)) {
                LangUtil.sendLang(event.getWhoClicked(), "command.create.null_result");
                return;
            }
            String resultName = getItemName(result, false);
            File recipeFile = new File(RecipeManager.getRecipeFileFolder(), recipeName + ".yml");
            if (!recipeFile.exists()) {
                FileUtil.createNewFile(recipeFile);
            }
            YamlConfigWrapper recipeConfig = new YamlConfigWrapper(recipeFile);
            recipeConfig.config().set("type", "cooking");
            recipeConfig.config().set("result", resultName);
            if (CrypticLib.minecraftVersion() >= 11400) {
                recipeConfig.config().set("multiple", true);
                List<Map<String, String>> sourceList = new ArrayList<>();
                int []toggleSlots = { 38, 39, 41, 42 };
                for (int slot : toggleSlots) {
                    ItemStack item = event.getClickedInventory().getItem(slot);
                    Material material = item.getType();
                    boolean toggle = item.containsEnchantment(Enchantment.MENDING);
                    if (toggle) {
                        Map<String, String> sourceMap = new HashMap<>();
                        sourceMap.put("block", material.name().toLowerCase(Locale.ROOT));
                        sourceMap.put("item", sourceName);
                        sourceList.add(sourceMap);
                    }
                }
                recipeConfig.config().set("source", sourceList);
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();
                Recipe[] multipleRecipes = RecipeFactory.newMultipleRecipe(recipeConfig.config(), recipeName);
                RecipeManager.regRecipes(recipeName, Arrays.asList(multipleRecipes), recipeConfig);
                RecipeManager.getRecipeConfigWrapperMap().put(recipeName, recipeConfig);
            } else {
                recipeConfig.config().set("source.block", "furnace");
                recipeConfig.config().set("source.item", sourceName);
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();
                Recipe[] recipes = RecipeFactory.newRecipe(recipeConfig.config(), recipeName);
                RecipeManager.regRecipes(recipeName, Arrays.asList(recipes), recipeConfig);
                RecipeManager.getRecipeConfigWrapperMap().put(recipeName, recipeConfig);
            }
            event.getWhoClicked().closeInventory();
            sendSuccessMsgAndReloadMap(event.getWhoClicked());
        });
        getMenuIconMap().put(22, confirmIcon);
    }

    private void setIconGlowing(int slot, InventoryClickEvent event) {
        event.setCancelled(true);
        RecipeCreatorMenuHolder holder = (RecipeCreatorMenuHolder) event.getClickedInventory().getHolder();
        ItemDisplayIcon icon = holder.getMenuIconMap().get(slot);
        ItemStack display = icon.getDisplay();
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

    private void setCraftMenuIcons() {
        int[] frameSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 13, 17,
                18, 26,
                27, 31, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.frame"));
        for (int slot : frameSlots) {
            getMenuIconMap().put(slot, frameIcon);
        }
        int[] resultFrameSlots = {
                14, 15, 16,
                23, 25,
                32, 33, 34
        };
        ItemDisplayIcon resultFrameIcon = ItemDisplayIcon.icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.langMsg("menu.recipe_creator.icon.result_frame"));
        for (int slot : resultFrameSlots) {
            getMenuIconMap().put(slot, resultFrameIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.CRAFTING_TABLE,
                LangUtil.langMsg("menu.recipe_creator.icon.confirm"),
                event -> {
                    event.setCancelled(true);
                    Inventory inventory = event.getView().getTopInventory();
                    ItemStack result = inventory.getItem(24);
                    if (result == null || result.getType().equals(Material.AIR)) {
                        LangUtil.sendLang(event.getWhoClicked(), "command.create.null_result");
                        return;
                    }
                    String resultName = getItemName(result, false);
                    int[] sourceSlots = {
                            10, 11, 12,
                            19, 20, 21,
                            28, 29, 30
                    };
                    List<String> sourceList = new ArrayList<>();
                    for (int slot : sourceSlots) {
                        ItemStack item = inventory.getItem(slot);
                        if (item == null || item.getType().equals(Material.AIR)) {
                            sourceList.add("");
                            continue;
                        }
                        String itemName = getItemName(item, true);
                        sourceList.add(itemName);
                    }
                    File recipeFile = new File(RecipeManager.getRecipeFileFolder(), recipeName + ".yml");
                    if (!recipeFile.exists()) {
                        FileUtil.createNewFile(recipeFile);
                    }
                    YamlConfigWrapper recipeConfig = new YamlConfigWrapper(recipeFile);
                    switch (recipeType) {
                        case SHAPED:
                            List<String> shape = new ArrayList<>(Arrays.asList("abc", "def", "ghi"));
                            Map<Character, String> itemMap = new HashMap<>();
                            char[] tmp = "abcdefghi".toCharArray();
                            for (int i = 0; i < sourceList.size(); i++) {
                                if (sourceList.get(i).isEmpty()) {
                                    continue;
                                }
                                itemMap.put(tmp[i], sourceList.get(i));
                            }
                            for (int i = 0; i < shape.size(); i++) {
                                String s = shape.get(i);
                                for (char c : s.toCharArray()) {
                                    if (!itemMap.containsKey(c)) {
                                        s = s.replace(c, ' ');
                                    }
                                }
                                shape.set(i, s);
                            }
                            shape.removeIf(s -> s.trim().isEmpty());
                            recipeConfig.config().set("type", "shaped");
                            recipeConfig.config().set("result", resultName);
                            recipeConfig.config().set("shape", shape);
                            recipeConfig.config().set("source", itemMap);
                            break;
                        case SHAPELESS:
                            sourceList.removeIf(String::isEmpty);
                            recipeConfig.config().set("type", "shapeless");
                            recipeConfig.config().set("result", resultName);
                            recipeConfig.config().set("source", sourceList);
                            break;
                    }
                    recipeConfig.saveConfig();
                    recipeConfig.reloadConfig();
                    Recipe[] recipes = RecipeFactory.newRecipe(recipeConfig.config(), recipeName);
                    RecipeManager.regRecipes(recipeName, Arrays.asList(recipes), recipeConfig);
                    RecipeManager.getRecipeConfigWrapperMap().put(recipeName, recipeConfig);
                    event.getWhoClicked().closeInventory();
                    sendSuccessMsgAndReloadMap(event.getWhoClicked());
                });
        getMenuIconMap().put(22, confirmIcon);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        String title = TextUtil.color(LangUtil.langMsg("menu.recipe_creator.title"));
        title = title.replace("<recipe_type>", recipeType.name().toLowerCase(Locale.ROOT));
        title = title.replace("<recipe_name>", recipeName);
        Inventory inventory = Bukkit.createInventory(this, 45, title);
        for (Integer slot : getMenuIconMap().keySet()) {
            inventory.setItem(slot, getMenuIconMap().get(slot).getDisplay());
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
        if (ItemUtil.isItemInvalidate(item)) {
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
        Map<String, String> replaceMap = ContainerUtil.newHashMap("<recipe_type>", recipeType.name().toLowerCase(),
                "<recipe_name>", recipeName);
        LangUtil.sendLang(entity, "command.create.success", replaceMap);
        RecipeManager.reloadServerRecipeCache();
    }

}
