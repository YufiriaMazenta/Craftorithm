package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.menu.recipeBook.RecipeListConfig;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.TranslatableMenu;
import pers.yufiria.craftorithm.ui.custom.RecipeDisplayIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.*;
import java.util.function.Supplier;

public class RecipeBookListMenu extends TranslatableMenu implements BackableMenu {

    private static final char RECIPE_CHAR = 'R';
    private static final char PREV_CHAR = 'P';
    private static final char NEXT_CHAR = 'N';
    private static final char SORT_CHAR = 'S';
    private static final char BACK_CHAR = 'B';

    private Menu parentMenu;
    private final RecipeType recipeType;
    private final int page;
    private final SortMode sortMode;
    private final List<Map.Entry<NamespacedKey, Recipe>> sortedRecipes;

    public RecipeBookListMenu(@NotNull Player player, RecipeType recipeType, int page, SortMode sortMode, Menu parentMenu) {
        super(player);
        this.recipeType = recipeType;
        this.page = page;
        this.sortMode = sortMode;
        this.parentMenu = parentMenu;
        this.sortedRecipes = getSortedRecipes(recipeType, sortMode);
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

        List<Integer> rSlotIndices = new ArrayList<>();
        int slotIndex = 0;
        for (String row : layoutStrings) {
            for (char c : row.toCharArray()) {
                if (c == RECIPE_CHAR) {
                    rSlotIndices.add(slotIndex);
                }
                slotIndex++;
            }
        }
        int pageSize = rSlotIndices.size();

        int maxPage = Math.max(0, (sortedRecipes.size() - 1) / pageSize);
        int currentPage = Math.min(page, maxPage);

        Map<Character, Supplier<Icon>> iconMap = new HashMap<>();
        if (iconsConfig != null) {
            for (String key : iconsConfig.getKeys(false)) {
                if (key.length() != 1) continue;
                char ch = key.charAt(0);
                ConfigurationSection iconConfig = iconsConfig.getConfigurationSection(key);
                if (iconConfig == null) continue;

                if (ch == PREV_CHAR) {
                    iconMap.put(ch, () -> createPrevIcon(iconConfig, currentPage));
                } else if (ch == NEXT_CHAR) {
                    iconMap.put(ch, () -> createNextIcon(iconConfig, currentPage, maxPage));
                } else if (ch == SORT_CHAR) {
                    iconMap.put(ch, () -> createSortIcon(iconConfig));
                } else if (ch == BACK_CHAR) {
                    iconMap.put(ch, () -> createBackIcon(iconConfig));
                } else if (ch != RECIPE_CHAR) {
                    iconMap.put(ch, () -> parseStaticIcon(iconConfig));
                }
            }
        }

        int startIndex = currentPage * pageSize;
        int recipeIndex = 0;
        List<String> resolvedLayout = new ArrayList<>();
        for (String row : layoutStrings) {
            StringBuilder resolvedRow = new StringBuilder();
            for (char c : row.toCharArray()) {
                if (c == RECIPE_CHAR) {
                    if (recipeIndex < pageSize) {
                        int idx = startIndex + recipeIndex;
                        if (idx < sortedRecipes.size()) {
                            NamespacedKey key = sortedRecipes.get(idx).getKey();
                            final int ri = recipeIndex;
                            iconMap.put((char)(ri + 'a'), () -> new RecipeDisplayIcon(key, List.of()));
                        } else {
                            final int ri = recipeIndex;
                            iconMap.put((char)(ri + 'a'), () -> new Icon(new ItemStack(Material.AIR)));
                        }
                        resolvedRow.append((char)(recipeIndex + 'a'));
                        recipeIndex++;
                    } else {
                        resolvedRow.append(' ');
                    }
                } else {
                    resolvedRow.append(c);
                }
            }
            resolvedLayout.add(resolvedRow.toString());
        }

        String title = RecipeListConfig.TITLE.value();
        title = title.replace("<type_name>", recipeType.typeKey())
                     .replace("<page>", String.valueOf(currentPage + 1))
                     .replace("<max_page>", String.valueOf(maxPage + 1))
                     .replace("<sort_mode>", getSortModeName(sortMode));

        return new MenuDisplay(title, new MenuLayout(resolvedLayout, () -> iconMap));
    }

    private String getSortModeName(SortMode mode) {
        return switch (mode) {
            case NAME_ASC -> "A-Z";
            case NAME_DESC -> "Z-A";
            case TIME_ASC -> "旧→新";
            case TIME_DESC -> "新→旧";
        };
    }

    private Icon parseStaticIcon(ConfigurationSection config) {
        Material material = MaterialHelper.matchMaterial(config.getString("material", "minecraft:stone"));
        String name = config.getString("name", "");
        List<String> lore = config.getStringList("lore");
        IconDisplay display = new IconDisplay(Objects.requireNonNull(material))
            .setName(name)
            .setLore(lore);
        return new TranslatableIcon(display);
    }

    private IconDisplay parseIconDisplay(ConfigurationSection config) {
        Material material = MaterialHelper.matchMaterial(config.getString("material", "minecraft:stone"));
        String name = config.getString("name", "");
        List<String> lore = config.getStringList("lore");
        return new IconDisplay(Objects.requireNonNull(material))
            .setName(name)
            .setLore(lore);
    }

    private Icon createSortIcon(ConfigurationSection config) {
        Material material = MaterialHelper.matchMaterial(config.getString("material", "minecraft:hopper"));
        String name = config.getString("name", "&e排序: <sort_mode>")
            .replace("<sort_mode>", getSortModeName(sortMode));
        List<String> lore = config.getStringList("lore");
        IconDisplay display = new IconDisplay(Objects.requireNonNull(material))
            .setName(name)
            .setLore(lore);

        return new TranslatableIcon(display) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                new RecipeBookListMenu((Player) event.getWhoClicked(), recipeType, 0, sortMode.next(), parentMenu).openMenu();
                return this;
            }
        };
    }

    private Icon createPrevIcon(ConfigurationSection config, int currentPage) {
        IconDisplay display = parseIconDisplay(config);
        return new TranslatableIcon(display) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                if (currentPage > 0) {
                    new RecipeBookListMenu((Player) event.getWhoClicked(), recipeType, currentPage - 1, sortMode, parentMenu).openMenu();
                }
                return this;
            }
        };
    }

    private Icon createNextIcon(ConfigurationSection config, int currentPage, int maxPage) {
        IconDisplay display = parseIconDisplay(config);
        return new TranslatableIcon(display) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                if (currentPage < maxPage) {
                    new RecipeBookListMenu((Player) event.getWhoClicked(), recipeType, currentPage + 1, sortMode, parentMenu).openMenu();
                }
                return this;
            }
        };
    }

    private Icon createBackIcon(ConfigurationSection config) {
        Material material = MaterialHelper.matchMaterial(config.getString("material", "minecraft:barrier"));
        String name = config.getString("name", "&c返回");
        List<String> lore = config.getStringList("lore");
        IconDisplay display = new IconDisplay(Objects.requireNonNull(material))
            .setName(name)
            .setLore(lore);

        return new TranslatableIcon(display) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                if (parentMenu != null) {
                    parentMenu.openMenu();
                }
                return this;
            }
        };
    }

    @Override
    public @Nullable Menu parentMenu() {
        return parentMenu;
    }

    @Override
    public void setParentMenu(@Nullable Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

}
