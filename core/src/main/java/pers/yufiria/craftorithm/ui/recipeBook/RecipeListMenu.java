package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.menu.Multipage;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.recipeBook.RecipeListConfig;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.icon.RecipeDisplayIcon;

import java.util.*;
import java.util.function.Supplier;

public class RecipeListMenu extends Menu implements BackableMenu, Multipage {

    private Menu parentMenu;
    private final RecipeType recipeType;
    private int page;
    private SortMode sortMode;
    private final List<Map.Entry<NamespacedKey, Recipe>> sortedRecipes;
    private final List<NamespacedKey> currentPageRecipes;
    private int recipeSlotIndex;
    private int maxPage;

    public RecipeListMenu(@NotNull Player player, RecipeType recipeType, SortMode sortMode) {
        this(player, recipeType, sortMode, null);
    }

    public RecipeListMenu(@NotNull Player player, RecipeType recipeType, SortMode sortMode, Menu parentMenu) {
        super(player);
        this.recipeType = recipeType;
        this.page = 0;
        this.sortMode = sortMode;
        this.parentMenu = parentMenu;
        this.sortedRecipes = getSortedRecipes(recipeType, sortMode);
        this.currentPageRecipes = new ArrayList<>();
        this.recipeSlotIndex = 0;
        this.display = buildDisplay();
    }

    private List<Map.Entry<NamespacedKey, Recipe>> getSortedRecipes(RecipeType type, SortMode mode) {
        List<Map.Entry<NamespacedKey, Recipe>> recipes = new ArrayList<>(RecipeManager.INSTANCE.getRecipesByType(type));

        switch (mode) {
            case NAME_ASC -> recipes.sort(Comparator.comparing(e -> e.getKey().getKey()));
            case NAME_DESC -> recipes.sort(Comparator.comparing((Map.Entry<NamespacedKey, Recipe> e) -> e.getKey().getKey()).reversed());
            case TIME_ASC -> recipes.sort(Comparator.comparingLong(e -> {
                Long time = RecipeManager.INSTANCE.getRecipeCreateTime(e.getKey());
                return time != null ? time : 0L;
            }));
            case TIME_DESC -> recipes.sort(Comparator.comparingLong((Map.Entry<NamespacedKey, Recipe> e) -> {
                Long time = RecipeManager.INSTANCE.getRecipeCreateTime(e.getKey());
                return time != null ? time : 0L;
            }).reversed());
        }

        return recipes;
    }

    private MenuDisplay buildDisplay() {
        List<String> layoutStrings = RecipeListConfig.LAYOUT.value();
        ConfigurationSection iconsConfig = RecipeListConfig.ICONS.value();

        Map<Character, Supplier<Icon>> iconMap = new HashMap<>();
        recipeSlotIndex = 0;

        for (String key : iconsConfig.getKeys(false)) {
            if (key.length() != 1) continue;
            char ch = key.charAt(0);
            ConfigurationSection iconConfig = iconsConfig.getConfigurationSection(key);
            if (iconConfig == null) continue;

            Supplier<Icon> iconSupplier = RecipeBookListIconParser.INSTANCE.parse(iconConfig);
            iconMap.put(ch, iconSupplier);
        }
        return new MenuDisplay(RecipeListConfig.TITLE.value(), new MenuLayout(layoutStrings, () -> iconMap));
    }

    @Override
    public void onLayoutUpdated() {
        int pageSize = 0;
        for (Icon icon : slotMap.values()) {
            if (icon instanceof RecipeDisplayIcon) {
                pageSize++;
            }
        }
        if (pageSize == 0) {
            pageSize = 1;
        }

        maxPage = Math.max(0, (sortedRecipes.size() - 1) / pageSize);
        if (page > maxPage) {
            page = maxPage;
        }

        updateCurrentPageRecipes(pageSize);
    }

    private void updateCurrentPageRecipes() {
        int pageSize = 0;
        for (Icon icon : slotMap.values()) {
            if (icon instanceof RecipeDisplayIcon) {
                pageSize++;
            }
        }
        if (pageSize == 0) {
            pageSize = 1;
        }
        updateCurrentPageRecipes(pageSize);
    }

    private void updateCurrentPageRecipes(int pageSize) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, sortedRecipes.size());
        currentPageRecipes.clear();
        for (int i = startIndex; i < endIndex; i++) {
            currentPageRecipes.add(sortedRecipes.get(i).getKey());
        }
        recipeSlotIndex = 0;
    }

    @Override
    public String parsedMenuTitle() {
        String title = this.display.title();
        Player player = player();
        title = LangManager.INSTANCE.replaceLang(title, player);
        title = title.replace("<page>", String.valueOf(page + 1))
            .replace("<max_page>", String.valueOf(maxPage + 1))
            .replace("<type_name>", recipeType.typeKey())
            .replace("<sort_mode>", getSortModeName(sortMode));
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }

    @Override
    public void preprocessIconWhenDraw(Integer slot, @NotNull Icon icon) {
        if (icon instanceof RecipeDisplayIcon recipeDisplayIcon) {
            int index =  recipeSlotIndex++;
            if (index < currentPageRecipes.size()) {
                recipeDisplayIcon.setRecipeKey(currentPageRecipes.get(index));
            } else {
                recipeDisplayIcon.setRecipeKey(null);
            }
        } else if (icon instanceof SortIcon sortIcon) {
            sortIcon.setSortMode(sortMode);
        }
    }

    @Override
    public void onDrawCompleted(Inventory inventory) {
        recipeSlotIndex = 0;
    }

    private String getSortModeName(SortMode mode) {
        return switch (mode) {
            case NAME_ASC -> Languages.MENU_RECIPE_BOOK_SORT_MODE_NAME_ASC.value(player());
            case NAME_DESC -> Languages.MENU_RECIPE_BOOK_SORT_MODE_NAME_DESC.value(player());
            case TIME_ASC -> Languages.MENU_RECIPE_BOOK_SORT_MODE_TIME_ASC.value(player());
            case TIME_DESC -> Languages.MENU_RECIPE_BOOK_SORT_MODE_TIME_DESC.value(player());
        };
    }

    public List<NamespacedKey> getCurrentPageRecipes() {
        return currentPageRecipes;
    }

    public int getNextRecipeSlotIndex() {
        return recipeSlotIndex++;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public SortMode getSortMode() {
        return sortMode;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    @Override
    public @Nullable Menu parentMenu() {
        return parentMenu;
    }

    @Override
    public void setParentMenu(@Nullable Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    @Override
    public void nextPage() {
        if (page < maxPage()) {
            page = Math.min(maxPage, page + 1);
            updateCurrentPageRecipes();
            this.updateMenu();
        }
    }

    @Override
    public void previousPage() {
        if (page > 0) {
            page = Math.max(0, page - 1);
            updateCurrentPageRecipes();
            this.updateMenu();
        }
    }

    @Override
    public Integer page() {
        return page;
    }

    @Override
    public void page(int i) {
        int maxPage = maxPage();
        if (i >= 0 && i <= maxPage) {
            updateMenu();
        }
    }

    @Override
    public Integer maxPage() {
        return maxPage;
    }

    public SortMode sortMode() {
        return sortMode;
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
        this.sortedRecipes.clear();
        this.sortedRecipes.addAll(getSortedRecipes(recipeType, sortMode));
        this.page = 0;
        updateCurrentPageRecipes();
    }

}
