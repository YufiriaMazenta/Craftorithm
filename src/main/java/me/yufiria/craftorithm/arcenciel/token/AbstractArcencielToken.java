package me.yufiria.craftorithm.arcenciel.token;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractArcencielToken<T> implements IArcencielToken<T> {

    private String tokenStr;

    protected AbstractArcencielToken(String tokenStr) {
        this.tokenStr = tokenStr;
    }


    @Override
    public abstract ReturnObj<T> exec(Player player, List<String> args);

    public void setTokenStr(String tokenStr) {
        this.tokenStr = tokenStr;
    }

    @Override
    public String getTokenStr() {
        return tokenStr;
    }

}
