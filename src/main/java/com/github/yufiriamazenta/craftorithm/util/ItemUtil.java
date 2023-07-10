package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ItemUtil {

    private static String cannotCraftLore;
    private static Pattern cannotCraftLorePattern;
    private static boolean cannotCraftLoreIsRegex;

    static {
        reloadCannotCraftLore();
    }

    public static void reloadCannotCraftLore() {
        cannotCraftLore = LangUtil.color(Craftorithm.getInstance().getConfig().getString("lore_cannot_craft", "lore_cannot_craft"));
        try {
            cannotCraftLorePattern = Pattern.compile(cannotCraftLore);
            cannotCraftLoreIsRegex = true;
        } catch (PatternSyntaxException e) {
            cannotCraftLoreIsRegex = false;
        }
    }

    /**
     * 检测物品是否含有不允许用于合成的lore
     * @param items 传入的物品数组
     * @return 是否包含所需字符串
     */
    public static boolean hasCannotCraftLore(ItemStack[] items) {
        boolean containsLore = false;

        for (ItemStack item : items) {
            if (item == null)
                continue;
            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                continue;
            List<String> lore = item.getItemMeta().getLore();
            if (lore == null)
                continue;
            for (String loreStr : lore) {
                if (!cannotCraftLoreIsRegex) {
                    if (loreStr.equals(cannotCraftLore)) {
                        containsLore = true;
                        break;
                    }
                } else {
                    Matcher matcher = cannotCraftLorePattern.matcher(loreStr);
                    if (matcher.find()) {
                        containsLore = true;
                        break;
                    }
                }
            }
            if (containsLore)
                break;
        }
        return containsLore;
    }

    public static boolean checkItemIsAir(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}
