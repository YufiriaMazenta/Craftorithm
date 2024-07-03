package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class CopyNbtProcessor {

    private Map<String, Function<ItemMeta, ItemMeta>> processors = new HashMap<>();

    public CopyNbtProcessor() {

    }

    public ItemMeta process(ItemMeta meta) {

    }

}
