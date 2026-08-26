package pers.yufiria.craftorithm.hook.item;

import emaki.jiuwu.craft.corelib.api.contract.EmakiResult;
import emaki.jiuwu.craft.item.api.EmakiItemApi;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum EmakiItemItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "emaikiitem";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        EmakiResult<String> identify = EmakiItemApi.catalog().identify(itemStack);
        if (!identify.isSuccess()) {
            return null;
        }
        String id;
        switch (identify) {
            case EmakiResult.Success<String>(String definitionId) -> id = definitionId;
            case EmakiResult.Partial<String>(String definitionId, String reasonKey) -> id = definitionId;
            default -> {
                return null;
            }
        }
        return new NamespacedItemIdStack(
            NamespacedItemId.of(
                namespace(),
                id
            ),
            ignoreAmount ? 1 : itemStack.getAmount()
        );
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        EmakiResult<ItemStack> result = EmakiItemApi.operations().create(itemId, 1);
        switch (result) {
            case EmakiResult.Success<ItemStack>(ItemStack stack) -> {
                return stack;
            }
            case EmakiResult.Partial<ItemStack>(ItemStack stack, String ignored) -> {
                return stack;
            }
            default -> {
                return null;
            }
        }
    }
}
