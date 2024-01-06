package com.github.yufiriamazenta.craftorithm.item;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.google.common.base.Preconditions;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ItemManager {

    INSTANCE;

    private final Map<String, ItemProvider> itemProviderMap;
    private final Map<String, Integer> customCookingFuelMap;

    ItemManager() {
        itemProviderMap = new LinkedHashMap<>();
        customCookingFuelMap = new ConcurrentHashMap<>();
    }

    public void loadItemManager() {
        regDefaultProviders();
        reloadCustomCookingFuel();
    }

    public void regDefaultProviders() {
        regItemProvider(CraftorithmItemProvider.INSTANCE);
    }

    public void regItemProvider(ItemProvider itemProvider) {
        Preconditions.checkArgument(
            !itemProvider.namespace().equalsIgnoreCase(NamespacedKey.MINECRAFT),
            "Item provider cannot use namespace minecraft"
        );
        itemProviderMap.put(itemProvider.namespace(), itemProvider);
    }

    /**
     * 根据名字获取一个物品
     * @param itemKey 包含命名空间的名字
     * @return 获取到的物品，如果为空则为不存在此物品
     */
    public @NotNull ItemStack matchItem(String itemKey) {
        ItemStack item;
        int lastSpaceIndex = itemKey.lastIndexOf(" ");
        int amountScale = 1;
        if (lastSpaceIndex > 0) {
            amountScale = Integer.parseInt(itemKey.substring(lastSpaceIndex + 1));
            itemKey = itemKey.substring(0, lastSpaceIndex);
        }
        itemKey = itemKey.replace(" ", "");
        if (!itemKey.contains(":")) {
            return matchVanillaItem(itemKey, amountScale);
        }

        int index = itemKey.indexOf(":");
        String namespace = itemKey.substring(0, index);
        String name = itemKey.substring(index + 1);

        ItemProvider provider = itemProviderMap.get(namespace);
        if (provider == null) {
            return matchVanillaItem(itemKey, amountScale);
        }

        item = provider.getItem(name);
        if (item == null)
            throw new IllegalArgumentException("Can not found item " + name + " from provider: " + namespace);
        item.setAmount(item.getAmount() * amountScale);
        return item;
    }

    /**
     * 获取一个物品的完整名字,包含命名空间和id
     * @param item 传入的物品
     * @param ignoreAmount 是否忽略数量
     * @return 传入的物品名字
     */
    @Nullable
    public String matchItemName(ItemStack item, boolean ignoreAmount) {
        if (ItemUtil.isAir(item))
            return null;

        for (Map.Entry<String, ItemProvider> itemProviderEntry : itemProviderMap.entrySet()) {
            String tmpName = itemProviderEntry.getValue().getItemName(item, ignoreAmount);
            if (tmpName != null) {
                return itemProviderEntry.getKey() + ":" + tmpName;
            }
        }

        return null;
    }

    /**
     * 获取原版物品
     * @param itemKey 物品名字
     * @param amount 物品数量
     * @return 物品
     */
    public ItemStack matchVanillaItem(String itemKey, int amount) {
        Material material = Material.matchMaterial(itemKey);
        if (material == null)
            throw new IllegalArgumentException("Can not found item " + itemKey);
        return new ItemStack(material, amount);
    }

    public void reloadCustomCookingFuel() {
        customCookingFuelMap.clear();
        for (String fuel : PluginConfigs.CUSTOM_COOKING_FUELS.value()) {
            try {
                String[] split = fuel.split(" ");
                if (split.length < 2) {
                    throw new IllegalArgumentException("Unable to parse the smelting time of this fuel: " + fuel);
                }
                int burnTime = Integer.parseInt(split[1]);
                customCookingFuelMap.put(split[0], burnTime);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCustomFuel(ItemStack item) {
        if (customCookingFuelMap.isEmpty())
            return false;
        Integer time = matchCustomFuelBurnTime(item);
        return time != null && time > 0;
    }

    public Integer matchCustomFuelBurnTime(ItemStack item) {
        if (customCookingFuelMap.isEmpty())
            return null;
        String itemName = matchItemName(item, true);
        if (itemName == null)
            itemName = item.getType().key().toString();
        return customCookingFuelMap.get(itemName);
    }

    public boolean addCustomFuel(ItemStack item, int burnTime) {
        String itemName = ItemUtils.matchItemNameOrCreate(item, true);
        if (customCookingFuelMap.containsKey(itemName))
            return false;
        customCookingFuelMap.put(itemName, burnTime);
        String fuel = itemName + " " + burnTime;
        List<String> customCookingFuels = Craftorithm.instance().getConfig().getStringList("custom_cooking_fuels");
        customCookingFuels.add(fuel);
        Craftorithm.instance().getConfig().set("custom_cooking_fuels", customCookingFuels);
        Craftorithm.instance().saveConfig();
        PluginConfigs.CUSTOM_COOKING_FUELS.value().add(fuel);
        return true;
    }

}
