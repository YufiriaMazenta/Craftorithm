package me.yufiria.craftorithm.api.arcenciel.block;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListArcencielBlock implements IArcencielBlock<List<String>> {

    private final List<String> arcencielStatementList;

    public ListArcencielBlock(String arcencielBlockBody) {
        arcencielStatementList = new ArrayList<>(Arrays.asList(arcencielBlockBody.split("\n")));
        arcencielStatementList.removeIf(String::isEmpty);
    }

    @Override
    public Object exec(Player player) {
        Object returnObj = null;
        for (int i = 0; i < arcencielStatementList.size(); i++) {
            returnObj = new StringArcencielBlock(arcencielStatementList.get(i)).exec(player);
            if (returnObj instanceof Boolean) {
                if (returnObj.equals(false) && i + 1 < arcencielStatementList.size())
                    i ++;
            }
        }
        return returnObj;
    }

    @Override
    public List<String> getArcencielBlockBody() {
        return arcencielStatementList;
    }

}
