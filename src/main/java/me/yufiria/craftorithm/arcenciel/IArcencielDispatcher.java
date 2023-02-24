package me.yufiria.craftorithm.arcenciel;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public interface IArcencielDispatcher {

    /**
     * 执行一个Arcenciel代码块
     * @param player 执行的玩家
     * @param arcencielBlockBody 执行的代码块
     * @return 最后得到的结果
     */
    ReturnObj<Object> dispatchArcencielBlock(Player player, String arcencielBlockBody);

    /**
     * 执行一个Arcenciel函数
     * @param player 执行的玩家
     * @param arcencielFuncBody 执行的函数体
     * @return 最后得到的结果
     */
    ReturnObj<Object> dispatchArcencielFunc(Player player, List<String> arcencielFuncBody);

}
