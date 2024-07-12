package com.github.yufiriamazenta.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.entry.BooleanConfigEntry;
import crypticlib.config.entry.StringConfigEntry;
import crypticlib.config.entry.StringListConfigEntry;

import java.util.ArrayList;
import java.util.Collections;

@ConfigHandler(path = "config.yml")
public class PluginConfigs {

    public final static BooleanConfigEntry CHECK_UPDATE = new BooleanConfigEntry("check_update", true);
    public final static BooleanConfigEntry REMOVE_ALL_VANILLA_RECIPE = new BooleanConfigEntry("remove_all_vanilla_recipe", false);
    public final static StringConfigEntry LORE_CANNOT_CRAFT = new StringConfigEntry("lore_cannot_craft", ".*不可用于合成.*");
    public final static BooleanConfigEntry DEFAULT_RECIPE_UNLOCK = new BooleanConfigEntry("default_recipe_unlock", false);
    public final static BooleanConfigEntry BSTATS = new BooleanConfigEntry("bstats", true);
    public final static BooleanConfigEntry RELEASE_DEFAULT_RECIPES = new BooleanConfigEntry("release_default_recipes", true);
    public final static BooleanConfigEntry ENABLE_ANVIL_RECIPE = new BooleanConfigEntry("enable_anvil_recipe", true);
    public final static BooleanConfigEntry RELOAD_WHEN_IA_RELOAD = new BooleanConfigEntry("reload_when_ia_reload", true);
    
}
