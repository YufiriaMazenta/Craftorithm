package me.yufiria.craftorithm.script;

import me.yufiria.craftorithm.api.script.IScriptNode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ScriptDispatcher {

    INSTANCE;

    public final Map<String, IScriptNode<?>> rootScriptNodeMap;

    ScriptDispatcher() {
        rootScriptNodeMap = new ConcurrentHashMap<>();
        regDefRootScriptNode();
    }

    private void regDefRootScriptNode() {

    }

    public Object dispatchScriptLine(Player player, String scriptLine) {
        List<String> scriptChain = new ArrayList<>(Arrays.asList(scriptLine.split(" ")));
        scriptChain.removeIf(String::isEmpty);
        if (scriptChain.size() < 1)
            return null;
        String scriptChainHead = scriptChain.get(0);
        if (!rootScriptNodeMap.containsKey(scriptChainHead)) {
            return null;
        }
        return rootScriptNodeMap.get(scriptChainHead).exec(player, scriptChain.subList(1, scriptChain.size()));
    }

}
