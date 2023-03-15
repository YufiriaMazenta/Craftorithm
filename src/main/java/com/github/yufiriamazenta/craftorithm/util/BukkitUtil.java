package com.github.yufiriamazenta.craftorithm.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BukkitUtil {

    public static boolean checkItemIsAir(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

}
