package me.yufiria.craftorithm.api.arcenciel.keyword;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface IArcencielKeyword<T> {

    Map<String, IArcencielKeyword<T>> getChildNodeMap();

    T exec(Player player, List<String> args);

    String getNodeName();

}
