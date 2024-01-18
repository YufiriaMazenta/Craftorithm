package com.github.yufiriamazenta.craftorithm.item;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.google.common.base.Preconditions;
import crypticlib.config.ConfigWrapper;
import crypticlib.util.ItemUtil;
import crypticlib.util.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum ItemManager {

    INSTANCE;

    private final Map<String, ItemProvider> itemProviderMap;
    private final Map<String, Integer> customCookingFuelMap;
    private final ConfigWrapper customFuelConfig = new ConfigWrapper(Craftorithm.instance(), "custom_fuels.yml");
    private final String BURN_TIME_KEY = "burn_time";

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
        Material material = MaterialUtil.matchMaterial(itemKey);
        if (material == null) {
            throw new IllegalArgumentException("Can not found item " + itemKey);
        }
        return new ItemStack(material, amount);
    }

    public void reloadCustomCookingFuel() {
        customFuelConfig.reloadConfig();
        customCookingFuelMap.clear();
        YamlConfiguration config = customFuelConfig.config();
        Set<String> keys = config.getKeys(false);
        for (String fuel : keys) {
            ConfigurationSection fuelConfig = config.getConfigurationSection(fuel);
            if (fuelConfig == null)
                continue;
            int time = fuelConfig.getInt(BURN_TIME_KEY, 200);
            if (time != 0)
                customCookingFuelMap.put(fuel, time);
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
            itemName = item.getType().getKey().toString();
        return customCookingFuelMap.get(itemName);
    }

    public boolean addCustomFuel(ItemStack item, int burnTime) {
        String itemName = ItemUtils.matchItemNameOrCreate(item, true);
        if (itemName == null)
            throw new IllegalArgumentException("Cannot add null item as a fuel");
        if (customCookingFuelMap.containsKey(itemName))
            return false;
        customCookingFuelMap.put(itemName, burnTime);
        customFuelConfig.config().set(itemName + "." + BURN_TIME_KEY, burnTime);
        customFuelConfig.saveConfig();
        customFuelConfig.reloadConfig();
        return true;
    }

    public boolean removeCustomFuel(@NotNull String fuelName) {
        if (!customCookingFuelMap.containsKey(fuelName))
            return false;
        customCookingFuelMap.remove(fuelName);
        customFuelConfig.config().set(fuelName, null);
        customFuelConfig.saveConfig();
        customFuelConfig.reloadConfig();
        return true;
    }

    public Map<String, Integer> customCookingFuelMap() {
        return customCookingFuelMap;
    }

}
