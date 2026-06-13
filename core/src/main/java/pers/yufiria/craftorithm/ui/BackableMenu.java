package pers.yufiria.craftorithm.ui;

import crypticlib.ui.menu.Menu;
import org.jetbrains.annotations.Nullable;

public interface BackableMenu {

    @Nullable Menu parentMenu();

    void setParentMenu(@Nullable Menu parentMenu);

}
