package me.yufiria.craftorithm.api.arcenciel.block;

import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

/**
 * 脚本的模块，一个模块可以是一行语句，也可以是多行语句
 * @param <T> 语句的类型
 */
public interface IArcencielBlock<T> {

    ReturnObj<Object> exec(Player player);

    T getArcencielBlockBody();

}