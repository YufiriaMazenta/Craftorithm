package top.oasismc.oasisrecipe.item;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import ink.ptms.zaphkiel.ZapAPI;
import ink.ptms.zaphkiel.Zaphkiel;
import ink.ptms.zaphkiel.api.Item;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.Plugin;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.nbt.NBTManager;
import top.oasismc.oasisrecipe.recipe.handler.RecipeManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum ItemLoader implements Listener {

    INSTANCE;
    private final Map<String, ItemStack> itemCache;

    ItemLoader() {
        itemCache = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        itemsAdderLoaded = true;
        Bukkit.getScheduler().callSyncMethod(OasisRecipe.getInstance(), () -> {
            Plugin itemsAdderPlugin = Bukkit.getPluginManager().getPlugin("ItemsAdder");
            if (itemsAdderPlugin != null)
                FurnaceSmeltEvent.getHandlerList().unregister(itemsAdderPlugin);
            RecipeManager.INSTANCE.reloadRecipes();
            return null;
        });
    }

    private static boolean itemsAdderLoaded = false;
    private static final ConfigFile itemFile = new ConfigFile("items.yml");
    private static final ConfigFile resultFile = new ConfigFile("results.yml");

    public static ConfigFile getItemFile() {
        return itemFile;
    }

    public static ConfigFile getResultFile() {
        return resultFile;
    }

    public static ItemStack getItemFromConfig(String itemName) {
        //ItemsAdder兼容
        if (itemName.startsWith("ItemsAdder:")) {
            return getItemsAdderItem(itemName);
        }

        if (itemName.startsWith("Zaphkiel:")) {
            return getZaphkielItem(itemName);
        }

        if (itemName.startsWith("MythicMobs:")) {
            return getMythicMobsItem(itemName);
        }

        if (itemName.startsWith("Oraxen")) {
            return getOraxenItem(itemName);
        }

        if (!itemName.startsWith("items:") && !itemName.startsWith("results:")) {
            Material material = Material.matchMaterial(itemName);
            if (material == null)
                throw new IllegalArgumentException(itemName + " is a non-existent item type");
            return new ItemStack(material);
        }

        if (INSTANCE.itemCache.containsKey(itemName)) {
            return INSTANCE.itemCache.get(itemName);
        }

        ConfigFile config;
        if (itemName.startsWith("items:")) {
            config = itemFile;
        } else {
            config = resultFile;
        }
        String keyName = itemName;
        itemName = itemName.substring(itemName.indexOf(":") + 1);

        if (config.getConfig().getString(itemName + ".material", "Null").equals("Null"))
            throw new IllegalArgumentException(itemName + " is not exist");
        String materialStr = config.getConfig().getString(itemName + ".material", "Null");
        Material material = Material.matchMaterial(materialStr);
        if (material == null)
            throw new IllegalArgumentException(materialStr + " is a non-existent item type");
        ItemStack itemStack = new ItemStack(material, config.getConfig().getInt(itemName + ".amount", 1));
        NBTManager.loadNBT2Item(itemName, itemStack, config);
        INSTANCE.itemCache.put(keyName, itemStack);
        return itemStack;
    }

    public static RecipeChoice getChoiceFromStr(String str) {
        if (str.contains(":")) {
            ItemStack item = getItemFromConfig(str);
            return new RecipeChoice.ExactChoice(item);
        }

        Material material = Material.matchMaterial(str);
        if (material == null)
            throw new IllegalArgumentException(str + " is a non-existent item type");
        else
            return new RecipeChoice.MaterialChoice(material);
    }

    public Map<String, ItemStack> getItemCache() {
        return itemCache;
    }

    public boolean isItemsAdderLoaded() {
        return itemsAdderLoaded;
    }

    private static ItemStack getZaphkielItem(String itemName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Zaphkiel");
        if (plugin == null)
            throw new IllegalArgumentException("Zaphkiel plugin not loaded");
        ZapAPI api = Zaphkiel.INSTANCE.api();
        itemName = itemName.substring(itemName.indexOf(":") + 1);
        Item item = api.getItemManager().getItem(itemName);
        if (item == null) {
            throw new IllegalArgumentException(itemName + " is not a valid Zaphkiel item");
        }
        return item.buildItemStack(null);
    }

    private static ItemStack getItemsAdderItem(String itemName) {
        if (!itemsAdderLoaded)
            throw new IllegalArgumentException("ItemsAdder plugin not loaded");
        itemName = itemName.substring(itemName.indexOf(":") + 1);
        CustomStack stack = CustomStack.getInstance(itemName);
        if (stack != null) {
            return stack.getItemStack();
        } else {
            throw new IllegalArgumentException(itemName + " is not a valid ItemsAdder item");
        }
    }

    private static ItemStack getMythicMobsItem(String itemName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (plugin == null)
            throw new IllegalArgumentException("MythicMobs plugin not loaded");
        ItemExecutor executor = MythicBukkit.inst().getItemManager();
        Optional<MythicItem> itemOptional = executor.getItem(itemName);
        if (!itemOptional.isPresent()) {
            throw new IllegalArgumentException(itemName + " is not a valid MythicMobs item");
        }
        MythicItem item = itemOptional.get();
        int amount = item.getAmount();
        return BukkitAdapter.adapt(itemOptional.get().generateItemStack(amount));
    }

    private static ItemStack getOraxenItem(String itemName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Oraxen");
        if (plugin == null)
            throw new IllegalArgumentException("Oraxen plugin not loaded");
        itemName = itemName.substring(itemName.indexOf(":") + 1);
        if (OraxenItems.exists(itemName)) {
            throw new IllegalArgumentException(itemName + " is not a valid Oraxen item");
        }
        return OraxenItems.getItemById(itemName).build();
    }


}
