package com.github.yufiriamazenta.craftorithm.menu.impl.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.YamlFileWrapper;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.ItemDisplayIcon;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.FileUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
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
    }

    private void setStoneCuttingMenuIcons() {
    }

    private void setSmithingMenuIcons() {
    }

    private void setCookingMenuIcons() {
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
                    String resultName;
                    if (result.hasItemMeta()) {
                        resultName = ItemManager.getItemName(result, false, true, "gui_result", UUID.randomUUID().toString());
                        resultName = "items:" + resultName;
                    } else {
                        resultName = result.getType().name();
                    }
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
                        String itemName;
                        if (item.hasItemMeta()) {
                            itemName = ItemManager.getItemName(item, true, true, "gui", UUID.randomUUID().toString());
                            itemName = "items:" + itemName;
                        } else {
                            itemName = item.getType().name();
                        }
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
                                    i++;
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
                                s = s.substring(0, s.lastIndexOf(' '));
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
                    Recipe recipe = RecipeManager.newRecipe(recipeConfig.getConfig(), recipeName);
                    RecipeManager.regRecipe(NamespacedKey.fromString(recipeName, Craftorithm.getInstance()), recipe, recipeConfig.getConfig());
                    event.getWhoClicked().closeInventory();
                });
        getMenuIconMap().put(22, confirmIcon);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        String title = LangUtil.color(LangUtil.lang("menu.recipe_creator.title"));
        title = title.replace("<recipe_type>", recipeType.name());
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

}
