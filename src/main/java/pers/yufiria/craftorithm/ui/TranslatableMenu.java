package pers.yufiria.craftorithm.ui;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class TranslatableMenu extends Menu {


    public TranslatableMenu(@NotNull Player player, @NotNull Supplier<MenuDisplay> displaySupplier) {
        super(player, displaySupplier);
    }

    public TranslatableMenu(@NotNull Player player) {
        super(player);
    }

    public TranslatableMenu(@NotNull Player player, @NotNull MenuDisplay display) {
        super(player, display);
    }

    @Override
    public String parsedMenuTitle() {
        String title = this.display.title();
        Player player = this.player();
        title = LangManager.INSTANCE.replaceLang(title, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }

}
