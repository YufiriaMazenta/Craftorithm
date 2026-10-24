package pers.yufiria.craftorithm.recipe.remainder.impl;

import crypticlib.CrypticLib;
import crypticlib.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.remainder.RemainderProcessor;

/**
 * 保留原物品并扣除固定耐久
 */
public record DamageRemainderProcessor(int damage) implements RemainderProcessor {

    public static @Nullable DamageRemainderProcessor fromConfig(
        @NotNull ConfigurationSection section,
        @NotNull NamespacedKey recipeKey
    ) {
        Object damageRaw = section.get("damage");
        if (!(damageRaw instanceof Number number) || number.intValue() <= 0) {
            CrypticLib.warn("Remainder damage must be a positive integer in recipe: " + recipeKey);
            return null;
        }
        return new DamageRemainderProcessor(number.intValue());
    }

    @Override
    public @NotNull ItemStack createRemainder(@NotNull ItemStack source) {
        ItemStack remainder = source.clone();
        remainder.setAmount(1);

        ItemMeta itemMeta = remainder.getItemMeta();
        if (!(itemMeta instanceof Damageable damageable)) {
            return remainder;
        }

        int maxDamage = resolveMaxDamage(remainder, damageable);
        if (maxDamage <= 0) {
            return remainder;
        }

        long nextDamage = (long) damageable.getDamage() + damage;
        if (nextDamage >= maxDamage) {
            return new ItemStack(Material.AIR);
        }

        damageable.setDamage((int) nextDamage);
        remainder.setItemMeta(itemMeta);
        return remainder;
    }

    private int resolveMaxDamage(ItemStack item, Damageable damageable) {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_5)) {
            if (damageable.hasMaxDamage()) {
                return damageable.getMaxDamage();
            }
        }
        return item.getType().getMaxDurability();
    }

}
