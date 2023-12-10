package com.github.yufiriamazenta.craftorithm.menu.api.bukkit;

public interface IChildBukkitMenu {
    BukkitMenuHandler parentMenu();

    void setParentMenu(BukkitMenuHandler parentMenu);
}
