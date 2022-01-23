package top.oasismc.oasisrecipe.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.ConfigFile;

import java.util.*;

import static top.oasismc.oasisrecipe.OasisRecipe.color;
import static top.oasismc.oasisrecipe.OasisRecipe.info;

public class RecipeManager {

    private final ConfigFile recipeFile;
    private final List<String> keyList;
    private static final RecipeManager manager;

    static {
        manager = new RecipeManager();
    }

    private RecipeManager() {
        recipeFile = new ConfigFile("recipe.yml");
        keyList = new ArrayList<>();
        loadRecipesFromConfig();
    }

    public static RecipeManager getManager() {
        return manager;
    }

    public void loadRecipesFromConfig() {
        YamlConfiguration config = (YamlConfiguration) recipeFile.getConfig();
        for (String recipeName : config.getKeys(false)) {
            addRecipe(recipeName, config);
        }
    }

    public void addRecipe(String recipeName, YamlConfiguration config) {
        try {
            String key = Objects.requireNonNull(config.getString(recipeName + ".key")).toLowerCase();
            List<String> materialList = config.getStringList(recipeName + ".items");
            Material[] materials = new Material[materialList.size()];
            RecipeChoice item1 = null, item2 = null;
            switch (config.getString(recipeName + ".type", "shaped")) {
                case "shaped":
                    materials = getShapedRecipeItems(materialList);
                    break;
                case "shapeless":
                    materials = getShapelessRecipeItems(materialList);
                    break;
                case "furnace":
                case "smoking":
                case "campfire":
                case "blasting":
                case "stoneCutting":
                    item1 = getRecipeChoiceItem(materialList.get(0));
                    break;
                case "smithing":
                    item1 = new RecipeChoice.MaterialChoice(Objects.requireNonNull(Material.matchMaterial(materialList.get(0))));
                    item2 = getRecipeChoiceItem(materialList.get(1));
                    break;
            }//获取配方

            Material resultMaterial = Material.matchMaterial(config.getString(recipeName + ".result", "AIR"));
            ItemStack result = new ItemStack(Objects.requireNonNull(resultMaterial));//获取合成的物品

            result.setAmount(config.getInt(recipeName + ".resultAmount", 1));//设置合成的物品数量

            ItemMeta meta = result.getItemMeta();
            result.setItemMeta(setResultMeta(recipeName, meta, config));

            switch (config.getString(recipeName + ".type", "shaped")) {
                case "shaped"://有序
                    addShapedRecipe(key, result, materials);
                    break;
                case "shapeless"://无序
                    addShapelessRecipe(key, result, materials);
                    break;
                case "furnace":
                case "smoking":
                case "campfire":
                case "blasting":
                    int exp = config.getInt(recipeName + ".exp", 0);
                    int cookTime = config.getInt(recipeName + ".time", 10) * 20;
                    addCookingRecipe(key, result, item1, exp, cookTime, config.getString(recipeName + ".type", "furnace"));
                    break;
                case "smithing":
                    addSmithingRecipe(key, result, item1, item2);
                    break;
                case "stoneCutting":
                    addStoneCuttingRecipe(key, result, item1);
                    break;
            }
            keyList.add(config.getString(recipeName + ".key"));
        } catch (Exception e) {
            info(color("&cSome errors occurred while loading the " + recipeName + " recipe, please check your config file"));
            e.printStackTrace();
        }
    }

    public ItemMeta setResultMeta(String recipeName, ItemMeta meta, YamlConfiguration config) {
        String customName = config.getString(recipeName + ".customName");
        if (customName != null && !customName.equals("")) {
            Objects.requireNonNull(meta).setDisplayName(color(config.getString(recipeName + ".customName")));
        }//设置合成物品的名字

        addEnchants2MetaFromConfig(meta, recipeName);//添加附魔
        addAttribute2Meta(meta, recipeName, config);//添加属性
        addLore2MetaFromConfig(meta, recipeName);//添加lore
        meta.setUnbreakable(config.getBoolean(recipeName + ".unbreakable", false));//设置是否无法破坏

        if (config.getInt(recipeName + ".customModelData") != 0)//设置CustomModelData
            meta.setCustomModelData(config.getInt(recipeName + ".customModelData"));

        for (String flag : config.getStringList(recipeName + ".hide")) {
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_" + flag));
            } catch (IllegalArgumentException ignored) {}
        }//添加Flag

        return meta;
    }

    public RecipeChoice getRecipeChoiceItem(String itemStr) {
        int space1 = itemStr.indexOf(" ");
        if (space1 == -1) {
            return new RecipeChoice.MaterialChoice(Material.matchMaterial(itemStr));
        }//若不存在”,“符号,则直接判定为Material返回

        String itemMaterial = itemStr.substring(0, space1);
        String itemName;
        String lore;

        int space2 = itemStr.indexOf(" ", space1 + 1);
        if (space2 == -1) {
            itemName = itemStr.substring(space1 + 1).replace("name:", "");
            lore = "";
        } else {
            itemName = itemStr.substring(space1 + 1, space2).replace("name:", "");
            lore = itemStr.substring(space2 + 1).replace("lore:", "");
        }//若不存在第二个空格,则判断为无lore

        Material material = Material.matchMaterial(itemMaterial);
        ItemStack item = new ItemStack(Objects.requireNonNull(material));
        ItemMeta meta = item.getItemMeta();
        if (!itemName.equals("")) {
            Objects.requireNonNull(meta).setDisplayName(itemName);
        }
        if (!lore.equals("")) {
            Objects.requireNonNull(meta).setLore(Collections.singletonList(lore));
        }
        item.setItemMeta(meta);
        return new RecipeChoice.ExactChoice(item);//返回有对应ItemMeta需求的RecipeChoice
    }

    public Material[] getShapedRecipeItems(List<String> items) {
        Material[] materials = new Material[9];
        for(int i = 0; i < 3; i++) {
            int space1 = items.get(i).indexOf(" ");
            materials[i * 3] = Material.matchMaterial(items.get(i).substring(0, space1));
            int space2 = items.get(i).indexOf(" ", space1 + 1);
            materials[i * 3 + 1] = Material.matchMaterial(items.get(i).substring(space1 + 1, space2));
            materials[i * 3 + 2] = Material.matchMaterial(items.get(i).substring(space2 + 1));
        }
        return materials;
    }

    public Material[] getShapelessRecipeItems(List<String> items) {
        Material[] materials = new Material[items.size()];
        for (int i = 0; i < items.size(); i++) {
            materials[i] = Material.matchMaterial(items.get(i));
        }
        return materials;
    }

    public void addEnchants2MetaFromConfig(ItemMeta meta, String recipeName) {
        List<String> enchantStrList = getRecipeFile().getConfig().getStringList(recipeName + ".enchants");
        if (enchantStrList.size() == 0)
            return;
        for (String enchantStr : enchantStrList) {
            Enchantment enchantType;
            int enchantLevel;
            enchantStr = enchantStr.toLowerCase();
            int spaceIndex = enchantStr.indexOf(" ");
            if (spaceIndex == -1) {
                enchantType = Enchantment.getByKey(NamespacedKey.minecraft(enchantStr));
                enchantLevel = 1;
            } else {
                enchantType = Enchantment.getByKey(NamespacedKey.minecraft(enchantStr.substring(0, spaceIndex)));
                enchantLevel = Integer.parseInt(enchantStr.substring(spaceIndex + 1));
            }
            if (enchantType != null) {
                meta.addEnchant(enchantType, enchantLevel, true);
            }
        }
    }

    public void addLore2MetaFromConfig(ItemMeta meta, String recipeName) {
        List<String> loreList = getRecipeFile().getConfig().getStringList(recipeName + ".lore");
        if (loreList.size() == 0)
            return;
        for (String lore : loreList) {
            String coloredLore = color(lore);
            loreList.set(loreList.indexOf(lore), coloredLore);
        }
        meta.setLore(loreList);
    }

    public void addAttribute2Meta(ItemMeta meta, String recipeName, YamlConfiguration config) {
        List<String> attrs = config.getStringList(recipeName + ".attributes");
        if (attrs.size() == 0) {
            return;
        }
        for (String attr : attrs) {
            try {
                int index1 = attr.indexOf(" ");
                int index2 = attr.indexOf(" ", index1 + 1);
                int index3 = attr.indexOf(" ", index2 + 1);
                String type = attr.substring(0, index1);
                int value = Integer.parseInt(attr.substring(index1 + 1, index2));
                String addType = attr.substring(index2 + 1, index3);
                String slot = attr.substring(index3 + 1);
                meta.addAttributeModifier(
                        Attribute.valueOf(type),
                        new AttributeModifier(
                                UUID.randomUUID(),
                                "",
                                value,
                                AttributeModifier.Operation.valueOf("ADD_" + addType),
                                EquipmentSlot.valueOf(slot)
                        )
                );
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public void addStoneCuttingRecipe(String key, ItemStack result, RecipeChoice item) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getPlugin(), key);
        StonecuttingRecipe recipe = new StonecuttingRecipe(recipeKey, result, item);
        Bukkit.getServer().addRecipe(recipe);
    }

    public void addSmithingRecipe(String key, ItemStack result, RecipeChoice base, RecipeChoice addition) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getPlugin(), key);
        SmithingRecipe recipe = new SmithingRecipe(recipeKey, result, base, addition);
        Bukkit.getServer().addRecipe(recipe);
    }

    public void addShapedRecipe(String key, ItemStack result, Material[] itemList) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getPlugin(), key);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe = recipe.shape("abc", "def", "ghi");
        int i = 0;
        String temp = "abcdefghi";
        for (Material material : itemList) {
            recipe.setIngredient(temp.charAt(i), material);
            i++;
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    public void addShapelessRecipe(String key, ItemStack result, Material[] itemList) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getPlugin(), key);
        ShapelessRecipe recipe = new ShapelessRecipe(recipeKey, result);
        for (Material material : itemList) {
            recipe.addIngredient(material);
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    public void addCookingRecipe(String key, ItemStack result, RecipeChoice item, int exp, int cookingTime, String type) {
        NamespacedKey recipeKey = new NamespacedKey(OasisRecipe.getPlugin(), key);
        CookingRecipe<?> recipe = null;
        switch (type) {
            case "furnace":
                recipe = new FurnaceRecipe(recipeKey, result, item, exp, cookingTime);
                break;
            case "smoking":
                recipe = new SmokingRecipe(recipeKey, result, item, exp, cookingTime);
                break;
            case "blasting":
                recipe = new BlastingRecipe(recipeKey, result, item, exp, cookingTime);
                break;
            case "campfire":
                recipe = new CampfireRecipe(recipeKey, result, item, exp, cookingTime);
                break;
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    public ConfigFile getRecipeFile() {
        return recipeFile;
    }

    public void reloadRecipes() {
        Bukkit.resetRecipes();
        getKeyList().clear();
        loadRecipesFromConfig();
    }

}
