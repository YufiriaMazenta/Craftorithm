package pers.yufiriamazenta.craftorithm.arcenciel.block;

import pers.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import pers.yufiriamazenta.craftorithm.arcenciel.obj.ArcencielSignal;
import pers.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiriamazenta.craftorithm.arcenciel.token.IArcencielToken;
import pers.yufiriamazenta.craftorithm.config.Languages;
import pers.yufiriamazenta.craftorithm.util.CollectionsUtil;
import pers.yufiriamazenta.craftorithm.util.LangUtil;
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
        if (scriptChain.isEmpty())
            return new ReturnObj<>(ArcencielSignal.CONTINUE, null);
        String keywordStr = scriptChain.get(0);
        IArcencielToken<?> keyword = arcencielKeywordMap.get(keywordStr);
        if (keyword == null) {
            List<String> func = ArcencielDispatcher.INSTANCE.getFunc(keywordStr);
            if (func.isEmpty()) {
                LangUtil.sendLang(player, Languages.ARCENCIEL_UNKNOWN_TOKEN, CollectionsUtil.newStringHashMap("<token>", keywordStr));
                return new ReturnObj<>(ArcencielSignal.CONTINUE);
            } else {
                return ArcencielDispatcher.INSTANCE.dispatchArcencielFunc(player, func);
            }
        }
        ReturnObj<?> returnObj = keyword.exec(player, scriptChain.subList(1, scriptChain.size()));
        Object obj = returnObj.obj();
        return new ReturnObj<>(returnObj.signal(), obj);
    }

    @Override
    public String arcencielBlockBody() {
        return arcencielBlockBody;
    }

    public static void regScriptKeyword(IArcencielToken<?> node) {
        arcencielKeywordMap.put(node.tokenStr(), node);
    }

    public static Map<String, IArcencielToken<?>> getArcencielKeywordMap() {
        return arcencielKeywordMap;
    }

}
