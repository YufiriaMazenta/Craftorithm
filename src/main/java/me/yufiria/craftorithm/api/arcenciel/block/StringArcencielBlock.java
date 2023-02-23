package me.yufiria.craftorithm.api.arcenciel.block;

import me.yufiria.craftorithm.api.arcenciel.keyword.IArcencielKeyword;
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
    public Object exec(Player player) {
        List<String> scriptChain = new ArrayList<>(Arrays.asList(arcencielBlockBody.split(" ")));
        scriptChain.removeIf(String::isEmpty);
        if (scriptChain.size() < 1)
            return null;
        String keyword = scriptChain.get(0);
        IArcencielKeyword<?> node = arcencielKeywordMap.get(keyword);
        if (node == null) {
            List<String> func = ArcencielDispatcher.INSTANCE.getFunc(keyword);
            if (func.size() < 1) {
                LangUtil.sendMsg(player, "arcenciel.unknown_keyword", MapUtil.newHashMap("<keyword>", keyword));
                return null;
            } else {
                return ArcencielDispatcher.INSTANCE.dispatchArcencielFunc(player, func);
            }
        }
        return node.exec(player, scriptChain.subList(1, scriptChain.size()));
    }

    @Override
    public String getArcencielBlockBody() {
        return arcencielBlockBody;
    }

    public static void regRootScriptNode(IArcencielKeyword<?> node) {
        arcencielKeywordMap.put(node.getNodeName(), node);
    }

}
