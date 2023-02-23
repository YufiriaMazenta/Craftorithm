package me.yufiria.craftorithm.api.arcenciel.block;

import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
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
    public ReturnObj<Object> exec(Player player) {
        ReturnObj<Object> returnObj = new ReturnObj<>(null);
        for (int i = 0; i < arcencielStatementList.size(); i++) {
            returnObj = new StringArcencielBlock(arcencielStatementList.get(i)).exec(player);
            if (returnObj.getObj() instanceof Boolean) {
                if (returnObj.getObj().equals(false) && i + 1 < arcencielStatementList.size())
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
