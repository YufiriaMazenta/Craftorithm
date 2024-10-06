package pers.yufiria.craftorithm.item;

import com.google.common.base.Preconditions;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.ItemHelper;
import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.CraftorithmBukkit;
import pers.yufiria.craftorithm.item.providers.CraftorithmItemProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD)
    }
)
public enum ItemManager implements BukkitLifeCycleTask {

    INSTANCE;

    private final Map<String, ItemProvider> itemProviderMap = new LinkedHashMap<>();
    private final Map<NamespacedItemId, Integer> customCookingFuelMap = new ConcurrentHashMap<>();
    private final BukkitConfigWrapper customFuelConfig = new BukkitConfigWrapper(CraftorithmBukkit.instance(), "custom_fuels.yml");
    private final String BURN_TIME_KEY = "burn_time";

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
     * @param itemId 物品的id
     * @param amount 物品的数量
     * @return 获取到的物品，如果为空则为不存在此物品
     */
    public @NotNull ItemStack matchItem(NamespacedItemId itemId, int amount) {
        return matchItem(itemId, amount, null);
    }

    /**
     * 根据名字获取一个物品,并解析玩家变量
     * @param itemId 物品的id
     * @param amount 物品的数量
     * @return 获取到的物品，如果为空则为不存在此物品
     */
    public @NotNull ItemStack matchItem(NamespacedItemId itemId, int amount, @Nullable OfflinePlayer player) {
        ItemStack item;
        ItemProvider provider = itemProviderMap.get(itemId.namespace());
        if (provider == null) {
            return matchVanillaItem(itemId, amount);
        }

        if (player != null)
            item = provider.matchItem(itemId.itemId(), player);
        else
            item = provider.matchItem(itemId.itemId());
        if (item == null)
            throw new IllegalArgumentException("Can not found item " + itemId.itemId() + " from provider: " + itemId.namespace());
        item.setAmount(amount);
        return item;
    }

    /**
     * 获取一个物品的完整id,包含命名空间和id
     * @param item 传入的物品
     * @return 传入的物品id
     */
    @Nullable
    public NamespacedItemId matchItemId(ItemStack item) {
        if (ItemHelper.isAir(item))
            return null;

        for (Map.Entry<String, ItemProvider> itemProviderEntry : itemProviderMap.entrySet()) {
            String tmpName = itemProviderEntry.getValue().matchItemId(item);
            if (tmpName != null) {
                return new NamespacedItemId(itemProviderEntry.getKey(), tmpName);
            }
        }

        return null;
    }

    /**
     * 获取一个物品的完整ID,包含命名空间与id,如果物品未找到,会将此物品保存
     * @param item 传入的物品
     * @return 传入的物品id
     */
    public @Nullable NamespacedItemId matchItemIdOrCreate(ItemStack item) {
        if (ItemHelper.isAir(item)) {
            return null;
        }
        NamespacedItemId itemId;
        if (item.hasItemMeta()) {
            itemId = ItemManager.INSTANCE.matchItemId(item);
            if (itemId == null) {
                String id = UUID.randomUUID().toString();
                itemId = new NamespacedItemId("items", CraftorithmItemProvider.INSTANCE.regCraftorithmItem("plugin_created", id, item));
            }
        } else {
            NamespacedKey key = item.getType().getKey();
            itemId = new NamespacedItemId(key.getNamespace(), key.getKey());
        }
        return itemId;
    }

    /**
     * 获取原版物品
     * @param itemId 物品的ID
     * @param amount 物品数量
     * @return 物品
     */
    public ItemStack matchVanillaItem(NamespacedItemId itemId, int amount) {
        String itemIdString = itemId.toString();
        Material material = MaterialHelper.matchMaterial(itemIdString);
        if (material == null) {
            throw new IllegalArgumentException("Can not found item " + itemIdString);
        }
        return new ItemStack(material, amount);
    }

    public void reloadCustomCookingFuel() {
        customFuelConfig.reloadConfig();
        customCookingFuelMap.clear();
        YamlConfiguration config = customFuelConfig.config();
        Set<String> keys = config.getKeys(false);
        for (String fuelId : keys) {
            ConfigurationSection fuelConfig = config.getConfigurationSection(fuelId);
            if (fuelConfig == null)
                continue;
            int time = fuelConfig.getInt(BURN_TIME_KEY, 200);
            NamespacedItemId itemId = NamespacedItemId.fromString(fuelId);
            if (time != 0)
                customCookingFuelMap.put(itemId, time);
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
        NamespacedItemId itemId = matchItemId(item);
        if (itemId == null)
            itemId = NamespacedItemId.fromString(item.getType().getKey().toString());
        return customCookingFuelMap.get(itemId);
    }

    public boolean addCustomFuel(ItemStack item, int burnTime) {
        NamespacedItemId itemId = matchItemIdOrCreate(item);
        if (itemId == null)
            throw new IllegalArgumentException("Cannot add null item as a fuel");
        if (customCookingFuelMap.containsKey(itemId))
            return false;
        customCookingFuelMap.put(itemId, burnTime);
        customFuelConfig.config().set(itemId + "." + BURN_TIME_KEY, burnTime);
        customFuelConfig.saveConfig();
        customFuelConfig.reloadConfig();
        return true;
    }

    public boolean removeCustomFuel(@NotNull NamespacedItemId itemId) {
        if (!customCookingFuelMap.containsKey(itemId))
            return false;
        customCookingFuelMap.remove(itemId);
        customFuelConfig.config().set(itemId.toString(), null);
        customFuelConfig.saveConfig();
        customFuelConfig.reloadConfig();
        return true;
    }

    public Map<NamespacedItemId, Integer> customCookingFuelMap() {
        return customCookingFuelMap;
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        if (lifeCycle.equals(LifeCycle.ENABLE)) {
            regDefaultProviders();
        }
        reloadCustomCookingFuel();
    }

}
