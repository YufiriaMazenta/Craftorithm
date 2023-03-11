package com.github.yufiriamazenta.craftorithm.menu.bukkit;

import com.github.yufiriamazenta.craftorithm.menu.api.IMenuIcon;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ItemDisplayIcon implements IMenuIcon<ItemStack, InventoryClickEvent> {

    private ItemStack display;
    private final Consumer<InventoryClickEvent> clickEventConsumer;

    protected ItemDisplayIcon(ItemStack display) {
        this(display, event -> event.setCancelled(true));
    }

    protected ItemDisplayIcon(ItemStack display, Consumer<InventoryClickEvent> clickEventConsumer) {
        this.display = display;
        this.clickEventConsumer = clickEventConsumer;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        clickEventConsumer.accept(event);
    }

    @Override
    public ItemStack getDisplay() {
        return this.display;
    }

    @Override
    public void setDisplay(ItemStack display) {
        this.display = display;
    }

    public static ItemDisplayIcon icon(ItemStack display) {
        return new ItemDisplayIcon(display);
    }

    public static ItemDisplayIcon icon(ItemStack display, Consumer<InventoryClickEvent> clickEventConsumer) {
        return new ItemDisplayIcon(display, clickEventConsumer);
    }

}
