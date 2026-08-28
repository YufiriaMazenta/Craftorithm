package pers.yufiria.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.BooleanConfig;
import crypticlib.config.node.impl.bukkit.ConfigSectionListConfig;
import crypticlib.config.node.impl.bukkit.IntConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.Collections;
import java.util.List;

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
    public final static IntConfig MAX_REG_RECIPE_PER_TICK = new IntConfig(
        "max_reg_recipe_per_tick",
        12,
        "每tick注册的配方数量，调低此数值可以减少服务器卡顿"
    );
    public final static ConfigSectionListConfig INGREDIENT_RESTRICTION_RULES = new ConfigSectionListConfig(
        "ingredient_restriction_rules",
        Collections.emptyList(),
        List.of(
            "设定材料的合成限制规则",
            "支持的规则类型: lore(基于lore判断), item_id(基于物品id判断)",
            "第三方插件可注册自定义规则类型"
        )
    );

    public final static IntConfig INGREDIENT_SET_THRESHOLD = new IntConfig(
        "ingredient_set_threshold",
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

}
