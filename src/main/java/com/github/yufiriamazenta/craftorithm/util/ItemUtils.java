package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import crypticlib.util.ItemUtil;
import crypticlib.util.TextUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ItemUtils {

    private static String cannotCraftLore;
    private static Pattern cannotCraftLorePattern;
    private static boolean cannotCraftLoreIsRegex;

    static {
        reloadCannotCraftLore();
    }

    public static void reloadCannotCraftLore() {
        cannotCraftLore = TextUtil.color(Craftorithm.instance().getConfig().getString("lore_cannot_craft", "lore_cannot_craft"));
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

    public static String getItemName(ItemStack item, boolean ignoreAmount) {
        if (ItemUtil.isAir(item)) {
            return null;
        }
        String itemName = checkIsOtherPluginName(item);
        if (itemName != null)
            return itemName;
        if (item.hasItemMeta()) {
            itemName = ItemManager.getItemName(item, ignoreAmount, true, "gui_items", UUID.randomUUID().toString());
            itemName = "items:" + itemName;
        } else {
            itemName = item.getType().name();
            if (!ignoreAmount) {
                itemName += (" " + item.getAmount());
            }
        }
        return itemName;
    }

    public static String checkIsOtherPluginName(ItemStack item) {
        //识别是否是ItemsAdder的物品
        String itemsAdderName = PluginHookUtil.getItemsAdderName(item);
        if (itemsAdderName != null)
            return "items_adder:" + itemsAdderName;

        //识别是否是Oraxen的物品
        String oraxenName = PluginHookUtil.getOraxenName(item);
        if (oraxenName != null) {
            return "oraxen:" + oraxenName;
        }

        //识别是否是MythicMobs的物品
        String mythicName = PluginHookUtil.getMythicMobsName(item);
        if (mythicName != null)
            return "mythic_mobs:" + mythicName;
        return null;
    }


}
