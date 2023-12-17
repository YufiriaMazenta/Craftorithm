package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.google.common.base.Preconditions;
import crypticlib.chat.TextProcessor;
import crypticlib.util.ItemUtil;
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
        cannotCraftLore = TextProcessor.color(PluginConfigs.LORE_CANNOT_CRAFT.value());
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

    public static String matchItemNameOrCreate(ItemStack item, boolean ignoreAmount) {
        if (ItemUtil.isAir(item)) {
            return null;
        }
        String itemName;
        if (item.hasItemMeta()) {
            itemName = ItemManager.INSTANCE.matchItemName(item, ignoreAmount);
            if (itemName == null) {
                String id = UUID.randomUUID().toString();
                itemName = "items:" + CraftorithmItemProvider.INSTANCE.regCraftorithmItem("gui_items", id, item);
            }
        } else {
            itemName = item.getType().getKey().toString();
            if (!ignoreAmount && item.getAmount() > 1) {
                itemName += (" " + item.getAmount());
            }
        }
        return itemName;
    }

    public static void setLore(ItemStack item, List<String> lore) {
        setLore(item, lore, true);
    }

    public static void setLore(ItemStack item, List<String> lore, boolean format) {
        Preconditions.checkArgument(!ItemUtil.isAir(item), "Item can not be air");
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        if (format)
            itemMeta.getLore().replaceAll(TextProcessor::color);
        item.setItemMeta(itemMeta);
    }

}
