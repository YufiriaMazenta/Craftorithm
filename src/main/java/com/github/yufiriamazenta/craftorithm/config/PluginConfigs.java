package com.github.yufiriamazenta.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.BooleanConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.List;

@ConfigHandler(path = "config.yml")
public class PluginConfigs {

    public final static BooleanConfig CHECK_UPDATE = new BooleanConfig("check_update", true);
    public final static BooleanConfig REMOVE_ALL_VANILLA_RECIPE = new BooleanConfig("remove_all_vanilla_recipe", false);
    public final static StringConfig LORE_CANNOT_CRAFT = new StringConfig("lore_cannot_craft", ".*不可用于合成.*");
    public final static BooleanConfig DEFAULT_RECIPE_UNLOCK = new BooleanConfig("default_recipe_unlock", false);
    public final static BooleanConfig BSTATS = new BooleanConfig("bstats", true);
    public final static BooleanConfig RELEASE_DEFAULT_RECIPES = new BooleanConfig("release_default_recipes", false);
    public final static BooleanConfig ENABLE_ANVIL_RECIPE = new BooleanConfig("enable_anvil_recipe", true);
    public final static BooleanConfig RELOAD_WHEN_IA_RELOAD = new BooleanConfig("reload_when_ia_reload", true);
    public final static BooleanConfig DEBUG = new BooleanConfig("debug", false);
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
