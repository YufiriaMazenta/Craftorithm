package me.yufiria.craftorithm.arcenciel;

import me.yufiria.craftorithm.arcenciel.block.ListArcencielBlock;
import me.yufiria.craftorithm.arcenciel.block.StringArcencielBlock;
import me.yufiria.craftorithm.arcenciel.keyword.*;
import me.yufiria.craftorithm.arcenciel.obj.ArcencielSignal;
import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.config.YamlFileWrapper;
import org.bukkit.entity.Player;

import java.util.List;

public enum ArcencielDispatcher implements IArcencielDispatcher {

    INSTANCE;
    private YamlFileWrapper functionFile;

    ArcencielDispatcher() {
        regDefScriptKeyword();
    }

    @Override
    public ReturnObj<Object> dispatchArcencielBlock(Player player, String arcencielBlockBody) {
        if (arcencielBlockBody.contains("\n"))
            return new ListArcencielBlock(arcencielBlockBody).exec(player);
        else
            return new StringArcencielBlock(arcencielBlockBody).exec(player);
    }

    @Override
    public ReturnObj<Object> dispatchArcencielFunc(Player player, List<String> arcencielFuncBody) {
        ReturnObj<Object> returnObj = new ReturnObj<>();
        for (int i = 0; i < arcencielFuncBody.size(); i++) {
            returnObj = dispatchArcencielBlock(player, arcencielFuncBody.get(i));
            if (returnObj.getObj() instanceof Boolean && returnObj.getSignal().equals(ArcencielSignal.IF)) {
                if (returnObj.getObj().equals(false) && i + 1 < arcencielFuncBody.size())
                    i ++;
            }
            if (returnObj.getSignal().equals(ArcencielSignal.END))
                break;
        }
        return returnObj;
    }

    private void regDefScriptKeyword() {
        StringArcencielBlock.regScriptKeyword(KeywordIf.INSTANCE);
        StringArcencielBlock.regScriptKeyword(KeywordHasPerm.INSTANCE);
        StringArcencielBlock.regScriptKeyword(KeywordRunCmd.INSTANCE);
        StringArcencielBlock.regScriptKeyword(KeywordReturn.INSTANCE);
        StringArcencielBlock.regScriptKeyword(KeywordAll.INSTANCE);
        StringArcencielBlock.regScriptKeyword(KeywordOr.INSTANCE);
    }

    public YamlFileWrapper getFunctionFile() {
        return functionFile;
    }

    public List<String> getFunc(String funcName) {
        return functionFile.getConfig().getStringList(funcName);
    }

    public void loadFuncFile() {
        if (functionFile == null)
            functionFile = new YamlFileWrapper("function.yml");
    }

}
