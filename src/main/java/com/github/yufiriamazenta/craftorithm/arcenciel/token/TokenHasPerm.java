package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenHasPerm extends AbstractArcencielToken<Boolean> {

    public static final TokenHasPerm INSTANCE = new TokenHasPerm();

    protected TokenHasPerm() {
        super("perm");
    }

    @Override
    public ReturnObj<Boolean> exec(Player player, List<String> args) {
        if (args.isEmpty()) {
            LangUtils.sendLang(player, Languages.ARCENCIEL_NOT_ENOUGH_PARAM, CollectionsUtils.newStringHashMap("<statement>", "if"));
            return new ReturnObj<>(false);
        }
        return new ReturnObj<>(player.hasPermission(args.get(0)));
    }
}
