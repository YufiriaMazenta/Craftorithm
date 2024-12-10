package pers.yufiria.craftorithm.arcenciel.block;

import pers.yufiria.craftorithm.arcenciel.obj.ArcencielSignal;
import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
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
        ReturnObj<Object> returnObj = new ReturnObj<>(ArcencielSignal.CONTINUE, null);
        for (int i = 0; i < arcencielStatementList.size(); i++) {
            returnObj = new StringArcencielBlock(arcencielStatementList.get(i)).exec(player);
            if (returnObj.obj() instanceof Boolean && returnObj.signal().equals(ArcencielSignal.IF)) {
                if (returnObj.obj().equals(false) && i + 1 < arcencielStatementList.size())
                    i ++;
            }
            if (returnObj.signal().equals(ArcencielSignal.END))
                break;
        }
        return new ReturnObj<>(returnObj.signal(), returnObj.obj());
    }

    @Override
    public List<String> arcencielBlockBody() {
        return arcencielStatementList;
    }

}
