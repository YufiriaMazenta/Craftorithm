package pers.yufiria.craftorithm.util;

import crypticlib.script.ScriptValue;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public class ItemUtils {

    public static void toggleItemGlowing(ItemStack item) {
        if (item.containsEnchantment(Enchantment.MENDING)) {
            item.removeEnchantment(Enchantment.MENDING);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(itemMeta);
        } else {
            item.addUnsafeEnchantment(Enchantment.MENDING, 1);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(itemMeta);
        }
    }

    public static ScriptValue resolveItemId(@Nullable ItemStack item) {
        if (item == null) return ScriptValue.nil();
        return ItemManager.INSTANCE.matchItemId(item, false)
            .map(id -> ScriptValue.of(id.itemId().toString()))
            .orElseGet(() -> ScriptValue.of(item.getType().getKey().toString()));
    }

    public static ScriptValue resolveItemAmount(@Nullable ItemStack item) {
        if (item == null) return ScriptValue.of(0);
        return ScriptValue.of(item.getAmount());
    }

}
