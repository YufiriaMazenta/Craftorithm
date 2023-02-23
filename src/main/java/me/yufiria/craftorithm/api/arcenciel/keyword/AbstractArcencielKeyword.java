package me.yufiria.craftorithm.api.arcenciel.keyword;

import me.yufiria.craftorithm.api.arcenciel.obj.ArcencielSignal;
import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractArcencielKeyword<T> implements IArcencielKeyword<T> {

    private String nodeName;
    private Map<String, IArcencielKeyword<T>> childNodeMap;

    protected AbstractArcencielKeyword(String nodeName) {
        this(nodeName, new ConcurrentHashMap<>());
    }

    protected AbstractArcencielKeyword(String nodeName, Map<String, IArcencielKeyword<T>> childNodeMap) {
        this.nodeName = nodeName;
        this.childNodeMap = childNodeMap;
    }

    @Override
    public ReturnObj<T> exec(Player player, List<String> args) {
        if (args == null || args.size() < 1)
            return new ReturnObj<>(null);
        return childNodeMap.get(args.get(0)).exec(player, args.subList(1, args.size()));
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    public void setChildNodeMap(Map<String, IArcencielKeyword<T>> childNodeMap) {
        this.childNodeMap = childNodeMap;
    }

    @Override
    public Map<String, IArcencielKeyword<T>> getChildNodeMap() {
        return childNodeMap;
    }

}
