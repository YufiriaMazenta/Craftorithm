package pers.yufiria.craftorithm.config;

import crypticlib.lang.LangHandler;
import crypticlib.lang.node.StringLang;

@LangHandler(langFileFolder = "lang")
public class Languages {

    public static final StringLang PREFIX = new StringLang("prefix");
    public static final StringLang UNSUPPORTED_VERSION = new StringLang("unsupported_version");
    public static final StringLang NEW_VERSION = new StringLang("new_version");
    public static final StringLang COMMAND_NO_PERM = new StringLang("command.no_perm");
    public static final StringLang COMMAND_PLAYER_ONLY = new StringLang("command.player_only");
    public static final StringLang COMMAND_UNKNOWN_PLAYER = new StringLang("command.unknown_player");
    public static final StringLang COMMAND_ITEM_SAVE_SUCCESS = new StringLang("command.item.save.success");
    public static final StringLang COMMAND_ITEM_SAVE_FAILED_SAVE_AIR = new StringLang("command.item.save.failed_save_air");
    public static final StringLang COMMAND_ITEM_GIVE_SUCCESS = new StringLang("command.item.give.success");
    public static final StringLang COMMAND_ITEM_GIVE_NOT_EXIST_ITEM = new StringLang("command.item.give.not_exist_item");
    public static final StringLang COMMAND_ITEM_GIVE_PLAYER_OFFLINE = new StringLang("command.item.give.player_offline");
    public static final StringLang COMMAND_ITEM_FUEL_ADD_SUCCESS = new StringLang("command.item.fuel.add.success");
    public static final StringLang COMMAND_ITEM_FUEL_ADD_FAILED_ADD_AIR = new StringLang("command.item.fuel.add.failed_add_air");
    public static final StringLang COMMAND_ITEM_FUEL_ADD_FAILED_EXIST = new StringLang("command.item.fuel.add.failed_exist");
    public static final StringLang COMMAND_ITEM_FUEL_REMOVE_SUCCESS = new StringLang("command.item.fuel.remove.success");
    public static final StringLang COMMAND_ITEM_FUEL_REMOVE_FAILED_NOT_EXIST = new StringLang("command.item.fuel.remove.failed_not_exist");
    public static final StringLang COMMAND_ITEM_GET_ITEM_GROUP_TAG_UNKNOWN_TAG = new StringLang("command.item.get_item_group.tag.unknown_tag");
    public static final StringLang COMMAND_ITEM_GET_ITEM_GROUP_ITEM_PACK_UNKNOWN_ITEM_PACK = new StringLang("command.item.get_item_group.item_pack.unknown_item_pack");
    public static final StringLang COMMAND_RELOAD_RELOADING = new StringLang("command.reload.reloading");
    public static final StringLang COMMAND_RELOAD_SUCCESS = new StringLang("command.reload.success");
    public static final StringLang COMMAND_RELOAD_EXCEPTION = new StringLang("command.reload.exception");
    public static final StringLang COMMAND_REMOVE_SUCCESS = new StringLang("command.remove.success");
    public static final StringLang COMMAND_REMOVE_NOT_EXIST = new StringLang("command.remove.not_exist");
    public static final StringLang COMMAND_DISABLE_SUCCESS = new StringLang("command.disable.success");
    public static final StringLang COMMAND_DISABLE_NOT_EXIST = new StringLang("command.disable.not_exist");
    public static final StringLang COMMAND_DISABLE_FAILED = new StringLang("command.disable.failed");
    public static final StringLang COMMAND_RESTORE_SUCCESS = new StringLang("command.restore.success");
    public static final StringLang COMMAND_RESTORE_INVALID_RECIPE_ID = new StringLang("command.restore.invalid_recipe_id");
    public static final StringLang COMMAND_RESTORE_FAILED = new StringLang("command.restore.failed");
    public static final StringLang COMMAND_VERSION = new StringLang("command.version");
    public static final StringLang COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE = new StringLang("command.create.unsupported_recipe_type");
    public static final StringLang COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME = new StringLang("command.create.unsupported_recipe_name");
    public static final StringLang COMMAND_CREATE_NAME_USED = new StringLang("command.create.name_used");
    public static final StringLang COMMAND_CREATE_INVALID_RECIPE_ID = new StringLang("command.create.invalid_recipe_id");
    public static final StringLang COMMAND_CREATE_SUCCESS = new StringLang("command.create.success");
    public static final StringLang COMMAND_CREATE_INPUT_HINT_SMELTING_TIME = new StringLang("command.create.input_hint.smelting_time");
    public static final StringLang COMMAND_CREATE_INPUT_HINT_SMELTING_EXP = new StringLang("command.create.input_hint.smelting_exp");
    public static final StringLang COMMAND_CREATE_INPUT_HINT_ANVIL_COST_LEVEL = new StringLang("command.create.input_hint.anvil_cost_level");
    public static final StringLang COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE = new StringLang("command.display.unsupported_recipe_type");
    public static final StringLang COMMAND_OPENMENU_UNKNOWN_MENU = new StringLang("command.openmenu.unknown_menu");
    public static final StringLang COMMAND_SCRIPT_OPERATION_TIME = new StringLang("command.script.operation_time");
    public static final StringLang COMMAND_EDIT_INVALID_RECIPE_ID = new StringLang("command.edit.invalid_recipe_id");
    public static final StringLang COMMAND_EDIT_RECIPE_NOT_FOUND = new StringLang("command.edit.recipe_not_found");
    public static final StringLang COMMAND_EDIT_SUCCESS = new StringLang("command.edit.success");
    public static final StringLang COMMAND_RECIPEBOOK_TYPE_NOT_FOUND = new StringLang("command.recipebook.type_not_found");
    public static final StringLang COMMAND_DISCOVER_SUCCESS = new StringLang("command.discover.success");
    public static final StringLang COMMAND_DISCOVER_NO_MATCH = new StringLang("command.discover.no_match");
    public static final StringLang COMMAND_UNDISCOVER_SUCCESS = new StringLang("command.undiscover.success");
    public static final StringLang COMMAND_UNDISCOVER_NO_MATCH = new StringLang("command.undiscover.no_match");

    public static final StringLang LOAD_FINISH = new StringLang("load_finish");
    public static final StringLang RECIPE_LOAD_EXCEPTION = new StringLang("recipe_load_exception");
    public static final StringLang ITEM_LOAD_EXCEPTION = new StringLang("item_load_exception");
    public static final StringLang LOAD_HOOK_PLUGIN_SUCCESS = new StringLang("hook_plugin_success");

    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SHAPED = new StringLang("recipe_type_name.vanilla_shaped");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SHAPELESS = new StringLang("recipe_type_name.vanilla_shapeless");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SMELTING_FURNACE = new StringLang("recipe_type_name.vanilla_smelting_furnace");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SMELTING_BLAST = new StringLang("recipe_type_name.vanilla_smelting_blast");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SMELTING_SMOKER = new StringLang("recipe_type_name.vanilla_smelting_smoker");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SMELTING_CAMPFIRE = new StringLang("recipe_type_name.vanilla_smelting_campfire");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SMITHING_TRANSFORM = new StringLang("recipe_type_name.vanilla_smithing_transform");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_SMITHING_TRIM = new StringLang("recipe_type_name.vanilla_smithing_trim");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_STONECUTTING = new StringLang("recipe_type_name.vanilla_stonecutting");
    public static final StringLang RECIPE_TYPE_NAME_VANILLA_BREWING = new StringLang("recipe_type_name.vanilla_brewing");
    public static final StringLang RECIPE_TYPE_NAME_ANVIL = new StringLang("recipe_type_name.anvil");

    public static final StringLang MENU_RECIPE_CREATOR_NAME = new StringLang("menu.recipe_creator.name");
    public static final StringLang MENU_RECIPE_CREATOR_NULL_RESULT = new StringLang("menu.recipe_creator.null_result");
    public static final StringLang MENU_RECIPE_CREATOR_NULL_INGREDIENTS = new StringLang("menu.recipe_creator.null_ingredients");
    public static final StringLang MENU_RECIPE_CREATOR_RESULT_FRAME_ICON_NAME = new StringLang("menu.recipe_creator.result_frame_icon_name");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_ICON_NAME = new StringLang("menu.recipe_creator.confirm_icon_name");

    public static final StringLang MENU_RECIPE_DISPLAY_ANVIL_COST_LEVEL = new StringLang("menu.recipe_display.anvil.cost_level");
    public static final StringLang MENU_RECIPE_DISPLAY_VANILLA_SMELTING_TIME = new StringLang("menu.recipe_display.vanilla_smelting.time");
    public static final StringLang MENU_RECIPE_DISPLAY_VANILLA_SMELTING_REWARD_EXP = new StringLang("menu.recipe_display.vanilla_smelting.reward_exp");
    public static final StringLang MENU_RECIPE_DISPLAY_BACK = new StringLang("menu.recipe_display.back");

    public static final StringLang MENU_RECIPE_BOOK_SORT_MODE_NAME_ASC = new StringLang("menu.recipe_book.sort_mode.name_asc");
    public static final StringLang MENU_RECIPE_BOOK_SORT_MODE_NAME_DESC = new StringLang("menu.recipe_book.sort_mode.name_desc");
    public static final StringLang MENU_RECIPE_BOOK_SORT_MODE_TIME_ASC = new StringLang("menu.recipe_book.sort_mode.time_asc");
    public static final StringLang MENU_RECIPE_BOOK_SORT_MODE_TIME_DESC = new StringLang("menu.recipe_book.sort_mode.time_desc");

    public static final StringLang MENU_COMMON_PREV_PAGE = new StringLang("menu.common.prev_page");
    public static final StringLang MENU_COMMON_NEXT_PAGE = new StringLang("menu.common.next_page");
    public static final StringLang MENU_COMMON_BACK = new StringLang("menu.common.back");
    public static final StringLang MENU_COMMON_BACK_LORE = new StringLang("menu.common.back_lore");
    public static final StringLang MENU_COMMON_CLICK_TO_VIEW = new StringLang("menu.common.click_to_view");
    public static final StringLang MENU_COMMON_CONFIRM_EDIT = new StringLang("menu.common.confirm_edit");
    public static final StringLang MENU_COMMON_CONFIRM_CREATE = new StringLang("menu.common.confirm_create");
    public static final StringLang MENU_COMMON_DELETE = new StringLang("menu.common.delete");

    public static final StringLang MENU_RECIPE_BOOK_TITLE = new StringLang("menu.recipe_book.title");
    public static final StringLang MENU_RECIPE_BOOK_SELECT_TITLE = new StringLang("menu.recipe_book.select_title");
    public static final StringLang MENU_RECIPE_BOOK_SORT_NAME = new StringLang("menu.recipe_book.sort_name");
    public static final StringLang MENU_RECIPE_BOOK_SORT_LORE = new StringLang("menu.recipe_book.sort_lore");
    public static final StringLang MENU_RECIPE_BOOK_RECIPE_COUNT = new StringLang("menu.recipe_book.recipe_count");
    public static final StringLang MENU_RECIPE_BOOK_CLICK_RECIPE = new StringLang("menu.recipe_book.click_recipe");
    public static final StringLang MENU_RECIPE_BOOK_ALL_RECIPES = new StringLang("menu.recipe_book.all_recipes");

    public static final StringLang MENU_RECIPE_EDITOR_NAME = new StringLang("menu.recipe_editor.name");
    public static final StringLang MENU_RECIPE_EDITOR_RESULT_FRAME = new StringLang("menu.recipe_editor.result_frame");
    public static final StringLang MENU_RECIPE_EDITOR_CONFIRM_LORE = new StringLang("menu.recipe_editor.confirm_lore");
    public static final StringLang MENU_RECIPE_EDITOR_BACK_LORE = new StringLang("menu.recipe_editor.back_lore");
    public static final StringLang MENU_RECIPE_EDITOR_DELETE_LORE = new StringLang("menu.recipe_editor.delete_lore");
    public static final StringLang MENU_RECIPE_EDITOR_CATEGORY_PREFIX = new StringLang("menu.recipe_editor.category_prefix");
    public static final StringLang MENU_RECIPE_EDITOR_CATEGORY_LORE = new StringLang("menu.recipe_editor.category_lore");

    public static final StringLang CRAFTING_CATEGORY_NAME_MISC = new StringLang("crafting_category_name.misc");
    public static final StringLang CRAFTING_CATEGORY_NAME_BUILDING = new StringLang("crafting_category_name.building");
    public static final StringLang CRAFTING_CATEGORY_NAME_REDSTONE = new StringLang("crafting_category_name.redstone");
    public static final StringLang CRAFTING_CATEGORY_NAME_TOOLS = new StringLang("crafting_category_name.tools");

    public static final StringLang COOKING_CATEGORY_NAME_FOOD = new StringLang("cooking_category_name.food");
    public static final StringLang COOKING_CATEGORY_NAME_BLOCKS = new StringLang("cooking_category_name.blocks");
    public static final StringLang COOKING_CATEGORY_NAME_MISC = new StringLang("cooking_category_name.misc");

    public static final StringLang MENU_RECIPE_EDITOR_SMELTING_TIME = new StringLang("menu.recipe_editor.smelting.time");
    public static final StringLang MENU_RECIPE_EDITOR_SMELTING_TIME_LORE = new StringLang("menu.recipe_editor.smelting.time_lore");
    public static final StringLang MENU_RECIPE_EDITOR_SMELTING_EXP = new StringLang("menu.recipe_editor.smelting.exp");
    public static final StringLang MENU_RECIPE_EDITOR_SMELTING_EXP_LORE = new StringLang("menu.recipe_editor.smelting.exp_lore");

    public static final StringLang MENU_RECIPE_EDITOR_ANVIL_COST_LEVEL = new StringLang("menu.recipe_editor.anvil.cost_level");
    public static final StringLang MENU_RECIPE_EDITOR_ANVIL_COST_LEVEL_LORE = new StringLang("menu.recipe_editor.anvil.cost_level_lore");

    public static final StringLang MENU_RECIPE_CREATOR_RESULT_FRAME = new StringLang("menu.recipe_creator.result_frame");
    public static final StringLang MENU_RECIPE_CREATOR_CATEGORY_PREFIX = new StringLang("menu.recipe_creator.category_prefix");
    public static final StringLang MENU_RECIPE_CREATOR_CATEGORY_LORE = new StringLang("menu.recipe_creator.category_lore");
    public static final StringLang MENU_RECIPE_CREATOR_SMELTING_TIME = new StringLang("menu.recipe_creator.smelting.time");
    public static final StringLang MENU_RECIPE_CREATOR_SMELTING_TIME_LORE = new StringLang("menu.recipe_creator.smelting.time_lore");
    public static final StringLang MENU_RECIPE_CREATOR_SMELTING_EXP = new StringLang("menu.recipe_creator.smelting.exp");
    public static final StringLang MENU_RECIPE_CREATOR_SMELTING_EXP_LORE = new StringLang("menu.recipe_creator.smelting.exp_lore");
    public static final StringLang MENU_RECIPE_CREATOR_ANVIL_COST_LEVEL = new StringLang("menu.recipe_creator.anvil.cost_level");
    public static final StringLang MENU_RECIPE_CREATOR_ANVIL_COST_LEVEL_LORE = new StringLang("menu.recipe_creator.anvil.cost_level_lore");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_CRAFTING = new StringLang("menu.recipe_creator.frame_lore.crafting");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_SMELTING = new StringLang("menu.recipe_creator.frame_lore.smelting");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_BREWING_INPUT = new StringLang("menu.recipe_creator.frame_lore.brewing_input");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_BREWING_INGREDIENT = new StringLang("menu.recipe_creator.frame_lore.brewing_ingredient");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_STONECUTTING = new StringLang("menu.recipe_creator.frame_lore.stonecutting");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_SMITHING = new StringLang("menu.recipe_creator.frame_lore.smithing");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_ANVIL = new StringLang("menu.recipe_creator.frame_lore.anvil");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_RESULT_RIGHT = new StringLang("menu.recipe_creator.frame_lore.result_right");
    public static final StringLang MENU_RECIPE_CREATOR_FRAME_LORE_CONFIRM_BUTTON = new StringLang("menu.recipe_creator.frame_lore.confirm_button");
    public static final StringLang MENU_RECIPE_CREATOR_RESULT_LORE_SHAPED = new StringLang("menu.recipe_creator.result_lore_shaped");
    public static final StringLang MENU_RECIPE_CREATOR_RESULT_LORE_SMELTING = new StringLang("menu.recipe_creator.result_lore_smelting");
    public static final StringLang MENU_RECIPE_CREATOR_RESULT_LORE_BREWING = new StringLang("menu.recipe_creator.result_lore_brewing");
    public static final StringLang MENU_RECIPE_CREATOR_RESULT_LORE_STONECUTTING = new StringLang("menu.recipe_creator.result_lore_stonecutting");
    public static final StringLang MENU_RECIPE_CREATOR_RESULT_LORE_SMITHING = new StringLang("menu.recipe_creator.result_lore_smithing");

    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_SHAPED = new StringLang("menu.recipe_creator.confirm_lore.shaped");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_SHAPELESS = new StringLang("menu.recipe_creator.confirm_lore.shapeless");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_SMELTING = new StringLang("menu.recipe_creator.confirm_lore.smelting");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_BLASTING = new StringLang("menu.recipe_creator.confirm_lore.blasting");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_SMOKING = new StringLang("menu.recipe_creator.confirm_lore.smoking");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_CAMPFIRE = new StringLang("menu.recipe_creator.confirm_lore.campfire");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_SMITHING = new StringLang("menu.recipe_creator.confirm_lore.smithing");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_STONECUTTING = new StringLang("menu.recipe_creator.confirm_lore.stonecutting");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_BREWING = new StringLang("menu.recipe_creator.confirm_lore.brewing");
    public static final StringLang MENU_RECIPE_CREATOR_CONFIRM_LORE_ANVIL = new StringLang("menu.recipe_creator.confirm_lore.anvil");

}
