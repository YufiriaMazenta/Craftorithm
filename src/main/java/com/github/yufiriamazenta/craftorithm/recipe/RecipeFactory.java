package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.exception.UnsupportedVersionException;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.recipe.registry.impl.*;
import crypticlib.CrypticLib;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

public class RecipeFactory {

    private static final Map<RecipeType, BiFunction<YamlConfiguration, String, List<RecipeRegistry>>> recipeRegistryProviderMap;
    private static final Map<RecipeType, BiFunction<YamlConfiguration, String, List<RecipeRegistry>>> multipleRecipeRegistryProviderMap;

    static {
        recipeRegistryProviderMap = new HashMap<>();
        multipleRecipeRegistryProviderMap = new HashMap<>();

        recipeRegistryProviderMap.put(RecipeType.SHAPED, RecipeFactory::newShapedRecipe);
        recipeRegistryProviderMap.put(RecipeType.SHAPELESS, RecipeFactory::newShapelessRecipe);
        multipleRecipeRegistryProviderMap.put(RecipeType.SHAPED, RecipeFactory::newMultipleShapedRecipe);
        multipleRecipeRegistryProviderMap.put(RecipeType.SHAPELESS, RecipeFactory::newMultipleShapelessRecipe);
        if (CrypticLib.minecraftVersion() >= 11400) {
            recipeRegistryProviderMap.put(RecipeType.COOKING, RecipeFactory::newCookingRecipe);
            multipleRecipeRegistryProviderMap.put(RecipeType.COOKING, RecipeFactory::newMultipleCookingRecipe);
            recipeRegistryProviderMap.put(RecipeType.STONE_CUTTING, RecipeFactory::newStoneCuttingRecipe);
            multipleRecipeRegistryProviderMap.put(RecipeType.STONE_CUTTING, RecipeFactory::newMultipleStoneCuttingRecipe);
            recipeRegistryProviderMap.put(RecipeType.SMITHING, RecipeFactory::newSmithingRecipe);
            multipleRecipeRegistryProviderMap.put(RecipeType.SMITHING, RecipeFactory::newMultipleSmithingRecipe);
        }

        if (CrypticLib.minecraftVersion() >= 11700) {
            recipeRegistryProviderMap.put(RecipeType.RANDOM_COOKING, RecipeFactory::newRandomCookingRecipe);
            multipleRecipeRegistryProviderMap.put(RecipeType.RANDOM_COOKING, RecipeFactory::newMultipleRandomCookingRecipe);
        }

        if (RecipeManager.INSTANCE.supportPotionMix()) {
            recipeRegistryProviderMap.put(RecipeType.POTION, RecipeFactory::newPotionMixRecipe);
            multipleRecipeRegistryProviderMap.put(RecipeType.POTION, RecipeFactory::newMultiplePotionMixRecipe);
        }

        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            recipeRegistryProviderMap.put(RecipeType.ANVIL, RecipeFactory::newAnvilRecipe);
            multipleRecipeRegistryProviderMap.put(RecipeType.ANVIL, RecipeFactory::newMultipleAnvilRecipe);
        }
    }

    public static List<RecipeRegistry> newRecipeRegistry(YamlConfiguration config, String key) {
        key = key.toLowerCase(Locale.ROOT);
        String recipeTypeStr = config.getString("type", "shaped");
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
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

    public static List<RecipeRegistry> newShapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);

        List<String> shapeStrList = config.getStringList("shape");
        if (shapeStrList.size() > 3) {
            shapeStrList = shapeStrList.subList(0, 3);
        }
        String[] shape = new String[shapeStrList.size()];
        shape = shapeStrList.toArray(shape);
        RecipeRegistry recipeRegistry = new ShapedRecipeRegistry(key, namespacedKey, result).setShape(shape).setRecipeChoiceMap(recipeChoiceMap);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegistry> newMultipleShapedRecipe(YamlConfiguration config, String key) {
        Map<Character, RecipeChoice> recipeChoiceMap = getShapedRecipeChoiceMap(config.getConfigurationSection("source"));
        ItemStack result = getResultItem(config);
        List<?> shapeList = config.getList("shape", new ArrayList<>());
        List<RecipeRegistry> recipeRegistries = new ArrayList<>();
        for (int i = 0; i < shapeList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            List<String> shapeStrList = (List<String>) shapeList.get(i);
            if (shapeStrList.size() > 3) {
                shapeStrList = shapeStrList.subList(0, 3);
            }
            String[] shape = new String[shapeStrList.size()];
            shape = shapeStrList.toArray(shape);
            recipeRegistries.add(new ShapedRecipeRegistry(key, namespacedKey, result).setShape(shape).setRecipeChoiceMap(recipeChoiceMap));
        }
        return recipeRegistries;
    }

    public static List<RecipeRegistry> newShapelessRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        List<String> itemStrList = config.getStringList("source");
        List<RecipeChoice> recipeChoiceList = new ArrayList<>();
        for (String itemStr : itemStrList) {
            recipeChoiceList.add(getRecipeChoice(itemStr));
        }
        RecipeRegistry recipeRegistry = new ShapelessRecipeRegistry(key, namespacedKey, result).setChoiceList(recipeChoiceList);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegistry> newMultipleShapelessRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<?> itemsList = config.getList("source", new ArrayList<>());
        List<RecipeRegistry> recipeRegistries = new ArrayList<>();

        for (int i = 0; i < itemsList.size(); i++) {
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            List<String> itemStrList = (List<String>) itemsList.get(i);
            List<RecipeChoice> choiceList = new ArrayList<>();
            for (String itemStr : itemStrList) {
                choiceList.add(getRecipeChoice(itemStr));
            }
            recipeRegistries.add(new ShapelessRecipeRegistry(key, namespacedKey, result).setChoiceList(choiceList));
        }
        return recipeRegistries;
    }

    public static List<RecipeRegistry> newCookingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        String choiceStr = config.getString("source.item", "");
        String cookingBlock = config.getString("source.block", "furnace");
        RecipeChoice source = getRecipeChoice(choiceStr);
        float exp = (float) config.getDouble("exp", 0);
        int time = config.getInt("time", 200);
        RecipeRegistry recipeRegistry = new CookingRecipeRegistry(key, namespacedKey, result).setCookingBlock(cookingBlock).setSource(source).setExp(exp).setTime(time);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegistry> newMultipleCookingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        float globalExp = (float) config.getDouble("exp", 0);
        int globalTime = config.getInt("time", 200);
        List<RecipeRegistry> recipeRegistries = new ArrayList<>();

        List<Map<?, ?>> sourceList = config.getMapList("source");
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            RecipeChoice source = getRecipeChoice((String) map.get("item"));
            String cookingBlock = (String) map.get("block");
            float exp = map.containsKey("exp") ? Float.parseFloat(String.valueOf(map.get("exp"))) : globalExp;
            int time = map.containsKey("time") ? (Integer) map.get("time") : globalTime;
            recipeRegistries.add(new CookingRecipeRegistry(key, namespacedKey, result).setCookingBlock(cookingBlock).setSource(source).setExp(exp).setTime(time));
        }
        return recipeRegistries;
    }


    private static List<RecipeRegistry> newRandomCookingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        String choiceStr = config.getString("source.item", "");
        String cookingBlock = config.getString("source.block", "furnace");
        RecipeChoice source = getRecipeChoice(choiceStr);
        float exp = (float) config.getDouble("exp", 0);
        int time = config.getInt("time", 200);
        RecipeRegistry recipeRegistry = new RandomCookingRecipeRegistry(key, namespacedKey, result).setCookingBlock(cookingBlock).setSource(source).setExp(exp).setTime(time);
        return Collections.singletonList(recipeRegistry);
    }

    private static List<RecipeRegistry> newMultipleRandomCookingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        float globalExp = (float) config.getDouble("exp", 0);
        int globalTime = config.getInt("time", 200);
        List<RecipeRegistry> recipeRegistries = new ArrayList<>();

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

    public static List<RecipeRegistry> newSmithingRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        RecipeChoice base = getRecipeChoice(config.getString("source.base", ""));
        RecipeChoice addition = getRecipeChoice(config.getString("source.addition", ""));
        RecipeRegistry recipeRegistry;
        if (CrypticLib.minecraftVersion() >= 12000) {
            RecipeChoice template = getRecipeChoice(config.getString("source.template", ""));
            XSmithingRecipeRegistry.SmithingType type = XSmithingRecipeRegistry.SmithingType.valueOf(config.getString("source.type", "default").toUpperCase());
            recipeRegistry = new XSmithingRecipeRegistry(key, namespacedKey, result).setSmithingType(type).setTemplate(template).setBase(base).setAddition(addition);
        } else {
            recipeRegistry = new SmithingRecipeRegistry(key, namespacedKey, result).setBase(base).setAddition(addition);
        }

        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegistry> newMultipleSmithingRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        List<RecipeRegistry> recipeRegistries = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            RecipeChoice base = getRecipeChoice((String) map.get("base"));
            RecipeChoice addition = getRecipeChoice((String) map.get("addition"));
            String typeStr = (String) map.get("type");
            if (typeStr == null) {
                typeStr = "DEFAULT";
            }
            if (CrypticLib.minecraftVersion() >= 12000) {
                RecipeChoice template = getRecipeChoice((String) map.get("template"));
                XSmithingRecipeRegistry.SmithingType type = XSmithingRecipeRegistry.SmithingType.valueOf(typeStr.toUpperCase());
                recipeRegistries.add(new XSmithingRecipeRegistry(key, namespacedKey, result).setSmithingType(type).setTemplate(template).setBase(base).setAddition(addition));
            } else {
                recipeRegistries.add(new SmithingRecipeRegistry(key, namespacedKey, result).setBase(base).setAddition(addition));
            }
        }
        return recipeRegistries;
    }

    public static List<RecipeRegistry> newStoneCuttingRecipe(YamlConfiguration config, String key) {
        RecipeChoice choice = getRecipeChoice(config.getString("source", ""));

        if (config.isList("result")) {
            List<String> resultList = config.getStringList("result");
            List<RecipeRegistry> recipeRegistries = new ArrayList<>();
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

    public static List<RecipeRegistry> newMultipleStoneCuttingRecipe(YamlConfiguration config, String key) {
        if (config.isList("result")) {
            List<String> resultList = config.getStringList("result");
            List<String> sourceList = config.getStringList("source");
            List<RecipeRegistry> recipeRegistries = new ArrayList<>();
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
            List<RecipeRegistry> recipeRegistries = new ArrayList<>();
            for (int i = 0; i < sourceList.size(); i++) {
                String fullKey = key + "." + i;
                NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
                RecipeChoice source = getRecipeChoice(sourceList.get(i));
                recipeRegistries.add(new StoneCuttingRecipeRegistry(key, namespacedKey, result).setSource(source));
            }
            return recipeRegistries;
        }
    }

    public static List<RecipeRegistry> newPotionMixRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        RecipeChoice input = getRecipeChoice(config.getString("source.input", ""));
        RecipeChoice ingredient = getRecipeChoice(config.getString("source.ingredient", ""));
        return Collections.singletonList(new PotionMixRecipeRegistry(key, namespacedKey, result).setInput(input).setIngredient(ingredient));
    }

    public static List<RecipeRegistry> newMultiplePotionMixRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        List<RecipeRegistry> recipeRegistries = new ArrayList<>();
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

    public static List<RecipeRegistry> newAnvilRecipe(YamlConfiguration config, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), key);
        ItemStack result = getResultItem(config);
        ItemStack base = ItemManager.INSTANCE.matchItem(config.getString("source.base", ""));
        ItemStack addition = ItemManager.INSTANCE.matchItem(config.getString("source.addition", ""));
        int costLevel = config.getInt("source.cost_level", 0);
        boolean copyNbt = config.getBoolean("source.copy_nbt", true);
        RecipeRegistry recipeRegistry = new AnvilRecipeRegistry(key, namespacedKey, result).setBase(base).setAddition(addition).setCopyNbt(copyNbt).setCostLevel(costLevel);
        return Collections.singletonList(recipeRegistry);
    }

    public static List<RecipeRegistry> newMultipleAnvilRecipe(YamlConfiguration config, String key) {
        ItemStack result = getResultItem(config);
        List<Map<?, ?>> sourceList = config.getMapList("source");
        List<RecipeRegistry> recipeRegistries = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            Map<?, ?> map = sourceList.get(i);
            String fullKey = key + "." + i;
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), fullKey);
            ItemStack base = ItemManager.INSTANCE.matchItem((String) map.get("base"));
            ItemStack addition = ItemManager.INSTANCE.matchItem((String) map.get("addition"));
            int costLevel = map.containsKey("cost_level") ? (Integer) map.get("cost_level") : 0;
            boolean copyNbt = map.containsKey("copy_nbt") ? (Boolean) map.get("copy_nbt") : true;
            recipeRegistries.add(new AnvilRecipeRegistry(key, namespacedKey, result).setBase(base).setAddition(addition).setCopyNbt(copyNbt).setCostLevel(costLevel));
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
        if (config.getString("type", "shaped").equals("random_cooking")) {
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
        if (itemStr.contains(":")) {
            if (itemStr.startsWith("tag:")) {
                String tagStr = itemStr.substring(4).toUpperCase(Locale.ROOT);
                Tag<Material> materialTag;
                try {
                    Field field = Tag.class.getField(tagStr);
                    materialTag = (Tag<Material>) field.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return new RecipeChoice.MaterialChoice(materialTag);
            }
            ItemStack item = ItemManager.INSTANCE.matchItem(itemStr);
            return new RecipeChoice.ExactChoice(item);
        } else {
            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                throw new IllegalArgumentException(itemStr + " is a not exist item type");
            }
            return new RecipeChoice.MaterialChoice(material);
        }
    }

}
