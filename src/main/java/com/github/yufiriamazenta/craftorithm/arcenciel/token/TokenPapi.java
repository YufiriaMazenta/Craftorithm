package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.ScriptValueUtil;
import crypticlib.util.TextUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenPapi extends AbstractArcencielToken<Boolean> {

    public static final TokenPapi INSTANCE = new TokenPapi();

    protected TokenPapi() {
        super("papi");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        String papiStr;
        if (args.size() > 2) {
            papiStr = args.get(0);
            papiStr = TextUtil.placeholder(player, papiStr);
            String operator = args.get(1);
            String valueStr = CollectionsUtil.list2ArcencielBlock(args.subList(2, args.size()));
            return new ReturnObj<>(ScriptValueUtil.compare(papiStr, valueStr, operator));
        } else {
            papiStr = CollectionsUtil.list2ArcencielBlock(args);
            papiStr = TextUtil.placeholder(player, papiStr);
            return new ReturnObj<>(Boolean.parseBoolean(TextUtil.placeholder(player, papiStr)));
        }
    }

}
