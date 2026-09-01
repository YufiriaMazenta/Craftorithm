package pers.yufiria.craftorithm.item;

import com.google.common.base.Preconditions;
import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.util.ItemHelper;
import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ingredientrestriction.IngredientRestrictionRegistry;
import pers.yufiria.craftorithm.item.ingredientrestriction.IngredientRestrictionRule;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ACTIVE, priority = 1),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, priority = 1, isAsync = true)
    }
)
public enum ItemManager implements LifecycleTask {

    INSTANCE;

    private final Map<String, ItemProvider> itemProviderMap = new LinkedHashMap<>();
    //记录已抛出过异常的物品提供源, 避免每次匹配都重复打印堆栈
    private final Set<String> erroredProviders = ConcurrentHashMap.newKeySet();
    private final Map<NamespacedItemId, Integer> customCookingFuelMap = new ConcurrentHashMap<>();
    private BukkitConfigWrapper customFuelConfig;
    private final String BURN_TIME_KEY = "burn_time";
    private BukkitConfigWrapper itemPacksConfig;
    private final Map<String, ItemPack> itemPacks = new ConcurrentHashMap<>();
    private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private final List<IngredientRestrictionRule> ingredientRestrictionRules = new ArrayList<>();

    /**
     * 注册一个物品提供源
     * @param itemProvider
     */
    public void regItemProvider(ItemProvider itemProvider) {
        Preconditions.checkArgument(
            !itemProvider.namespace().equalsIgnoreCase(NamespacedKey.MINECRAFT),
            "Item provider cannot use namespace minecraft"
        );
        itemProviderMap.put(itemProvider.namespace(), itemProvider);
    }

    /**
     * 删除一个物品提供源
     * @param providerNamespace
     * @return
     */
    public ItemProvider removeItemProvider(String providerNamespace) {
        return itemProviderMap.remove(providerNamespace);
    }

    /**
     * 重置物品提供源列表
     */
    public void resetItemProviders() {
        itemProviderMap.clear();
    }

    /**
     * 根据名字获取一个物品
     * @return 获取到的物品，不存在返回 Optional.empty()
     */
    public Optional<ItemStack> matchItem(NamespacedItemIdStack stackedItemId) {
        return matchItem(stackedItemId, null);
    }

    /**
     * 根据名字获取一个物品,并解析玩家变量
     * @return 获取到的物品，不存在返回 Optional.empty()
     */
    public Optional<ItemStack> matchItem(NamespacedItemIdStack stackedItemId, @Nullable OfflinePlayer player) {
        ItemStack item;
        NamespacedItemId itemId = stackedItemId.itemId();
        int amount = stackedItemId.amount();
        ItemProvider provider = itemProviderMap.get(itemId.namespace());
        if (provider == null) {
            return matchVanillaItem(itemId, amount);
        }

        try {
            if (player != null)
                item = provider.matchItem(itemId.itemId(), player);
            else
                item = provider.matchItem(itemId.itemId());
        } catch (Throwable t) {
            logProviderError(itemId.namespace(), t);
            return Optional.empty();
        }
        if (item == null)
            return Optional.empty();
        if (item.getAmount() != amount) {
            //克隆后再改数量, 避免污染提供源可能返回的缓存物品实例
            item = item.clone();
            item.setAmount(amount);
        }
        return Optional.of(item);
    }

    private void logProviderError(String providerNamespace, Throwable throwable) {
        if (erroredProviders.add(providerNamespace)) {
            CrypticLib.info("&cItem provider '" + providerNamespace + "' threw an exception, it may be incompatible with the installed plugin version");
            throwable.printStackTrace();
        }
    }

    /**
     * 获取一个物品的完整id,包含命名空间和id
     * 不会匹配没有数据的原版物品
     * @param item 传入的物品
     * @return 传入的物品id，未找到返回 Optional.empty()
     */
    public Optional<NamespacedItemIdStack> matchItemId(ItemStack item, boolean ignoreAmount) {
        if (ItemHelper.isAir(item)) {
            return Optional.empty();
        }

        if (!item.hasItemMeta()) {
            return Optional.empty();
        }

        return matchItemIdFromProviders(item, ignoreAmount);
    }

    /**
     * 获取一个物品的完整id,包含命名空间和id
     * 如果无法在已经挂钩的其他插件里找到这个物品，但它又不为空气，那么将会返回原版物品id
     * @param item 传入的物品
     * @return 传入的物品id
     */
    public Optional<NamespacedItemIdStack> matchItemIdOrVanilla(ItemStack item, boolean ignoreAmount) {
        if (ItemHelper.isAir(item)) {
            return Optional.empty();
        }

        if (!item.hasItemMeta()) {
            return Optional.of(new NamespacedItemIdStack(
                NamespacedItemId.fromMaterial(item.getType()),
                ignoreAmount ? 1 : item.getAmount()
            ));
        }

        return matchItemIdFromProviders(item, ignoreAmount)
            .or(() -> Optional.of(new NamespacedItemIdStack(
                NamespacedItemId.fromMaterial(item.getType()),
                ignoreAmount ? 1 : item.getAmount()
            )));
    }

    /**
     * 遍历所有物品提供源尝试匹配物品id
     */
    private Optional<NamespacedItemIdStack> matchItemIdFromProviders(ItemStack item, boolean ignoreAmount) {
        for (Map.Entry<String, ItemProvider> itemProviderEntry : itemProviderMap.entrySet()) {
            NamespacedItemIdStack namespacedItemIdStack;
            try {
                namespacedItemIdStack = itemProviderEntry.getValue().matchItemId(item, ignoreAmount);
            } catch (Throwable t) {
                logProviderError(itemProviderEntry.getKey(), t);
                continue;
            }
            if (namespacedItemIdStack != null) {
                return Optional.of(namespacedItemIdStack);
            }
        }
        return Optional.empty();
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
            itemId = matchItemId(item, ignoreAmount).orElse(null);
            if (itemId == null) {
                String id = item.getType().getKey().getKey();
                if (CraftorithmItemProvider.INSTANCE.matchItem("plugin_created:" + id) != null) {
                    id += TIME_FORMAT.format(System.currentTimeMillis());
                }
                itemId = CraftorithmItemProvider.INSTANCE.regCraftorithmItem("plugin_created", id, item);
                if (ignoreAmount) {
                    itemId.setAmount(1);
                }
            }
        } else {
            itemId = new NamespacedItemIdStack(
                NamespacedItemId.fromMaterial(item.getType()),
                ignoreAmount ? 1 : item.getAmount()
            );
        }
        return itemId;
    }

    /**
     * 获取原版物品
     * @param itemId 物品的ID
     * @param amount 物品数量
     * @return 物品，不存在返回 Optional.empty()
     */
    public Optional<ItemStack> matchVanillaItem(NamespacedItemId itemId, int amount) {
        String itemIdString = itemId.toString();
        Material material = MaterialHelper.matchMaterial(itemIdString);
        if (material == null) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack(material, amount));
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
        NamespacedItemId itemId = matchItemId(item, true)
            .map(NamespacedItemIdStack::itemId)
            .orElseGet(() -> NamespacedItemId.fromMaterial(item.getType()));
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
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        if (lifeCycle.equals(LifecyclePhase.ACTIVE)) {
            customFuelConfig = new BukkitConfigWrapper(Craftorithm.instance(), "custom_fuels.yml");
            itemPacksConfig = new BukkitConfigWrapper(Craftorithm.instance(), "item_packs.yml");
        }
        erroredProviders.clear();
        reloadCustomCookingFuel();
        reloadItemPacks();
        reloadIngredientRestrictionRules();
    }

    private void reloadItemPacks() {
        itemPacksConfig.reloadConfig();
        itemPacks.clear();
        YamlConfiguration config = itemPacksConfig.config();
        for (String key : config.getKeys(false)) {
            List<String> itemIdStrList = config.getStringList(key);
            if (itemIdStrList.isEmpty()) {
                continue;
            }
            ItemPack itemPack = new ItemPack(key, itemIdStrList);
            itemPacks.put(key, itemPack);
        }
    }

    public @Nullable ItemPack getItemPack(String itemId) {
        return itemPacks.get(itemId);
    }

    //合成限制规则相关

    private void reloadIngredientRestrictionRules() {
        ingredientRestrictionRules.clear();
        for (ConfigurationSection section : PluginConfigs.INGREDIENT_RESTRICTION_RULES.value()) {
            IngredientRestrictionRule rule = IngredientRestrictionRegistry.INSTANCE.create(section);
            if (rule == null) {
                CrypticLib.info("&cUnknown ingredient restriction rule type: " + section.getString("type", ""));
                continue;
            }
            ingredientRestrictionRules.add(rule);
        }
    }

    /**
     * 检查物品是否允许参与指定配方
     * @param items 物品数组
     * @param recipeKey 配方的NamespacedKey
     * @return 允许合成返回true，被规则阻止返回false
     */
    public boolean canCraft(ItemStack[] items, NamespacedKey recipeKey) {
        if (ingredientRestrictionRules.isEmpty())
            return true;
        for (ItemStack item : items) {
            if (ItemHelper.isAir(item))
                continue;
            for (IngredientRestrictionRule rule : ingredientRestrictionRules) {
                if (rule.isBlocked(item, recipeKey))
                    return false;
            }
        }
        return true;
    }

}