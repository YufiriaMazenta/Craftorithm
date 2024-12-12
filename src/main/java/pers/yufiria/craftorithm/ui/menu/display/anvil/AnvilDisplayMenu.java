package pers.yufiria.craftorithm.ui.menu.display.anvil;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.extra.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;
import pers.yufiria.craftorithm.ui.menu.display.anvil.icon.AnvilAdditionIcon;
import pers.yufiria.craftorithm.ui.menu.display.anvil.icon.AnvilBaseIcon;
import pers.yufiria.craftorithm.ui.menu.display.anvil.icon.AnvilResultIcon;

import java.util.List;

public class AnvilDisplayMenu extends Menu {

    private final AnvilRecipe anvilRecipe;

    public AnvilDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, AnvilRecipe anvilRecipe) {
        super(player, display);
        this.anvilRecipe = anvilRecipe;
    }

    @Override
    public String formattedTitle() {
        String originTitle = this.display.title();
        String title = LangManager.INSTANCE.replaceLang(originTitle, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(this.player, replaceCostLevel(title)));
    }

    @Override
    public void preProcessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        switch (icon) {
            case AnvilBaseIcon anvilBaseIcon -> {
                anvilBaseIcon.setDisplayItem(anvilRecipe.base().getItemStack());
            }
            case AnvilAdditionIcon anvilAdditionIcon -> {
                anvilAdditionIcon.setDisplayItem(anvilRecipe.addition().getItemStack());
            }
            case AnvilResultIcon anvilResultIcon -> {
                anvilResultIcon.setDisplayItem(anvilRecipe.getResult());
            }
            default -> {}
        }
    }

    @Override
    protected void draw(Inventory inventory) {
        this.beforeDraw();
        this.slotMap.forEach((slot, icon) -> {
            if (icon != null) {
                this.preProcessIconWhenDraw(slot, icon);
                if (!(icon instanceof ItemDisplayIcon)) {
                    ItemStack display = icon.display();
                    ItemMeta meta = display.getItemMeta();
                    if (meta != null) {
                        if (meta.hasDisplayName()) {
                            meta.setDisplayName(
                                BukkitTextProcessor.color(
                                    BukkitTextProcessor.placeholder(
                                        this.player,
                                        replaceCostLevel(meta.getDisplayName())
                                    )
                                )
                            );
                        }
                        List<String> lore = meta.getLore();
                        if (lore != null) {
                            lore.replaceAll(it -> BukkitTextProcessor.color(
                                BukkitTextProcessor.placeholder(
                                    this.player,
                                    replaceCostLevel(it)
                                )
                            ));
                        }

                        meta.setLore(lore);
                        display.setItemMeta(meta);
                        inventory.setItem(slot, display);
                    }
                } else {
                    inventory.setItem(slot, icon.display());
                }
            }
        });
        this.onDrawCompleted();
    }

    private String replaceCostLevel(String originText) {
        return originText.replace("<level>", anvilRecipe.costLevel() + "");
    }

}
