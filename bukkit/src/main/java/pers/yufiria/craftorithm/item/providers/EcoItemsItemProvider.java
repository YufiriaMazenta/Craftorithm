package pers.yufiria.craftorithm.item.providers;

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import com.willfp.ecoitems.items.ItemUtilsKt;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;

import java.util.Objects;

public enum EcoItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "ecoitems";
    }

    @Override
    public String matchItemId(ItemStack itemStack) {
        EcoItem ecoItem = ItemUtilsKt.getEcoItem(itemStack);
        if (ecoItem == null) {
            return null;
        }
        return ecoItem.getID();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        EcoItem ecoItem = EcoItems.INSTANCE.getByID(itemId);
        if (ecoItem == null) {
            return null;
        }
        return ecoItem.getItemStack();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        return matchItem(itemId);
    }

}
