package pers.yufiria.craftorithm.util;

import crypticlib.script.ScriptValue;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;

public class ItemUtils {

    public static ScriptValue resolveItemId(@Nullable ItemStack item) {
        if (item == null) return ScriptValue.nil();
        return ItemManager.INSTANCE.matchItemId(item, false)
            .map(id -> ScriptValue.of(id.itemId().toString()))
            .orElseGet(() -> ScriptValue.of(item.getType().getKey().toString()));
    }

    public static ScriptValue resolveItemAmount(@Nullable ItemStack item) {
        if (item == null) return ScriptValue.of(0);
        return ScriptValue.of(item.getAmount());
    }

}
