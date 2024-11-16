package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.ItemHelper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD)
    }
)
public class ItemUtils implements BukkitLifeCycleTask {

    private static String cannotCraftLore;
    private static Pattern cannotCraftLorePattern;
    private static boolean cannotCraftLoreIsRegex;

    public static void reloadCannotCraftLore() {
        cannotCraftLore = BukkitTextProcessor.color(PluginConfigs.LORE_CANNOT_CRAFT.value());
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
    public static boolean hasCannotCraftLore(ItemStack... items) {
        if (cannotCraftLore == null)
            return false;
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
        if (ItemHelper.isAir(item)) {
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

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        reloadCannotCraftLore();
    }

}
