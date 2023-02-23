package me.yufiria.craftorithm.api.arcenciel.block;

import me.yufiria.craftorithm.api.arcenciel.keyword.IArcencielKeyword;
import me.yufiria.craftorithm.api.arcenciel.obj.ArcencielSignal;
import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.MapUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringArcencielBlock implements IArcencielBlock<String> {

    private final String arcencielBlockBody;
    public static final Map<String, IArcencielKeyword<?>> arcencielKeywordMap;

    static {
        arcencielKeywordMap = new ConcurrentHashMap<>();
    }

    public StringArcencielBlock(String arcencielBlockBody) {
        this.arcencielBlockBody = arcencielBlockBody;
    }

    @Override
    public ReturnObj<Object> exec(Player player) {
        List<String> scriptChain = new ArrayList<>(Arrays.asList(arcencielBlockBody.split(" ")));
        scriptChain.removeIf(String::isEmpty);
        if (scriptChain.size() < 1)
            return new ReturnObj<>(null);
        String keywordStr = scriptChain.get(0);
        IArcencielKeyword<?> keyword = arcencielKeywordMap.get(keywordStr);
        if (keyword == null) {
            List<String> func = ArcencielDispatcher.INSTANCE.getFunc(keywordStr);
            if (func.size() < 1) {
                LangUtil.sendMsg(player, "arcenciel.unknown_keyword", MapUtil.newHashMap("<keyword>", keywordStr));
                return new ReturnObj<>(ArcencielSignal.CONTINUE);
            } else {
                return ArcencielDispatcher.INSTANCE.dispatchArcencielFunc(player, func);
            }
        }
        ReturnObj<?> returnObj = keyword.exec(player, scriptChain.subList(1, scriptChain.size()));
        Object obj = returnObj.getObj();
        return new ReturnObj<>(returnObj.getSignal(), obj);
    }

    @Override
    public String getArcencielBlockBody() {
        return arcencielBlockBody;
    }

    public static void regRootScriptNode(IArcencielKeyword<?> node) {
        arcencielKeywordMap.put(node.getNodeName(), node);
    }

}
