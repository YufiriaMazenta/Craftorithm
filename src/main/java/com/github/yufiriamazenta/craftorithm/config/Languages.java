package com.github.yufiriamazenta.craftorithm.config;

import crypticlib.chat.LangConfigHandler;
import crypticlib.chat.entry.StringLangConfigEntry;
import crypticlib.chat.entry.StringListLangConfigEntry;

@LangConfigHandler(langFileFolder = "lang")
public class Languages {

    public static final StringLangConfigEntry PREFIX = new StringLangConfigEntry("prefix");
    public static final StringLangConfigEntry UNSUPPORTED_VERSION = new StringLangConfigEntry("unsupported_version");
    public static final StringLangConfigEntry NEW_VERSION = new StringLangConfigEntry("new_version");
    public static final StringLangConfigEntry COMMAND_NO_PERM = new StringLangConfigEntry("command.no_perm");
    public static final StringLangConfigEntry COMMAND_PLAYER_ONLY = new StringLangConfigEntry("command.player_only");
    public static final StringLangConfigEntry COMMAND_NOT_ENOUGH_PARAM = new StringLangConfigEntry("command.not_enough_param");
    public static final StringLangConfigEntry COMMAND_UNDEFINED_SUBCMD = new StringLangConfigEntry("command.undefined_subcmd");
    public static final StringLangConfigEntry COMMAND_ITEM_SAVE_SUCCESS = new StringLangConfigEntry("command.item.save.success");
    public static final StringLangConfigEntry COMMAND_ITEM_SAVE_FAILED_SAVE_AIR = new StringLangConfigEntry("command.item.save.failed_save_air");
    public static final StringLangConfigEntry COMMAND_ITEM_GIVE_SUCCESS = new StringLangConfigEntry("command.item.give.success");
    public static final StringLangConfigEntry COMMAND_ITEM_GIVE_NOT_EXIST_ITEM = new StringLangConfigEntry("command.item.give.not_exist_item");
    public static final StringLangConfigEntry COMMAND_ITEM_GIVE_PLAYER_OFFLINE = new StringLangConfigEntry("command.item.give.player_offline");
    public static final StringLangConfigEntry COMMAND_RELOAD_SUCCESS = new StringLangConfigEntry("command.reload.success");
    public static final StringLangConfigEntry COMMAND_RELOAD_EXCEPTION = new StringLangConfigEntry("command.reload.exception");
    public static final StringLangConfigEntry COMMAND_REMOVE_SUCCESS = new StringLangConfigEntry("command.remove.success");
    public static final StringLangConfigEntry COMMAND_REMOVE_NOT_EXIST = new StringLangConfigEntry("command.remove.not_exist");
    public static final StringLangConfigEntry COMMAND_DISABLE_SUCCESS = new StringLangConfigEntry("command.disable.success");
    public static final StringLangConfigEntry COMMAND_DISABLE_NOT_EXIST = new StringLangConfigEntry("command.disable.not_exist");
    public static final StringLangConfigEntry COMMAND_DISABLE_FAILED = new StringLangConfigEntry("command.disable.failed");
    public static final StringLangConfigEntry COMMAND_VERSION = new StringLangConfigEntry("command.version");
    public static final StringLangConfigEntry COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE = new StringLangConfigEntry("command.create.unsupported_recipe_type");
    public static final StringLangConfigEntry COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME = new StringLangConfigEntry("command.create.unsupported_recipe_name");
    public static final StringLangConfigEntry COMMAND_CREATE_NAME_USED = new StringLangConfigEntry("command.create.name_used");
    public static final StringLangConfigEntry COMMAND_CREATE_NULL_RESULT = new StringLangConfigEntry("command.create.null_result");
    public static final StringLangConfigEntry COMMAND_CREATE_NULL_SOURCE = new StringLangConfigEntry("command.create.null_source");
    public static final StringLangConfigEntry COMMAND_CREATE_SUCCESS = new StringLangConfigEntry("command.create.success");
    public static final StringLangConfigEntry COMMAND_RUN_ARCENCIEL_SUCCESS = new StringLangConfigEntry("command.run_arcenciel.success");
    public static final StringLangConfigEntry COMMAND_LIST_UNSUPPORTED_VERSION = new StringLangConfigEntry("command.list.unsupported_version");
    public static final StringLangConfigEntry MENU_RECIPE_LIST_TITLE = new StringLangConfigEntry("menu.recipe_list.title");
    public static final StringLangConfigEntry MENU_RECIPE_LIST_ICON_FRAME = new StringLangConfigEntry("menu.recipe_list.icon.frame");
    public static final StringLangConfigEntry MENU_RECIPE_LIST_ICON_PREVIOUS = new StringLangConfigEntry("menu.recipe_list.icon.previous");
    public static final StringLangConfigEntry MENU_RECIPE_LIST_ICON_NEXT = new StringLangConfigEntry("menu.recipe_list.icon.next");
    public static final StringLangConfigEntry MENU_NEW_RECIPE_LIST_TITLE = new StringLangConfigEntry("menu.new_recipe_list.title");
    public static final StringLangConfigEntry MENU_NEW_RECIPE_LIST_ICON_FRAME = new StringLangConfigEntry("menu.new_recipe_list.icon.frame");
    public static final StringLangConfigEntry MENU_NEW_RECIPE_LIST_ICON_PREVIOUS = new StringLangConfigEntry("menu.new_recipe_list.icon.previous");
    public static final StringLangConfigEntry MENU_NEW_RECIPE_LIST_ICON_NEXT = new StringLangConfigEntry("menu.new_recipe_list.icon.next");
    public static final StringListLangConfigEntry MENU_NEW_RECIPE_LIST_ICON_ELEMENTS_LORE = new StringListLangConfigEntry("menu.new_recipe_list.icon.elements");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_SHAPED = new StringLangConfigEntry("menu.recipe_display.title.shaped");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_SHAPELESS = new StringLangConfigEntry("menu.recipe_display.title.shapeless");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_FURNACE = new StringLangConfigEntry("menu.recipe_display.title.furnace");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_BLASTING = new StringLangConfigEntry("menu.recipe_display.title.blasting");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_SMOKING = new StringLangConfigEntry("menu.recipe_display.title.smoking");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_CAMPFIRE = new StringLangConfigEntry("menu.recipe_display.title.campfire");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_SMITHING = new StringLangConfigEntry("menu.recipe_display.title.smithing");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_STONE_CUTTING = new StringLangConfigEntry("menu.recipe_display.title.stone_cutting");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_POTION = new StringLangConfigEntry("menu.recipe_display.title.potion");
    public static final StringLangConfigEntry MENU_RECIPE_DISPLAY_TITLE_ANVIL = new StringLangConfigEntry("menu.recipe_display.title.anvil");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_TITLE = new StringLangConfigEntry("menu.recipe_creator.title");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_FRAME = new StringLangConfigEntry("menu.recipe_creator.icon.frame");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_RESULT_FRAME = new StringLangConfigEntry("menu.recipe_creator.icon.result_frame");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_CONFIRM = new StringLangConfigEntry("menu.recipe_creator.icon.confirm");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_UNLOCK = new StringLangConfigEntry("menu.recipe_creator.icon.unlock");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_FRAME = new StringLangConfigEntry("menu.recipe_creator.icon.cooking_frame");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_SMITHING_FRAME = new StringLangConfigEntry("menu.recipe_creator.icon.smithing_frame");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_POTION_FRAME = new StringLangConfigEntry("menu.recipe_creator.icon.potion_frame");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_ANVIL_FRAME = new StringLangConfigEntry("menu.recipe_creator.icon.anvil_frame");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_ANVIL_COPY_NBT_TOGGLE = new StringLangConfigEntry("menu.recipe_creator.icon.anvil_copy_nbt_toggle");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_NAME = new StringLangConfigEntry("menu.recipe_creator.icon.anvil_cost_level.name");
    public static final StringListLangConfigEntry MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_LORE = new StringListLangConfigEntry("menu.recipe_creator.icon.anvil_cost_level.lore");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_INPUT_HINT = new StringLangConfigEntry("menu.recipe_creator.icon.anvil_cost_level.input_hint");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_TIME_NAME = new StringLangConfigEntry("menu.recipe_creator.icon.cooking_time.name");
    public static final StringListLangConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_TIME_LORE = new StringListLangConfigEntry("menu.recipe_creator.icon.cooking_time.lore");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_TIME_INPUT_HINT = new StringLangConfigEntry("menu.recipe_creator.icon.cooking_time.input_hint");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_EXP_NAME = new StringLangConfigEntry("menu.recipe_creator.icon.cooking_exp.name");
    public static final StringListLangConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_EXP_LORE = new StringListLangConfigEntry("menu.recipe_creator.icon.cooking_exp.lore");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_EXP_INPUT_HINT = new StringLangConfigEntry("menu.recipe_creator.icon.cooking_exp.input_hint");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_FURNACE_TOGGLE = new StringLangConfigEntry("menu.recipe_creator.icon.furnace_toggle");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_BLAST_FURNACE_TOGGLE = new StringLangConfigEntry("menu.recipe_creator.icon.blast_furnace_toggle");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_SMOKER_TOGGLE = new StringLangConfigEntry("menu.recipe_creator.icon.smoker_toggle");
    public static final StringLangConfigEntry MENU_RECIPE_CREATOR_ICON_CAMPFIRE_TOGGLE = new StringLangConfigEntry("menu.recipe_creator.icon.campfire_toggle");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_TITLE = new StringLangConfigEntry("menu.recipe_editor.title");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_FRAME = new StringLangConfigEntry("menu.recipe_editor.icon.frame");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_NEXT = new StringLangConfigEntry("menu.recipe_editor.icon.next");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_PREVIOUS = new StringLangConfigEntry("menu.recipe_editor.icon.previous");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_SORT_ID_NAME = new StringLangConfigEntry("menu.recipe_editor.icon.slot_id.name");
    public static final StringListLangConfigEntry MENU_RECIPE_EDITOR_ICON_SORT_ID_LORE = new StringListLangConfigEntry("menu.recipe_editor.icon.slot_id.lore");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_SORT_ID_INPUT_HINT = new StringLangConfigEntry("menu.recipe_editor.icon.slot_id.input_hint");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_UNLOCK = new StringLangConfigEntry("menu.recipe_editor.icon.unlock");
    public static final StringListLangConfigEntry MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_LORE = new StringListLangConfigEntry("menu.recipe_editor.icon.cooking_element.lore");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_INPUT_COOKING_TIME_HINT = new StringLangConfigEntry("menu.recipe_editor.icon.cooking_element.input_cooking_time_hint");
    public static final StringLangConfigEntry MENU_RECIPE_EDITOR_ICON_COOKING_ELEMENT_INPUT_COOKING_EXP_HINT = new StringLangConfigEntry("menu.recipe_editor.icon.cooking_element.input_cooking_exp_hint");
    public static final StringLangConfigEntry ARCENCIEL_NOT_ENOUGH_PARAM = new StringLangConfigEntry("arcenciel.not_enough_param");
    public static final StringLangConfigEntry ARCENCIEL_UNKNOWN_TOKEN = new StringLangConfigEntry("arcenciel.unknown_token");
    public static final StringLangConfigEntry LOAD_FINISH = new StringLangConfigEntry("load.finish");
    public static final StringLangConfigEntry LOAD_RECIPE_LOAD_EXCEPTION = new StringLangConfigEntry("load.recipe_load_exception");
    public static final StringLangConfigEntry LOAD_ITEM_LOAD_EXCEPTION = new StringLangConfigEntry("load.item_load_exception");
    public static final StringLangConfigEntry LOAD_HOOK_PLUGIN_SUCCESS = new StringLangConfigEntry("load.hook_plugin.success");
    public static final StringLangConfigEntry LOAD_HOOK_PLUGIN_NOT_EXIST = new StringLangConfigEntry("load.hook_plugin.not_exist");
    public static final StringLangConfigEntry RECIPE_TYPE_NAME_SHAPED = new StringLangConfigEntry("recipe_type_name.shaped");
    public static final StringLangConfigEntry RECIPE_TYPE_NAME_SHAPELESS = new StringLangConfigEntry("recipe_type_name.shapeless");
    public static final StringLangConfigEntry RECIPE_TYPE_NAME_COOKING = new StringLangConfigEntry("recipe_type_name.cooking");
    public static final StringLangConfigEntry RECIPE_TYPE_NAME_SMITHING = new StringLangConfigEntry("recipe_type_name.smithing");
    public static final StringLangConfigEntry RECIPE_TYPE_NAME_STONE_CUTTING = new StringLangConfigEntry("recipe_type_name.stone_cutting");
    public static final StringLangConfigEntry RECIPE_TYPE_NAME_POTION = new StringLangConfigEntry("recipe_type_name.potion");
    public static final StringLangConfigEntry RECIPE_TYPE_NAME_ANVIL = new StringLangConfigEntry("recipe_type_name.anvil");

}
