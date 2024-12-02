package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.exception.UnsupportedVersionException;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.recipe.registry.impl.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

@Deprecated
public class RecipeFactory {

    private static final Map<SimpleRecipeTypes, BiFunction<YamlConfiguration, String, List<RecipeRegister>>> recipeRegistryProviderMap;
    private static final Map<SimpleRecipeTypes, BiFunction<YamlConfiguration, String, List<RecipeRegister>>> multipleRecipeRegistryProviderMap;

    static {
        recipeRegistryProviderMap = new HashMap<>();
        multipleRecipeRegistryProviderMap = new HashMap<>();

        recipeRegistryProviderMap.put(SimpleRecipeTypes.VANILLA_SHAPED, RecipeFactory::newShapedRecipe);
        recipeRegistryProviderMap.put(SimpleRecipeTypes.SHAPELESS, RecipeFactory::newShapelessRecipe);
        multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.VANILLA_SHAPED, RecipeFactory::newMultipleShapedRecipe);
        multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.SHAPELESS, RecipeFactory::newMultipleShapelessRecipe);
        recipeRegistryProviderMap.put(SimpleRecipeTypes.COOKING, RecipeFactory::newCookingRecipe);
        multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.COOKING, RecipeFactory::newMultipleCookingRecipe);
        recipeRegistryProviderMap.put(SimpleRecipeTypes.STONE_CUTTING, RecipeFactory::newStoneCuttingRecipe);
        multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.STONE_CUTTING, RecipeFactory::newMultipleStoneCuttingRecipe);
        recipeRegistryProviderMap.put(SimpleRecipeTypes.SMITHING, RecipeFactory::newSmithingRecipe);
        multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.SMITHING, RecipeFactory::newMultipleSmithingRecipe);
        recipeRegistryProviderMap.put(SimpleRecipeTypes.RANDOM_COOKING, RecipeFactory::newRandomCookingRecipe);
        multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.RANDOM_COOKING, RecipeFactory::newMultipleRandomCookingRecipe);

        if (RecipeManager.INSTANCE.supportPotionMix()) {
            recipeRegistryProviderMap.put(SimpleRecipeTypes.POTION, RecipeFactory::newPotionMixRecipe);
            multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.POTION, RecipeFactory::newMultiplePotionMixRecipe);
        }

        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            recipeRegistryProviderMap.put(SimpleRecipeTypes.ANVIL, RecipeFactory::newAnvilRecipe);
            multipleRecipeRegistryProviderMap.put(SimpleRecipeTypes.ANVIL, RecipeFactory::newMultipleAnvilRecipe);
        }
    }

    public static List<RecipeRegister> newRecipeRegistry(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeTypeStr = config.getString("type", "shaped.yml");
        SimpleRecipeTypes recipeType = SimpleRecipeTypes.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
        boolean multiple = config.getBoolean("multiple", false);
        if (multiple) {
            return multipleRecipeRegistryProviderMap.getOrDefault(recipeType, (c, k) -> {
                throw new UnsupportedVersionException("Can not create " + recipeType.name().toLowerCase() + " recipe registry");
            }).apply(config, key);
        } else {
            return recipeRegistryProviderMap.getOrDefault(recipeType, (c, k) -> {
                throw new UnsupportedVersionException("Can not create " + recipeType.name().toLowerCase() + " recipe registry");
            }).apply(config, key);
        }
    }

    public static List<RecipeRegister> newShapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);

        List<String> shapeStrList = config.getStringList("shape");
        CraftingBookCategory category = null;
        if (config.contains("category")) {
            String categoryStr = config.getString("category").toUpperCase();
            category = CraftingBookCategory.valueOf(categoryStr);
        }
        if (shapeStrList.size() > 3) {
            shapeStrList = shapeStrList.subList(0, 3);
        }
        String[] shape = new String[shapeStrList.size()];
        shape = shapeStrList.toArray(shape);
        RecipeRegister recipeRegistry = new ShapedRecipeRegistry(key, namespacedKey, result)
            .setShape(shape)
            .setRecipeChoiceMap(recipeChoiceMap)
            .setCraftingBookCategory(category);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegister> newMultipleShapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        List<?> shapeList = config.getList("shape", new ArrayList<>());
        CraftingBookCategory category = null;
        if (config.contains("category")) {
            String categoryStr = config.getString("category").toUpperCase();
            category = CraftingBookCategory.valueOf(categoryStr);
        }
        List<RecipeRegister> recipeRegistries = new ArrayList<>();
        for (int i = 0; i < shapeList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            List<String> shapeStrList = (List<String>) shapeList.get(i);
            if (shapeStrList.size() > 3) {
                shapeStrList = shapeStrList.subList(0, 3);
            }
            String[] shape = new String[shapeStrList.size()];
            shape = shapeStrList.toArray(shape);
            recipeRegistries.add(new ShapedRecipeRegistry(key, namespacedKey, result)
                .setShape(shape)
                .setRecipeChoiceMap(recipeChoiceMap)
                .setCraftingBookCategory(category)
            );
        }
        return recipeRegistries;
    }

    public static List<RecipeRegister> newShapelessRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        List<String> itemStrList = config.getStringList("source");
        List<RecipeChoice> recipeChoiceList = new ArrayList<>();
        for (String itemStr : itemStrList) {
            recipeChoiceList.add(getRecipeChoice(itemStr));
        }
        CraftingBookCategory category = null;
        if (config.contains("category")) {
            String categoryStr = config.getString("category").toUpperCase();
            category = CraftingBookCategory.valueOf(categoryStr);
        }
        RecipeRegister recipeRegistry = new ShapelessRecipeRegistry(key, namespacedKey, result)
            .setChoiceList(recipeChoiceList)
            .setCraftingBookCategory(category);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegister> newMultipleShapelessRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<?> itemsList = config.getList("source", new ArrayList<>());
        List<RecipeRegister> recipeRegistries = new ArrayList<>();
        CraftingBookCategory category = null;
        if (config.contains("category")) {
            String categoryStr = config.getString("category").toUpperCase();
            category = CraftingBookCategory.valueOf(categoryStr);
        }

        for (int i = 0; i < itemsList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            List<String> itemStrList = (List<String>) itemsList.get(i);
            List<RecipeChoice> choiceList = new ArrayList<>();
            for (String itemStr : itemStrList) {
                choiceList.add(getRecipeChoice(itemStr));
            }
            recipeRegistries.add(new ShapelessRecipeRegistry(key, namespacedKey, result)
                .setChoiceList(choiceList)
                .setCraftingBookCategory(category)
            );
        }
        return recipeRegistries;
    }

    public static List<RecipeRegister> newCookingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        String choiceStr = config.getString("source.item", "");
        String cookingBlock = config.getString("source.block", "furnace");
        RecipeChoice source = getRecipeChoice(choiceStr);
        float exp = (float) config.getDouble("exp", 0);
        int time = config.getInt("time", 200);

        CookingBookCategory category = null;
        if (config.contains("category")) {
            String categoryStr = config.getString("category").toUpperCase();
            category = CookingBookCategory.valueOf(categoryStr);
        }
        RecipeRegister recipeRegistry = new CookingRecipeRegistry(key, namespacedKey, result)
            .setCookingBlock(cookingBlock)
            .setSource(source)
            .setExp(exp)
            .setTime(time)
            .setCookingBookCategory(category);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegister> newMultipleCookingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        float globalExp = (float) config.getDouble("exp", 0);
        int globalTime = config.getInt("time", 200);
        List<RecipeRegister> recipeRegistries = new ArrayList<>();

        CookingBookCategory category = null;
        if (config.contains("category")) {
            String categoryStr = config.getString("category").toUpperCase();
            category = CookingBookCategory.valueOf(categoryStr);
        }
        List<Map<?, ?>> sourceList = config.getMapList("source");
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            RecipeChoice source = getRecipeChoice((String) map.get("item"));
            String cookingBlock = (String) map.get("block");
            float exp = map.containsKey("exp") ? Float.parseFloat(String.valueOf(map.get("exp"))) : globalExp;
            int time = map.containsKey("time") ? (Integer) map.get("time") : globalTime;
            recipeRegistries.add(new CookingRecipeRegistry(key, namespacedKey, result)
                .setCookingBlock(cookingBlock)
                .setSource(source)
                .setExp(exp)
                .setTime(time)
                .setCookingBookCategory(category)
            );
        }
        return recipeRegistries;
    }


    private static List<RecipeRegister> newRandomCookingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        String choiceStr = config.getString("source.item", "");
        String cookingBlock = config.getString("source.block", "furnace");
        RecipeChoice source = getRecipeChoice(choiceStr);
        float exp = (float) config.getDouble("exp", 0);
        int time = config.getInt("time", 200);
        RecipeRegister recipeRegistry = new RandomCookingRecipeRegistry(key, namespacedKey, result).setCookingBlock(cookingBlock).setSource(source).setExp(exp).setTime(time);
        return Collections.singletonList(recipeRegistry);
    }

    private static List<RecipeRegister> newMultipleRandomCookingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        float globalExp = (float) config.getDouble("exp", 0);
        int globalTime = config.getInt("time", 200);
        List<RecipeRegister> recipeRegistries = new ArrayList<>();

        List<Map<?, ?>> sourceList = config.getMapList("source");
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            RecipeChoice source = getRecipeChoice((String) map.get("item"));
            String cookingBlock = (String) map.get("block");
            float exp = map.containsKey("exp") ? Float.parseFloat(String.valueOf(map.get("exp"))) : globalExp;
            int time = map.containsKey("time") ? (Integer) map.get("time") : globalTime;
            recipeRegistries.add(new RandomCookingRecipeRegistry(key, namespacedKey, result).setCookingBlock(cookingBlock).setSource(source).setExp(exp).setTime(time));
        }
        return recipeRegistries;
    }

    public static List<RecipeRegister> newSmithingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        RecipeChoice base = getRecipeChoice(config.getString("source.base", ""));
        RecipeChoice addition = getRecipeChoice(config.getString("source.addition", ""));
        boolean copyNbt = config.getBoolean("source.copy_nbt", false);
        boolean copyEnchantments = config.getBoolean("source.copy_enchantments", true);
        RecipeRegister recipeRegistry;
        RecipeChoice template = getRecipeChoice(config.getString("source.template", ""));
        SmithingRecipeRegistry.SmithingType type = SmithingRecipeRegistry.SmithingType.valueOf(config.getString("source.type", "default").toUpperCase());
        recipeRegistry = new SmithingRecipeRegistry(key, namespacedKey, result).setSmithingType(type).setTemplate(template).setBase(base).setAddition(addition).setCopyNbt(copyNbt).setCopyEnchantments(copyEnchantments);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegister> newMultipleSmithingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        List<RecipeRegister> recipeRegistries = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            RecipeChoice base = getRecipeChoice((String) map.get("base"));
            RecipeChoice addition = getRecipeChoice((String) map.get("addition"));
            boolean copyNbt = map.containsKey("copy_nbt") ? (Boolean) map.get("copy_nbt") : false;
            boolean copyEnchantments = map.containsKey("copy_enchantments") ? (Boolean) map.get("copy_enchantments") : true;
            String typeStr = (String) map.get("type");
            if (typeStr == null) {
                typeStr = "DEFAULT";
            }
            RecipeChoice template = getRecipeChoice((String) map.get("template"));
            SmithingRecipeRegistry.SmithingType type = SmithingRecipeRegistry.SmithingType.valueOf(typeStr.toUpperCase());
            recipeRegistries.add(new SmithingRecipeRegistry(key, namespacedKey, result).setSmithingType(type).setTemplate(template).setBase(base).setAddition(addition).setCopyNbt(copyNbt).setCopyEnchantments(copyEnchantments));
        }
        return recipeRegistries;
    }

    public static List<RecipeRegister> newStoneCuttingRecipe(YamlConfiguration config, String key) {
        RecipeChoice choice = getRecipeChoice(config.getString("source", ""));

        if (config.isList("result")) {
            List<String> resultList = config.getStringList("result");
            List<RecipeRegister> recipeRegistries = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                ItemStack result = ItemManager.INSTANCE.matchItem(resultList.get(i));
                String fullKey = key + "." + i;
                NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
                recipeRegistries.add(new StoneCuttingRecipeRegistry(key, namespacedKey, result).setSource(choice));;
            }
            return recipeRegistries;
        } else {
            ItemStack result = getResultItem(config);
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
            return Collections.singletonList(new StoneCuttingRecipeRegistry(key, namespacedKey, result).setSource(choice));
        }
    }

    public static List<RecipeRegister> newMultipleStoneCuttingRecipe(YamlConfiguration config, String key) {
        if (config.isList("result")) {
            List<String> resultList = config.getStringList("result");
            List<String> sourceList = config.getStringList("source");
            List<RecipeRegister> recipeRegistries = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                ItemStack result = ItemManager.INSTANCE.matchItem(resultList.get(i));
                for (int j = 0; j < sourceList.size(); j++) {
                    String fullKey = String.format(key + ".%d.%d", i, j);
                    NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
                    RecipeChoice source = getRecipeChoice(sourceList.get(j));
                    recipeRegistries.add(new StoneCuttingRecipeRegistry(key, namespacedKey, result).setSource(source));
                }
            }
            return recipeRegistries;
        } else {
            ItemStack result = getResultItem(config);
            List<String> sourceList = config.getStringList("source");
            List<RecipeRegister> recipeRegistries = new ArrayList<>();
            for (int i = 0; i < sourceList.size(); i++) {
                String fullKey = key + "." + i;
                NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
                RecipeChoice source = getRecipeChoice(sourceList.get(i));
                recipeRegistries.add(new StoneCuttingRecipeRegistry(key, namespacedKey, result).setSource(source));
            }
            return recipeRegistries;
        }
    }

    public static List<RecipeRegister> newPotionMixRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        RecipeChoice input = getRecipeChoice(config.getString("source.input", ""));
        RecipeChoice ingredient = getRecipeChoice(config.getString("source.ingredient", ""));
        return Collections.singletonList(new PotionMixRecipeRegistry(key, namespacedKey, result).setInput(input).setIngredient(ingredient));
    }

    public static List<RecipeRegister> newMultiplePotionMixRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        List<RecipeRegister> recipeRegistries = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            RecipeChoice input = getRecipeChoice((String) map.get("input"));
            RecipeChoice ingredient = getRecipeChoice((String) map.get("ingredient"));
            recipeRegistries.add(new PotionMixRecipeRegistry(key, namespacedKey, result).setInput(input).setIngredient(ingredient));
        }
        return recipeRegistries;
    }

    public static List<RecipeRegister> newAnvilRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        ItemStack base = ItemManager.INSTANCE.matchItem(config.getString("source.base", ""));
        ItemStack addition = ItemManager.INSTANCE.matchItem(config.getString("source.addition", ""));
        int costLevel = config.getInt("source.cost_level", 0);
        boolean copyNbt = config.getBoolean("source.copy_nbt", false);
        boolean copyEnchantments = config.getBoolean("source.copy_enchantments", true);
        RecipeRegister recipeRegistry = new AnvilRecipeRegistry(key, namespacedKey, result)
            .setBase(base)
            .setAddition(addition)
            .setCopyNbt(copyNbt)
            .setCopyEnchantments(copyEnchantments)
            .setCostLevel(costLevel);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegister> newMultipleAnvilRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        List<RecipeRegister> recipeRegistries = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            ItemStack base = ItemManager.INSTANCE.matchItem((String) map.get("base"));
            ItemStack addition = ItemManager.INSTANCE.matchItem((String) map.get("addition"));
            int costLevel = map.containsKey("cost_level") ? (Integer) map.get("cost_level") : 0;
            boolean copyNbt = map.containsKey("copy_nbt") ? (Boolean) map.get("copy_nbt") : false;
            boolean copyEnchantments = map.containsKey("copy_enchantments") ? (Boolean) map.get("copy_enchantments") : true;
            recipeRegistries.add(new AnvilRecipeRegistry(key, namespacedKey, result)
                .setBase(base)
                .setAddition(addition)
                .setCopyNbt(copyNbt)
                .setCopyEnchantments(copyEnchantments)
                .setCostLevel(costLevel)
            );
        }
        return recipeRegistries;
    }

    private static Map<Character, RecipeChoice> getShapedRecipeChoiceMap(ConfigurationSection section) {
        Map<Character, RecipeChoice> recipeChoiceMap = new HashMap<>();
        if (section == null)
            return recipeChoiceMap;
        for (String key : section.getKeys(false)) {
            char keyWord = key.toCharArray()[0];
            String itemStr = section.getString(key, "");
            if (itemStr.isEmpty()) {
                throw new IllegalArgumentException("Empty recipe ingredient: " + key);
            }
            recipeChoiceMap.put(keyWord, getRecipeChoice(itemStr));
        }
        return recipeChoiceMap;
    }

    private static ItemStack getResultItem(YamlConfiguration config) {
        String resultStr;
        if (config.getString("type", "shaped.yml").equals("random_cooking")) {
            String tmpStr = config.getStringList("result").get(0);
            tmpStr = tmpStr.substring(0, tmpStr.lastIndexOf(" "));
            resultStr = tmpStr;
        }
        else {
            resultStr = config.getString("result", "");
        }

        if (resultStr.isEmpty()) {
            return null;
        }
        return ItemManager.INSTANCE.matchItem(resultStr);
    }

    public static RecipeChoice getRecipeChoice(String itemStr) {
        if (!itemStr.contains(":")) {
            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                throw new IllegalArgumentException(itemStr + " is a not exist item type");
            }
            return new RecipeChoice.MaterialChoice(material);
        }
        int index = itemStr.indexOf(":");
        String namespace = itemStr.substring(0, index);
        namespace = namespace.toLowerCase();
        switch (namespace) {
            case "minecraft":
                Material material = Material.matchMaterial(itemStr);
                if (material == null) {
                    throw new IllegalArgumentException(itemStr + " is a not exist item type");
                }
                return new RecipeChoice.MaterialChoice(material);
            case "tag":
                String tagStr = itemStr.substring(4).toUpperCase(Locale.ROOT);
                Tag<Material> materialTag;
                try {
                    Field field = Tag.class.getField(tagStr);
                    materialTag = (Tag<Material>) field.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return new RecipeChoice.MaterialChoice(materialTag);
            default:
                ItemStack item = ItemManager.INSTANCE.matchItem(itemStr).clone();
                item.setAmount(1);
                return new RecipeChoice.ExactChoice(item);
        }
    }

}
