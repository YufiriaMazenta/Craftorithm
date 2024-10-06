package pers.yufiria.craftorithm.item.providers;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;

import java.util.Optional;

public enum ExecutableItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "executableitems";
    }

    @Override
    public String matchItemId(ItemStack itemStack) {
        ExecutableItemsManagerInterface executableItemsManager = ExecutableItemsAPI.getExecutableItemsManager();
        Optional<ExecutableItemInterface> executableItemOpt = executableItemsManager.getExecutableItem(itemStack);
        if (executableItemOpt.isPresent()) {
            ExecutableItemInterface executableItem = executableItemOpt.get();
            return executableItem.getId();
        } else {
            return null;
        }
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        return matchItem(itemId, null);
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        ExecutableItemsManagerInterface executableItemsManager = ExecutableItemsAPI.getExecutableItemsManager();
        Optional<ExecutableItemInterface> executableItemOpt = executableItemsManager.getExecutableItem(itemId);
        if (executableItemOpt.isPresent()) {
            ExecutableItemInterface executableItem = executableItemOpt.get();
            return executableItem.buildItem(1, Optional.ofNullable(player == null ? null : player.getPlayer()));
        } else {
            return null;
        }
    }
}
