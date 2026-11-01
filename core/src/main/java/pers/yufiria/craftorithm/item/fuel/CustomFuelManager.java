package pers.yufiria.craftorithm.item.fuel;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.listener.EventListener;
import crypticlib.util.BukkitConfigHelper;
import crypticlib.util.InventoryViewHelper;
import crypticlib.util.ItemHelper;
import crypticlib.util.Lazy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义燃料管理。
 * <p>
 * 负责 {@code custom_fuels.yml} 的加载、旧格式迁移、查询与增删, 以及燃料槽的点击交互与燃烧时间。
 * <p>
 * 配置格式:
 * <pre>
 * fuels:
 *   - item: "minecraft:command_block"
 *     burn_time: 100
 * </pre>
 * <p>
 * 原版燃料槽只接受原版燃料, 自定义燃料的入槽由本类按原版燃料的点击行为模拟:
 * <ul>
 * <li>左键(光标带物品): 空槽放整组, 同类合并到堆叠上限, 不同物品整组互换</li>
 * <li>右键(光标带物品): 空槽放 1 个, 同类加 1 个, 不同物品无事发生(原版右键不互换)</li>
 * <li>Shift(从玩家背包): 只转入燃料槽, 能放多少放多少, 放不下时无事发生</li>
 * <li>数字键: 快捷栏物品与燃料槽换位, 燃料槽为空时移入</li>
 * </ul>
 * 光标为空的拾取(左整组/右半组)、从燃料槽取出、Q 丢弃、双击聚拢等由原版自行处理。
 */
@EventListener
@LifecycleTaskConfig(schedules = {
    @LifecycleSchedule(phase = LifecyclePhase.ACTIVE),
    @LifecycleSchedule(phase = LifecyclePhase.RELOAD, isAsync = true),
    @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
})
public enum CustomFuelManager implements Listener, LifecycleTask {

    INSTANCE;

    //配置里记录燃烧时间的键
    private static final String BURN_TIME_KEY = "burn_time";
    //配置里记录物品id的键
    private static final String ITEM_KEY = "item";
    //燃料列表的顶层键(旧格式是「物品id: {burn_time: n}」的顶层映射)
    private static final String FUELS_KEY = "fuels";
    private static final DateTimeFormatter BACKUP_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    //未指定燃烧时间时的默认值, 单位tick
    private static final int DEFAULT_BURN_TIME = 200;
    //熔炉(含高炉、烟熏炉)容器的燃料槽序号
    private static final int FUEL_SLOT = 1;

    //重载在异步线程进行, 而该表会被主线程的燃料事件读取, 因此使用并发表并提供不可修改视图
    private final Map<NamespacedItemId, Integer> fuels = new ConcurrentHashMap<>();
    private final Lazy<Map<NamespacedItemId, Integer>> lazyFuelsView = Lazy.of(() -> Collections.unmodifiableMap(fuels));
    private volatile BukkitConfigWrapper config;

    /**
     * 配置包装器。延迟创建, 保证任一生命周期阶段调用都不会拿到 null
     */
    private BukkitConfigWrapper config() {
        BukkitConfigWrapper current = config;
        if (current == null) {
            synchronized (this) {
                current = config;
                if (current == null) {
                    current = new BukkitConfigWrapper(Craftorithm.instance(), "custom_fuels.yml");
                    config = current;
                }
            }
        }
        return current;
    }

    /**
     * 当前生效的自定义燃料(不可修改视图)
     */
    public @Unmodifiable Map<NamespacedItemId, Integer> fuels() {
        return lazyFuelsView.get();
    }

    /**
     * 物品是否为自定义燃料
     */
    public boolean isCustomFuel(ItemStack item) {
        if (fuels.isEmpty()) {
            return false;
        }
        Integer burnTime = matchBurnTime(item);
        return burnTime != null && burnTime > 0;
    }

    /**
     * 取物品对应的燃烧时间, 不是自定义燃料时返回 null
     */
    public @Nullable Integer matchBurnTime(ItemStack item) {
        if (fuels.isEmpty()) {
            return null;
        }
        NamespacedItemId itemId = ItemManager.INSTANCE.matchItemIdOrVanilla(item, true)
            .map(NamespacedItemIdStack::itemId)
            .orElseThrow();
        return fuels.get(itemId);
    }

    /**
     * 注册一个自定义燃料, 已存在时返回 false
     */
    public boolean addFuel(ItemStack item, int burnTime) {
        if (burnTime <= 0) {
            throw new IllegalArgumentException("Custom fuel burn time must be positive: " + burnTime);
        }
        NamespacedItemIdStack stackedItemId = ItemManager.INSTANCE.matchItemIdOrCreate(item, false);
        if (stackedItemId == null) {
            throw new IllegalArgumentException("Cannot add null item as a fuel");
        }
        NamespacedItemId itemId = stackedItemId.itemId();
        if (fuels.containsKey(itemId)) {
            return false;
        }
        fuels.put(itemId, burnTime);
        CrypticLibBukkit.scheduler().async(() -> {
            List<Map<String, Object>> entries = readFuelList();
            entries.removeIf(entry -> itemId.toString().equals(entry.get(ITEM_KEY)));
            Map<String, Object> fuel = new LinkedHashMap<>();
            //列表里存纯字符串id, 不把id拼进YAML键, 避免id含点号时被拆成多级节点
            fuel.put(ITEM_KEY, itemId.toString());
            fuel.put(BURN_TIME_KEY, burnTime);
            entries.add(fuel);
            writeFuelList(entries);
        });
        return true;
    }

    /**
     * 移除一个自定义燃料, 不存在时返回 false
     */
    public boolean removeFuel(@Nullable NamespacedItemId itemId) {
        if (itemId == null || !fuels.containsKey(itemId)) {
            return false;
        }
        fuels.remove(itemId);
        CrypticLibBukkit.scheduler().async(() -> {
            List<Map<String, Object>> entries = readFuelList();
            entries.removeIf(entry -> itemId.toString().equals(entry.get(ITEM_KEY)));
            writeFuelList(entries);
        });
        return true;
    }

    private void reload() {
        config().reloadConfig();
        YamlConfiguration yaml = config().config();
        migrateLegacyFuels(yaml);
        //先构造到局部表, 单条失败只跳过该条, 全部解析成功后再整体替换, 避免半加载状态
        Map<NamespacedItemId, Integer> loaded = new ConcurrentHashMap<>();
        for (Map<?, ?> entry : yaml.getMapList(FUELS_KEY)) {
            ConfigurationSection config = BukkitConfigHelper.map2ConfigSection(entry);
            String itemIdStr = config.getString(ITEM_KEY);
            if (itemIdStr == null || itemIdStr.isEmpty()) {
                CrypticLib.warn("Custom fuel entry has no valid item id, skipped: " + entry);
                continue;
            }
            NamespacedItemId itemId = NamespacedItemId.fromString(itemIdStr);
            if (itemId == null) {
                CrypticLib.warn("Invalid custom fuel item id '" + itemIdStr + "', skipped");
                continue;
            }
            int burnTime = config.getInt(BURN_TIME_KEY, 0);
            if (burnTime <= 0) {
                CrypticLib.warn("Invalid burn_time " + burnTime + " for custom fuel '" + itemIdStr + "', skipped");
                continue;
            }
            loaded.put(itemId, burnTime);
        }
        fuels.clear();
        fuels.putAll(loaded);
    }

    /**
     * 把旧格式(顶层键为物品id, 值为 {burn_time: n})迁移为 fuels 列表。
     * 只在新格式缺失且存在旧键时执行一次; 仅在有条目读不出来时先备份原文件, 避免留下多余文件。
     */
    private void migrateLegacyFuels(YamlConfiguration yaml) {
        if (yaml.contains(FUELS_KEY)) {
            return;
        }
        Set<String> legacyKeys = yaml.getKeys(false);
        if (legacyKeys.isEmpty()) {
            return;
        }
        List<Map<String, Object>> migrated = new ArrayList<>();
        List<String> invalidKeys = new ArrayList<>();
        for (String legacyKey : legacyKeys) {
            ConfigurationSection section = yaml.getConfigurationSection(legacyKey);
            int burnTime = section != null ? section.getInt(BURN_TIME_KEY, 0) : 0;
            if (burnTime <= 0) {
                invalidKeys.add(legacyKey);
                continue;
            }
            Map<String, Object> fuel = new LinkedHashMap<>();
            fuel.put(ITEM_KEY, legacyKey);
            fuel.put(BURN_TIME_KEY, burnTime);
            migrated.add(fuel);
        }
        //只有会丢条目的迁移才备份: 全部条目都读得出来时属于纯格式转换, 不留多余文件
        if (!invalidKeys.isEmpty()) {
            backupConfig();
        }
        yaml.set(FUELS_KEY, migrated);
        for (String legacyKey : legacyKeys) {
            yaml.set(legacyKey, null);
        }
        config().saveConfig();
        CrypticLib.info("Migrated " + migrated.size() + " custom fuel(s) to the new '" + FUELS_KEY + "' list format");
        if (!invalidKeys.isEmpty()) {
            CrypticLib.warn("Skipped " + invalidKeys.size() + " unreadable custom fuel entr(y/ies): " + invalidKeys);
        }
    }

    private void backupConfig() {
        File source = config().configFile();
        if (!source.exists()) {
            return;
        }
        File backup = new File(
            source.getParentFile(),
            source.getName() + ".bak-" + LocalDateTime.now().format(BACKUP_TIME_FORMAT)
        );
        try {
            Files.copy(source.toPath(), backup.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            CrypticLib.info("Backed up custom fuel config to " + backup.getName());
        } catch (IOException e) {
            CrypticLib.error("Failed to back up custom fuel config to " + backup.getName(), e);
        }
    }

    private List<Map<String, Object>> readFuelList() {
        config().reloadConfig();
        List<Map<String, Object>> entries = new ArrayList<>();
        for (Map<?, ?> entry : config().config().getMapList(FUELS_KEY)) {
            Map<String, Object> copy = new LinkedHashMap<>();
            entry.forEach((key, value) -> copy.put(String.valueOf(key), value));
            entries.add(copy);
        }
        return entries;
    }

    private void writeFuelList(List<Map<String, Object>> entries) {
        config().set(FUELS_KEY, entries);
        config().saveConfig();
        config().reloadConfig();
    }

    // ===== 燃料槽交互 =====

    @EventHandler(priority = EventPriority.MONITOR)
    public void putFuelOnClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        Inventory topInventory = InventoryViewHelper.getTopInventory(event);
        if (!(topInventory instanceof FurnaceInventory furnaceInventory))
            return;
        switch (event.getClick()) {
            case LEFT, RIGHT -> handlePickupClick(event, furnaceInventory);
            case SHIFT_LEFT, SHIFT_RIGHT -> handleQuickMoveClick(event, furnaceInventory);
            case NUMBER_KEY -> handleSwapClick(event, furnaceInventory);
            default -> {
            }
        }
    }

    /**
     * 光标带自定义燃料点击燃料槽(原版 PICKUP)。
     * 左键空槽放整组、同类合并到堆叠上限、不同物品整组互换; 右键放 1 个, 不同物品无事发生
     */
    private void handlePickupClick(InventoryClickEvent event, FurnaceInventory furnaceInventory) {
        if (!furnaceInventory.equals(event.getClickedInventory()) || event.getSlot() != FUEL_SLOT)
            return;
        ItemStack cursor = event.getCursor();
        //光标为空是原版拾取; 原版燃料也交给原版处理
        if (ItemHelper.isAir(cursor) || cursor.getType().isFuel() || !isCustomFuel(cursor))
            return;
        boolean placeOne = event.getClick() == ClickType.RIGHT;
        ItemStack current = event.getCurrentItem();
        if (ItemHelper.isAir(current)) {
            //空槽: 左键放整组, 右键放 1 个
            int moved = placeOne ? 1 : cursor.getAmount();
            ItemStack placed = cursor.clone();
            placed.setAmount(moved);
            cursor.setAmount(cursor.getAmount() - moved);
            event.setCancelled(true);
            event.setCurrentItem(placed);
            event.setCursor(cursor.getAmount() > 0 ? cursor : null);
            ((Player) event.getWhoClicked()).updateInventory();
        } else if (cursor.isSimilar(current)) {
            //同类: 合并到堆叠上限, 右键只加 1 个; 已满则无事发生
            int moved = Math.min(placeOne ? 1 : cursor.getAmount(), current.getMaxStackSize() - current.getAmount());
            if (moved <= 0) {
                return;
            }
            current.setAmount(current.getAmount() + moved);
            cursor.setAmount(cursor.getAmount() - moved);
            event.setCancelled(true);
            event.setCurrentItem(current);
            event.setCursor(cursor.getAmount() > 0 ? cursor : null);
            ((Player) event.getWhoClicked()).updateInventory();
        } else if (!placeOne) {
            //不同物品: 左键整组互换; 右键无事发生(不取消, 由原版保持原样)
            event.setCancelled(true);
            event.setCursor(current);
            event.setCurrentItem(cursor);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    /**
     * Shift 从玩家背包转移自定义燃料(原版 QUICK_MOVE 对燃料的行为)。
     * 只转入燃料槽, 能放多少放多少; 燃料槽放不下时无事发生
     */
    private void handleQuickMoveClick(InventoryClickEvent event, FurnaceInventory furnaceInventory) {
        //燃料槽内的物品 shift 出去由原版处理
        if (furnaceInventory.equals(event.getClickedInventory()))
            return;
        ItemStack current = event.getCurrentItem();
        if (ItemHelper.isAir(current) || current.getType().isFuel() || !isCustomFuel(current))
            return;
        //统一先拦截, 避免原版把自定义燃料当普通物品走别的转移路径
        event.setCancelled(true);
        ItemStack fuel = furnaceInventory.getFuel();
        int moved;
        if (ItemHelper.isAir(fuel)) {
            moved = Math.min(current.getAmount(), current.getMaxStackSize());
            if (moved <= 0) {
                return;
            }
            ItemStack placed = current.clone();
            placed.setAmount(moved);
            furnaceInventory.setFuel(placed);
        } else if (current.isSimilar(fuel)) {
            moved = Math.min(current.getAmount(), fuel.getMaxStackSize() - fuel.getAmount());
            if (moved <= 0) {
                return;
            }
            fuel.setAmount(fuel.getAmount() + moved);
            furnaceInventory.setFuel(fuel);
        } else {
            //燃料槽被其他物品占据: 与原版燃料一样不转移
            return;
        }
        //用实际转移量扣减, 避免把背包格减成负数
        current.setAmount(current.getAmount() - moved);
        event.setCurrentItem(current.getAmount() > 0 ? current : null);
        ((Player) event.getWhoClicked()).updateInventory();
    }

    /**
     * 数字键换位(原版 SWAP): 快捷栏自定义燃料与燃料槽换位, 燃料槽为空时移入。
     * 快捷栏为空时把燃料换出、快捷栏是其他物品时不换位, 均与原版一致, 由原版处理
     */
    private void handleSwapClick(InventoryClickEvent event, FurnaceInventory furnaceInventory) {
        if (!furnaceInventory.equals(event.getClickedInventory()) || event.getSlot() != FUEL_SLOT)
            return;
        int hotbarButton = event.getHotbarButton();
        if (hotbarButton < 0 || hotbarButton > 8)
            return;
        Player player = (Player) event.getWhoClicked();
        ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);
        if (ItemHelper.isAir(hotbarItem) || hotbarItem.getType().isFuel() || !isCustomFuel(hotbarItem))
            return;
        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();
        if (ItemHelper.isAir(current)) {
            int moved = Math.min(hotbarItem.getAmount(), hotbarItem.getMaxStackSize());
            if (moved <= 0) {
                return;
            }
            ItemStack placed = hotbarItem.clone();
            placed.setAmount(moved);
            hotbarItem.setAmount(hotbarItem.getAmount() - moved);
            furnaceInventory.setFuel(placed);
            player.getInventory().setItem(hotbarButton, hotbarItem.getAmount() > 0 ? hotbarItem : null);
        } else {
            //换位: 燃料槽物品进快捷栏, 快捷栏物品进燃料槽
            furnaceInventory.setFuel(hotbarItem);
            player.getInventory().setItem(hotbarButton, current);
        }
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void setFuelBurnTime(FurnaceBurnEvent event) {
        if (event.isCancelled())
            return;
        ItemStack fuel = event.getFuel();
        Integer time = matchBurnTime(fuel);
        if (time == null || time <= 0)
            return;
        event.setBurnTime(time);
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        switch (lifeCycle) {
            case ACTIVE -> config();
            case RELOAD -> reload();
            case DISABLE -> fuels.clear();
            default -> {
            }
        }
    }

}
