package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import crypticlib.listener.BukkitListener;
import crypticlib.util.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

@BukkitListener
public class AnvilHandler implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack base = event.getInventory().getFirstItem();
        ItemStack addition = event.getInventory().getSecondItem();
        if (ItemUtil.isAir(base) || ItemUtil.isAir(addition))
            return;
        AnvilRecipe anvilRecipe = RecipeManager.matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;
        //todo 从RecipeManager获取配方
    }

}
