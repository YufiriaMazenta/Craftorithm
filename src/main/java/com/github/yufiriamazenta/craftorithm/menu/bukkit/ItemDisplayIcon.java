package com.github.yufiriamazenta.craftorithm.menu.bukkit;

import com.github.yufiriamazenta.craftorithm.menu.api.IMenuIcon;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.function.Consumer;

public class ItemDisplayIcon implements IMenuIcon<ItemStack, InventoryClickEvent> {

    private ItemStack display;
    private final Consumer<InventoryClickEvent> clickEventConsumer;

    protected ItemDisplayIcon(ItemStack display, boolean cancel) {
        this(display, event -> { event.setCancelled(cancel); });
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
        return new ItemDisplayIcon(display, true);
    }

    public static ItemDisplayIcon icon(ItemStack display, boolean cancel) {
        return new ItemDisplayIcon(display, cancel);
    }

    public static ItemDisplayIcon icon(ItemStack display, Consumer<InventoryClickEvent> clickEventConsumer) {
        return new ItemDisplayIcon(display, clickEventConsumer);
    }

    public static ItemDisplayIcon icon(Material display, String name, Consumer<InventoryClickEvent> clickEventConsumer) {
        ItemStack item = new ItemStack(display);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(display);
        meta.setDisplayName(LangUtil.color(name));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), UUID.randomUUID().toString(), 1000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return new ItemDisplayIcon(item, clickEventConsumer);
    }

    public static ItemDisplayIcon icon(Material display, String name) {
        ItemStack item = new ItemStack(display);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(display);
        meta.setDisplayName(LangUtil.color(name));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), UUID.randomUUID().toString(), 1000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return new ItemDisplayIcon(item, true);
    }

    public static ItemDisplayIcon icon(Material display, String name, boolean cancel) {
        ItemStack item = new ItemStack(display);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(display);
        meta.setDisplayName(LangUtil.color(name));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), UUID.randomUUID().toString(), 1000, AttributeModifier.Operation.ADD_NUMBER));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return new ItemDisplayIcon(item, cancel);
    }

}
