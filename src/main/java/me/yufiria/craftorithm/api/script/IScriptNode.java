package me.yufiria.craftorithm.api.script;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface IScriptNode<T> {

    Map<String, IScriptNode<T>> getChildNodeMap();

    T exec(Player player, List<String> args);

    String getNodeName();

}
