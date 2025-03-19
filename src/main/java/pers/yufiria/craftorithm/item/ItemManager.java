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
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.impl.CraftorithmItemProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AutoTask(
    rules = {
        @TaskRule(
            lifeCycle = LifeCycle.ACTIVE, priority = 1
        ),
        @TaskRule(
            lifeCycle = LifeCycle.RELOAD, priority = 1
        )
    }
)
public enum ItemManager implements BukkitLifeCycleTask {

    INSTANCE;

    private final Map<String, ItemProvider> itemProviderMap = new LinkedHashMap<>();
    private final Map<NamespacedItemId, Integer> customCookingFuelMap = new ConcurrentHashMap<>();
    private BukkitConfigWrapper customFuelConfig;
    private final String BURN_TIME_KEY = "burn_time";
    private BukkitConfigWrapper itemPacksConfig;
    private final Map<String, ItemPack> itemPacks = new ConcurrentHashMap<>();
    //存储不能用于合成的物品列表
    private final Set<NamespacedItemId> cannotCraftItems = new HashSet<>();

    public void regItemProvider(ItemProvider itemProvider) {
        Preconditions.checkArgument(
            !itemProvider.namespace().equalsIgnoreCase(NamespacedKey.MINECRAFT),
            "Item provider cannot use namespace minecraft"
        );
        itemProviderMap.put(itemProvider.namespace(), itemProvider);
    }

    /**
     * 根据名字获取一个物品
     * @return 获取到的物品，如果为空则为不存在此物品
     */
    public @NotNull ItemStack matchItem(NamespacedItemIdStack stackedItemId) {
        return matchItem(stackedItemId, null);
    }

    /**
     * 根据名字获取一个物品,并解析玩家变量
     * @return 获取到的物品，如果为空则为不存在此物品
     */
    public @NotNull ItemStack matchItem(NamespacedItemIdStack stackedItemId, @Nullable OfflinePlayer player) {
        ItemStack item;
        NamespacedItemId itemId = stackedItemId.itemId();
        int amount = stackedItemId.amount();
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
    public NamespacedItemIdStack matchItemId(ItemStack item, boolean ignoreAmount) {
        if (ItemHelper.isAir(item))
            return null;

        for (Map.Entry<String, ItemProvider> itemProviderEntry : itemProviderMap.entrySet()) {
            NamespacedItemIdStack namespacedItemIdStack = itemProviderEntry.getValue().matchItemId(item, ignoreAmount);
            if (namespacedItemIdStack != null) {
                return namespacedItemIdStack;
            }
        }

        return null;
    }

    /**
     * 获取一个物品的完整ID,包含命名空间与id,如果物品未找到,会将此物品保存
     * @param item 传入的物品
     * @return 传入的物品id
     */
    public @Nullable NamespacedItemIdStack matchItemIdOrCreate(ItemStack item, boolean ignoreAmount) {
        if (ItemHelper.isAir(item)) {
            return null;
        }
        NamespacedItemIdStack itemId;
        if (item.hasItemMeta()) {
            itemId = ItemManager.INSTANCE.matchItemId(item, ignoreAmount);
            if (itemId == null) {
                String id = UUID.randomUUID().toString();
                itemId = CraftorithmItemProvider.INSTANCE.regCraftorithmItem("plugin_created", id, item);
                if (ignoreAmount) {
                    itemId.setAmount(1);
                }
            }
        } else {
            NamespacedKey key = item.getType().getKey();
            itemId = new NamespacedItemIdStack(
                new NamespacedItemId(key.getNamespace(), key.getKey()),
                ignoreAmount ? 1 : item.getAmount()
            );
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
        NamespacedItemIdStack stackedItemId = matchItemId(item, true);
        NamespacedItemId itemId;
        if (stackedItemId == null) {
            itemId = NamespacedItemId.fromMaterial(item.getType());
        } else {
            itemId = stackedItemId.itemId();
        }
        return customCookingFuelMap.get(itemId);
    }

    public boolean addCustomFuel(ItemStack item, int burnTime) {
        NamespacedItemIdStack stackedItemId = matchItemIdOrCreate(item, false);
        if (stackedItemId == null)
            throw new IllegalArgumentException("Cannot add null item as a fuel");
        if (customCookingFuelMap.containsKey(stackedItemId.itemId()))
            return false;
        customCookingFuelMap.put(stackedItemId.itemId(), burnTime);
        customFuelConfig.config().set(stackedItemId + "." + BURN_TIME_KEY, burnTime);
        customFuelConfig.saveConfig();
        customFuelConfig.reloadConfig();
        return true;
    }

    public boolean removeCustomFuel(@Nullable NamespacedItemId itemId) {
        if (itemId == null || !customCookingFuelMap.containsKey(itemId))
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
        if (lifeCycle.equals(LifeCycle.ACTIVE)) {
            customFuelConfig = new BukkitConfigWrapper(Craftorithm.instance(), "custom_fuels.yml");
            itemPacksConfig = new BukkitConfigWrapper(Craftorithm.instance(), "item_packs.yml");
            regItemProvider(CraftorithmItemProvider.INSTANCE);
        }
        reloadCustomCookingFuel();
        reloadItemPacks();
        reloadCannotCraftItems();
    }

    private void reloadCannotCraftItems() {
        //TODO 加载不能用于合成的物品列表
        cannotCraftItems.clear();
    }

    private void reloadItemPacks() {
        itemPacksConfig.reloadConfig();
        itemPacks.clear();
        YamlConfiguration config = itemPacksConfig.config();
        for (String key : config.getKeys(false)) {
            List<String> itemIds = config.getStringList(key);
            if (itemIds.isEmpty()) {
                continue;
            }
            ItemPack itemPack = new ItemPack(key, itemIds.stream().map(NamespacedItemIdStack::fromString).toList());
            itemPacks.put(key, itemPack);
        }
    }

    public @Nullable ItemPack getItemPack(String itemId) {
        return itemPacks.get(itemId);
    }
}