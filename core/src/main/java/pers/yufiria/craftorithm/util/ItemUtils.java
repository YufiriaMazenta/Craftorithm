package pers.yufiria.craftorithm.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

}
