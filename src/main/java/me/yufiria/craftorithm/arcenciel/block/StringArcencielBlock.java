package me.yufiria.craftorithm.arcenciel.block;

import me.yufiria.craftorithm.arcenciel.keyword.IArcencielToken;
import me.yufiria.craftorithm.arcenciel.obj.ArcencielSignal;
import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.ContainerUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringArcencielBlock implements IArcencielBlock<String> {

    private final String arcencielBlockBody;
    private static final Map<String, IArcencielToken<?>> arcencielKeywordMap;

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
            return new ReturnObj<>(ArcencielSignal.CONTINUE, null);
        String keywordStr = scriptChain.get(0);
        IArcencielToken<?> keyword = arcencielKeywordMap.get(keywordStr);
        if (keyword == null) {
            List<String> func = ArcencielDispatcher.INSTANCE.getFunc(keywordStr);
            if (func.size() < 1) {
                LangUtil.sendMsg(player, "arcenciel.unknown_token", ContainerUtil.newHashMap("<token>", keywordStr));
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

    public static void regScriptKeyword(IArcencielToken<?> node) {
        arcencielKeywordMap.put(node.getTokenStr(), node);
    }

    public static Map<String, IArcencielToken<?>> getArcencielKeywordMap() {
        return arcencielKeywordMap;
    }

}
