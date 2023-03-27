package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.YamlFileWrapper;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.ItemDisplayIcon;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.FileUtil;
import com.github.yufiriamazenta.craftorithm.util.ItemUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
            case ANVIL:
                setAnvilMenuIcons();
                break;
            default:
                break;
        }
    }

    private void setAnvilMenuIcons() {
        //TODO
    }

    private void setStoneCuttingMenuIcons() {
        int[] frameSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17,
                18, 19, 20, 21, 23, 24, 25, 26,
                27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.frame"));
        for (int frameSlot : frameSlots) {
            getMenuIconMap().put(frameSlot, frameIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.STONECUTTER, LangUtil.lang("menu.recipe_creator.icon.frame"),
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
                    YamlFileWrapper recipeConfig = new YamlFileWrapper(recipeFile);
                    recipeConfig.getConfig().set("multiple", true);
                    recipeConfig.getConfig().set("result", results);
                    recipeConfig.getConfig().set("source", sources);
                    recipeConfig.getConfig().set("type", "stone_cutting");
                    recipeConfig.saveConfig();
                    recipeConfig.reloadConfig();
                    Recipe[] recipes = RecipeFactory.newMultipleRecipe(recipeConfig.getConfig(), recipeName);
                    for (Recipe recipe : recipes) {
                        NamespacedKey key = RecipeManager.getRecipeKey(recipe);
                        RecipeManager.regRecipe(key, recipe, recipeConfig.getConfig());
                    }
                    event.getWhoClicked().closeInventory();
                    sendSuccessMsg(event.getWhoClicked());
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
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.frame"));
        for (int slot : frameSlots) {
            getMenuIconMap().put(slot, frameIcon);
        }
        int[] resultFrameSlots = {
                14, 15, 16,
                23, 25,
                32, 33, 34
        };
        ItemDisplayIcon resultFrameIcon = ItemDisplayIcon.icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.result_frame"));
        for (int slot : resultFrameSlots) {
            getMenuIconMap().put(slot, resultFrameIcon);
        }
        int[] smithingFrameSlots = {
                10, 12,
                19, 20, 21,
                28, 30
        };
        ItemDisplayIcon smithingFrameIcon = ItemDisplayIcon.icon(Material.CYAN_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.smithing_frame"));
        for (int slot : smithingFrameSlots) {
            getMenuIconMap().put(slot, smithingFrameIcon);
        }
        ItemDisplayIcon confirmFrameIcon = ItemDisplayIcon.icon(Material.SMITHING_TABLE, LangUtil.lang("menu.recipe_creator.icon.confirm"), event -> {
            event.setCancelled(true);
            ItemStack result = event.getClickedInventory().getItem(24);
            if (result == null || result.getType().equals(Material.AIR)) {
                LangUtil.sendMsg(event.getWhoClicked(), "command.create.null_result");
                return;
            }
            String resultName = getItemName(result, false);
            ItemStack base = event.getClickedInventory().getItem(11);
            ItemStack addition = event.getClickedInventory().getItem(29);
            String baseName = getItemName(base, true);
            String additionName = getItemName(addition, true);
            File recipeFile = new File(RecipeManager.getRecipeFileFolder(), recipeName + ".yml");
            if (!recipeFile.exists()) {
                FileUtil.createNewFile(recipeFile);
            }
            YamlFileWrapper recipeConfig = new YamlFileWrapper(recipeFile);
            recipeConfig.getConfig().set("result", resultName);
            recipeConfig.getConfig().set("source.base", baseName);
            recipeConfig.getConfig().set("source.addition", additionName);
            recipeConfig.getConfig().set("type", "smithing");
            recipeConfig.saveConfig();
            recipeConfig.reloadConfig();
            Recipe recipe = RecipeFactory.newRecipe(recipeConfig.getConfig(), recipeName);
            RecipeManager.regRecipe(NamespacedKey.fromString(recipeName, Craftorithm.getInstance()), recipe, recipeConfig.getConfig());
            event.getWhoClicked().closeInventory();
            sendSuccessMsg(event.getWhoClicked());
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
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.frame"));
        for (int slot : frameSlots) {
            getMenuIconMap().put(slot, frameIcon);
        }
        int[] resultFrameSlots = {
                14, 15, 16,
                23, 25,
                32, 33, 34
        };
        ItemDisplayIcon resultFrameIcon = ItemDisplayIcon.icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.result_frame"));
        for (int slot : resultFrameSlots) {
            getMenuIconMap().put(slot, resultFrameIcon);
        }
        int[] cookingFrameSlots = {
                10, 11, 12,
                19, 21,
                28, 29, 30
        };
        ItemDisplayIcon cookingFrameIcon = ItemDisplayIcon.icon(Material.CYAN_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.cooking_frame"));
        for (int slot : cookingFrameSlots) {
            getMenuIconMap().put(slot, cookingFrameIcon);
        }
        if (Craftorithm.getInstance().getVanillaVersion() >= 14) {
            ItemDisplayIcon furnaceIcon = ItemDisplayIcon.icon(Material.FURNACE, LangUtil.lang("menu.recipe_creator.icon.furnace_toggle"), event -> {
                setIconGlowing(38, event);
            });
            getMenuIconMap().put(38, furnaceIcon);
            ItemDisplayIcon blastingIcon = ItemDisplayIcon.icon(Material.BLAST_FURNACE, LangUtil.lang("menu.recipe_creator.icon.blasting_toggle"), event -> {
                setIconGlowing(39, event);
            });
            getMenuIconMap().put(39, blastingIcon);
            ItemDisplayIcon smokingIcon = ItemDisplayIcon.icon(Material.SMOKER, LangUtil.lang("menu.recipe_creator.icon.smoking_toggle"), event -> {
                setIconGlowing(41, event);
            });
            getMenuIconMap().put(41, smokingIcon);
            ItemDisplayIcon campfireIcon = ItemDisplayIcon.icon(Material.CAMPFIRE, LangUtil.lang("menu.recipe_creator.icon.campfire_toggle"), event -> {
                setIconGlowing(42, event);
            });
            getMenuIconMap().put(42, campfireIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.FURNACE, LangUtil.lang("menu.recipe_creator.icon.confirm"), event -> {
            event.setCancelled(true);
            ItemStack source = event.getClickedInventory().getItem(20);
            String sourceName = getItemName(source, true);
            ItemStack result = event.getClickedInventory().getItem(24);
            if (result == null || result.getType().equals(Material.AIR)) {
                LangUtil.sendMsg(event.getWhoClicked(), "command.create.null_result");
                return;
            }
            String resultName = getItemName(result, false);
            File recipeFile = new File(RecipeManager.getRecipeFileFolder(), recipeName + ".yml");
            if (!recipeFile.exists()) {
                FileUtil.createNewFile(recipeFile);
            }
            YamlFileWrapper recipeConfig = new YamlFileWrapper(recipeFile);
            recipeConfig.getConfig().set("type", "cooking");
            recipeConfig.getConfig().set("result", resultName);
            if (Craftorithm.getInstance().getVanillaVersion() >= 14) {
                recipeConfig.getConfig().set("multiple", true);
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
                recipeConfig.getConfig().set("source", sourceList);
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();
                Recipe[] multipleRecipes = RecipeFactory.newMultipleRecipe(recipeConfig.getConfig(), recipeName);
                for (Recipe recipe : multipleRecipes) {
                    NamespacedKey key = RecipeManager.getRecipeKey(recipe);
                    RecipeManager.regRecipe(key, recipe, recipeConfig.getConfig());
                }
            } else {
                recipeConfig.getConfig().set("source.block", "furnace");
                recipeConfig.getConfig().set("source.item", sourceName);
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();
                Recipe recipe = RecipeFactory.newRecipe(recipeConfig.getConfig(), recipeName);
                RecipeManager.regRecipe(NamespacedKey.fromString(recipeName, Craftorithm.getInstance()), recipe, recipeConfig.getConfig());
            }
            event.getWhoClicked().closeInventory();
            sendSuccessMsg(event.getWhoClicked());
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
        ItemDisplayIcon frameIcon = ItemDisplayIcon.icon(Material.BLACK_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.frame"));
        for (int slot : frameSlots) {
            getMenuIconMap().put(slot, frameIcon);
        }
        int[] resultFrameSlots = {
                14, 15, 16,
                23, 25,
                32, 33, 34
        };
        ItemDisplayIcon resultFrameIcon = ItemDisplayIcon.icon(Material.LIME_STAINED_GLASS_PANE, LangUtil.lang("menu.recipe_creator.icon.result_frame"));
        for (int slot : resultFrameSlots) {
            getMenuIconMap().put(slot, resultFrameIcon);
        }
        ItemDisplayIcon confirmIcon = ItemDisplayIcon.icon(Material.CRAFTING_TABLE,
                LangUtil.lang("menu.recipe_creator.icon.confirm"),
                event -> {
                    event.setCancelled(true);
                    Inventory inventory = event.getView().getTopInventory();
                    ItemStack result = inventory.getItem(24);
                    if (result == null || result.getType().equals(Material.AIR)) {
                        LangUtil.sendMsg(event.getWhoClicked(), "command.create.null_result");
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
                    YamlFileWrapper recipeConfig = new YamlFileWrapper(recipeFile);
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
                            recipeConfig.getConfig().set("type", "shaped");
                            recipeConfig.getConfig().set("result", resultName);
                            recipeConfig.getConfig().set("shape", shape);
                            recipeConfig.getConfig().set("source", itemMap);
                            break;
                        case SHAPELESS:
                            recipeConfig.getConfig().set("type", "shapeless");
                            recipeConfig.getConfig().set("result", resultName);
                            recipeConfig.getConfig().set("source", sourceList);
                            break;
                    }
                    recipeConfig.saveConfig();
                    recipeConfig.reloadConfig();
                    Recipe recipe = RecipeFactory.newRecipe(recipeConfig.getConfig(), recipeName);
                    RecipeManager.regRecipe(NamespacedKey.fromString(recipeName, Craftorithm.getInstance()), recipe, recipeConfig.getConfig());
                    event.getWhoClicked().closeInventory();
                    sendSuccessMsg(event.getWhoClicked());
                });
        getMenuIconMap().put(22, confirmIcon);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        String title = LangUtil.color(LangUtil.lang("menu.recipe_creator.title"));
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
        if (ItemUtil.checkItemIsAir(item)) {
            return null;
        }
        String itemName;
        if (item.hasItemMeta()) {
            itemName = ItemManager.getItemName(item, ignoreAmount, true, "gui_items", UUID.randomUUID().toString());
            itemName = "items:" + itemName;
        } else {
            itemName = item.getType().name();
        }
        return itemName;
    }

    private void sendSuccessMsg(HumanEntity entity) {
        Map<String, String> replaceMap = ContainerUtil.newHashMap("<recipe_type>", recipeType.name().toLowerCase(),
                "<recipe_name>", recipeName);
        LangUtil.sendMsg(entity, "command.create.success", replaceMap);
    }

}
