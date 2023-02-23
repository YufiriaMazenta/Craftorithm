package me.yufiria.craftorithm.api.arcenciel.keyword;

import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface IArcencielKeyword<T> {

    Map<String, IArcencielKeyword<T>> getChildNodeMap();

    ReturnObj<T> exec(Player player, List<String> args);

    String getNodeName();

}
