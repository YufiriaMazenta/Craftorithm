package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class AbstractArcencielKeyword<T> implements IArcencielKeyword<T> {

    private String keyword;

    protected AbstractArcencielKeyword(String keyword) {
        this.keyword = keyword;
    }


    @Override
    public abstract ReturnObj<T> exec(Player player, List<String> args);

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

}
