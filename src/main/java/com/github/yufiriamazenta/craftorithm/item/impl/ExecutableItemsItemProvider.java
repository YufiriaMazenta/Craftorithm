package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public enum ExecutableItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "executableitems";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        ExecutableItemsManagerInterface executableItemsManager = ExecutableItemsAPI.getExecutableItemsManager();
        Optional<ExecutableItemInterface> executableItemOpt = executableItemsManager.getExecutableItem(itemStack);
        if (executableItemOpt.isPresent()) {
            ExecutableItemInterface executableItem = executableItemOpt.get();
            String id = executableItem.getId();
            if (ignoreAmount) {
                return id;
            } else {
                return id + " " + (itemStack.getAmount() / Objects.requireNonNull(getItem(id)).getAmount());
            }
        } else {
            return null;
        }
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        return getItem(itemName, null);
    }

    @Override
    public @Nullable ItemStack getItem(String itemName, @Nullable OfflinePlayer player) {
        ExecutableItemsManagerInterface executableItemsManager = ExecutableItemsAPI.getExecutableItemsManager();
        Optional<ExecutableItemInterface> executableItemOpt = executableItemsManager.getExecutableItem(itemName);
        if (executableItemOpt.isPresent()) {
            ExecutableItemInterface executableItem = executableItemOpt.get();
            return executableItem.buildItem(1, Optional.ofNullable(player == null ? null : player.getPlayer()));
        } else {
            return null;
        }
    }
}
