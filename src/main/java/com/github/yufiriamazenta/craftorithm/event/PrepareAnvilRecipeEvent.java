package com.github.yufiriamazenta.craftorithm.event;

import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 当玩家将要合成插件提供的铁砧配方时触发
 */
public class PrepareAnvilRecipeEvent extends Event {

    private PrepareAnvilEvent anvilEvent;
    private ItemStack result;
    private AnvilRecipe recipe;
    private int costLevel;
    private final static HandlerList handlerList = new HandlerList();

    public int getCostLevel() {
        return costLevel;
    }

    public void setCostLevel(int costLevel) {
        this.costLevel = costLevel;
    }

    public PrepareAnvilRecipeEvent(PrepareAnvilEvent event, AnvilRecipe recipe) {
        super(false);
        this.anvilEvent = event;
        this.recipe = recipe;
        this.result = recipe.getResult();
        this.costLevel = recipe.getCostLevel();
    }

    public AnvilRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(AnvilRecipe recipe) {
        this.recipe = recipe;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }


    public PrepareAnvilEvent getBukkitAnvilEvent() {
        return anvilEvent;
    }

    public void setBukkitEvent(PrepareAnvilEvent anvilEvent) {
        this.anvilEvent = anvilEvent;
    }

    public static PrepareAnvilRecipeEvent instance(PrepareAnvilEvent event, AnvilRecipe recipe) {
        return new PrepareAnvilRecipeEvent(event, recipe);
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

}
