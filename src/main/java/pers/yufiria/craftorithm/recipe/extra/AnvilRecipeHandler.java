package pers.yufiria.craftorithm.recipe.extra;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtManager;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtRules;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
public enum AnvilRecipeHandler implements Listener {

    INSTANCE;

    private final Map<NamespacedKey, AnvilRecipe> anvilRecipes = new ConcurrentHashMap<>();

    public boolean registerAnvilRecipe(final AnvilRecipe anvilRecipe) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            throw new UnsupportedOperationException("AnvilRecipe is not enabled");
        if (anvilRecipes.containsKey(anvilRecipe.getKey())) {
            return false;
        }
        anvilRecipes.put(anvilRecipe.getKey(), anvilRecipe);
        return true;
    }

    public boolean unregisterAnvilRecipe(final NamespacedKey key) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            throw new UnsupportedOperationException("AnvilRecipe is not enabled");
        return anvilRecipes.remove(key) != null;
    }

    public AnvilRecipe getAnvilRecipe(final NamespacedKey key) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            throw new UnsupportedOperationException("AnvilRecipe is not enabled");
        return anvilRecipes.get(key);
    }

    @Nullable
    public AnvilRecipe matchAnvilRecipe(ItemStack base, ItemStack addition) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            throw new UnsupportedOperationException("AnvilRecipe is not enabled");
        for (Map.Entry<NamespacedKey, AnvilRecipe> anvilRecipeEntry : anvilRecipes.entrySet()) {
            AnvilRecipe anvilRecipe = anvilRecipeEntry.getValue();
            StackableItemIdChoice recipeBaseId = anvilRecipe.base();
            StackableItemIdChoice recipeAdditionId = anvilRecipe.addition();
            if (!recipeBaseId.test(base))
                continue;
            BukkitMsgSender.INSTANCE.debug("matched base");
            if (!recipeAdditionId.test(addition))
                continue;
            BukkitMsgSender.INSTANCE.debug("matched addition");
            return anvilRecipe;
        }
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;
        ItemStack base = event.getInventory().getItem(0);
        ItemStack addition = event.getInventory().getItem(1);
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
            return;
        //TODO 判断是否是不能参与合成的物品
        AnvilRecipe anvilRecipe = matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;

        ItemStack result = anvilRecipe.getResult();
        NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemId(result, false);
        if (resultId != null) {
            ItemStack refreshItem = ItemManager.INSTANCE.matchItem(resultId, (Player) event.getViewers().get(0));
            result.setItemMeta(refreshItem.getItemMeta());
        }

        //处理NBT保留操作
        Optional<KeepNbtRules> recipeKeepNbtRules = KeepNbtManager.INSTANCE.getRecipeKeepNbtRules(anvilRecipe.getKey());
        recipeKeepNbtRules.ifPresent(
            rules -> {
                ItemMeta resultMeta = result.getItemMeta();
                ItemMeta baseMeta = Objects.requireNonNull(base).getItemMeta();
                resultMeta = rules.processItemMeta(baseMeta, resultMeta);
                result.setItemMeta(resultMeta);
            }
        );
        event.getInventory().setRepairCost(anvilRecipe.costLevel());
        //刷新物品

        event.setResult(result);
        event.getInventory().setItem(2, result);
        Bukkit.getPluginManager().callEvent(new PrepareAnvilRecipeEvent(event, anvilRecipe));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClickAnvilResult(InventoryClickEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;

        if (!(event.getInventory() instanceof AnvilInventory anvilInventory))
            return;
        if (event.getSlot() != 2)
            return;
        ItemStack base = anvilInventory.getItem(0);
        ItemStack addition = anvilInventory.getItem(1);
        ItemStack result = anvilInventory.getItem(2);
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition) || ItemHelper.isAir(result))
            return;
        AnvilRecipe anvilRecipe = matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;
        NamespacedItemIdStack baseId = ItemManager.INSTANCE.matchItemId(base, true);
        baseId = baseId != null ? baseId : new NamespacedItemIdStack(NamespacedItemId.fromMaterial(base.getType()), base.getAmount());
        NamespacedItemIdStack additionId = ItemManager.INSTANCE.matchItemId(addition, true);
        additionId = additionId != null ? additionId : new NamespacedItemIdStack(NamespacedItemId.fromMaterial(addition.getType()), addition.getAmount());
        Player player = (Player) event.getWhoClicked();

        //处理NBT保留操作
        Optional<KeepNbtRules> recipeKeepNbtRules = KeepNbtManager.INSTANCE.getRecipeKeepNbtRules(anvilRecipe.getKey());
        recipeKeepNbtRules.ifPresent(
            rules -> {
                ItemMeta resultMeta = result.getItemMeta();
                ItemMeta baseMeta = Objects.requireNonNull(base).getItemMeta();
                resultMeta = rules.processItemMeta(baseMeta, resultMeta);
                result.setItemMeta(resultMeta);
            }
        );

        int baseNum = base.getAmount(), additionNum = addition.getAmount();
        int needBaseNum = anvilRecipe.base().getUseAmount(baseId.itemId()), needAdditionNum = anvilRecipe.addition().getUseAmount(additionId.itemId());
        int costLevel = anvilRecipe.costLevel();
        event.setCancelled(true);
        int canCraftNum = Math.min(baseNum / needBaseNum, additionNum / needAdditionNum);
        canCraftNum = Math.min(result.getMaxStackSize(), canCraftNum);
        //判断是否合成成功,用于触发事件等操作

        boolean craftResult = false;
        switch (event.getClick()) {
            case LEFT:
            case RIGHT:
            case DOUBLE_CLICK:
                if (PlayerUtils.isSurvivalOrAdventure(player) && player.getLevel() < costLevel) {
                    break;
                }
                ItemStack cursor = event.getCursor();
                if (ItemHelper.isAir(cursor)) {
                    base.setAmount(baseNum - needBaseNum);
                    addition.setAmount(additionNum - needAdditionNum);
                    event.setCursor(result);
                    if (PlayerUtils.isSurvivalOrAdventure(player)) {
                        player.setLevel(player.getLevel() - costLevel);
                    }
                } else {
                    int resultCursor = cursor.getAmount() + result.getAmount();
                    if (resultCursor > result.getMaxStackSize())
                        break;
                    base.setAmount(baseNum - needBaseNum);
                    addition.setAmount(additionNum - needAdditionNum);
                    event.getCursor().setAmount(resultCursor);
                    if (PlayerUtils.isSurvivalOrAdventure(player)) {
                        player.setLevel(player.getLevel() - costLevel);
                    }
                }
                craftResult = true;
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                int costAmount1 = needBaseNum * canCraftNum;
                int costAmount2 = needAdditionNum * canCraftNum;
                int finalCostLevel = costLevel * canCraftNum;
                if (!PlayerUtils.isCreativeOrSpectator(player) && player.getLevel() < costLevel) {
                    break;
                }
                base.setAmount(baseNum - costAmount1);
                addition.setAmount(additionNum - costAmount2);
                result.setAmount(canCraftNum * result.getAmount());
                HashMap<Integer, ItemStack> failedItems = event.getWhoClicked().getInventory().addItem(result);
                if (PlayerUtils.isSurvivalOrAdventure(player)) {
                    player.setLevel(player.getLevel() - finalCostLevel);
                }
                if (failedItems.isEmpty())
                    break;
                for (ItemStack value : failedItems.values()) {
                    player.getWorld().dropItem(event.getWhoClicked().getLocation(), value);
                }
                craftResult = true;
                break;
            case DROP:
                if (player.getLevel() < costLevel)
                    break;
                base.setAmount(baseNum - needBaseNum);
                addition.setAmount(additionNum - needAdditionNum);
                if (PlayerUtils.isSurvivalOrAdventure(player)) {
                    player.setLevel(player.getLevel() - costLevel);
                }
                player.getWorld().dropItem(event.getWhoClicked().getLocation(), result);
                craftResult = true;
                break;
            case CONTROL_DROP:
                int costAmount11 = needBaseNum * canCraftNum;
                int costAmount22 = needAdditionNum * canCraftNum;
                int finalCostLevel2 = costLevel * canCraftNum;
                if (player.getLevel() < finalCostLevel2)
                    break;
                base.setAmount(baseNum - costAmount11);
                addition.setAmount(additionNum - costAmount22);
                result.setAmount(canCraftNum * result.getAmount());
                if (PlayerUtils.isSurvivalOrAdventure(player)) {
                    player.setLevel(player.getLevel() - finalCostLevel2);
                }
                player.getWorld().dropItem(event.getWhoClicked().getLocation(), result);
                craftResult = true;
                break;
            default:
                break;
        }

        //todo 触发事件

        //更新页面
        AnvilRecipe afterClickRecipe = matchAnvilRecipe(base, addition);
        if (afterClickRecipe == null) {
            anvilInventory.setItem(2, null);
            anvilInventory.setRepairCost(0);
            return;
        }
        anvilInventory.setItem(2, afterClickRecipe.getResult());
        anvilInventory.setRepairCost(anvilRecipe.costLevel());
    }

}
