package me.yufiria.craftorithm.arcenciel;

import me.yufiria.craftorithm.api.arcenciel.IArcencielDispatcher;
import me.yufiria.craftorithm.api.arcenciel.block.ListArcencielBlock;
import me.yufiria.craftorithm.api.arcenciel.block.StringArcencielBlock;
import me.yufiria.craftorithm.api.arcenciel.obj.ArcencielSignal;
import me.yufiria.craftorithm.api.arcenciel.obj.ReturnObj;
import me.yufiria.craftorithm.arcenciel.keyword.KeywordHasPerm;
import me.yufiria.craftorithm.arcenciel.keyword.KeywordIf;
import me.yufiria.craftorithm.arcenciel.keyword.KeywordReturn;
import me.yufiria.craftorithm.arcenciel.keyword.KeywordRunCmd;
import me.yufiria.craftorithm.config.YamlFileWrapper;
import org.bukkit.entity.Player;

import java.util.List;

public enum ArcencielDispatcher implements IArcencielDispatcher {

    INSTANCE;
    private YamlFileWrapper functionFile;

    ArcencielDispatcher() {
        regDefRootScriptKeyword();
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
            if (returnObj.getObj() instanceof Boolean) {
                if (returnObj.getObj().equals(false) && i + 1 < arcencielFuncBody.size())
                    i ++;
            }
            if (returnObj.getSignal().equals(ArcencielSignal.END))
                break;
        }
        return returnObj;
    }

    private void regDefRootScriptKeyword() {
        StringArcencielBlock.regRootScriptNode(KeywordIf.INSTANCE);
        StringArcencielBlock.regRootScriptNode(KeywordHasPerm.INSTANCE);
        StringArcencielBlock.regRootScriptNode(KeywordRunCmd.INSTANCE);
        StringArcencielBlock.regRootScriptNode(KeywordReturn.INSTANCE);
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
