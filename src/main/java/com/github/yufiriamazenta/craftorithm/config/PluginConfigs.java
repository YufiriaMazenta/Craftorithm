package com.github.yufiriamazenta.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.entry.BooleanConfig;
import crypticlib.config.entry.StringConfig;
import crypticlib.config.entry.StringListConfig;

import java.util.ArrayList;
import java.util.Collections;

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

}
