package pers.yufiria.craftorithm.migrator;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.util.BukkitConfigHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.*;

/**
 * 旧版合成限制规则配置迁移器
 * <p>
 * 将 cannot_craft_items 和 blocked_crafting_lore_rules 迁移为新的 block_crafting_rules 格式
 */
@LifecycleTaskSettings(
    rules = {
        @LifecycleRule(lifeCycle = Lifecycle.INIT)
    }
)
public enum LegacyIngredientRestrictionMigrator implements LifecycleTask {

    INSTANCE;

    private static final String OLD_KEY_CANNOT_CRAFT = "cannot_craft_items";
    private static final String OLD_KEY_LORE_RULES = "blocked_crafting_lore_rules";
    private static final String NEW_KEY = "ingredient_restriction_rules";

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        BukkitConfigWrapper configWrapper = ((Craftorithm) plugin).getConfigWrapperOrCreate("config.yml");
        YamlConfiguration config = configWrapper.config();

        boolean hasCannotCraft = config.contains(OLD_KEY_CANNOT_CRAFT);
        boolean hasLoreRules = config.contains(OLD_KEY_LORE_RULES);

        if (!hasCannotCraft && !hasLoreRules) {
            return;
        }

        List<Map<String, Object>> newRules = new ArrayList<>();

        // 1. 迁移 cannot_craft_items
        if (hasCannotCraft) {
            List<String> items = config.getStringList(OLD_KEY_CANNOT_CRAFT);
            for (String itemId : items) {
                Map<String, Object> rule = new LinkedHashMap<>();
                rule.put("type", "item_id");
                rule.put("item_id", itemId);
                rule.put("recipes", List.of(".*"));
                newRules.add(rule);
            }
            CrypticLib.info("Migrating " + items.size() + " cannot_craft_items rule(s)...");
        }

        // 2. 迁移 blocked_crafting_lore_rules
        if (hasLoreRules) {
            List<Map<?, ?>> oldRules = config.getMapList(OLD_KEY_LORE_RULES);
            for (Map<?, ?> raw : oldRules) {
                ConfigurationSection oldRule = BukkitConfigHelper.map2ConfigSection(raw);
                String lore = oldRule.getString("lore", "");
                if (lore.isEmpty()) {
                    continue;
                }
                List<String> recipes = oldRule.getStringList("blocked_recipes");
                if (recipes.isEmpty()) {
                    continue;
                }
                Map<String, Object> rule = new LinkedHashMap<>();
                rule.put("type", "lore");
                rule.put("lore", lore);
                rule.put("recipes", recipes);
                newRules.add(rule);
            }
            CrypticLib.info("Migrating " + oldRules.size() + " blocked_crafting_lore_rules rule(s)...");
        }

        // 3. 合并到已有的 block_crafting_rules（追加，不覆盖）
        if (config.contains(NEW_KEY)) {
            List<Map<?, ?>> existing = config.getMapList(NEW_KEY);
            Set<String> existingSignatures = new HashSet<>();
            for (Map<?, ?> r : existing) {
                existingSignatures.add(ruleSignature(r));
            }
            int before = newRules.size();
            newRules.removeIf(r -> existingSignatures.contains(ruleSignature(r)));
            if (newRules.size() < before) {
                CrypticLib.info("Skipped " + (before - newRules.size()) + " duplicate rule(s) already in ingredient restriction rules.");
            }
        }

        if (newRules.isEmpty()) {
            removeOldKeys(config);
            configWrapper.saveConfig();
            return;
        }

        // 4. 写入新 key
        List<Map<?, ?>> existingNew = config.contains(NEW_KEY) ? config.getMapList(NEW_KEY) : new ArrayList<>();
        List<Map<?, ?>> merged = new ArrayList<>(existingNew);
        merged.addAll(newRules);
        config.set(NEW_KEY, merged);

        // 5. 删除旧 key
        removeOldKeys(config);

        // 6. 保存
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
        CrypticLib.info("Config migration complete: " + newRules.size() + " rule(s) added to " + NEW_KEY);
    }

    private void removeOldKeys(YamlConfiguration config) {
        config.set(OLD_KEY_CANNOT_CRAFT, null);
        config.set(OLD_KEY_LORE_RULES, null);
    }

    private String ruleSignature(Map<?, ?> rule) {
        String type = Objects.toString(rule.get("type"), "");
        return switch (type) {
            case "lore" -> "lore:" + rule.get("lore") + ":" + rule.get("recipes");
            case "item_id" -> "item_id:" + rule.get("item_id") + ":" + rule.get("recipes");
            default -> type + ":" + rule;
        };
    }
}
