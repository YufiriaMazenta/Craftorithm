package pers.yufiria.craftorithm;

import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Logger;

/**
 * 旧版本配方文件格式迁移器
 * 仅在插件 ENABLE 阶段执行一次
 */
@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE, priority = -1)
    }
)
public enum LegacyRecipeMigrator implements BukkitLifeCycleTask {

    INSTANCE;

    private static final String BACKUP_FOLDER_NAME = "legacy_backup";
    private File recipeFolder;
    private File backupFolder;
    private Logger logger;
    private int migratedCount = 0;
    private int skippedCount = 0;
    private int errorCount = 0;

    /**
     * 检测并迁移旧版配方文件
     *
     * @return 是否执行了迁移（true 表示有旧版文件被处理）
     */
    public boolean migrateIfNeeded() {
        this.recipeFolder = RecipeManager.INSTANCE.RECIPE_FILE_FOLDER;
        this.logger = Craftorithm.instance().getLogger();

        if (!recipeFolder.exists()) {
            return false;
        }

        List<File> yamlFiles = IOHelper.allYamlFiles(recipeFolder);
        List<File> legacyFiles = new ArrayList<>();

        // 检测旧版配方文件（包含 source 配置项的文件）
        for (File file : yamlFiles) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (isLegacyFormat(config)) {
                    legacyFiles.add(file);
                }
            } catch (Exception e) {
                // 跳过无法读取的文件
            }
        }

        if (legacyFiles.isEmpty()) {
            return false;
        }

        logger.info("[Craftorithm] Detected " + legacyFiles.size() + " legacy recipe file(s), starting migration...");

        // 创建备份文件夹
        this.backupFolder = new File(Craftorithm.instance().getDataFolder(), BACKUP_FOLDER_NAME);
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        for (File legacyFile : legacyFiles) {
            try {
                migrateFile(legacyFile);
            } catch (Exception e) {
                errorCount++;
                logger.warning("[Craftorithm] Failed to migrate " + legacyFile.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        logger.info("[Craftorithm] Migration complete: " + migratedCount + " migrated, " + skippedCount + " skipped, " + errorCount + " errors");
        return true;
    }

    /**
     * 判断一个配置文件是否为旧版格式
     * 旧版配方的特征是包含 source 配置项
     */
    private boolean isLegacyFormat(YamlConfiguration config) {
        // 旧版配方基本都有 source 配置项
        return config.contains("source");
    }

    private boolean isNewFormatType(String type) {
        return switch (type) {
            case "vanilla_shaped", "vanilla_shapeless",
                 "vanilla_smelting_furnace", "vanilla_smelting_blast",
                 "vanilla_smelting_smoker", "vanilla_smelting_campfire",
                 "vanilla_smithing_transform", "vanilla_smithing_trim",
                 "vanilla_stonecutting", "vanilla_brewing", "anvil" -> true;
            default -> false;
        };
    }

    /**
     * 迁移单个旧版配方文件
     */
    private void migrateFile(File legacyFile) throws IOException {
        // 获取相对于 recipes 文件夹的路径
        String relativePath = IOHelper.getRelativeFileName(recipeFolder, legacyFile);
        String baseName = relativePath.endsWith(".yml")
            ? relativePath.substring(0, relativePath.length() - 4)
            : relativePath;

        // 备份原始文件
        File backupFile = new File(backupFolder, relativePath);
        backupFile.getParentFile().mkdirs();
        Files.copy(legacyFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(legacyFile);
        String detectedType = detectRecipeType(config);
        boolean isMultiple = config.getBoolean("multiple", false);

        if (isMultiple) {
            // 多配方模式：拆分为多个文件
            migrateMultipleRecipes(legacyFile, baseName, config, detectedType);
        } else if ("stone_cutting".equals(detectedType) && config.isList("result")) {
            // 切石机配方 result 为列表时也要拆分（新版不支持多结果）
            migrateMultipleRecipes(legacyFile, baseName, config, detectedType);
        } else {
            // 单配方模式：直接转换
            Map<String, Object> newConfig = convertRecipe(config, detectedType);
            if (newConfig != null) {
                writeNewRecipeFile(legacyFile, newConfig);
                migratedCount++;
            } else {
                skippedCount++;
            }
        }
    }

    /**
     * 检测旧版配方的类型
     */
    private String detectRecipeType(YamlConfiguration config) {
        String type = config.getString("type", "");
        if (!type.isEmpty()) {
            return type;
        }
        // 没有 type 字段时，根据 source 的结构推断类型
        Object source = config.get("source");
        if (source instanceof ConfigurationSection sourceSection) {
            if (sourceSection.contains("block")) {
                return "cooking";
            }
            if (sourceSection.contains("base") && sourceSection.contains("addition")) {
                return "smithing";
            }
            if (sourceSection.contains("input") && sourceSection.contains("ingredient")) {
                return "potion";
            }
        }
        // 有 shape 的是有序配方
        if (config.contains("shape")) {
            return "shaped";
        }
        // source 为简单字符串且没有 shape，可能是切石机配方
        if (source instanceof String) {
            return "stone_cutting";
        }
        // 默认为无序配方
        return "shapeless";
    }

    /**
     * 迁移多配方文件，将每个配方拆分为独立文件
     */
    private void migrateMultipleRecipes(File legacyFile, String baseName, YamlConfiguration config, String detectedType) {
        Object source = config.get("source");
        if (source == null) {
            skippedCount++;
            return;
        }

        List<Map<String, Object>> recipeEntries = splitMultipleRecipes(config, detectedType, source);
        if (recipeEntries.isEmpty()) {
            skippedCount++;
            return;
        }

        for (int i = 0; i < recipeEntries.size(); i++) {
            String newFileName = baseName + "_" + i + ".yml";
            File newFile = new File(recipeFolder, newFileName);

            // 确保不覆盖已存在的文件
            if (newFile.exists()) {
                newFile = new File(recipeFolder, baseName + "_migrated_" + i + ".yml");
            }

            Map<String, Object> recipeData = recipeEntries.get(i);
            YamlConfiguration newConfig = new YamlConfiguration();
            for (Map.Entry<String, Object> entry : recipeData.entrySet()) {
                newConfig.set(entry.getKey(), entry.getValue());
            }

            try {
                newConfig.save(newFile);
                migratedCount++;
            } catch (IOException e) {
                errorCount++;
                logger.warning("[Craftorithm] Failed to write migrated recipe file: " + newFile.getName());
            }
        }

        // 删除旧文件
        legacyFile.delete();
    }

    /**
     * 将 multiple 模式的配方拆分为多个独立的配方数据
     */
    private List<Map<String, Object>> splitMultipleRecipes(YamlConfiguration config, String type, Object source) {
        List<Map<String, Object>> results = new ArrayList<>();

        // 获取公共配置
        String resultValue = config.getString("result", "");
        int sortId = config.getInt("sort_id", 0);

        switch (type) {
            case "shaped" -> {
                List<?> shapesList = config.getList("shape");
                Map<String, String> sourceMap = asStringMap(source);
                if (shapesList == null || sourceMap == null) {
                    break;
                }
                // multiple shaped: shape 是列表的列表，source 是共享的
                for (int i = 0; i < shapesList.size(); i++) {
                    Object shapeEntry = shapesList.get(i);
                    Map<String, Object> recipe = new LinkedHashMap<>();
                    recipe.put("type", "vanilla_shaped");
                    recipe.put("result", resultValue);
                    if (shapeEntry instanceof List<?> shapeLines) {
                        recipe.put("shape", shapeLines);
                        // 只保留 shape 中实际出现的字母对应的 ingredients
                        Set<Character> usedChars = new HashSet<>();
                        for (Object line : shapeLines) {
                            for (char c : line.toString().toCharArray()) {
                                usedChars.add(c);
                            }
                        }
                        Map<String, String> ingredients = new LinkedHashMap<>();
                        for (Map.Entry<String, String> e : sourceMap.entrySet()) {
                            if (!e.getKey().isEmpty() && usedChars.contains(e.getKey().charAt(0))) {
                                ingredients.put(e.getKey(), e.getValue());
                            }
                        }
                        recipe.put("ingredients", ingredients);
                    }
                    if (sortId > 0) recipe.put("sort_id", sortId);
                    results.add(recipe);
                }
            }
            case "shapeless" -> {
                if (!(source instanceof List<?> sourceList)) break;
                for (int i = 0; i < sourceList.size(); i++) {
                    Object entry = sourceList.get(i);
                    Map<String, Object> recipe = new LinkedHashMap<>();
                    recipe.put("type", "vanilla_shapeless");
                    recipe.put("result", resultValue);
                    if (entry instanceof List<?> items) {
                        recipe.put("ingredients", items);
                    }
                    if (sortId > 0) recipe.put("sort_id", sortId);
                    results.add(recipe);
                }
            }
            case "cooking" -> {
                if (!(source instanceof List<?> sourceList)) break;
                int globalTime = config.getInt("time", 200);
                double globalExp = config.getDouble("exp", 0);
                for (Object entry : sourceList) {
                    Map<String, Object> entryMap = asObjectMap(entry);
                    if (entryMap == null) continue;
                    Map<String, Object> recipe = new LinkedHashMap<>();
                    String block = Objects.toString(entryMap.get("block"), "furnace");
                    recipe.put("type", mapCookingType(block));
                    recipe.put("result", resultValue);
                    recipe.put("ingredient", Objects.toString(entryMap.get("item"), ""));
                    recipe.put("time", entryMap.containsKey("time") ? ((Number) entryMap.get("time")).intValue() : globalTime);
                    recipe.put("exp", entryMap.containsKey("exp") ? ((Number) entryMap.get("exp")).doubleValue() : globalExp);
                    if (sortId > 0) recipe.put("sort_id", sortId);
                    results.add(recipe);
                }
            }
            case "smithing" -> {
                if (!(source instanceof List<?> sourceList)) break;
                for (Object entry : sourceList) {
                    Map<String, Object> entryMap = asObjectMap(entry);
                    if (entryMap == null) continue;
                    Map<String, Object> recipe = convertSmithingEntryMap(entryMap, resultValue, config);
                    if (recipe != null) {
                        if (sortId > 0) recipe.put("sort_id", sortId);
                        results.add(recipe);
                    }
                }
            }
            case "stone_cutting" -> {
                List<?> resultList = config.getList("result");
                boolean hasMultipleResults = resultList != null && !resultList.isEmpty();

                if (source instanceof List<?> sourceList) {
                    // source 是列表
                    if (hasMultipleResults) {
                        // 多个 source × 多个 result，每个组合一个文件
                        for (Object srcItem : sourceList) {
                            for (Object resItem : resultList) {
                                Map<String, Object> recipe = new LinkedHashMap<>();
                                recipe.put("type", "vanilla_stonecutting");
                                recipe.put("result", resItem.toString());
                                recipe.put("ingredient", srcItem.toString());
                                if (sortId > 0) recipe.put("sort_id", sortId);
                                results.add(recipe);
                            }
                        }
                    } else {
                        for (Object srcItem : sourceList) {
                            Map<String, Object> recipe = new LinkedHashMap<>();
                            recipe.put("type", "vanilla_stonecutting");
                            recipe.put("result", resultValue);
                            recipe.put("ingredient", srcItem.toString());
                            if (sortId > 0) recipe.put("sort_id", sortId);
                            results.add(recipe);
                        }
                    }
                } else {
                    // source 是单个值，但 result 是列表
                    String sourceItem = source.toString();
                    if (hasMultipleResults) {
                        for (Object resItem : resultList) {
                            Map<String, Object> recipe = new LinkedHashMap<>();
                            recipe.put("type", "vanilla_stonecutting");
                            recipe.put("result", resItem.toString());
                            recipe.put("ingredient", sourceItem);
                            if (sortId > 0) recipe.put("sort_id", sortId);
                            results.add(recipe);
                        }
                    }
                }
            }
            case "potion" -> {
                if (!(source instanceof List<?> sourceList)) break;
                for (Object entry : sourceList) {
                    Map<String, Object> entryMap = asObjectMap(entry);
                    if (entryMap == null) continue;
                    Map<String, Object> recipe = new LinkedHashMap<>();
                    recipe.put("type", "vanilla_brewing");
                    recipe.put("result", resultValue);
                    recipe.put("input", Objects.toString(entryMap.get("input"), ""));
                    recipe.put("ingredient", Objects.toString(entryMap.get("ingredient"), ""));
                    if (sortId > 0) recipe.put("sort_id", sortId);
                    results.add(recipe);
                }
            }
            case "anvil" -> {
                if (!(source instanceof List<?> sourceList)) break;
                for (Object entry : sourceList) {
                    Map<String, Object> entryMap = asObjectMap(entry);
                    if (entryMap == null) continue;
                    Map<String, Object> recipe = convertAnvilEntryMap(entryMap, resultValue, config);
                    if (recipe != null) {
                        if (sortId > 0) recipe.put("sort_id", sortId);
                        results.add(recipe);
                    }
                }
            }
            case "random_cooking" -> {
                logger.warning("[Craftorithm] random_cooking recipe is no longer supported in the new version.");
            }
        }

        return results;
    }

    /**
     * 转换单个旧版配方为新版格式
     */
    private Map<String, Object> convertRecipe(YamlConfiguration config, String type) {
        String resultValue = config.getString("result", "");
        Object source = config.get("source");
        int sortId = config.getInt("sort_id", 0);

        Map<String, Object> recipe = new LinkedHashMap<>();

        switch (type) {
            case "shaped" -> {
                recipe.put("type", "vanilla_shaped");
                recipe.put("result", resultValue);
                List<?> shapeList = config.getList("shape");
                if (shapeList != null) {
                    recipe.put("shape", shapeList);
                    // 收集 shape 中实际出现的字符
                    Set<Character> usedChars = new HashSet<>();
                    for (Object line : shapeList) {
                        for (char c : line.toString().toCharArray()) {
                            usedChars.add(c);
                        }
                    }
                    if (source instanceof ConfigurationSection sourceSection) {
                        Map<String, String> ingredients = new LinkedHashMap<>();
                        for (String key : sourceSection.getKeys(false)) {
                            if (!key.isEmpty() && usedChars.contains(key.charAt(0))) {
                                ingredients.put(key, sourceSection.getString(key));
                            }
                        }
                        recipe.put("ingredients", ingredients);
                    }
                }
            }
            case "shapeless" -> {
                recipe.put("type", "vanilla_shapeless");
                recipe.put("result", resultValue);
                if (source instanceof List<?> sourceList) {
                    recipe.put("ingredients", sourceList);
                }
            }
            case "cooking" -> {
                Map<String, Object> sourceMap = asObjectMap(source);
                if (sourceMap != null) {
                    String block = Objects.toString(sourceMap.get("block"), "furnace");
                    recipe.put("type", mapCookingType(block));
                    recipe.put("result", resultValue);
                    recipe.put("ingredient", Objects.toString(sourceMap.get("item"), ""));
                    recipe.put("time", config.getInt("time", 200));
                    recipe.put("exp", config.getDouble("exp", 0));
                }
            }
            case "smithing" -> {
                Map<String, Object> sourceMap = asObjectMap(source);
                if (sourceMap != null) {
                    return convertSmithingEntryMap(sourceMap, resultValue, config);
                }
                return null;
            }
            case "stone_cutting" -> {
                recipe.put("type", "vanilla_stonecutting");
                List<?> resultList = config.getList("result");
                if (resultList != null && !resultList.isEmpty()) {
                    // 多结果的切石机配方，每个 result 拆为一个文件
                    recipe.put("result", resultList.get(0).toString());
                } else {
                    recipe.put("result", resultValue);
                }
                if (source instanceof List<?> sourceList) {
                    if (!sourceList.isEmpty()) {
                        recipe.put("ingredient", sourceList.get(0).toString());
                    }
                } else {
                    recipe.put("ingredient", source.toString());
                }
            }
            case "potion" -> {
                recipe.put("type", "vanilla_brewing");
                recipe.put("result", resultValue);
                Map<String, Object> sourceMap = asObjectMap(source);
                if (sourceMap != null) {
                    recipe.put("input", Objects.toString(sourceMap.get("input"), ""));
                    recipe.put("ingredient", Objects.toString(sourceMap.get("ingredient"), ""));
                }
            }
            case "anvil" -> {
                Map<String, Object> sourceMap = asObjectMap(source);
                if (sourceMap != null) {
                    return convertAnvilEntryMap(sourceMap, resultValue, config);
                }
                return null;
            }
            case "random_cooking" -> {
                logger.warning("[Craftorithm] random_cooking recipe is no longer supported in the new version, skipping.");
                return null;
            }
            default -> {
                logger.warning("[Craftorithm] Unknown legacy recipe type: " + type);
                return null;
            }
        }

        if (sortId > 0) recipe.put("sort_id", sortId);
        return recipe;
    }

    /**
     * 转换旧版 smithing 配方条目
     */
    private Map<String, Object> convertSmithingEntry(ConfigurationSection sourceSection, String resultValue, YamlConfiguration config) {
        Map<String, Object> recipe = new LinkedHashMap<>();
        String smithingType = sourceSection.getString("type", "");
        boolean isTrim = "trim".equalsIgnoreCase(smithingType);

        recipe.put("type", isTrim ? "vanilla_smithing_trim" : "vanilla_smithing_transform");
        if (!isTrim) {
            recipe.put("result", resultValue);
        }
        recipe.put("base", sourceSection.getString("base", ""));
        recipe.put("addition", sourceSection.getString("addition", ""));

        if (sourceSection.contains("template")) {
            recipe.put("template", sourceSection.getString("template"));
        }

        // 旧版 copy_nbt -> 新版 copy_components_rules
        if (sourceSection.contains("copy_nbt") && sourceSection.getBoolean("copy_nbt")) {
            List<String> rules = new ArrayList<>();
            rules.add("all");
            recipe.put("copy_components_rules", rules);
        }

        return recipe;
    }

    /**
     * 转换旧版 anvil 配方条目
     */
    private Map<String, Object> convertAnvilEntry(ConfigurationSection sourceSection, String resultValue, YamlConfiguration config) {
        Map<String, Object> recipe = new LinkedHashMap<>();
        recipe.put("type", "anvil");
        recipe.put("result", resultValue);
        recipe.put("base", sourceSection.getString("base", ""));
        recipe.put("addition", sourceSection.getString("addition", ""));

        if (sourceSection.contains("cost_level")) {
            recipe.put("cost_level", sourceSection.getInt("cost_level"));
        }

        // 旧版 copy_nbt -> 新版 copy_components_rules
        if (sourceSection.contains("copy_nbt") && sourceSection.getBoolean("copy_nbt")) {
            List<String> rules = new ArrayList<>();
            rules.add("all");
            recipe.put("copy_components_rules", rules);
        }

        return recipe;
    }

    /**
     * 将 ConfigurationSection 或 Map 统一转换为 Map<String, Object>
     */
    private Map<String, Object> asObjectMap(Object obj) {
        if (obj instanceof ConfigurationSection section) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (String key : section.getKeys(false)) {
                map.put(key, section.get(key));
            }
            return map;
        }
        if (obj instanceof Map<?, ?> raw) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue());
            }
            return map;
        }
        return null;
    }

    /**
     * 将 ConfigurationSection 或 Map 统一转换为 Map<String, String>
     */
    private Map<String, String> asStringMap(Object obj) {
        if (obj instanceof ConfigurationSection section) {
            Map<String, String> map = new LinkedHashMap<>();
            for (String key : section.getKeys(false)) {
                map.put(key, section.getString(key));
            }
            return map;
        }
        if (obj instanceof Map<?, ?> raw) {
            Map<String, String> map = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue().toString());
            }
            return map;
        }
        return null;
    }

    /**
     * 转换旧版 smithing 配方条目（接受 Map）
     */
    private Map<String, Object> convertSmithingEntryMap(Map<String, Object> sourceMap, String resultValue, YamlConfiguration config) {
        Map<String, Object> recipe = new LinkedHashMap<>();
        String smithingType = Objects.toString(sourceMap.get("type"), "");
        boolean isTrim = "trim".equalsIgnoreCase(smithingType);

        recipe.put("type", isTrim ? "vanilla_smithing_trim" : "vanilla_smithing_transform");
        if (!isTrim) {
            recipe.put("result", resultValue);
        }
        recipe.put("base", Objects.toString(sourceMap.get("base"), ""));
        recipe.put("addition", Objects.toString(sourceMap.get("addition"), ""));

        if (sourceMap.containsKey("template")) {
            recipe.put("template", Objects.toString(sourceMap.get("template"), ""));
        }

        // 旧版 copy_nbt -> 新版 copy_components_rules
        Object copyNbt = sourceMap.get("copy_nbt");
        if (copyNbt instanceof Boolean b && b) {
            List<String> rules = new ArrayList<>();
            rules.add("all");
            recipe.put("copy_components_rules", rules);
        }

        return recipe;
    }

    /**
     * 转换旧版 anvil 配方条目（接受 Map）
     */
    private Map<String, Object> convertAnvilEntryMap(Map<String, Object> sourceMap, String resultValue, YamlConfiguration config) {
        Map<String, Object> recipe = new LinkedHashMap<>();
        recipe.put("type", "anvil");
        recipe.put("result", resultValue);
        recipe.put("base", Objects.toString(sourceMap.get("base"), ""));
        recipe.put("addition", Objects.toString(sourceMap.get("addition"), ""));

        if (sourceMap.containsKey("cost_level")) {
            Object costLevel = sourceMap.get("cost_level");
            if (costLevel instanceof Number n) {
                recipe.put("cost_level", n.intValue());
            }
        }

        // 旧版 copy_nbt -> 新版 copy_components_rules
        Object copyNbt = sourceMap.get("copy_nbt");
        if (copyNbt instanceof Boolean b && b) {
            List<String> rules = new ArrayList<>();
            rules.add("all");
            recipe.put("copy_components_rules", rules);
        }

        return recipe;
    }

    /**
     * 将旧版的 cooking block 类型映射为新版的 type 值
     */
    private String mapCookingType(String block) {
        return switch (block.toLowerCase()) {
            case "blast_furnace", "blast" -> "vanilla_smelting_blast";
            case "smoker" -> "vanilla_smelting_smoker";
            case "campfire" -> "vanilla_smelting_campfire";
            default -> "vanilla_smelting_furnace";
        };
    }

    /**
     * 将转换后的配方数据写入新文件（替换旧文件）
     */
    private void writeNewRecipeFile(File originalFile, Map<String, Object> recipeData) throws IOException {
        YamlConfiguration newConfig = new YamlConfiguration();
        for (Map.Entry<String, Object> entry : recipeData.entrySet()) {
            newConfig.set(entry.getKey(), entry.getValue());
        }
        newConfig.save(originalFile);
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        migrateIfNeeded();
    }
}
