package pers.yufiria.craftorithm.ui.creator.vanillaShaped;

import crypticlib.config.BukkitConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.creator.VanillaShapedCreatorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.Supplier;

public final class VanillaShapedCreator extends RecipeCreator {

    public VanillaShapedCreator(@NotNull Player player, @NotNull String recipeName) {
        super(player, recipeName);
        this.display = new MenuDisplay(
            VanillaShapedCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "#123#***#",
                "#456A* *#",
                "#789#***#",
                "####C####"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('*', this::getResultFrameIcon);
                layoutMap.put('A', this::getConfirmIcon);
                layoutMap.put('C', RecipeBookCategoryIcon::new);
                return layoutMap;
            })
        );
    }

    private void removeEmptyColumn(List<String> shape) {
        boolean[] empty = new boolean[3];
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            empty[i] = shape.stream().allMatch(s -> s.charAt(finalI) == ' ');
        }
        if (empty[0]) {
            if (empty[1]) {
                if (!empty[2]) {
                    shape.replaceAll(s -> s.substring(2));
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(1, 2));
                } else {
                    shape.replaceAll(s -> s.substring(1));
                }
            }
        } else {
            if (empty[1]) {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, 1));
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, 2));
                }
            }
        }
    }

    @Override
    protected Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapedCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapedCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(VanillaShapedCreatorConfig.CONFIRM_ICON.value());
        return new TranslatableIcon(iconDisplay) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                Map<Integer, ItemStack> storedItems = Objects.requireNonNull(creator).storedItems();
                ItemStack result = storedItems.get(24);
                if (ItemHelper.isAir(result)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                int[] sourceSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                List<NamespacedItemId> ingredientsList = new ArrayList<>();
                for (int slot : sourceSlots) {
                    ItemStack source = storedItems.get(slot);
                    if (ItemHelper.isAir(source)) {
                        ingredientsList.add(null);
                        continue;
                    }
                    NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemIdOrCreate(source, true);
                    ingredientsList.add(Objects.requireNonNull(itemId).itemId());
                }
                boolean allNull = true;
                for (NamespacedItemId ingredient : ingredientsList) {
                    if (ingredient != null) {
                        allNull = false;
                        break;
                    }
                }
                if (allNull) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }
                BukkitConfigWrapper recipeConfig = createRecipeConfig();
                Map<NamespacedItemId, Character> itemRepeatMap = new LinkedHashMap<>();
                List<String> shape = new ArrayList<>();
                Map<Character, NamespacedItemId> ingredientIdMap = new LinkedHashMap<>();
                char[] tmp = "         ".toCharArray(); //9个空格
                char c = 'a';
                for(int i = 0; i < ingredientsList.size(); i++){
                    NamespacedItemId ingredient = ingredientsList.get(i);
                    if (ingredient == null) {
                        continue;
                    }
                    if (!itemRepeatMap.containsKey(ingredient)){
                        itemRepeatMap.put(ingredient ,c);
                        c++;
                    }
                    tmp[i] = itemRepeatMap.get(ingredient);
                }
                for (int i = 0; i < 9; i += 3) {
                    shape.add(new String(tmp,i,3));
                }
                itemRepeatMap.forEach((k,v) -> ingredientIdMap.put(v,k));
                shape.removeIf(s -> s.trim().isEmpty());
                removeEmptyColumn(shape);
                //TODO Recipe book category
                CraftingBookCategory category = ((RecipeBookCategoryIcon) Objects
                    .requireNonNull(VanillaShapedCreator.this.getIcon(40))
                ).category();

                recipeConfig.set("recipe_book_category", category.name().toLowerCase());
                recipeConfig.set("type", SimpleRecipeTypes.VANILLA_SHAPED.typeKey());
                recipeConfig.set("shape", shape);
                recipeConfig.set("ingredients", ingredientIdMap);//不知道会不会自动toString,如果不会的话还需要处理
                recipeConfig.set("result", Objects.requireNonNull(resultId).toString());
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();
                //TODO 加载配方
                event.getWhoClicked().closeInventory();
                //TODO 消息
                return this;
            }
        };
    }

}
