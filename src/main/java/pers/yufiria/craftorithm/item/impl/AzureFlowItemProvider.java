package pers.yufiria.craftorithm.item.impl;

import io.rokuko.azureflow.api.AzureFlowAPI;
import io.rokuko.azureflow.features.item.AzureFlowItem;
import io.rokuko.azureflow.features.item.factory.AzureFlowItemFactory;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum AzureFlowItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "azureflow";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        AzureFlowItem azureFlowItem = AzureFlowAPI.toItem(itemStack);
        if (azureFlowItem == null) {
            return null;
        }
        String itemId = azureFlowItem.getUuid();
        if (ignoreAmount) {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemId
                )
            );
        } else {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemId
                ),
                itemStack.getAmount()
            );
        }
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        AzureFlowItemFactory factory = AzureFlowAPI.getFactory(itemId);
        if (factory == null) {
            return null;
        }
        AzureFlowItem azureFlowItem = factory.build();
        return azureFlowItem.staticItemStack();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        AzureFlowItemFactory factory = AzureFlowAPI.getFactory(itemId);
        if (factory == null) {
            return null;
        }
        AzureFlowItem azureFlowItem = factory.build();
        if (player == null) {
            return azureFlowItem.staticItemStack();
        }
        return azureFlowItem.virtualItemStack(player.getPlayer());
    }

}
