package com.github.yufiriamazenta.craftorithm.config;

import crypticlib.config.yaml.YamlConfigHandler;
import crypticlib.config.yaml.entry.StringConfigEntry;

@YamlConfigHandler(path = "lang.yml")
public class Languages {

    public static final StringConfigEntry PREFIX = new StringConfigEntry("prefix", "&8[&3Craftorithm&8]");
    public static final StringConfigEntry UNSUPPORTED_VERSION = new StringConfigEntry("unsupported_version", "<prefix> 不支持的版本");
    public static final StringConfigEntry NEW_VERSION = new StringConfigEntry("new_version", "<prefix> &a检测到有新版本<new_version>, 请及时更新到新版本");
    public static final StringConfigEntry COMMAND_NO_PERM = new StringConfigEntry("command.no_perm", "<prefix> &c你没有使用此命令的权限");
    public static final StringConfigEntry COMMAND_PLAYER_ONLY = new StringConfigEntry("command.player_only", "<prefix> &c只有玩家才能执行此命令");
    public static final StringConfigEntry COMMAND_NOT_ENOUGH_PARAM = new StringConfigEntry("command.not_enough_param", "<prefix> &c命令缺少参数，缺少<number>个参数");
    public static final StringConfigEntry COMMAND_UNDEFINED_SUBCMD = new StringConfigEntry("command.undefined_subcmd", "<prefix> &c未定义的子命令");
    public static final StringConfigEntry COMMAND_ITEM_SAVE_SUCCESS = new StringConfigEntry("command.item.save.success", "<prefix> &a物品保存成功");
    public static final StringConfigEntry COMMAND_ITEM_SAVE_FAILED_SAVE_AIR = new StringConfigEntry("command.item.save.failed_save_air", "<prefix> &c物品保存失败，原因：不能保存不存在的物品");
    public static final StringConfigEntry COMMAND_ITEM_GIVE_SUCCESS = new StringConfigEntry("command.item.give.success", "<prefix> &a物品获取成功");
    public static final StringConfigEntry COMMAND_ITEM_GIVE_NOT_EXIST_ITEM = new StringConfigEntry("command.item.give.not_exist_item", "<prefix> &c不存在物品 <item_name>");
    public static final StringConfigEntry COMMAND_ITEM_GIVE_PLAYER_OFFLINE = new StringConfigEntry("command.item.give.player_offline", "<prefix> &c该玩家不在线或不存在");
    public static final StringConfigEntry COMMAND_RELOAD_SUCCESS = new StringConfigEntry("command.reload.success", "<prefix> &a插件重载成功");
    public static final StringConfigEntry COMMAND_RELOAD_EXCEPTION = new StringConfigEntry("command.reload.exception", "<prefix> &c插件重载过程中发生错误，请查看控制台");
    public static final StringConfigEntry COMMAND_REMOVE_SUCCESS = new StringConfigEntry("command.remove.success", "<prefix> &a配方删除成功");
    public static final StringConfigEntry COMMAND_REMOVE_NOT_EXIST = new StringConfigEntry("command.remove.not_exist", "<prefix> &c配方不存在");
    public static final StringConfigEntry COMMAND_DISABLE_SUCCESS = new StringConfigEntry("command.disable.success", "<prefix> &c禁用配方成功");
    public static final StringConfigEntry COMMAND_DISABLE_NOT_EXIST = new StringConfigEntry("command.disable.not_exist", "<prefix> &c配方不存在或已经被禁用");
    public static final StringConfigEntry COMMAND_DISABLE_FAILED = new StringConfigEntry("command.disable.failed", "<prefix> &c配方禁用失败，可能已被禁用或不存在");
    public static final StringConfigEntry COMMAND_VERSION = new StringConfigEntry("command.version", "<prefix> &a插件版本：<version>");
    public static final StringConfigEntry COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE = new StringConfigEntry("command.create.unsupported_recipe_type", "<prefix> &c不支持的配方类型");
    public static final StringConfigEntry COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME = new StringConfigEntry("command.create.unsupported_recipe_name", "<prefix> &c不支持的配方名字，只能使用[a-z0-9/._-]+允许的内容");
    public static final StringConfigEntry COMMAND_CREATE_NAME_USED = new StringConfigEntry("command.create.name_used", "<prefix> &c配方ID已经被使用");
    public static final StringConfigEntry COMMAND_CREATE_NULL_RESULT = new StringConfigEntry("command.create.null_result", "<prefix> &c配方结果不允许为空！");
    public static final StringConfigEntry COMMAND_CREATE_NULL_SOURCE = new StringConfigEntry("command.create.null_source", "<prefix> &c配方原料不允许为空！");
    public static final StringConfigEntry COMMAND_CREATE_SUCCESS = new StringConfigEntry("command.create.success", "<prefix> &a<recipe_type>类型配方<recipe_name>创建成功");
    public static final StringConfigEntry COMMAND_RUN_ARCENCIEL_SUCCESS = new StringConfigEntry("command.run_arcenciel.success", "<prefix> &a运行成功，耗时<time>ms");
    public static final StringConfigEntry COMMAND_LIST_UNSUPPORTED_VERSION = new StringConfigEntry("list.unsupported_version", "<prefix> &c此功能只在1.16及以上版本可用");
    public static final StringConfigEntry MENU_RECIPE_LIST_TITLE = new StringConfigEntry("menu.recipe_list.title", "&3&l配方列表");
    public static final StringConfigEntry MENU_RECIPE_LIST_ICON_FRAME = new StringConfigEntry("menu.recipe_list.icon.frame", "&3&l配方列表");
    public static final StringConfigEntry MENU_RECIPE_LIST_ICON_PREVIOUS = new StringConfigEntry("menu.recipe_list.icon.previous", "&a上一页");
    public static final StringConfigEntry MENU_RECIPE_LIST_ICON_NEXT = new StringConfigEntry("menu.recipe_list.icon.next", "&a下一页");
    public static final StringConfigEntry MENU_NEW_RECIPE_LIST_TITLE = new StringConfigEntry("menu.new_recipe_list.title", "&3&lCraftorithm新增配方列表");
    public static final StringConfigEntry MENU_NEW_RECIPE_LIST_ICON_FRAME = new StringConfigEntry("menu.new_recipe_list.icon.frame", "&3&l新增配方列表");
    public static final StringConfigEntry MENU_NEW_RECIPE_LIST_ICON_PREVIOUS = new StringConfigEntry("menu.new_recipe_list.icon.previous", "&a上一页");
    public static final StringConfigEntry MENU_NEW_RECIPE_LIST_ICON_NEXT = new StringConfigEntry("menu.new_recipe_list.icon.next", "&a下一页");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_SHAPED = new StringConfigEntry("menu.recipe_display.title.shaped", "有序配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_SHAPELESS = new StringConfigEntry("menu.recipe_display.title.shapeless", "无序配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_FURNACE = new StringConfigEntry("menu.recipe_display.title.furnace", "熔炉配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_BLASTING = new StringConfigEntry("menu.recipe_display.title.blasting", "高炉配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_SMOKING = new StringConfigEntry("menu.recipe_display.title.smoking", "烟熏炉配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_CAMPFIRE = new StringConfigEntry("menu.recipe_display.title.campfire", "营火配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_SMITHING = new StringConfigEntry("menu.recipe_display.title.smithing", "锻造配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_STONE_CUTTING = new StringConfigEntry("menu.recipe_display.title.stone_cutting", "切石配方");
    public static final StringConfigEntry MENU_RECIPE_DISPLAY_TITLE_POTION = new StringConfigEntry("menu.recipe_display.title.potion", "酿造配方");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_TITLE = new StringConfigEntry("menu.recipe_creator.title", "&3创建<recipe_type>配方: <recipe_name>");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_FRAME = new StringConfigEntry("menu.recipe_creator.icon.frame", "&a创建配方");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_RESULT_FRAME = new StringConfigEntry("menu.recipe_creator.icon.result_frame", "&a配方结果");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_CONFIRM = new StringConfigEntry("menu.recipe_creator.icon.confirm", "&3&l确认创建");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_COOKING_FRAME = new StringConfigEntry("menu.recipe_creator.icon.cooking_frame", "&a烧炼原料");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_SMITHING_FRAME = new StringConfigEntry("menu.recipe_creator.icon.smithing_frame", "&a锻造原料");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_POTION_FRAME = new StringConfigEntry("menu.recipe_creator.icon.potion_frame", "&a酿造原料");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_FURNACE_TOGGLE = new StringConfigEntry("menu.recipe_creator.icon.furnace_toggle", "&a熔炉配方");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_BLASTING_TOGGLE = new StringConfigEntry("menu.recipe_creator.icon.blasting_toggle", "&a高炉配方");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_SMOKING_TOGGLE = new StringConfigEntry("menu.recipe_creator.icon.smoking_toggle", "&a烟熏炉配方");
    public static final StringConfigEntry MENU_RECIPE_CREATOR_ICON_CAMPFIRE_TOGGLE = new StringConfigEntry("menu.recipe_creator.icon.campfire_toggle", "&a营火配方");
    public static final StringConfigEntry ARCENCIEL_NOT_ENOUGH_PARAM = new StringConfigEntry("arcenciel.not_enough_param", "<prefix> &c语句\"<statement>\"不完整");
    public static final StringConfigEntry ARCENCIEL_UNKNOWN_TOKEN = new StringConfigEntry("arcenciel.unknown_token", "<prefix> &c未定义的关键词或函数<token>");
    public static final StringConfigEntry LOAD_FINISH = new StringConfigEntry("load.finish", "<prefix> &a插件加载完毕");
    public static final StringConfigEntry LOAD_RECIPE_LOAD_EXCEPTION = new StringConfigEntry("load.recipe_load_exception", "<prefix> &c加载配方<recipe_name>时出现错误");
    public static final StringConfigEntry LOAD_ITEM_LOAD_EXCEPTION = new StringConfigEntry("load.item_load_exception", "<prefix> &c加载物品<item_name>时出现错误");
    public static final StringConfigEntry LOAD_HOOK_PLUGIN_SUCCESS = new StringConfigEntry("load.hook_plugin.success", "<prefix> &a发现<plugin>，已挂钩");
    public static final StringConfigEntry LOAD_HOOK_PLUGIN_NOT_EXIST = new StringConfigEntry("load.hook_plugin.not_exist", "<prefix> &c未发现<plugin>");

}
