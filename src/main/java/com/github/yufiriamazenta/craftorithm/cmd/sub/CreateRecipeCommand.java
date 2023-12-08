package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.config.yaml.YamlConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.FileUtil;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CreateRecipeCommand extends AbstractSubCommand {

    public static final CreateRecipeCommand INSTANCE = new CreateRecipeCommand();
    private final List<String> recipeTypeList;
    private final Pattern recipeNamePattern = Pattern.compile("[a-z0-9/._-]+");

    private CreateRecipeCommand() {
        super("create", "craftorithm.command.create");
        recipeTypeList = Arrays.stream(RecipeType.values()).map(RecipeType::name).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        List<String> unsupportedRecipeTypeList = new ArrayList<>();
        unsupportedRecipeTypeList.add("random_cooking");
        unsupportedRecipeTypeList.add("unknown");
        if (CrypticLib.minecraftVersion() < 11400) {
            unsupportedRecipeTypeList.add("stone_cutting");
            unsupportedRecipeTypeList.add("smithing");
            unsupportedRecipeTypeList.add("cooking");
        }
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            unsupportedRecipeTypeList.add("potion");
        }
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            unsupportedRecipeTypeList.add("anvil");
        recipeTypeList.removeAll(unsupportedRecipeTypeList);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender))
            return true;
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        String recipeTypeStr = args.get(0).toLowerCase(Locale.ROOT);
        if (!recipeTypeList.contains(recipeTypeStr)) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE.value());
            return true;
        }
        String recipeName = args.get(1);
        Matcher matcher = recipeNamePattern.matcher(recipeName);
        if (!matcher.matches()) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME.value());
            return true;
        }
        if (RecipeManager.INSTANCE.hasCraftorithmRecipe(recipeName)) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_NAME_USED.value());
            return true;
        }
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
        switch (recipeType) {
            case SHAPED:
            case SHAPELESS:
                openCraftingRecipeCreator((Player) sender, recipeType, recipeName);
                break;
            case COOKING:
                openCookingRecipeCreator((Player) sender, recipeType, recipeName);
                break;
            case SMITHING:
                openSmithingRecipeCreator((Player) sender, recipeType, recipeName);
                break;
            case STONE_CUTTING:
                openStoneCuttingRecipeCreator((Player) sender, recipeType, recipeName);
                break;
            case POTION:
                openPotionMixCreator((Player) sender, recipeType, recipeName);
                break;
            case ANVIL:
                openAnvilRecipeCreator((Player) sender, recipeType, recipeName);
                break;
            default:
                LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE.value());
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>(recipeTypeList);
            filterTabList(tabList, args.get(0));
            return tabList;
        }
        return Collections.singletonList("<recipe_name>");
    }

    private void openCraftingRecipeCreator(Player player, RecipeType recipeType, String recipeName) {
        StoredMenu craftingRecipeCreator = new StoredMenu(player, new MenuDisplay(
            Languages.MENU_RECIPE_CREATOR_TITLE.value()
                .replace("<recipe_type>", recipeType.name())
                .replace("<recipe_name>", recipeName),
            new MenuLayout(Arrays.asList(
                "####F####",
                "#   #***#",
                "#   A* *#",
                "#   #***#",
                "#########"
            ), () -> {
                Map<Character, Icon> layoutMap = new HashMap<>();
                layoutMap.put('#', getFrameIcon());
                layoutMap.put('*', getResultFrameIcon());
                layoutMap.put('F', getUnlockIcon());
                layoutMap.put('A', new Icon(
                    Material.CRAFTING_TABLE,
                    Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                    event -> {
                        StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                        Map<Integer, ItemStack> storedItems = Objects.requireNonNull(creator).storedItems();
                        ItemStack result = storedItems.get(24);
                        if (ItemUtil.isAir(result)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT.value());
                            return;
                        }
                        String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                        int[] sourceSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                        List<String> sourceList = new ArrayList<>();
                        for (int slot : sourceSlots) {
                            ItemStack source = storedItems.get(slot);
                            if (ItemUtil.isAir(source)) {
                                sourceList.add("");
                                continue;
                            }
                            String sourceName = ItemUtils.matchItemNameOrCreate(source, true);
                            sourceList.add(sourceName);
                        }
                        YamlConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                        switch (recipeType) {
                            case SHAPED:
                                List<String> shape = new ArrayList<>(Arrays.asList("abc", "def", "ghi"));
                                Map<Character, String> itemNameMap = new HashMap<>();
                                char[] tmp = "abcdefghi".toCharArray();
                                for (int i = 0; i < sourceList.size(); i++) {
                                    if (sourceList.get(i).isEmpty()) {
                                        continue;
                                    }
                                    itemNameMap.put(tmp[i], sourceList.get(i));
                                }
                                //删除无映射的字符
                                for (int i = 0; i < shape.size(); i++) {
                                    String s = shape.get(i);
                                    for (char c : s.toCharArray()) {
                                        if (!itemNameMap.containsKey(c)) {
                                            s = s.replace(c, ' ');
                                        }
                                    }
                                    shape.set(i, s);
                                }
                                shape.removeIf(s -> s.trim().isEmpty());
                                recipeConfig.set("type", "shaped");
                                recipeConfig.set("shape", shape);
                                recipeConfig.set("source", itemNameMap);
                                break;
                            case SHAPELESS:
                                sourceList.removeIf(String::isEmpty);
                                recipeConfig.set("type", "shapeless");
                                recipeConfig.set("source", sourceList);
                                break;
                        }
                        if (!event.getClickedInventory().getItem(4).getEnchantments().isEmpty()) {
                            recipeConfig.set("unlock", true);
                        }
                        recipeConfig.set("result", resultName);
                        recipeConfig.saveConfig();
                        recipeConfig.reloadConfig();
                        for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                            recipeRegistry.register();
                        }
                        RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                        event.getWhoClicked().closeInventory();
                        sendSuccessMsg(event.getWhoClicked(), recipeType, recipeName);
                    })
                );
                return layoutMap;
            }))
        );
        craftingRecipeCreator.openMenu();
    }

    private void openCookingRecipeCreator(Player player, RecipeType recipeType, String recipeName) {
        StoredMenu cookingRecipeCreator = new StoredMenu(player, new MenuDisplay(
            Languages.MENU_RECIPE_CREATOR_TITLE.value()
                .replace("<recipe_type>", recipeType.name())
                .replace("<recipe_name>", recipeName),
            new MenuLayout(Arrays.asList(
                "####F####",
                "#***#%%%#",
                "#* *A% %#",
                "#***#%%%#",
                "##BC#DE##"
            ), () -> {
                Map<Character, Icon> layoutMap = new HashMap<>();
                layoutMap.put('#', getFrameIcon());
                layoutMap.put('%', getResultFrameIcon());
                layoutMap.put('*', new Icon(Material.CYAN_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_COOKING_FRAME.value()));
                layoutMap.put('A', new Icon(
                    Material.FURNACE,
                    Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                    event -> {
                        StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                        ItemStack source = Objects.requireNonNull(creator).storedItems().get(20);
                        ItemStack result = creator.storedItems().get(24);
                        if (ItemUtil.isAir(source)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE.value());
                            return;
                        }
                        if (ItemUtil.isAir(result)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT.value());
                            return;
                        }
                        String sourceName = ItemUtils.matchItemNameOrCreate(source, true);
                        String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                        YamlConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                        recipeConfig.set("type", "cooking");
                        recipeConfig.set("result", resultName);
                        recipeConfig.set("multiple", true);
                        List<Map<String, String>> sourceList = new ArrayList<>();
                        int[] toggleSlots = {38, 39, 41, 42};
                        for (int toggleSlot : toggleSlots) {
                            ItemStack item = event.getClickedInventory().getItem(toggleSlot);
                            Material block = item.getType();
                            boolean toggle = !item.getEnchantments().isEmpty();
                            if (toggle) {
                                Map<String, String> sourceMap = new HashMap<>();
                                sourceMap.put("block", block.name().toLowerCase());
                                sourceMap.put("item", sourceName);
                                sourceList.add(sourceMap);
                            }
                        }
                        if (sourceList.isEmpty()) {
                            sourceList.add(CollectionsUtil.newStringHashMap("block", "furnace", "item", sourceName));
                        }
                        if (!event.getClickedInventory().getItem(4).getEnchantments().isEmpty()) {
                            recipeConfig.set("unlock", true);
                        }
                        recipeConfig.set("source", sourceList);
                        recipeConfig.saveConfig();
                        recipeConfig.reloadConfig();
                        for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                            recipeRegistry.register();
                        }
                        RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                        event.getWhoClicked().closeInventory();
                        sendSuccessMsg(event.getWhoClicked(), recipeType, recipeName);
                    })
                );
                layoutMap.put('B', new Icon(
                    Material.FURNACE,
                    Languages.MENU_RECIPE_CREATOR_ICON_FURNACE_TOGGLE.value(),
                    event -> setIconGlowing(event.getSlot(), event)
                ));
                layoutMap.put('C', new Icon(
                    Material.BLAST_FURNACE,
                    Languages.MENU_RECIPE_CREATOR_ICON_BLASTING_TOGGLE.value(),
                    event -> setIconGlowing(event.getSlot(), event)
                ));
                layoutMap.put('D', new Icon(
                    Material.SMOKER,
                    Languages.MENU_RECIPE_CREATOR_ICON_SMOKING_TOGGLE.value(),
                    event -> setIconGlowing(event.getSlot(), event)
                ));
                layoutMap.put('E', new Icon(
                    Material.CAMPFIRE,
                    Languages.MENU_RECIPE_CREATOR_ICON_CAMPFIRE_TOGGLE.value(),
                    event -> setIconGlowing(event.getSlot(), event)
                ));
                layoutMap.put('F', getUnlockIcon());
                return layoutMap;
            })
        ));
        cookingRecipeCreator.openMenu();
    }

    private void openSmithingRecipeCreator(Player player, RecipeType recipeType, String recipeName) {
        StoredMenu smithingCreator = new StoredMenu(player, new MenuDisplay(
            Languages.MENU_RECIPE_CREATOR_TITLE.value()
                .replace("<recipe_type>", recipeType.name())
                .replace("<recipe_name>", recipeName),
            new MenuLayout(
                () -> {
                    if (CrypticLib.minecraftVersion() < 12000) {
                        return Arrays.asList(
                            "####F####",
                            "#***#%%%#",
                            "# * A% %#",
                            "#***#%%%#",
                            "#########"
                        );
                    } else {
                        return Arrays.asList(
                            "####F####",
                            "#***#%%%#",
                            "#   A% %#",
                            "#***#%%%#",
                            "#########"
                        );
                    }
                },
                () -> {
                    Map<Character, Icon> layoutMap = new HashMap<>();
                    layoutMap.put('#', getFrameIcon());
                    layoutMap.put('*', new Icon(Material.CYAN_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_SMITHING_FRAME.value()));
                    layoutMap.put('%', getResultFrameIcon());
                    layoutMap.put('F', getUnlockIcon());
                    layoutMap.put('A', new Icon(
                        Material.SMITHING_TABLE,
                        Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                        event -> {
                            StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                            ItemStack result = Objects.requireNonNull(creator).storedItems().get(24);
                            if (ItemUtil.isAir(result)) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT.value());
                                return;
                            }
                            String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                            ItemStack base, addition, template;
                            String baseName, additionName, templateName = null;
                            if (CrypticLib.minecraftVersion() < 12000) {
                                base = creator.storedItems().get(19);
                                addition = creator.storedItems().get(21);
                            } else {
                                template = creator.storedItems().get(19);
                                base = creator.storedItems().get(20);
                                addition = creator.storedItems().get(21);
                                templateName = ItemUtils.matchItemNameOrCreate(template, true);
                                if (ItemUtil.isAir(template)) {
                                    LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE.value());
                                    return;
                                }
                            }
                            if (ItemUtil.isAir(base) || ItemUtil.isAir(addition)) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE.value());
                                return;
                            }
                            baseName = ItemUtils.matchItemNameOrCreate(base, true);
                            additionName = ItemUtils.matchItemNameOrCreate(addition, true);
                            YamlConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                            recipeConfig.set("result", resultName);
                            recipeConfig.set("source.base", baseName);
                            recipeConfig.set("source.addition", additionName);
                            recipeConfig.set("type", "smithing");
                            if (CrypticLib.minecraftVersion() >= 12000) {
                                recipeConfig.set("source.type", "transform");
                                recipeConfig.set("source.template", templateName);
                            }
                            if (!event.getClickedInventory().getItem(4).getEnchantments().isEmpty()) {
                                recipeConfig.set("unlock", true);
                            }
                            recipeConfig.saveConfig();
                            recipeConfig.reloadConfig();
                            for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                                recipeRegistry.register();
                            }
                            RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                            event.getWhoClicked().closeInventory();
                            sendSuccessMsg(event.getWhoClicked(), recipeType, recipeName);
                        })
                    );
                    return layoutMap;
                }
            ))
        );
        smithingCreator.openMenu();
    }

    private void openStoneCuttingRecipeCreator(Player player, RecipeType recipeType, String recipeName) {
        StoredMenu stoneCuttingCreator = new StoredMenu(player, new MenuDisplay(
            Languages.MENU_RECIPE_CREATOR_TITLE.value()
                .replace("<recipe_type>", recipeType.name())
                .replace("<recipe_name>", recipeName),
            new MenuLayout(Arrays.asList(
                "#########",
                "#       #",
                "####A####",
                "#       #",
                "#########"
            ), () -> {
                Map<Character, Icon> layoutMap = new HashMap<>();
                layoutMap.put('#', getFrameIcon());
                layoutMap.put('F', getUnlockIcon());
                layoutMap.put('A', new Icon(Material.STONECUTTER, Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                    event -> {
                        StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                        List<String> sourceList = new ArrayList<>();
                        List<String> resultList = new ArrayList<>();
                        for (int i = 10; i < 17; i++) {
                            ItemStack source = Objects.requireNonNull(creator).storedItems().get(i);
                            if (ItemUtil.isAir(source))
                                continue;
                            sourceList.add(ItemUtils.matchItemNameOrCreate(source, true));
                        }
                        for (int i = 28; i < 35; i++) {
                            ItemStack result = creator.storedItems().get(i);
                            if (ItemUtil.isAir(result))
                                continue;
                            resultList.add(ItemUtils.matchItemNameOrCreate(result, false));
                        }
                        if (sourceList.isEmpty()) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE.value());
                            return;
                        }
                        if (resultList.isEmpty()) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT.value());
                            return;
                        }
                        YamlConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                        recipeConfig.set("multiple", true);
                        recipeConfig.set("result", resultList);
                        recipeConfig.set("type", "stone_cutting");
                        recipeConfig.set("source", sourceList);
                        if (!event.getClickedInventory().getItem(4).getEnchantments().isEmpty()) {
                            recipeConfig.set("unlock", true);
                        }
                        recipeConfig.saveConfig();
                        recipeConfig.reloadConfig();
                        for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                            recipeRegistry.register();
                        }
                        RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                        event.getWhoClicked().closeInventory();
                        sendSuccessMsg(event.getWhoClicked(), recipeType, recipeName);
                    })
                );
                return layoutMap;
            })
        ));
        stoneCuttingCreator.openMenu();
    }

    private void openPotionMixCreator(Player player, RecipeType recipeType, String recipeName) {
        StoredMenu potionMixCreator = new StoredMenu(player, new MenuDisplay(
            Languages.MENU_RECIPE_CREATOR_TITLE.value()
                .replace("<recipe_type>", recipeType.name())
                .replace("<recipe_name>", recipeName),
            new MenuLayout(Arrays.asList(
                "#########",
                "#***#%%%#",
                "# * A% %#",
                "#***#%%%#",
                "#########"
            ), () -> {
                Map<Character, Icon> layoutMap = new HashMap<>();
                layoutMap.put('#', getFrameIcon());
                layoutMap.put('%', getResultFrameIcon());
                layoutMap.put('*', new Icon(Material.CYAN_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_POTION_FRAME.value()));
                layoutMap.put('A', new Icon(Material.BREWING_STAND, Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                    event -> {
                        StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                        ItemStack result = Objects.requireNonNull(creator).storedItems().get(24);
                        ItemStack input = creator.storedItems().get(19);
                        ItemStack ingredient = creator.storedItems().get(21);
                        if (ItemUtil.isAir(result)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT.value());
                            return;
                        }
                        if (ItemUtil.isAir(ingredient) || ItemUtil.isAir(input)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE.value());
                            return;
                        }
                        String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                        String inputName = ItemUtils.matchItemNameOrCreate(input, true);
                        String ingredientName = ItemUtils.matchItemNameOrCreate(ingredient, true);
                        YamlConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                        recipeConfig.set("type", "potion");
                        recipeConfig.set("source.input", inputName);
                        recipeConfig.set("source.ingredient", ingredientName);
                        recipeConfig.set("result", resultName);
                        recipeConfig.saveConfig();
                        recipeConfig.reloadConfig();
                        for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                            recipeRegistry.register();
                        }
                        RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                        event.getWhoClicked().closeInventory();
                        sendSuccessMsg(player, recipeType, recipeName);
                    })
                );
                return layoutMap;
            })
        ));
        potionMixCreator.openMenu();
    }

    private void openAnvilRecipeCreator(Player player, RecipeType recipeType, String recipeName) {
        StoredMenu anvilRecipeCreator = new StoredMenu(player, new MenuDisplay(
            Languages.MENU_RECIPE_CREATOR_TITLE.value()
                .replace("<recipe_type>", recipeType.name())
                .replace("<recipe_name>", recipeName),
            new MenuLayout(Arrays.asList(
                "#########",
                "#***#%%%#",
                "# * A% %#",
                "#***#%%%#",
                "####B####"
            ), () -> {
                Map<Character, Icon> layoutMap = new HashMap<>();
                layoutMap.put('#', getFrameIcon());
                layoutMap.put('%', getResultFrameIcon());
                layoutMap.put('*', new Icon(Material.CYAN_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_FRAME.value()));
                Icon toggleCopyNbt = new Icon(
                    Material.NAME_TAG,
                    Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COPY_NBT_TOGGLE.value(),
                    event -> setIconGlowing(event.getSlot(), event)
                );
                toggleCopyNbt.display().addUnsafeEnchantment(Enchantment.MENDING, 1);
                toggleCopyNbt.display().addItemFlags(ItemFlag.HIDE_ENCHANTS);
                layoutMap.put('B', toggleCopyNbt);
                layoutMap.put('A', new Icon(Material.ANVIL, Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                    event -> {
                        StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                        ItemStack result = Objects.requireNonNull(creator).storedItems().get(24);
                        ItemStack base = creator.storedItems().get(19);
                        ItemStack addition = creator.storedItems().get(21);
                        if (ItemUtil.isAir(result)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT.value());
                            return;
                        }
                        if (ItemUtil.isAir(addition) || ItemUtil.isAir(base)) {
                            LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE.value());
                            return;
                        }
                        String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                        String inputName = ItemUtils.matchItemNameOrCreate(base, false);
                        String ingredientName = ItemUtils.matchItemNameOrCreate(addition, false);
                        YamlConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                        recipeConfig.set("source.copy_nbt", event.getInventory().getItem(40).getItemMeta().hasEnchants());
                        recipeConfig.set("type", "anvil");
                        recipeConfig.set("source.base", inputName);
                        recipeConfig.set("source.addition", ingredientName);
                        recipeConfig.set("result", resultName);
                        recipeConfig.saveConfig();
                        recipeConfig.reloadConfig();
                        for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                            recipeRegistry.register();
                        }
                        RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                        event.getWhoClicked().closeInventory();
                        sendSuccessMsg(player, recipeType, recipeName);
                    })
                );
                return layoutMap;
            })
        ));
        anvilRecipeCreator.openMenu();
    }

    private void sendSuccessMsg(HumanEntity receiver, RecipeType recipeType, String recipeName) {
        LangUtil.sendLang(
            receiver,
            Languages.COMMAND_CREATE_SUCCESS.value(),
            CollectionsUtil.newStringHashMap("<recipe_type>", recipeType.name(), "<recipe_name>", recipeName)
        );
    }

    private YamlConfigWrapper createRecipeConfig(String recipeName) {
        File recipeFile = new File(RecipeManager.INSTANCE.RECIPE_FILE_FOLDER, recipeName + ".yml");
        if (!recipeFile.exists()) {
            FileUtil.createNewFile(recipeFile);
        }
        return new YamlConfigWrapper(recipeFile);
    }

    private void setIconGlowing(int slot, InventoryClickEvent event) {
        ItemStack display = event.getCurrentItem();
        if (ItemUtil.isAir(display))
            return;
        if (!display.containsEnchantment(Enchantment.MENDING)) {
            display.addUnsafeEnchantment(Enchantment.MENDING, 1);
            ItemMeta meta = display.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            display.setItemMeta(meta);
            event.getClickedInventory().setItem(slot, display);
        } else {
            display.removeEnchantment(Enchantment.MENDING);
            event.getClickedInventory().setItem(slot, display);
        }
    }

    private Icon getFrameIcon() {
        return new Icon(Material.BLACK_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_FRAME.value());
    }

    private Icon getUnlockIcon() {
        return new Icon(
            Material.KNOWLEDGE_BOOK,
            Languages.MENU_RECIPE_CREATOR_ICON_UNLOCK.value(),
            event -> setIconGlowing(event.getSlot(), event)
        );
    }

    private Icon getResultFrameIcon() {
        return new Icon(Material.LIME_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_RESULT_FRAME.value());
    }

}
