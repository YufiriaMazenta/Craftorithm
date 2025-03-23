package pers.yufiria.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.BooleanConfig;
import crypticlib.config.node.impl.bukkit.IntConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.Collections;
import java.util.List;

@ConfigHandler(path = "config.yml")
public class PluginConfigs {

    public final static BooleanConfig CHECK_UPDATE = new BooleanConfig("check_update", true);
    public final static BooleanConfig REMOVE_ALL_VANILLA_RECIPE = new BooleanConfig("remove_all_vanilla_recipe", false);
    public final static BooleanConfig BSTATS = new BooleanConfig("bstats", true);
    public final static BooleanConfig ENABLE_ANVIL_RECIPE = new BooleanConfig("enable_anvil_recipe", true);
    public final static BooleanConfig RELOAD_WHEN_IA_RELOAD = new BooleanConfig("reload_when_ia_reload", true);
    public final static BooleanConfig DEBUG = new BooleanConfig("debug", false);
    public final static IntConfig MAX_REG_RECIPE_PER_TICK = new IntConfig("max_reg_recipe_per_tick", 12);
    public final static StringListConfig CANNOT_CRAFT_ITEMS = new StringListConfig("cannot_craft_items", Collections.emptyList());
    public final static StringListConfig ITEM_PLUGIN_HOOK_PRIORITY = new StringListConfig(
        "item_plugin_hook_priority",
        List.of(
            "Nexo",
            "AzureFlow",
            "NeigeItems",
            "ItemsAdder",
            "Oraxen",
            "EcoItems",
            "ExecutableItems",
            "MMOItems",
            "MythicMobs"
        ),
        List.of(
            "依照上面的挂钩顺序挂钩插件可以挂钩的物品插件,插件自动识别物品ID时将会优先识别上面的插件",
            "不包含在此列表里的物品插件将不会尝试挂钩,除非该插件主动挂钩"
        )
    );

}
