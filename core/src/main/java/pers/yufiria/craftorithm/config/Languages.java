package pers.yufiria.craftorithm.config;

import crypticlib.lang.LangHandler;
import crypticlib.lang.entry.StringLangEntry;

@LangHandler(langFileFolder = "lang")
public class Languages {

    public static final StringLangEntry PREFIX = new StringLangEntry("prefix");
    public static final StringLangEntry UNSUPPORTED_VERSION = new StringLangEntry("unsupported_version");
    public static final StringLangEntry NEW_VERSION = new StringLangEntry("new_version");
    public static final StringLangEntry COMMAND_NO_PERM = new StringLangEntry("command.no_perm");
    public static final StringLangEntry COMMAND_PLAYER_ONLY = new StringLangEntry("command.player_only");
    public static final StringLangEntry COMMAND_UNKNOWN_PLAYER = new StringLangEntry("command.unknown_player");
    public static final StringLangEntry COMMAND_ITEM_SAVE_SUCCESS = new StringLangEntry("command.item.save.success");
    public static final StringLangEntry COMMAND_ITEM_SAVE_FAILED_SAVE_AIR = new StringLangEntry("command.item.save.failed_save_air");
    public static final StringLangEntry COMMAND_ITEM_GIVE_SUCCESS = new StringLangEntry("command.item.give.success");
    public static final StringLangEntry COMMAND_ITEM_GIVE_NOT_EXIST_ITEM = new StringLangEntry("command.item.give.not_exist_item");
    public static final StringLangEntry COMMAND_ITEM_GIVE_PLAYER_OFFLINE = new StringLangEntry("command.item.give.player_offline");
    public static final StringLangEntry COMMAND_ITEM_FUEL_ADD_SUCCESS = new StringLangEntry("command.item.fuel.add.success");
    public static final StringLangEntry COMMAND_ITEM_FUEL_ADD_FAILED_ADD_AIR = new StringLangEntry("command.item.fuel.add.failed_add_air");
    public static final StringLangEntry COMMAND_ITEM_FUEL_ADD_FAILED_EXIST = new StringLangEntry("command.item.fuel.add.failed_exist");
    public static final StringLangEntry COMMAND_ITEM_FUEL_REMOVE_SUCCESS = new StringLangEntry("command.item.fuel.remove.success");
    public static final StringLangEntry COMMAND_ITEM_FUEL_REMOVE_FAILED_NOT_EXIST = new StringLangEntry("command.item.fuel.remove.failed_not_exist");
    public static final StringLangEntry COMMAND_RELOAD_SUCCESS = new StringLangEntry("command.reload.success");
    public static final StringLangEntry COMMAND_RELOAD_EXCEPTION = new StringLangEntry("command.reload.exception");
    public static final StringLangEntry COMMAND_RELOAD_RECIPE_MANAGER_RELOADING = new StringLangEntry("command.reload.recipe_manager_reloading");
    public static final StringLangEntry COMMAND_REMOVE_SUCCESS = new StringLangEntry("command.remove.success");
    public static final StringLangEntry COMMAND_REMOVE_NOT_EXIST = new StringLangEntry("command.remove.not_exist");
    public static final StringLangEntry COMMAND_DISABLE_SUCCESS = new StringLangEntry("command.disable.success");
    public static final StringLangEntry COMMAND_DISABLE_NOT_EXIST = new StringLangEntry("command.disable.not_exist");
    public static final StringLangEntry COMMAND_DISABLE_FAILED = new StringLangEntry("command.disable.failed");
    public static final StringLangEntry COMMAND_VERSION = new StringLangEntry("command.version");
    public static final StringLangEntry COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE = new StringLangEntry("command.create.unsupported_recipe_type");
    public static final StringLangEntry COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME = new StringLangEntry("command.create.unsupported_recipe_name");
    public static final StringLangEntry COMMAND_CREATE_NAME_USED = new StringLangEntry("command.create.name_used");
    public static final StringLangEntry COMMAND_CREATE_INVALID_RECIPE_ID = new StringLangEntry("command.create.invalid_recipe_id");
    public static final StringLangEntry COMMAND_CREATE_SUCCESS = new StringLangEntry("command.create.success");
    public static final StringLangEntry COMMAND_CREATE_INPUT_HINT_SMELTING_TIME = new StringLangEntry("command.create.input_hint.smelting_time");
    public static final StringLangEntry COMMAND_CREATE_INPUT_HINT_SMELTING_EXP = new StringLangEntry("command.create.input_hint.smelting_exp");
    public static final StringLangEntry COMMAND_CREATE_INPUT_HINT_ANVIL_COST_LEVEL = new StringLangEntry("command.create.input_hint.anvil_cost_level");
    public static final StringLangEntry COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE = new StringLangEntry("command.display.unsupported_recipe_type");
    public static final StringLangEntry COMMAND_OPENMENU_UNKNOWN_MENU = new StringLangEntry("command.openmenu.unknown_menu");
    public static final StringLangEntry COMMAND_SCRIPT_OPERATION_TIME = new StringLangEntry("command.script.operation_time");
    public static final StringLangEntry COMMAND_EDIT_INVALID_RECIPE_ID = new StringLangEntry("command.edit.invalid_recipe_id");
    public static final StringLangEntry COMMAND_EDIT_RECIPE_NOT_FOUND = new StringLangEntry("command.edit.recipe_not_found");
    public static final StringLangEntry COMMAND_EDIT_SUCCESS = new StringLangEntry("command.edit.success");
    public static final StringLangEntry COMMAND_RECIPEBOOK_SUCCESS = new StringLangEntry("command.recipebook.success");
    public static final StringLangEntry COMMAND_RECIPEBOOK_TYPE_NOT_FOUND = new StringLangEntry("command.recipebook.type_not_found");

    public static final StringLangEntry LOAD_FINISH = new StringLangEntry("load_finish");
    public static final StringLangEntry RECIPE_LOAD_EXCEPTION = new StringLangEntry("recipe_load_exception");
    public static final StringLangEntry ITEM_LOAD_EXCEPTION = new StringLangEntry("item_load_exception");
    public static final StringLangEntry LOAD_HOOK_PLUGIN_SUCCESS = new StringLangEntry("hook_plugin_success");

    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SHAPED = new StringLangEntry("recipe_type_name.vanilla_shaped");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SHAPELESS = new StringLangEntry("recipe_type_name.vanilla_shapeless");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SMELTING_FURNACE = new StringLangEntry("recipe_type_name.vanilla_smelting_furnace");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SMELTING_BLAST = new StringLangEntry("recipe_type_name.vanilla_smelting_blast");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SMELTING_SMOKER = new StringLangEntry("recipe_type_name.vanilla_smelting_smoker");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SMELTING_CAMPFIRE = new StringLangEntry("recipe_type_name.vanilla_smelting_campfire");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SMITHING_TRANSFORM = new StringLangEntry("recipe_type_name.vanilla_smithing_transform");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_SMITHING_TRIM = new StringLangEntry("recipe_type_name.vanilla_smithing_trim");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_STONECUTTING = new StringLangEntry("recipe_type_name.vanilla_stonecutting");
    public static final StringLangEntry RECIPE_TYPE_NAME_VANILLA_BREWING = new StringLangEntry("recipe_type_name.vanilla_brewing");
    public static final StringLangEntry RECIPE_TYPE_NAME_ANVIL = new StringLangEntry("recipe_type_name.anvil");

    public static final StringLangEntry MENU_RECIPE_CREATOR_NAME = new StringLangEntry("menu.recipe_creator.name");
    public static final StringLangEntry MENU_RECIPE_CREATOR_NULL_RESULT = new StringLangEntry("menu.recipe_creator.null_result");
    public static final StringLangEntry MENU_RECIPE_CREATOR_NULL_INGREDIENTS = new StringLangEntry("menu.recipe_creator.null_ingredients");
    public static final StringLangEntry MENU_RECIPE_CREATOR_RESULT_FRAME_ICON_NAME = new StringLangEntry("menu.recipe_creator.result_frame_icon_name");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_ICON_NAME = new StringLangEntry("menu.recipe_creator.confirm_icon_name");

    public static final StringLangEntry MENU_RECIPE_DISPLAY_ANVIL_COST_LEVEL = new StringLangEntry("menu.recipe_display.anvil.cost_level");
    public static final StringLangEntry MENU_RECIPE_DISPLAY_VANILLA_SMELTING_TIME = new StringLangEntry("menu.recipe_display.vanilla_smelting.time");
    public static final StringLangEntry MENU_RECIPE_DISPLAY_VANILLA_SMELTING_REWARD_EXP = new StringLangEntry("menu.recipe_display.vanilla_smelting.reward_exp");

    public static final StringLangEntry MENU_RECIPE_BOOK_SORT_MODE_NAME_ASC = new StringLangEntry("menu.recipe_book.sort_mode.name_asc");
    public static final StringLangEntry MENU_RECIPE_BOOK_SORT_MODE_NAME_DESC = new StringLangEntry("menu.recipe_book.sort_mode.name_desc");
    public static final StringLangEntry MENU_RECIPE_BOOK_SORT_MODE_TIME_ASC = new StringLangEntry("menu.recipe_book.sort_mode.time_asc");
    public static final StringLangEntry MENU_RECIPE_BOOK_SORT_MODE_TIME_DESC = new StringLangEntry("menu.recipe_book.sort_mode.time_desc");

    public static final StringLangEntry MENU_COMMON_PREV_PAGE = new StringLangEntry("menu.common.prev_page");
    public static final StringLangEntry MENU_COMMON_NEXT_PAGE = new StringLangEntry("menu.common.next_page");
    public static final StringLangEntry MENU_COMMON_BACK = new StringLangEntry("menu.common.back");
    public static final StringLangEntry MENU_COMMON_BACK_LORE = new StringLangEntry("menu.common.back_lore");
    public static final StringLangEntry MENU_COMMON_CLICK_TO_VIEW = new StringLangEntry("menu.common.click_to_view");
    public static final StringLangEntry MENU_COMMON_CONFIRM_EDIT = new StringLangEntry("menu.common.confirm_edit");
    public static final StringLangEntry MENU_COMMON_CONFIRM_CREATE = new StringLangEntry("menu.common.confirm_create");

    public static final StringLangEntry MENU_RECIPE_BOOK_TITLE = new StringLangEntry("menu.recipe_book.title");
    public static final StringLangEntry MENU_RECIPE_BOOK_SELECT_TITLE = new StringLangEntry("menu.recipe_book.select_title");
    public static final StringLangEntry MENU_RECIPE_BOOK_SORT_NAME = new StringLangEntry("menu.recipe_book.sort_name");
    public static final StringLangEntry MENU_RECIPE_BOOK_SORT_LORE = new StringLangEntry("menu.recipe_book.sort_lore");
    public static final StringLangEntry MENU_RECIPE_BOOK_RECIPE_COUNT = new StringLangEntry("menu.recipe_book.recipe_count");
    public static final StringLangEntry MENU_RECIPE_BOOK_CLICK_RECIPE = new StringLangEntry("menu.recipe_book.click_recipe");

    public static final StringLangEntry MENU_RECIPE_EDITOR_NAME = new StringLangEntry("menu.recipe_editor.name");
    public static final StringLangEntry MENU_RECIPE_EDITOR_RESULT_FRAME = new StringLangEntry("menu.recipe_editor.result_frame");
    public static final StringLangEntry MENU_RECIPE_EDITOR_CONFIRM_LORE = new StringLangEntry("menu.recipe_editor.confirm_lore");
    public static final StringLangEntry MENU_RECIPE_EDITOR_BACK_LORE = new StringLangEntry("menu.recipe_editor.back_lore");
    public static final StringLangEntry MENU_RECIPE_EDITOR_CATEGORY_PREFIX = new StringLangEntry("menu.recipe_editor.category_prefix");
    public static final StringLangEntry MENU_RECIPE_EDITOR_CATEGORY_LORE = new StringLangEntry("menu.recipe_editor.category_lore");

    public static final StringLangEntry MENU_RECIPE_EDITOR_CRAFTING_CATEGORY_MISC = new StringLangEntry("menu.recipe_editor.crafting_category.misc");
    public static final StringLangEntry MENU_RECIPE_EDITOR_CRAFTING_CATEGORY_BUILDING = new StringLangEntry("menu.recipe_editor.crafting_category.building");
    public static final StringLangEntry MENU_RECIPE_EDITOR_CRAFTING_CATEGORY_REDSTONE = new StringLangEntry("menu.recipe_editor.crafting_category.redstone");
    public static final StringLangEntry MENU_RECIPE_EDITOR_CRAFTING_CATEGORY_TOOLS = new StringLangEntry("menu.recipe_editor.crafting_category.tools");

    public static final StringLangEntry MENU_RECIPE_EDITOR_COOKING_CATEGORY_FOOD = new StringLangEntry("menu.recipe_editor.cooking_category.food");
    public static final StringLangEntry MENU_RECIPE_EDITOR_COOKING_CATEGORY_BLOCKS = new StringLangEntry("menu.recipe_editor.cooking_category.blocks");
    public static final StringLangEntry MENU_RECIPE_EDITOR_COOKING_CATEGORY_MISC = new StringLangEntry("menu.recipe_editor.cooking_category.misc");

    public static final StringLangEntry MENU_RECIPE_EDITOR_SMELTING_TIME = new StringLangEntry("menu.recipe_editor.smelting.time");
    public static final StringLangEntry MENU_RECIPE_EDITOR_SMELTING_TIME_LORE = new StringLangEntry("menu.recipe_editor.smelting.time_lore");
    public static final StringLangEntry MENU_RECIPE_EDITOR_SMELTING_EXP = new StringLangEntry("menu.recipe_editor.smelting.exp");
    public static final StringLangEntry MENU_RECIPE_EDITOR_SMELTING_EXP_LORE = new StringLangEntry("menu.recipe_editor.smelting.exp_lore");

    public static final StringLangEntry MENU_RECIPE_EDITOR_ANVIL_COST_LEVEL = new StringLangEntry("menu.recipe_editor.anvil.cost_level");
    public static final StringLangEntry MENU_RECIPE_EDITOR_ANVIL_COST_LEVEL_LORE = new StringLangEntry("menu.recipe_editor.anvil.cost_level_lore");

    public static final StringLangEntry MENU_RECIPE_CREATOR_RESULT_FRAME = new StringLangEntry("menu.recipe_creator.result_frame");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_CRAFTING = new StringLangEntry("menu.recipe_creator.frame_lore.crafting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_SMELTING = new StringLangEntry("menu.recipe_creator.frame_lore.smelting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_BREWING_INPUT = new StringLangEntry("menu.recipe_creator.frame_lore.brewing_input");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_BREWING_INGREDIENT = new StringLangEntry("menu.recipe_creator.frame_lore.brewing_ingredient");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_STONECUTTING = new StringLangEntry("menu.recipe_creator.frame_lore.stonecutting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_SMITHING = new StringLangEntry("menu.recipe_creator.frame_lore.smithing");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_ANVIL = new StringLangEntry("menu.recipe_creator.frame_lore.anvil");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_RESULT_RIGHT = new StringLangEntry("menu.recipe_creator.frame_lore.result_right");
    public static final StringLangEntry MENU_RECIPE_CREATOR_FRAME_LORE_CONFIRM_BUTTON = new StringLangEntry("menu.recipe_creator.frame_lore.confirm_button");
    public static final StringLangEntry MENU_RECIPE_CREATOR_RESULT_LORE_SHAPED = new StringLangEntry("menu.recipe_creator.result_lore_shaped");
    public static final StringLangEntry MENU_RECIPE_CREATOR_RESULT_LORE_SMELTING = new StringLangEntry("menu.recipe_creator.result_lore_smelting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_RESULT_LORE_BREWING = new StringLangEntry("menu.recipe_creator.result_lore_brewing");
    public static final StringLangEntry MENU_RECIPE_CREATOR_RESULT_LORE_STONECUTTING = new StringLangEntry("menu.recipe_creator.result_lore_stonecutting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_RESULT_LORE_SMITHING = new StringLangEntry("menu.recipe_creator.result_lore_smithing");

    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_SHAPED = new StringLangEntry("menu.recipe_creator.confirm_lore.shaped");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_SHAPELESS = new StringLangEntry("menu.recipe_creator.confirm_lore.shapeless");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_SMELTING = new StringLangEntry("menu.recipe_creator.confirm_lore.smelting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_BLASTING = new StringLangEntry("menu.recipe_creator.confirm_lore.blasting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_SMOKING = new StringLangEntry("menu.recipe_creator.confirm_lore.smoking");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_CAMPFIRE = new StringLangEntry("menu.recipe_creator.confirm_lore.campfire");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_SMITHING = new StringLangEntry("menu.recipe_creator.confirm_lore.smithing");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_STONECUTTING = new StringLangEntry("menu.recipe_creator.confirm_lore.stonecutting");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_BREWING = new StringLangEntry("menu.recipe_creator.confirm_lore.brewing");
    public static final StringLangEntry MENU_RECIPE_CREATOR_CONFIRM_LORE_ANVIL = new StringLangEntry("menu.recipe_creator.confirm_lore.anvil");

    public static final StringLangEntry MENU_RECIPE_DISPLAY_BACK = new StringLangEntry("menu.recipe_display.back");

}
