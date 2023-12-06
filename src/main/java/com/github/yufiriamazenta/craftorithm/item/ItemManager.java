package com.github.yufiriamazenta.craftorithm.item;

import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ItemManager {

    INSTANCE;

    private final Map<String, ItemProvider> itemProviderMap;

    ItemManager() {
        itemProviderMap = new ConcurrentHashMap<>();
    }

    public void regItemProvider(ItemProvider itemProvider) {
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
            Material material = Material.matchMaterial(itemKey);
            if (material == null)
                throw new IllegalArgumentException("Can not found item " + itemKey);
            return new ItemStack(material, amountScale);
        }

        int index = itemKey.indexOf(":");
        String namespace = itemKey.substring(0, index);
        String name = itemKey.substring(index + 1);

        ItemProvider provider = itemProviderMap.get(namespace);
        if (provider == null) {
            throw new IllegalArgumentException("Can not found item provider: " + namespace);
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
            if (tmpName != null)
                return itemProviderEntry.getKey() + ":" + tmpName;
        }

        return null;
    }

}
