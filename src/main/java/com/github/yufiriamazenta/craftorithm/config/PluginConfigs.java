package com.github.yufiriamazenta.craftorithm.config;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.entry.BooleanConfigEntry;
import com.github.yufiriamazenta.craftorithm.config.entry.StringConfigEntry;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginConfigs {

    public final static StringConfigEntry version = new StringConfigEntry("version", Craftorithm.instance().getDescription().getVersion());
    public final static BooleanConfigEntry checkUpdate = new BooleanConfigEntry("check_update", true);
    public final static BooleanConfigEntry removeAllVanillaRecipe = new BooleanConfigEntry("remove_all_vanilla_recipe", false);
    public final static StringConfigEntry loreCannotCraft = new StringConfigEntry("lore_cannot_craft", ".*不可用于合成.*");
    public final static BooleanConfigEntry allRecipeUnlocked = new BooleanConfigEntry("all_recipe_unlocked", false);
    public final static BooleanConfigEntry bStats = new BooleanConfigEntry("bstats", true);
    public final static BooleanConfigEntry releaseDefaultRecipes = new BooleanConfigEntry("release_default_recipes", true);

    public static void reloadConfigs() {
        Craftorithm.instance().reloadConfig();
        YamlConfiguration config = (YamlConfiguration) Craftorithm.instance().getConfig();
        version.load(config);
        checkUpdate.load(config);
        removeAllVanillaRecipe.load(config);
        loreCannotCraft.load(config);
        allRecipeUnlocked.load(config);
        bStats.load(config);
        releaseDefaultRecipes.load(config);
        Craftorithm.instance().saveConfig();
    }

}
