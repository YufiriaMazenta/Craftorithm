package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.registry.impl.CookingRecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.chat.TextProcessor;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CookingRecipeGroupEditor extends UnlockableRecipeGroupEditor {

    private final List<CookingRecipeSource> cookingRecipeSourceList = new ArrayList<>();

    public CookingRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup, RecipeGroupListMenu parent) {
        super(player, recipeGroup, parent);
        setDisplay(
            new MenuDisplay(
                title,
                new MenuLayout(
                    Arrays.asList(
                        "###A#B##Z",
                        "X%%%%%%%Y",
                        "#########"
                    ),
                    () -> {
                        Map<Character, Icon> layoutMap = new HashMap<>();
                        layoutMap.put('A', getSortIdEditIcon(3));
                        layoutMap.put('B', getUnlockIcon());
                        layoutMap.put('#', getFrameIcon());
                        layoutMap.put('X', getPreviousIcon());
                        layoutMap.put('Y', getNextIcon());
                        layoutMap.put('Z', getRemoveIcon());
                        return layoutMap;
                    }
                )
            )
        );
        loadCookingRecipeInfoList();
        loadElements();
    }

    private void loadElements() {
        List<Icon> elements = new ArrayList<>();
        for (int i = 0; i < cookingRecipeSourceList.size(); i++) {
            CookingRecipeSource cookingRecipeSource = cookingRecipeSourceList.get(i);
            Icon icon = new CookingSourceIcon(
                i,
                cookingRecipeSource);
            elements.add(icon);
        }
        setElements(elements);
    }

    private void loadCookingRecipeInfoList() {
        cookingRecipeSourceList.clear();
        YamlConfiguration config = recipeGroup.recipeGroupConfig().config();
        if (config.isList("source")) {
            for (Map<?, ?> map : config.getMapList("source")) {
                CookingRecipeRegistry.CookingBlock cookingBlock = CookingRecipeRegistry.CookingBlock.valueOf(map.get("block").toString().toUpperCase());
                float exp;
                if (map.containsKey("exp")) {
                    exp = Float.parseFloat(String.valueOf(map.get("exp")));
                } else {
                    exp = config.contains("exp") ? (float) config.getDouble("exp") : 0;
                }
                int time;
                if (map.containsKey("time")) {
                    time = (int) map.get("time");
                } else {
                    time = config.contains("time") ? config.getInt("time") : 200;
                }
                ItemStack ingredient = ItemManager.INSTANCE.matchItem(map.get("item").toString());
                ingredient.setAmount(1);
                cookingRecipeSourceList.add(new CookingRecipeSource(time, exp, ingredient, cookingBlock));
            }
        } else {
            CookingRecipeRegistry.CookingBlock cookingBlock = CookingRecipeRegistry.CookingBlock.valueOf(config.getString("source.block", "furnace").toUpperCase());
            float exp = (float) config.getDouble("source.exp", config.getInt("exp", 0));
            int time = config.getInt("source.time", config.getInt("time", 200));
            ItemStack ingredient = ItemManager.INSTANCE.matchItem(Objects.requireNonNull(config.getString("source.item")));
            cookingRecipeSourceList.add(new CookingRecipeSource(time, exp, ingredient, cookingBlock));
        }
    }

    protected void updateCookingIcon(CookingSourceIcon cookingSourceIcon, CookingRecipeSource cookingRecipeSource) {
        List<String> lore = new ArrayList<>(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_LORE.value(player));
        String ingredientName = ItemUtils.matchItemNameOrCreate(cookingRecipeSource.ingredient, false);
        lore.replaceAll(it -> it
            .replace("<time>", cookingRecipeSource.cookingTime + "")
            .replace("<exp>", cookingRecipeSource.exp + "")
            .replace("<ingredient>", Objects.requireNonNull(ingredientName))
        );
        ItemUtil.setLore(cookingSourceIcon.display(), lore);
    }

    public class CookingSourceIcon extends Icon {

        public CookingSourceIcon(int sourceIndex, CookingRecipeSource cookingRecipeSource) {
            super(cookingRecipeSource.cookingBlock.blockMaterial());
            updateCookingIcon(this, cookingRecipeSource);
            switch (cookingRecipeSource.cookingBlock) {
                case FURNACE:
                default:
                    setName(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_FURNACE.value(player));
                    break;
                case BLAST_FURNACE:
                    setName(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_BLAST_FURNACE.value(player));
                    break;
                case SMOKER:
                    setName(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_SMOKER.value(player));
                    break;
                case CAMPFIRE:
                    setName(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_CAMPFIRE.value(player));
                    break;
            }

            setClickAction(event -> {
                switch (event.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        new Conversation(
                            Craftorithm.instance(),
                            player,
                            new TimeInputPrompt(sourceIndex, this),
                            data -> openMenu()
                        ).start();
                        inConversation = true;
                        player.closeInventory();
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        new Conversation(
                            Craftorithm.instance(),
                            player,
                            new ExpInputPrompt(sourceIndex, this),
                            data -> openMenu()
                        ).start();
                        inConversation = true;
                        player.closeInventory();
                        break;
                    default:
                        event.setCancelled(true);
                        break;
                }
            });
        }

    }

    public static class CookingRecipeSource {
        private int cookingTime;
        private float exp;
        private final ItemStack ingredient;
        private final CookingRecipeRegistry.CookingBlock cookingBlock;

        public CookingRecipeSource(int cookingTime, float exp, ItemStack ingredient, CookingRecipeRegistry.CookingBlock cookingBlock) {
            this.cookingTime = cookingTime;
            this.exp = exp;
            this.ingredient = ingredient;
            this.cookingBlock = cookingBlock;
        }

    }

    class TimeInputPrompt implements NumberPrompt {

        private final int sourceIndex;
        private final CookingSourceIcon cookingRecipeIcon;

        public TimeInputPrompt(int sourceIndex, CookingSourceIcon cookingRecipeIcon) {
            this.sourceIndex = sourceIndex;
            this.cookingRecipeIcon = cookingRecipeIcon;
        }

        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            int cookingTime = number.intValue();
            YamlConfiguration config = recipeGroup.recipeGroupConfig().config();
            if (config.isList("source")) {
                List<Map<?, ?>> sourceList = config.getMapList("source");
                Map<String, Object> source = (Map<String, Object>) sourceList.get(sourceIndex);
                source.put("time", cookingTime);
                sourceList.set(sourceIndex, source);
                config.set("source", sourceList);
            } else {
                config.set("source.time", cookingTime);
            }
            recipeGroup.recipeGroupConfig().saveConfig();
            reloadRecipeGroup();
            cookingRecipeSourceList.get(sourceIndex).cookingTime = cookingTime;
            updateCookingIcon(cookingRecipeIcon, cookingRecipeSourceList.get(sourceIndex));
            draw(inventoryCache);
            openMenu();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return TextProcessor.toComponent(TextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_INPUT_COOKING_TIME_HINT.value(player)));
        }

    }

    class ExpInputPrompt implements NumberPrompt {

        private final int sourceIndex;
        private final CookingSourceIcon cookingRecipeIcon;

        public ExpInputPrompt(int sourceIndex, CookingSourceIcon cookingRecipeIcon) {
            this.sourceIndex = sourceIndex;
            this.cookingRecipeIcon = cookingRecipeIcon;
        }

        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            float exp = number.floatValue();
            YamlConfiguration config = recipeGroup.recipeGroupConfig().config();
            if (config.isList("source")) {
                List<Map<?, ?>> sourceList = config.getMapList("source");
                Map<String, Object> source = (Map<String, Object>) sourceList.get(sourceIndex);
                source.put("exp", exp);
                sourceList.set(sourceIndex, source);
                config.set("source", sourceList);
                recipeGroup.recipeGroupConfig().saveConfig();
            } else {
                config.set("source.exp", exp);
                recipeGroup.recipeGroupConfig().saveConfig();
            }
            reloadRecipeGroup();
            cookingRecipeSourceList.get(sourceIndex).exp = exp;
            updateCookingIcon(cookingRecipeIcon, cookingRecipeSourceList.get(sourceIndex));
            draw(inventoryCache);
            openMenu();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return TextProcessor.toComponent(TextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_INPUT_COOKING_EXP_HINT.value(player)));
        }
    }

}
