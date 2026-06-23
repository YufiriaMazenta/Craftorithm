package pers.yufiria.craftorithm.recipe.extra;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.listener.EventListener;
import crypticlib.util.IOHelper;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRules;
import pers.yufiria.craftorithm.trigger.BuiltInTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition)) {
            return null;
        }
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

    @SuppressWarnings("removal")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;
        IOHelper.info("Call event");

        ItemStack base = event.getInventory().getItem(0);
        ItemStack addition = event.getInventory().getItem(1);
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
            return;

        //如果原材料包含不能用于合成的物品，结束流程
        boolean cannotCraft = ItemManager.INSTANCE.containsCannotCraftItem(base, addition);
        if (cannotCraft) {
            return;
        }

        //处理trigger模块的条件判断
        TriggerContext ctx = BuiltInTriggerTypes.ANVIL.extractPrepareContext(event);
        if (ctx == null) return;
        int denied = TriggerManager.INSTANCE.firePrepare(BuiltInTriggerTypes.ANVIL.typeKey(), ctx);
        if (denied > 0) {
            return;
        }

        AnvilRecipe anvilRecipe = matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;

        IOHelper.info("Recipe id: " + anvilRecipe.getKey());
        ItemStack result = anvilRecipe.getResult();
        NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemId(result, false);
        if (resultId != null) {
            ItemStack refreshItem = ItemManager.INSTANCE.matchItem(resultId, (Player) event.getViewers().getFirst());
            result.setItemMeta(refreshItem.getItemMeta());
        }

        //处理NBT保留操作
        Optional<CopyComponentsRules> recipeCopyNbtRules = CopyComponentsManager.INSTANCE.getRecipeCopyNbtRules(anvilRecipe.getKey());
        recipeCopyNbtRules.ifPresent(
            rules -> {
                ItemMeta resultMeta = result.getItemMeta();
                ItemMeta baseMeta = Objects.requireNonNull(base).getItemMeta();
                resultMeta = rules.processItemMeta(baseMeta, resultMeta);
                result.setItemMeta(resultMeta);
            }
        );

        event.setResult(result);
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21)) {
            AnvilView view = event.getView();
            view.setRepairCost(anvilRecipe.costLevel());
            view.setItem(2, result);
        } else {
            InventoryView view = ((InventoryEvent) event).getView();
            view.setItem(2, result);
            view.setProperty(InventoryView.Property.REPAIR_COST, anvilRecipe.costLevel());
        }
        Bukkit.getPluginManager().callEvent(new CraftorithmPrepareAnvilEvent(event, anvilRecipe));
    }

    @SuppressWarnings({"removal"})
    @EventHandler(priority = EventPriority.MONITOR)
    public void onClickAnvilResult(InventoryClickEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;
        if (!(event.getInventory() instanceof AnvilInventory anvilInventory))
            return;

        ItemStack base = anvilInventory.getItem(0);
        ItemStack addition = anvilInventory.getItem(1);
        //如果原材料包含不能用于合成的物品，结束流程
        boolean cannotCraft = ItemManager.INSTANCE.containsCannotCraftItem(base, addition);
        if (cannotCraft) {
            return;
        }

        ItemStack result = anvilInventory.getItem(2);
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition) || ItemHelper.isAir(result))
            return;

        if (!(event.getClickedInventory() instanceof AnvilInventory)) {
            //如果点击的不是铁砧的页面，那么需要拦截双击收集所有物品这个操作
            if (result.isSimilar(event.getCurrentItem())) {
                if (event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                    event.setCancelled(true);
                }
            }
        }

        //处理trigger模块的条件判断
        TriggerContext ctx = BuiltInTriggerTypes.ANVIL.extractContext(event);
        if (ctx == null) return;
        int denied = TriggerManager.INSTANCE.firePrepare(BuiltInTriggerTypes.ANVIL.typeKey(), ctx);
        if (denied > 0) {
            return;
        }

        AnvilRecipe anvilRecipe = matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;
        NamespacedItemIdStack baseId = ItemManager.INSTANCE.matchItemId(base, true);
        baseId = baseId != null ? baseId : new NamespacedItemIdStack(NamespacedItemId.fromMaterial(base.getType()), base.getAmount());
        NamespacedItemIdStack additionId = ItemManager.INSTANCE.matchItemId(addition, true);
        additionId = additionId != null ? additionId : new NamespacedItemIdStack(NamespacedItemId.fromMaterial(addition.getType()), addition.getAmount());
        Player player = (Player) event.getWhoClicked();

        //处理NBT保留操作
        Optional<CopyComponentsRules> recipeCopyNbtRules = CopyComponentsManager.INSTANCE.getRecipeCopyNbtRules(anvilRecipe.getKey());
        recipeCopyNbtRules.ifPresent(
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
        int canCraftNum = Math.min(baseNum / needBaseNum, additionNum / needAdditionNum);
        canCraftNum = Math.min(result.getMaxStackSize(), canCraftNum);

        if (!(event.getClickedInventory() instanceof AnvilInventory)) {
            return;
        }
        if (event.getSlot() != 2)
            return;
//        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
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
                    if (!result.isSimilar(cursor)) {
                        break;
                    }
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

        //合成成功后执行trigger的actions
        if (craftResult) {
            TriggerManager.INSTANCE.fire(BuiltInTriggerTypes.ANVIL.typeKey(), ctx);
        }

        //更新页面
        AnvilRecipe afterClickRecipe = matchAnvilRecipe(base, addition);
        ItemStack afterResult = null;
        int afterRepairCost = 0;
        if (afterClickRecipe != null) {
            afterResult = afterClickRecipe.getResult();
            afterRepairCost = anvilRecipe.costLevel();
        }

        PrepareAnvilEvent prepareAnvilEvent;
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21)) {
            AnvilView view = (AnvilView) event.getView();
            view.setRepairCost(afterRepairCost);
            view.setItem(2, afterResult);
            prepareAnvilEvent = new PrepareAnvilEvent(
                view,
                afterResult
            );
        } else {
            InventoryView view = event.getView();
            view.setProperty(InventoryView.Property.REPAIR_COST, afterRepairCost);
            view.setItem(2, afterResult);
            try {
                Class<PrepareAnvilEvent> prepareAnvilEventClass = PrepareAnvilEvent.class;
                Constructor<PrepareAnvilEvent> constructor = prepareAnvilEventClass
                    .getConstructor(InventoryView.class, ItemStack.class);
                prepareAnvilEvent = constructor.newInstance(event.getView(), afterClickRecipe.getResult());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        Bukkit.getPluginManager().callEvent(prepareAnvilEvent);
        player.updateInventory();
    }

}
