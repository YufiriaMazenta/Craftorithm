package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.registry.impl.CookingRecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.chat.TextProcessor;
import crypticlib.config.ConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import crypticlib.util.ItemUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CookingRecipeGroupEditor extends UnlockableRecipeGroupEditor {

    private final List<CookingRecipeSource> cookingRecipeSourceList = new CopyOnWriteArrayList<>();

    public CookingRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup, Menu parent) {
        super(player, recipeGroup, parent);
        setDisplay(
            new MenuDisplay(
                title,
                new MenuLayout(
                    Arrays.asList(
                        "###A#B###",
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
            String block;
            switch (cookingRecipeSource.cookingBlock) {
                case FURNACE:
                default:
                    block = Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_FURNACE.value(player);
                    break;
                case BLAST_FURNACE:
                    block = Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_BLAST_FURNACE.value(player);
                    break;
                case SMOKER:
                    block = Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_SMOKER.value(player);
                    break;
                case CAMPFIRE:
                    block = Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_TYPE_NAME_CAMPFIRE.value(player);
                    break;
            }
            Icon icon = new CookingEditorIcon(
                cookingRecipeSource.cookingBlock.blockMaterial(),
                TextProcessor.color(block),
                i,
                cookingRecipeSource);
            elements.add(icon);
        }
        setElements(elements);
    }

    private void loadCookingRecipeInfoList() {
        cookingRecipeSourceList.clear();
        ConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
        Validate.notNull(configWrapper);
        if (configWrapper.config().isList("source")) {
            for (Map<?, ?> map : configWrapper.config().getMapList("source")) {
                CookingRecipeRegistry.CookingBlock cookingBlock = CookingRecipeRegistry.CookingBlock.valueOf(map.get("block").toString().toUpperCase());
                float exp;
                if (map.containsKey("exp")) {
                    exp = Float.parseFloat(String.valueOf(map.get("exp")));
                } else {
                    if (configWrapper.contains("exp")) {
                        exp = (float) configWrapper.config().getDouble("exp");
                    }
                    else {
                        exp = 0;
                    }
                }
                int time;
                if (map.containsKey("time")) {
                    time = (int) map.get("time");
                } else {
                    if (configWrapper.contains("time")) {
                        time = configWrapper.config().getInt("time");
                    } else {
                        time = 200;
                    }
                }
                ItemStack ingredient = ItemManager.INSTANCE.matchItem(map.get("item").toString());
                ingredient.setAmount(1);
                cookingRecipeSourceList.add(new CookingRecipeSource(time, exp, ingredient, cookingBlock));
            }
        } else {
            CookingRecipeRegistry.CookingBlock cookingBlock = CookingRecipeRegistry.CookingBlock.valueOf(configWrapper.config().getString("source.block").toUpperCase());
            float exp = (float) configWrapper.config().getDouble("exp");
            int time = configWrapper.config().getInt("time");
            ItemStack ingredient = ItemManager.INSTANCE.matchItem(Objects.requireNonNull(configWrapper.config().getString("source.item")));
            cookingRecipeSourceList.add(new CookingRecipeSource(time, exp, ingredient, cookingBlock));
        }
    }

    protected void updateCookingIcon(CookingEditorIcon cookingEditorIcon, CookingRecipeSource cookingRecipeSource) {
        List<String> lore = new ArrayList<>(Languages.MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_LORE.value(player));
        lore.replaceAll(it -> it
            .replace("<time>", cookingRecipeSource.cookingTime + "")
            .replace("<exp>", cookingRecipeSource.exp + "")
            .replace("<ingredient>", ItemUtils.getItemName(cookingRecipeSource.ingredient))
        );
        ItemUtil.setLore(cookingEditorIcon.display(), lore);
    }

    protected void reloadCookingRecipe() {
        RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeGroup().groupName(), false);
        RecipeManager.INSTANCE.addRecipeGroup(recipeGroup);
        RecipeManager.INSTANCE.loadRecipeGroup(recipeGroup);
    }

    public class CookingEditorIcon extends Icon {

        public CookingEditorIcon(@NotNull Material material, String name, int sourceIndex, CookingRecipeSource cookingRecipeSource) {
            super(material, name);
            updateCookingIcon(this, cookingRecipeSource);
            setClickAction(event -> {
                switch (event.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        new Conversation(
                            Craftorithm.instance(),
                            player,
                            new TimeInputPrompt(sourceIndex, this)
                        ).start();
                        inConversation = true;
                        player.closeInventory();
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        new Conversation(
                            Craftorithm.instance(),
                            player,
                            new ExpInputPrompt(sourceIndex, this)
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
        private ItemStack ingredient;
        private CookingRecipeRegistry.CookingBlock cookingBlock;

        public CookingRecipeSource(int cookingTime, float exp, ItemStack ingredient, CookingRecipeRegistry.CookingBlock cookingBlock) {
            this.cookingTime = cookingTime;
            this.exp = exp;
            this.ingredient = ingredient;
            this.cookingBlock = cookingBlock;
        }

        public int cookingTime() {
            return cookingTime;
        }

        public CookingRecipeSource setCookingTime(int cookingTime) {
            this.cookingTime = cookingTime;
            return this;
        }

        public float exp() {
            return exp;
        }

        public CookingRecipeSource setExp(float exp) {
            this.exp = exp;
            return this;
        }

        public ItemStack source() {
            return ingredient;
        }

        public CookingRecipeSource setIngredient(ItemStack ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public CookingRecipeRegistry.CookingBlock cookingBlock() {
            return cookingBlock;
        }

        public CookingRecipeSource setCookingBlock(CookingRecipeRegistry.CookingBlock cookingBlock) {
            this.cookingBlock = cookingBlock;
            return this;
        }

    }

    class TimeInputPrompt implements NumberPrompt {

        private final int sourceIndex;
        private final CookingEditorIcon cookingRecipeIcon;

        public TimeInputPrompt(int sourceIndex, CookingEditorIcon cookingRecipeIcon) {
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
                recipeGroup.recipeGroupConfig().saveConfig();
            } else {
                config.set("source.time", cookingTime);
                recipeGroup.recipeGroupConfig().saveConfig();
            }
            reloadCookingRecipe();
            cookingRecipeSourceList.get(sourceIndex).setCookingTime(cookingTime);
            updateCookingIcon(cookingRecipeIcon, cookingRecipeSourceList.get(sourceIndex));
            draw(openedInventory);
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
        private final CookingEditorIcon cookingRecipeIcon;

        public ExpInputPrompt(int sourceIndex, CookingEditorIcon cookingRecipeIcon) {
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
            reloadCookingRecipe();
            cookingRecipeSourceList.get(sourceIndex).setExp(exp);
            updateCookingIcon(cookingRecipeIcon, cookingRecipeSourceList.get(sourceIndex));
            draw(openedInventory);
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
