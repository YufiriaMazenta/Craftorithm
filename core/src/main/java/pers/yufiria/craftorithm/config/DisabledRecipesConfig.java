package pers.yufiria.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.Collections;

@ConfigHandler(path = "disabled_recipes.yml")
public class DisabledRecipesConfig {

    public static final StringListConfig RECIPES = new StringListConfig("recipes", Collections.emptyList());

}
