package pers.yufiria.craftorithm.ui.creator.tag;

import crypticlib.CrypticLibBukkit;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Multipage;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.creator.TagSelectConfig;
import pers.yufiria.craftorithm.ui.TranslatableMenu;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.creator.IngredientIcon;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class TagSelectMenu extends TranslatableMenu implements Multipage {

    private Tag<Material> selectedTag;
    private final RecipeCreator parentMenu;
    private final IngredientIcon ingredientIcon;
    private int page = 0;
    private final List<Tag<Material>> TAGS = Tags.INSTANCE.vanillaTags();
    private final int maxPage = (TAGS.size() + 44) / 45;

    public TagSelectMenu(@NotNull RecipeCreator parentMenu, @NotNull IngredientIcon ingredientIcon) {
        super(Objects.requireNonNull(parentMenu.player()));
        this.parentMenu = parentMenu;
        this.ingredientIcon = ingredientIcon;
        this.display = new MenuDisplay(
            TagSelectConfig.TITLE.value(),
            new MenuLayout(
                List.of(
                    "---------",
                    "---------",
                    "---------",
                    "---------",
                    "---------",
                    "##<###>##"
                ),
                () -> {
                    Map<Character, Supplier<Icon>> iconMap = new HashMap<>();
                    iconMap.put('<', () -> new TranslatableIcon(
                        CreatorIconParser.INSTANCE.parseIconDisplay(
                            TagSelectConfig.PREVIOUS_ICON.value()
                        )
                    ) {
                        @Override
                        public Icon onClick(InventoryClickEvent event) {
                            previousPage();
                            return this;
                        }
                    });
                    iconMap.put('>', () -> new TranslatableIcon(
                        CreatorIconParser.INSTANCE.parseIconDisplay(
                            TagSelectConfig.NEXT_ICON.value()
                        )
                    ) {
                        @Override
                        public Icon onClick(InventoryClickEvent event) {
                            nextPage();
                            return this;
                        }
                    });
                    iconMap.put('#', CreatorIconParser.INSTANCE.parse(TagSelectConfig.FRAME_ICON.value()));
                    iconMap.put('-', () -> {
                        //TODO
                        return null;
                    });
                    return iconMap;
                }
            )
        );
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (selectedTag != null) {
            ingredientIcon.setTag(selectedTag);
            ingredientIcon.setUseTag(true);
        }
        CrypticLibBukkit.scheduler().sync(parentMenu::openMenu);
    }

    @Override
    public void nextPage() {
        page = Math.min(page + 1, page - 1);
        updateMenu();
    }

    @Override
    public void previousPage() {
        page = Math.max(page - 1, 0);
        updateMenu();
    }

    @Override
    public Integer page() {
        return page ;
    }

    @Override
    public void page(int i) {
        if (i == this.page) {
            return;
        }
        this.page = Math.max(0, Math.min(i, page - 1));
        updateMenu();
    }

    @Override
    public Integer maxPage() {
        return 0;
    }
}
