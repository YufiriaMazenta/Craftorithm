package top.oasismc.oasisrecipe.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import top.oasismc.oasisrecipe.util.LangUtil;

public class CreateRecipeMenu {

    public static Inventory buildMenu(String recipeType, String recipeName) {
        MenuHolder holder = new MenuHolder();
        String title = LangUtil.lang("menu.title");
        Inventory menu = Bukkit.createInventory(holder, 54, LangUtil.color(title));
        holder.setInventory(menu);
        //TODO 菜单布局
        return menu;
    }

}
