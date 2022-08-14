package top.oasismc.oasisrecipe.script.condition;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;
import top.oasismc.oasisrecipe.script.condition.impl.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ConditionDispatcher {

    INSTANCE;
    private final Map<String, IRecipeCondition> conditionMap;

    ConditionDispatcher() {
        conditionMap = new ConcurrentHashMap<>();
        regDefConditions();
    }

    private void regDefConditions() {
        regCondition(PermCondition.INSTANCE);
        regCondition(LevelCondition.INSTANCE);
        regCondition(TimeCondition.INSTANCE);
        regCondition(ChunkCondition.INSTANCE);
        regCondition(EffectCondition.INSTANCE);
        regCondition(BiomeCondition.INSTANCE);
        regCondition(RandomCondition.INSTANCE);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            regCondition(PapiCondition.INSTANCE);
        }
        if (ActionDispatcher.INSTANCE.getEconomy() != null)
            regCondition(MoneyCondition.INSTANCE);
        if (ActionDispatcher.INSTANCE.getPlayerPoints() != null)
            regCondition(PointsCondition.INSTANCE);
    }

    public boolean regCondition(IRecipeCondition action, boolean force) {
        if (conditionMap.containsKey(action.getStatement())) {
            if (!force)
                throw new IllegalArgumentException("Condition " + action + " already registered");
        }
        conditionMap.put(action.getStatement(), action);
        return true;
    }

    public boolean regCondition(IRecipeCondition condition) {
        return regCondition(condition, false);
    }

    public boolean dispatchConditions(List<String> actions, Player player) {
        System.out.println(actions);
        if (actions.size() < 1)
            return true;
        boolean result = dispatchCondition(actions.get(0), player);
        actions.remove(0);
        for (String action : actions) {
            boolean tmpResult = dispatchCondition(action.substring(1), player);
            char prefix = action.charAt(0);
            switch (prefix) {
                case '&':
                    result = result && tmpResult;
                    break;
                case '|':
                    result = result || tmpResult;
                    break;
                default:
                    throw new IllegalArgumentException("You must specify a condition type");
            }
        }
        return result;
    }

    public boolean dispatchCondition(String conditionLine, Player player) {
        boolean negate = false;
        if (conditionLine.startsWith("!")) {
            negate = true;
            conditionLine = conditionLine.substring(1);
        }
        int index = conditionLine.indexOf(" ");
        String condition = conditionLine.substring(0, index);
        String conditionArg = conditionLine.substring(index + 1);
        if (!conditionMap.containsKey(condition)) {
            throw new IllegalArgumentException("Condition " + condition + " does not exist");
        }
        boolean result = conditionMap.get(condition).exec(conditionArg, player);
        if (negate)
            return !result;
        return result;
    }

    public Map<String, IRecipeCondition> getConditionMap() {
        return Collections.unmodifiableMap(conditionMap);
    }

}
