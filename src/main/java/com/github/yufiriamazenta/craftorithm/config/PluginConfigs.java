package com.github.yufiriamazenta.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.BooleanConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "config.yml")
public class PluginConfigs {

    public final static BooleanConfig CHECK_UPDATE = new BooleanConfig("check_update", true, "是否检查更新");
    public final static BooleanConfig REMOVE_ALL_VANILLA_RECIPE = new BooleanConfig("remove_all_vanilla_recipe", false, "是否移除所有原版配方");
    public final static StringConfig LORE_CANNOT_CRAFT = new StringConfig("lore_cannot_craft", ".*不可用于合成.*", "包含有某lore的物品不得作为原材料");
    public final static BooleanConfig DEFAULT_RECIPE_UNLOCK = new BooleanConfig("default_recipe_unlock", false, "默认情况下是否为玩家解锁配方");
    public final static BooleanConfig BSTATS = new BooleanConfig("bstats", true, "是否允许插件进行bstats数据统计");
    public final static BooleanConfig RELEASE_DEFAULT_RECIPES = new BooleanConfig("release_default_recipes", false, "是否释放默认配方文件");
    public final static BooleanConfig ENABLE_ANVIL_RECIPE = new BooleanConfig("enable_anvil_recipe", true, "是否启用铁砧配方");
    public final static BooleanConfig RELOAD_WHEN_IA_RELOAD = new BooleanConfig("reload_when_ia_reload", true, "是否在IA重载的时候一起重载");

}
