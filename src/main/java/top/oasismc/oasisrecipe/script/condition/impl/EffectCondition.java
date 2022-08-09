package top.oasismc.oasisrecipe.script.condition.impl;

import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;
import top.oasismc.oasisrecipe.script.util.ValueUtil;

public enum EffectCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        String compareType = null;
        int value = 0;
        int asteriskIndex = arg.indexOf("*");
        if (asteriskIndex != -1) {
            value = Integer.parseInt(arg.substring(asteriskIndex + 1));
            int spaceIndex = arg.indexOf(" ");
            if (spaceIndex == -1)
                return false;
            compareType = arg.substring(spaceIndex + 1, asteriskIndex);
            arg = arg.substring(0, spaceIndex);
        }
        arg = arg.replaceAll("\\s", "");
        PotionEffectType type;
        try {
            type = PotionEffectType.getByName(arg);
        } catch (IllegalArgumentException e) {
            type = PotionEffectType.getByKey(NamespacedKey.fromString(arg));
        }

        Validate.notNull(type, "PotionEffectType " + arg + " does not exist");

        PotionEffect effect = player.getPotionEffect(type);
        if (effect == null)
            return false;
        if (compareType != null)
            return ValueUtil.compare(effect.getAmplifier(), value, compareType);
        else
            return true;
    }

    @Override
    public String getStatement() {
        return "has-effect";
    }
}
