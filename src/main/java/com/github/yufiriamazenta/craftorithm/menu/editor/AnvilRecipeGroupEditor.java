package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class AnvilRecipeGroupEditor extends RecipeGroupEditor {

    private final List<AnvilRecipeSource> anvilRecipeSourceList = new ArrayList<>();

    public AnvilRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup, RecipeGroupListMenu parent) {
        super(player, recipeGroup, parent);
        setDisplay(
            new MenuDisplay(
                title,
                new MenuLayout(
                    Arrays.asList(
                        "####A###Z",
                        "X%%%%%%%Y",
                        "#########"
                    ),
                    () -> {
                        Map<Character, Supplier<Icon>> iconMap = new HashMap<>();
                        iconMap.put('#', this::getFrameIcon);
                        iconMap.put('A', () -> getSortIdEditIcon(4));
                        iconMap.put('X', this::getPreviousIcon);
                        iconMap.put('Y', this::getNextIcon);
                        iconMap.put('Z', this::getRemoveIcon);
                        //TODO condition和action
                        return iconMap;
                    }
                )
            )
        );
        loadAnvilRecipeSourceList();
        loadElements();
    }

    private void loadAnvilRecipeSourceList() {
        anvilRecipeSourceList.clear();
        YamlConfiguration config = recipeGroup.recipeGroupConfig().config();
        if (config.isList("source")) {
            List<Map<?, ?>> sourceList = config.getMapList("source");
            for (Map<?, ?> map : sourceList) {
                ItemStack base = ItemManager.INSTANCE.matchItem(map.get("base").toString());
                ItemStack addition = ItemManager.INSTANCE.matchItem(map.get("addition").toString());
                int costLevel = map.containsKey("cost_level") ? (int) map.get("cost_level") : 0;
                boolean copyNbt = !map.containsKey("copy_nbt") || Boolean.parseBoolean(map.get("copy_nbt").toString());
                anvilRecipeSourceList.add(new AnvilRecipeSource(copyNbt, costLevel, base, addition));
            }
        } else {
            int costLevel = config.getInt("source.cost_level", 0);
            boolean copyNbt = config.getBoolean("source.time", true);
            ItemStack base = ItemManager.INSTANCE.matchItem(config.getString("source.base", "STONE"));
            ItemStack addition = ItemManager.INSTANCE.matchItem(config.getString("source.addition", "STONE"));
            anvilRecipeSourceList.add(new AnvilRecipeSource(copyNbt, costLevel, base, addition));
        }
    }

    private void loadElements() {
        List<Icon> elements = new ArrayList<>();
        for (int i = 0; i < anvilRecipeSourceList.size(); i++) {
            AnvilRecipeSource source = anvilRecipeSourceList.get(i);
            Icon icon = new AnvilSourceIcon(i, source);
            elements.add(icon);
        }
        setElements(elements);
    }


    class AnvilSourceIcon extends Icon {

        private final List<String> originLore;

        private final AnvilRecipeSource anvilRecipeSource;

        public AnvilSourceIcon(int sourceIndex, @NotNull AnvilRecipeSource anvilRecipeSource) {
            super(anvilRecipeSource.base);
            this.anvilRecipeSource = anvilRecipeSource;
            ItemMeta itemMeta = this.display().getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            List<String> append = Languages.MENU_RECIPE_EDITOR_ICON_ANVIL_ELEMENT_LORE.value(player);
            lore.addAll(append);
            originLore = new ArrayList<>(lore);
            refreshIconLore();
            setClickAction(event -> {
               switch (event.getClick()) {
                   case LEFT:
                   case SHIFT_LEFT:
                       new Conversation(
                           Craftorithm.instance(),
                           player,
                           new CostLevelInputPrompt(sourceIndex, this),
                           data -> openMenu()
                       ).start();
                       inConversation = true;
                       player.closeInventory();
                       break;
                   case RIGHT:
                   case SHIFT_RIGHT:
                       BukkitConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
                       YamlConfiguration config = configWrapper.config();
                       anvilRecipeSource.copyNbt = !anvilRecipeSource.copyNbt;
                       if (config.isList("source")) {
                           List<Map<?, ?>> sourceList = config.getMapList("source");
                           Map<String, Object> source = (Map<String, Object>) sourceList.get(sourceIndex);
                           source.put("copy_nbt", anvilRecipeSource.copyNbt);
                           sourceList.set(sourceIndex, source);
                           config.set("source", sourceList);
                       } else {
                           config.set("source.copy_nbt", anvilRecipeSource.copyNbt);
                       }
                       configWrapper.saveConfig();
                       reloadRecipeGroup();
                       refreshIconLore();
                       draw(inventoryCache);
                       break;
               }
            });
        }

        public void refreshIconLore() {
            List<String> lore = new ArrayList<>(originLore);
            lore.replaceAll(it -> BukkitTextProcessor.color(it)
                .replace("<level>", String.valueOf(anvilRecipeSource.costLevel))
                .replace("<enable>", String.valueOf(anvilRecipeSource.copyNbt))
            );
            setLore(lore);
        }

    }

    static class AnvilRecipeSource {

        private boolean copyNbt;
        private int costLevel;
        private final ItemStack base;
        private final ItemStack addition;

        public AnvilRecipeSource(boolean copyNbt, int costLevel, ItemStack base, ItemStack addition) {
            this.copyNbt = copyNbt;
            this.costLevel = costLevel;
            this.base = base;
            this.addition = addition;
        }

    }

    class CostLevelInputPrompt implements NumberPrompt {

        private final int sourceIndex;
        private final AnvilSourceIcon anvilSourceIcon;

        public CostLevelInputPrompt(int sourceIndex, AnvilSourceIcon anvilSourceIcon) {
            this.sourceIndex = sourceIndex;
            this.anvilSourceIcon = anvilSourceIcon;
        }

        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            int costLevel = number.intValue();
            BukkitConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
            YamlConfiguration config = configWrapper.config();
            if (config.isList("source")) {
                List<Map<?, ?>> sourceList = config.getMapList("source");
                Map<String, Object> source = (Map<String, Object>) sourceList.get(sourceIndex);
                source.put("cost_level", costLevel);
                sourceList.set(sourceIndex, source);
                config.set("source", sourceList);
            } else {
                config.set("source.cost_level", costLevel);
            }
            recipeGroup.recipeGroupConfig().saveConfig();
            anvilRecipeSourceList.get(sourceIndex).costLevel = costLevel;
            anvilSourceIcon.refreshIconLore();
            reloadRecipeGroup();
            draw(inventoryCache);
            openMenu();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return BukkitTextProcessor.toComponent(BukkitTextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_ANVIL_ELEMENT_INPUT_HINT.value(player)));
        }

    }

}
