package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.menu.display.icon.IngredientIcon;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShapedDisplayMenu extends AbstractRecipeDisplayMenu {

    private final Map<Integer, IngredientIcon> ingredientIcons = new LinkedHashMap<>();

    public ShapedDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, @NotNull ShapedRecipe shapedRecipe) {
        super(player, display, shapedRecipe);
    }

    @Override
    public void updateLayout() {
        this.slotMap.clear();
        this.layoutSlotMap.clear();
        this.ingredientIcons.clear();
        MenuLayout layout = this.display.layout();

        for(int x = 0; x < layout.layout().size(); ++x) {
            String line = layout.layout().get(x);

            for(int y = 0; y < Math.min(line.length(), 9); ++y) {
                char key = line.charAt(y);
                if (layout.layoutMap().containsKey(key)) {
                    int slot = x * 9 + y;
                    if (this.layoutSlotMap.get(key) == null) {
                        this.layoutSlotMap.put(key, new ArrayList<>(Collections.singletonList(slot)));
                    } else {
                        this.layoutSlotMap.get(key).add(slot);
                    }

                    Icon icon = layout.layoutMap().get(key).get();
                    if (icon instanceof IngredientIcon ingredientIcon) {
                        ingredientIcons.put(slot, ingredientIcon);
                    }

                    this.slotMap.put(slot, icon);
                }
            }
        }

        if (ingredientIcons.size() != 9) {
            throw r
        }

    }

    @Override
    protected void draw(Inventory inventory) {
        this.slotMap.forEach((slot, icon) -> {
            if (icon != null) {
                ItemStack display = icon.display().clone();
                ItemMeta meta = display.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(BukkitTextProcessor.color(BukkitTextProcessor.placeholder(this.player, meta.getDisplayName())));
                    List<String> lore = meta.getLore();
                    if (lore != null) {
                        lore.replaceAll((source) -> {
                            return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(this.player, source));
                        });
                    }

                    meta.setLore(lore);
                    display.setItemMeta(meta);
                }

                inventory.setItem(slot, display);
            }
        });
    }

}
