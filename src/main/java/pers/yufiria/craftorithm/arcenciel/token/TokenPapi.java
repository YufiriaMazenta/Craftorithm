package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.ScriptValueComparator;
import crypticlib.chat.BukkitTextProcessor;
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
            papiStr = BukkitTextProcessor.placeholder(player, papiStr);
            String operator = args.get(1);
            String valueStr = CollectionsUtils.list2ArcencielBlock(args.subList(2, args.size()));
            return new ReturnObj<>(ScriptValueComparator.compare(papiStr, valueStr, operator));
        } else {
            papiStr = CollectionsUtils.list2ArcencielBlock(args);
            papiStr = BukkitTextProcessor.placeholder(player, papiStr);
            return new ReturnObj<>(Boolean.parseBoolean(BukkitTextProcessor.placeholder(player, papiStr)));
        }
    }

}
