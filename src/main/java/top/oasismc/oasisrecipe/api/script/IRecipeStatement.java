package top.oasismc.oasisrecipe.api.script;

import org.bukkit.entity.Player;

public interface IRecipeStatement<T> {

    T exec(String arg, Player player);

    String getStatement();

}
