package top.oasismc.oasisrecipe.script.condition.impl;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;

public enum AdvancementCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        NamespacedKey key = NamespacedKey.fromString(arg);
        Validate.notNull(key, arg + " is not a valid key");

        Advancement advancement = Bukkit.getAdvancement(key);
        Validate.notNull(advancement, arg + " is not a valid advancement");

        return player.getAdvancementProgress(advancement).isDone();
    }

    @Override
    public String getStatement() {
        return "advancement";
    }

}
