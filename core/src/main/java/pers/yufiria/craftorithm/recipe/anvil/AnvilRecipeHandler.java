package pers.yufiria.craftorithm.recipe.anvil;

import crypticlib.MinecraftVersion;
import crypticlib.listener.EventListener;
import crypticlib.script.ScriptValue;
import crypticlib.util.InventoryHelper;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.api.event.CraftorithmPrepareAnvilEvent;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.choice.ItemIdStackRecipeChoice;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessors;
import pers.yufiria.craftorithm.trigger.CraftTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;
import pers.yufiria.craftorithm.util.EventUtils;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

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

    public boolean unregisterAnvilRecipe(final NamespacedKey recipeKey) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            throw new UnsupportedOperationException("AnvilRecipe is not enabled");
        return anvilRecipes.remove(recipeKey) != null;
    }

    public AnvilRecipe getAnvilRecipe(final NamespacedKey recipeKey) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            throw new UnsupportedOperationException("AnvilRecipe is not enabled");
        return anvilRecipes.get(recipeKey);
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
            ItemIdStackRecipeChoice recipeBaseId = anvilRecipe.base();
            ItemIdStackRecipeChoice recipeAdditionId = anvilRecipe.addition();
            if (!recipeBaseId.test(base))
                continue;
            if (!recipeAdditionId.test(addition))
                continue;
            return anvilRecipe;
        }
        return null;
    }

    @SuppressWarnings("removal")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            return;

        ItemStack base = event.getInventory().getItem(0);
        ItemStack addition = event.getInventory().getItem(1);
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition))
            return;

        AnvilRecipe anvilRecipe = matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;

        if (!TriggerManager.INSTANCE.hasTrigger(CraftTriggerTypes.ANVIL, anvilRecipe.getKey())) {
            //只有存在配方对应触发器的时候，才进行触发器检查
            TriggerContext ctx = CraftTriggerTypes.ANVIL.extractPrepareContext(event);
            if (ctx != null) {
                int denied = TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.ANVIL.typeKey(), ctx);
                if (denied > 0) {
                    return;
                }
            }
        }

        // 检查 blocked_crafting_lore_rules
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{base, addition}, anvilRecipe.getKey())) {
            return;
        }

        AtomicReference<ItemStack> result = new AtomicReference<>(anvilRecipe.getResult());
        Optional<Player> playerOpt = EventUtils.getViewer(event);

        if (playerOpt.isEmpty()) {
            return;
        }

        Player player = playerOpt.get();
        NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemId(result.get(), false).orElse(null);
        if (resultId != null) {
            ItemManager.INSTANCE.matchItem(resultId, player).ifPresent(refreshItem -> result.get().setItemMeta(refreshItem.getItemMeta()));
        }

        //处理结果处理器
        Optional<ResultProcessors> recipeProcessors = ResultProcessorManager.INSTANCE.getRecipeProcessors(anvilRecipe.getKey());
        recipeProcessors.ifPresent(
            rules -> {
                rules.processItem(base, result.get(), player);
            }
        );

        event.setResult(result.get());
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21)) {
            AnvilView view = event.getView();
            view.setRepairCost(anvilRecipe.costLevel());
            view.setItem(2, result.get());
        } else {
            InventoryView view = ((InventoryEvent) event).getView();
            view.setItem(2, result.get());
            view.setProperty(InventoryView.Property.REPAIR_COST, anvilRecipe.costLevel());
        }
        player.updateInventory();
        new CraftorithmPrepareAnvilEvent(event, anvilRecipe).callEvent();
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

        AtomicReference<ItemStack> result = new AtomicReference<>(anvilInventory.getItem(2));
        if (ItemHelper.isAir(base) || ItemHelper.isAir(addition) || ItemHelper.isAir(result.get()))
            return;

        if (!(event.getClickedInventory() instanceof AnvilInventory)) {
            //如果点击的不是铁砧的页面，那么需要拦截双击收集所有物品这个操作
            if (result.get().isSimilar(event.getCurrentItem())) {
                if (event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                    event.setCancelled(true);
                }
            }
        }

        AnvilRecipe anvilRecipe = matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;

        TriggerContext ctx = null;
        if (!TriggerManager.INSTANCE.hasTrigger(CraftTriggerTypes.ANVIL, anvilRecipe.getKey())) {
            //只有存在配方对应触发器的时候，才进行触发器检查
            ctx = CraftTriggerTypes.ANVIL.extractContext(event);
            if (ctx != null) {
                int denied = TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.ANVIL.typeKey(), ctx);
                if (denied > 0) {
                    return;
                }
            }
        }

        // 检查能否合成
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{base, addition}, anvilRecipe.getKey())) {
            return;
        }

        NamespacedItemIdStack baseId = ItemManager.INSTANCE.matchItemId(base, true)
            .orElseGet(() -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(base.getType()), base.getAmount()));
        NamespacedItemIdStack additionId = ItemManager.INSTANCE.matchItemId(addition, true)
            .orElseGet(() -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(addition.getType()), addition.getAmount()));
        Player player = (Player) event.getWhoClicked();

        //处理结果处理器
        Optional<ResultProcessors> recipeProcessors = ResultProcessorManager.INSTANCE.getRecipeProcessors(anvilRecipe.getKey());
        recipeProcessors.ifPresent(
            rules -> {
                rules.processItem(base, result.get(), player);
            }
        );

        int baseNum = base.getAmount(), additionNum = addition.getAmount();
        int needBaseNum = anvilRecipe.base().getUseAmount(baseId.itemId()), needAdditionNum = anvilRecipe.addition().getUseAmount(additionId.itemId());
        int costLevel = anvilRecipe.costLevel();
        int canCraftNum = Math.min(baseNum / needBaseNum, additionNum / needAdditionNum);
        canCraftNum = Math.min(result.get().getMaxStackSize(), canCraftNum);

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
                    event.setCursor(result.get());
                    if (PlayerUtils.isSurvivalOrAdventure(player)) {
                        player.setLevel(player.getLevel() - costLevel);
                    }
                } else {
                    if (!result.get().isSimilar(cursor)) {
                        break;
                    }
                    int resultCursor = cursor.getAmount() + result.get().getAmount();
                    if (resultCursor > result.get().getMaxStackSize())
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
                if (!PlayerUtils.isCreativeOrSpectator(player) && player.getLevel() < finalCostLevel) {
                    break;
                }
                base.setAmount(baseNum - costAmount1);
                addition.setAmount(additionNum - costAmount2);
                result.get().setAmount(canCraftNum * result.get().getAmount());
                InventoryHelper.addItemOrDrop(event.getWhoClicked().getInventory(), result.get());
                if (PlayerUtils.isSurvivalOrAdventure(player)) {
                    player.setLevel(player.getLevel() - finalCostLevel);
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
                player.getWorld().dropItem(event.getWhoClicked().getLocation(), result.get());
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
                result.get().setAmount(canCraftNum * result.get().getAmount());
                if (PlayerUtils.isSurvivalOrAdventure(player)) {
                    player.setLevel(player.getLevel() - finalCostLevel2);
                }
                player.getWorld().dropItem(event.getWhoClicked().getLocation(), result.get());
                craftResult = true;
                break;
            default:
                break;
        }

        //合成成功后执行trigger的actions
        if (craftResult && ctx != null) {
            int craftNum = (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
                ? canCraftNum : 1;
            ctx.setVariable("craft_num", ScriptValue.of(craftNum));
            TriggerManager.INSTANCE.fire(CraftTriggerTypes.ANVIL.typeKey(), ctx);
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
                prepareAnvilEvent = constructor.newInstance(event.getView(), afterResult);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        Bukkit.getPluginManager().callEvent(prepareAnvilEvent);
        player.updateInventory();
    }

}
