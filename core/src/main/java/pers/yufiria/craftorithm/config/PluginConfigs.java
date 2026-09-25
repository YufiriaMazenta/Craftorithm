package pers.yufiria.craftorithm.config;

import crypticlib.CrypticLib;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.*;
import crypticlib.util.BukkitConfigHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.*;
import java.util.function.Supplier;

@ConfigHandler(path = "config.yml")
public class PluginConfigs {

    public final static BooleanConfig CHECK_UPDATE = new BooleanConfig(
        "check_update",
        true,
        "是否进行更新检测"
    );
    public final static BooleanConfig REMOVE_ALL_VANILLA_RECIPE = new BooleanConfig(
        "remove_all_vanilla_recipe",
        false,
        "是否卸载所有的原版配方"
    );
    public final static BooleanConfig BSTATS = new BooleanConfig(
        "bstats",
        true,
        "是否允许插件通过bStats收集使用信息"
    );
    public final static BooleanConfig ENABLE_ANVIL_RECIPE = new BooleanConfig(
        "enable_anvil_recipe",
        true,
        "是否启用铁砧配方"
    );
    public final static BooleanConfig DEBUG = new BooleanConfig("debug", false);
    public final static BooleanConfig SAVE_DISCOVERED_RECIPES_ENABLE = new BooleanConfig(
        "save_discovered_recipes.enable",
        true,
        List.of(
            "将已解锁配方键存储到数据库",
            "在连接同一个数据库的情况下可以跨服同步已解锁配方"
        )
    );
    public final static IntConfig SAVE_DISCOVERED_RECIPES_JOIN_DISCOVER_DELAY_TICKS = new IntConfig(
        "save_discovered_recipes.join_discover_delay_ticks",
        () -> {
            BukkitConfigWrapper configWrapper = Craftorithm.instance().getConfigWrapperOrCreate("config.yml");
            YamlConfiguration config = configWrapper.config();
            String oldKey = "recipe_discovery_sync.join_sync_delay_ticks";
            if (config.isInt(oldKey)) {
                int def = config.getInt(oldKey, 20);
                config.set(oldKey, null);
                return def;
            }
            return 20;
        },
        "玩家加入服务器后，等待多少tick再从数据库读取已解锁配方（建议10-40，即0.5-2秒）"
    );

    public final static IntConfig SAVE_DISCOVERED_RECIPES_INTERVAL_TICKS = new IntConfig(
        "save_discovered_recipes.interval_ticks",
        () -> {
            BukkitConfigWrapper configWrapper = Craftorithm.instance().getConfigWrapperOrCreate("config.yml");
            YamlConfiguration config = configWrapper.config();
            String oldKey = "recipe_discovery_sync.interval_ticks";
            if (config.isInt(oldKey)) {
                int def = config.getInt(oldKey, 6000);
                config.set(oldKey, null);
                return def;
            }
            return 6000;
        },
        "定时保存在线玩家已解锁配方到数据库的间隔（tick），默认300秒（5分钟）"
    );

    public final static IntConfig MAX_REG_RECIPE_PER_TICK = new IntConfig(
        "max_reg_recipe_per_tick",
        100,
        "每tick注册的配方数量，调低此数值可以减少服务器卡顿"
    );
    public final static ConfigSectionListConfig INGREDIENT_RESTRICTION_RULES = new ConfigSectionListConfig(
        "ingredient_restriction_rules",
        new Supplier<>() {
            @Override
            public List<ConfigurationSection> get() {
                String oldKeyCannotCraft = "cannot_craft_items";
                String oldKeyLoreRules = "blocked_crafting_lore_rules";
                String newKey = "ingredient_restriction_rules";
                BukkitConfigWrapper configWrapper = Craftorithm.instance().getConfigWrapperOrCreate("config.yml");
                YamlConfiguration config = configWrapper.config();

                boolean hasCannotCraft = config.contains(oldKeyCannotCraft);
                boolean hasLoreRules = config.contains(oldKeyLoreRules);

                if (!hasCannotCraft && !hasLoreRules) {
                    return Collections.emptyList();
                }

                List<Map<String, Object>> newRules = new ArrayList<>();

                // 1. 迁移 cannot_craft_items
                if (hasCannotCraft) {
                    List<String> items = config.getStringList(oldKeyCannotCraft);
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
                    List<Map<?, ?>> oldRules = config.getMapList(oldKeyLoreRules);
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
                if (config.contains(newKey)) {
                    List<Map<?, ?>> existing = config.getMapList(newKey);
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
                    configWrapper.saveConfig();
                    return Collections.emptyList();
                }

                //清理原本的内容
                config.set(oldKeyCannotCraft, null);
                config.set(oldKeyLoreRules, null);

                CrypticLib.info("Config migration complete: " + newRules.size() + " rule(s) added to " + newKey);
                return newRules.stream().map(BukkitConfigHelper::map2ConfigSection).toList();
            }

            private String ruleSignature(Map<?, ?> rule) {
                String type = Objects.toString(rule.get("type"), "");
                return switch (type) {
                    case "lore" -> "lore:" + rule.get("lore") + ":" + rule.get("recipes");
                    case "item_id" -> "item_id:" + rule.get("item_id") + ":" + rule.get("recipes");
                    default -> type + ":" + rule;
                };
            }
        },
        List.of(
            "设定材料的合成限制规则",
            "支持的规则类型: lore(基于lore判断), item_id(基于物品id判断)",
            "第三方插件可注册自定义规则类型"
        )
    );

    public final static IntConfig INGREDIENT_USE_SET_THRESHOLD = new IntConfig(
        "ingredient_use_set_threshold",
        8,
        "配方材料数量超过此阈值时使用 Set 替代 List 进行匹配，提升大量材料时的查找性能"
    );

    public final static BooleanConfig USE_EXPERIMENTAL_RECIPE_INGREDIENTS = new BooleanConfig(
        "use_experimental_recipe_ingredients",
        true,
        List.of(
            "是否启用实验性配方材料功能",
            "启用后，除1.21.3及以上的切石机配方外，合成材料的识别将不会受到NBT/组件变更的影响，但可能在配方书等场景下出现一些问题"
        )
    );

    public final static StringListConfig ITEM_PLUGIN_HOOK_PRIORITY = new StringListConfig(
        "item_plugin_hook_priority",
        List.of(
            "CustomFishing",
            "CraftEngine",
            "Nexo",
            "AzureFlow",
            "SX-Item",
            "EmakiItem",
            "NeigeItems",
            "ItemsAdder",
            "Oraxen",
            "EcoItems",
            "ExecutableItems",
            "MMOItems",
            "MythicMobs",
            "Craftorithm"
        ),
        List.of(
            "依照上面的挂钩顺序挂钩插件可以挂钩的物品插件,插件自动识别物品ID时将会从上到下依次判断",
            "不包含在此列表里的物品插件将不会尝试挂钩,除非该插件主动挂钩"
        )
    );

    public final static StringListConfig MAIN_COMMAND_ALIASES = new StringListConfig(
        "main_command_aliases",
        List.of("cra", "craft", "crafto"),
        List.of("插件主命令的别名，只在插件启动时读取一次")
    );
    public final static StringListConfig NOT_CONVERT_LISTENER_CLASSES = new StringListConfig(
        "not_convert_listener_classes",
        List.of(
            "a4.papers.chatfilter.chatfilter.events.AnvilListener",
            "com.ghostchu.quickshop.shade.tne.menu.paper.listener.PaperInventoryClickListener",
            "com.earth2me.essentials.EssentialsPlayerListener",
            "net.coreprotect.listener.player.InventoryChangeListener",
            "net.coreprotect.listener.player.CraftItemListener",
            "com.extendedclip.deluxemenus.listener.PlayerListener",
            "com.dre.brewery.listeners.InventoryListener",
            "com.xyrisdev.svalues.shaded.library.menu.MenuManager$InventoryListener",
            "me.arcaniax.hdb.listener.InventoryListener",
            "net.momirealms.craftengine.bukkit.item.listener.ItemEventListener",
            "net.momirealms.customfishing.bukkit.hook.BukkitHookManager",
            "net.momirealms.customfishing.bukkit.market.BukkitMarketManager",
            "dev.jsinco.recipes.listeners.Events",
            "fr.moribus.imageonmap.image.MapInitEvent",
            "com.badbones69.crazycrates.paper.listeners.crates.types.WarCrateListener",
            "com.ryderbelserion.fusion.paper.api.builders.gui.listeners.GuiListener",
            "club.kid7.bannermaker.pluginutilities.gui.CustomGUIInventoryListener"
        ),
        "不进行隔离的监听器类，在此列表里的监听器类可以检测到Craftorithm的配方"
    );

    public static final StringConfig ITEM_GROUP_ITEM_NAME = new StringConfig("item_group_item.name", "&r<group_type>:<group_id>(<group_item_count>):");
    public static final StringConfig ITEM_GROUP_ITEM_LORE_ELEMENT = new StringConfig("item_group_item.lore.element", "&7<item_id>");
    public static final StringConfig ITEM_GROUP_ITEM_LORE_END = new StringConfig("item_group_item.lore.end", "&7...");
    public static final IntConfig ITEM_GROUP_ITEM_LORE_MAX_SIZE = new IntConfig("item_group_item.lore.max_size", 5);

}
