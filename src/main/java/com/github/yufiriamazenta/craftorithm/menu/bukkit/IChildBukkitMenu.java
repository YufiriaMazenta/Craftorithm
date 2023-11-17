package com.github.yufiriamazenta.craftorithm.menu.bukkit;

import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuHandler;

public interface IChildBukkitMenu {
    BukkitMenuHandler parentMenu();

    void setParentMenu(BukkitMenuHandler parentMenu);
}
