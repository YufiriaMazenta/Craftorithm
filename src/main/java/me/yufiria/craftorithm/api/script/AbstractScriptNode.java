package me.yufiria.craftorithm.api.script;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class AbstractScriptNode<T> implements IScriptNode<T> {

    private String nodeName;
    private Map<String, IScriptNode<T>> childNodeMap;

    protected AbstractScriptNode(String nodeName, Map<String, IScriptNode<T>> childNodeMap) {
        this.nodeName = nodeName;
        this.childNodeMap = childNodeMap;
    }

    @Override
    public T exec(Player player, List<String> args) {
        if (args == null || args.size() < 1)
            return null;
        return childNodeMap.get(args.get(0)).exec(player, args.subList(1, args.size()));
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    public void setChildNodeMap(Map<String, IScriptNode<T>> childNodeMap) {
        this.childNodeMap = childNodeMap;
    }

    @Override
    public Map<String, IScriptNode<T>> getChildNodeMap() {
        return childNodeMap;
    }

}
